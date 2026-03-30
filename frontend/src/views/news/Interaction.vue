<template>
  <div class="news-interaction-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('news.interactionManagement') }}</span>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('common.query')" :label-width="isEnglish ? '60px' : 'auto'">
          <el-input v-model="queryForm.keyword" :placeholder="$t('news.newsTitle')" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" :label="$t('news.newsTitle')" min-width="260" show-overflow-tooltip />
        <el-table-column prop="viewCount" :label="$t('news.viewCount')" width="100" />
        <el-table-column prop="likeCount" :label="$t('news.likeCount')" width="100" />
        <el-table-column prop="favoriteCount" :label="$t('news.favoriteCount')" width="100" />
        <el-table-column prop="commentCount" :label="$t('news.commentCount')" width="100" />
        <el-table-column prop="publishTime" :label="$t('news.publishTime')" width="180" />
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
