<template>
  <div class="page-wrap">
    <div class="shop-banner" style="margin-bottom: 16px;">
      <div class="spread" style="margin-bottom: 12px;">
        <h3 style="margin: 0;">商城资料</h3>
        <div class="flex-row">
          <el-button plain @click="go('/')">首页</el-button>
          <el-button v-if="userStore.isLogin()" type="primary" plain @click="go('/profile')">个人中心</el-button>
          <el-button v-else type="primary" plain @click="go('/login')">登录购买</el-button>
        </div>
      </div>
      <div class="shop-filter">
        <el-input v-model="query.keyword" placeholder="搜索标题/简介/标签" style="width: 260px;" clearable @keyup.enter="search" />
        <el-select v-model="query.categoryId" placeholder="分类" clearable style="width: 180px;" @change="search">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="query.sort" placeholder="排序" style="width: 180px;" @change="search">
          <el-option label="最新上架" value="latest" />
          <el-option label="销量优先" value="sales" />
          <el-option label="价格从低到高" value="priceAsc" />
          <el-option label="价格从高到低" value="priceDesc" />
        </el-select>
        <el-button type="primary" @click="search">查询</el-button>
      </div>
    </div>

    <div class="panel">
      <div class="goods-grid">
        <div v-for="item in records" :key="item.id" class="goods-card" @click="go('/detail/' + item.id)">
          <img class="goods-image" :src="item.coverImage || defaultCover" alt="cover" />
          <div class="goods-body">
            <div class="goods-title">{{ item.title }}</div>
            <div class="goods-summary">{{ item.summary || '暂无简介' }}</div>
            <div class="goods-meta">
              <span class="price">¥{{ item.price }}</span>
              <span class="muted">销量 {{ item.salesCount }}</span>
            </div>
            <div class="muted" style="margin-top: 6px; font-size: 12px;">{{ item.category }} · {{ item.publishTime }}</div>
          </div>
        </div>
      </div>
      <el-empty v-if="records.length === 0" description="暂无商品" style="padding: 24px 0;" />
      <div class="spread" style="margin-top: 14px;">
        <span class="muted">共 {{ total }} 条</span>
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :current-page="query.current"
          :page-size="query.size"
          @current-change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getMaterialList } from '@/api/material'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const categories = ref([])
const records = ref([])
const total = ref(0)
const defaultCover = 'https://picsum.photos/seed/material-cover/600/400'
const query = ref({ current: 1, size: 10, keyword: '', categoryId: undefined, sort: 'latest' })

const go = (path) => router.push(path)

const fetchList = async () => {
  const res = await getMaterialList(query.value)
  records.value = res.data.records || []
  total.value = res.data.total || 0
}

const search = async () => {
  query.value.current = 1
  await fetchList()
}

const onPageChange = async (page) => {
  query.value.current = page
  await fetchList()
}

onMounted(async () => {
  const cid = route.query.categoryId
  if (cid) query.value.categoryId = Number(cid)
  const categoryRes = await getCategories()
  categories.value = categoryRes.data || []
  await fetchList()
})
</script>
