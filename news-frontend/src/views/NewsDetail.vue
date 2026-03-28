<template>
  <div class="page" v-loading="loading">
    <el-card v-if="detail">
      <h1>{{ detail.title }}</h1>
      <div class="meta" style="margin-bottom: 12px">
        <span>{{ detail.publishTime }}</span>
        <span style="margin-left: 10px">作者：{{ detail.author || '平台' }}</span>
        <span style="margin-left: 10px">阅读：{{ detail.viewCount || 0 }}</span>
      </div>

      <el-image v-if="detail.coverImage" :src="detail.coverImage" style="width: 100%; border-radius: 12px; margin-bottom: 16px" />

      <el-alert
        v-if="detail.locked"
        title="该内容需登录后查看完整详情"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 16px"
      />

      <div class="article" v-html="detail.content"></div>

      <div style="margin-top: 20px; display: flex; gap: 10px">
        <el-button type="success" plain @click="toggleLike" :disabled="!userStore.isLogin()">
          {{ detail.liked ? '取消点赞' : '点赞' }}（{{ detail.likeCount || 0 }}）
        </el-button>
        <el-button type="primary" plain @click="toggleFavorite" :disabled="!userStore.isLogin()">
          {{ detail.favorited ? '取消收藏' : '收藏' }}（{{ detail.favoriteCount || 0 }}）
        </el-button>
        <el-button @click="toList">返回列表</el-button>
        <el-button v-if="detail.locked" type="success" @click="toLogin">登录解锁</el-button>
      </div>

      <el-divider />

      <h3>评论（{{ comments.length }}）</h3>
      <div style="display: flex; gap: 10px; margin-bottom: 12px">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          :disabled="!userStore.isLogin()"
        />
      </div>
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="submitComment" :disabled="!userStore.isLogin()">发表评论</el-button>
      </div>

      <el-empty v-if="!comments.length" description="暂无评论" />
      <div v-else class="comment-list">
        <el-card v-for="item in comments" :key="item.id" shadow="never" style="margin-bottom: 10px">
          <div style="font-size: 13px; color: #6b7280; margin-bottom: 8px">
            {{ item.nickname || item.username || '用户' }} · {{ item.createTime }}
          </div>
          <div>{{ item.content }}</div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  addComment,
  favoriteNews,
  getComments,
  getNewsDetail,
  likeNews,
  unlikeNews,
  unfavoriteNews
} from '@/api/news'
import { normalizeHtmlFileUrls, toFileUrl } from '@/utils/file-url'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref(null)
const comments = ref([])
const commentContent = ref('')

const load = async () => {
  loading.value = true
  try {
    const res = await getNewsDetail(route.params.id)
    detail.value = {
      ...res.data,
      coverImage: toFileUrl(res.data.coverImage),
      content: normalizeHtmlFileUrls(res.data.content)
    }
    const commentRes = await getComments(route.params.id)
    comments.value = commentRes.data || []
  } finally {
    loading.value = false
  }
}

const toggleLike = async () => {
  if (!userStore.isLogin()) {
    ElMessage.warning('请先登录')
    return
  }
  if (detail.value.liked) {
    await unlikeNews(detail.value.id)
    detail.value.liked = false
    detail.value.likeCount = Math.max((detail.value.likeCount || 1) - 1, 0)
  } else {
    await likeNews(detail.value.id)
    detail.value.liked = true
    detail.value.likeCount = (detail.value.likeCount || 0) + 1
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLogin()) {
    ElMessage.warning('请先登录')
    return
  }
  if (detail.value.favorited) {
    await unfavoriteNews(detail.value.id)
    detail.value.favorited = false
    detail.value.favoriteCount = Math.max((detail.value.favoriteCount || 1) - 1, 0)
  } else {
    await favoriteNews(detail.value.id)
    detail.value.favorited = true
    detail.value.favoriteCount = (detail.value.favoriteCount || 0) + 1
  }
}

const submitComment = async () => {
  if (!userStore.isLogin()) {
    ElMessage.warning('请先登录')
    return
  }
  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }
  await addComment(detail.value.id, { content: commentContent.value.trim() })
  commentContent.value = ''
  const commentRes = await getComments(detail.value.id)
  comments.value = commentRes.data || []
}

const toList = () => router.push('/list')
const toLogin = () => router.push('/login')

onMounted(load)
</script>

<style scoped>
.article {
  line-height: 1.9;
  color: #374151;
}

:deep(.article img) {
  max-width: 100%;
}
</style>
