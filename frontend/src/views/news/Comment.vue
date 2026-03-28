<template>
  <div class="news-comment-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="评论内容" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="newsTitle" label="资讯标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="nickname" label="用户" width="120" />
        <el-table-column prop="content" label="评论内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="createTime" label="评论时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { newsManageApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  keyword: '',
  current: 1,
  size: 10
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await newsManageApi.comments(queryForm)
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

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该评论吗？', '提示', { type: 'warning' })
  await newsManageApi.deleteComment(row.id)
  ElMessage.success('删除成功')
  await loadList()
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
