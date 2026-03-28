<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="关键词" style="max-width: 260px" clearable />
        <el-select v-model="query.categoryId" placeholder="分类" clearable style="max-width: 180px">
          <el-option v-for="item in categories" :key="item.id" :label="`${item.name} (${item.count})`" :value="item.id" />
        </el-select>
        <el-select v-model="query.sort" style="max-width: 140px">
          <el-option label="最新" value="latest" />
          <el-option label="最热" value="hot" />
          <el-option label="置顶" value="top" />
        </el-select>
        <el-button type="primary" @click="load(1)">搜索</el-button>
      </div>
    </el-card>

    <div class="card-grid" style="margin-top: 16px">
      <NewsCardItem v-for="item in list" :key="item.id" :item="item" />
    </div>

    <el-empty v-if="!list.length" description="暂无数据" style="margin-top: 16px" />

    <div style="display: flex; justify-content: center; margin-top: 20px">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :current-page="query.current"
        :page-size="query.size"
        @current-change="load"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories, getNewsList } from '@/api/news'
import NewsCardItem from '@/components/NewsCardItem.vue'

const route = useRoute()
const router = useRouter()

const categories = ref([])
const list = ref([])
const total = ref(0)

const query = reactive({
  current: 1,
  size: 10,
  keyword: '',
  categoryId: undefined,
  sort: 'latest'
})

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

const syncRoute = () => {
  query.keyword = route.query.keyword || ''
  query.sort = route.query.sort || 'latest'
  query.categoryId = route.query.categoryId ? Number(route.query.categoryId) : undefined
  query.current = route.query.current ? Number(route.query.current) : 1
}

const pushRoute = () => {
  router.replace({
    path: '/list',
    query: {
      keyword: query.keyword || undefined,
      sort: query.sort || undefined,
      categoryId: query.categoryId || undefined,
      current: query.current
    }
  })
}

const load = async (current = query.current) => {
  query.current = current
  pushRoute()
  const res = await getNewsList({ ...query })
  list.value = res.data.records || []
  total.value = res.data.total || 0
}

watch(() => route.fullPath, syncRoute)

onMounted(async () => {
  syncRoute()
  await loadCategories()
  await load(query.current)
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
