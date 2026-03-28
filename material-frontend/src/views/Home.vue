<template>
  <div class="page-wrap">
    <div class="shop-banner" style="margin-bottom: 16px;">
      <div class="spread">
        <h2 style="margin: 0;">资料线上售卖平台</h2>
        <div class="flex-row">
          <el-button type="primary" plain @click="go('/list')">逛商城</el-button>
          <el-button v-if="!userStore.isLogin()" plain @click="go('/login')">登录</el-button>
          <el-button v-else plain @click="go('/profile')">个人中心</el-button>
        </div>
      </div>
      <p style="margin: 12px 0 0; opacity: 0.95;">精品资料聚合，支持筛选、下单、支付和自动发货。</p>
    </div>

    <div class="panel" style="margin-bottom: 16px;">
      <div class="spread" style="margin-bottom: 10px;">
        <h3 style="margin: 0;">分类导航</h3>
        <el-button link type="primary" @click="loadData">刷新</el-button>
      </div>
      <div class="flex-row" style="flex-wrap: wrap;">
        <el-tag v-for="c in categories" :key="c.id" @click="go('/list?categoryId=' + c.id)" style="cursor: pointer;">
          {{ c.name }} ({{ c.count }})
        </el-tag>
      </div>
    </div>

    <div class="panel">
      <div class="spread" style="margin-bottom: 12px;">
        <h3 style="margin: 0;">最新上架</h3>
        <el-button link type="primary" @click="go('/list')">查看全部</el-button>
      </div>
      <div class="goods-grid">
        <div v-for="item in latest" :key="item.id" class="goods-card" @click="go('/detail/' + item.id)">
          <img class="goods-image" :src="item.coverImage || defaultCover" alt="cover" />
          <div class="goods-body">
            <div class="goods-title">{{ item.title }}</div>
            <div class="goods-summary">{{ item.summary || '暂无简介' }}</div>
            <div class="goods-meta">
              <span class="price">¥{{ item.price }}</span>
              <span class="muted">销量 {{ item.salesCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getMaterialList } from '@/api/material'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const categories = ref([])
const latest = ref([])
const defaultCover = 'https://picsum.photos/seed/material-cover/600/400'

const go = (path) => router.push(path)

const loadData = async () => {
  const [categoryRes, listRes] = await Promise.all([
    getCategories(),
    getMaterialList({ current: 1, size: 10, sort: 'latest' })
  ])
  categories.value = categoryRes.data || []
  latest.value = listRes.data?.records || []
}

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
  await loadData()
})
</script>
