<template>
  <div class="news-interaction-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>互动管理</span>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="标题/摘要" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" label="资讯标题" min-width="260" show-overflow-tooltip />
        <el-table-column prop="viewCount" label="阅读" width="100" />
        <el-table-column prop="likeCount" label="点赞" width="100" />
        <el-table-column prop="favoriteCount" label="收藏" width="100" />
        <el-table-column prop="commentCount" label="评论" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { newsManageApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  keyword: '',
  current: 1,
  size: 10,
  status: '1'
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await newsManageApi.list(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  loadList()
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.current = 1
  handleQuery()
}

onMounted(loadList)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
