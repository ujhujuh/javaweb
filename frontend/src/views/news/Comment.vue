<template>
  <div class="news-comment-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('news.commentManagement') }}</span>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('common.query')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-input v-model="queryForm.keyword" :placeholder="$t('news.commentContent')" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="newsTitle" :label="$t('news.newsTitle')" min-width="220" show-overflow-tooltip />
        <el-table-column prop="nickname" :label="$t('news.commentUser')" width="120" />
        <el-table-column prop="content" :label="$t('news.commentContent')" min-width="260" show-overflow-tooltip />
        <el-table-column prop="createTime" :label="$t('news.commentTime')" width="180" />
        <el-table-column :label="$t('common.operation')" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
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
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { newsManageApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

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
