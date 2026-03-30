<template>
  <div class="operlog-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.operLog') }}</span>
          <el-button type="danger" @click="handleClean">{{ $t('log.clean') }}</el-button>
        </div>
      </template>
<el-form :model="queryForm" inline>
        <el-form-item :label="$t('log.title')" :label-width="isEnglish ? '140px' : 'auto'">
          <el-input v-model="queryForm.title" :placeholder="$t('log.title')" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item :label="$t('log.operName')" :label-width="isEnglish ? '120px' : 'auto'">
          <el-input v-model="queryForm.operName" :placeholder="$t('log.operName')" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item :label="$t('log.status')" :label-width="isEnglish ? '80px' : 'auto'">
          <el-select v-model="queryForm.status" :placeholder="$t('common.query')" clearable style="width: 150px">
            <el-option :label="$t('log.statusSuccess')" value="0" />
            <el-option :label="$t('log.statusFail')" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" :label="$t('log.title')" min-width="150" />
        <el-table-column prop="businessType" :label="$t('log.businessType')" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.businessType === 0" type="info">{{ $t('log.other') }}</el-tag>
            <el-tag v-else-if="row.businessType === 1" type="success">{{ $t('log.insert') }}</el-tag>
            <el-tag v-else-if="row.businessType === 2" type="warning">{{ $t('log.update') }}</el-tag>
            <el-tag v-else-if="row.businessType === 3" type="danger">{{ $t('log.delete') }}</el-tag>
            <el-tag v-else-if="row.businessType === 4" type="primary">{{ $t('log.grant') }}</el-tag>
            <el-tag v-else-if="row.businessType === 5">{{ $t('log.export') }}</el-tag>
            <el-tag v-else-if="row.businessType === 6">{{ $t('log.import') }}</el-tag>
            <el-tag v-else-if="row.businessType === 7">{{ $t('log.forceLogout') }}</el-tag>
            <el-tag v-else-if="row.businessType === 8">{{ $t('log.genCode') }}</el-tag>
            <el-tag v-else-if="row.businessType === 9">{{ $t('log.clean') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="method" :label="$t('log.method')" min-width="200" show-overflow-tooltip />
        <el-table-column prop="requestMethod" :label="$t('log.requestMethod')" width="100" />
        <el-table-column prop="operName" :label="$t('log.operName')" width="120" />
        <el-table-column prop="operUrl" :label="$t('log.requestUrl')" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operIp" :label="$t('log.operIp')" width="130" />
        <el-table-column prop="operLocation" :label="$t('log.operLocation')" width="150" />
        <el-table-column prop="status" :label="$t('log.status')" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? $t('log.statusSuccess') : $t('log.statusFail') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" :label="$t('log.operTime')" width="180" />
        <el-table-column prop="costTime" :label="$t('log.costTime')" width="100">
          <template #default="{ row }">
            {{ row.costTime }}ms
          </template>
        </el-table-column>
        <el-table-column :label="$t('common.operation')" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">{{ $t('common.view') }}</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">{{ $t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewVisible" :title="$t('log.detail')" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item :label="$t('log.title')">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.businessType')">
          <el-tag v-if="viewData.businessType === 0" type="info">{{ $t('log.other') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 1" type="success">{{ $t('log.insert') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 2" type="warning">{{ $t('log.update') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 3" type="danger">{{ $t('log.delete') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 4" type="primary">{{ $t('log.grant') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 5">{{ $t('log.export') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 6">{{ $t('log.import') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 7">{{ $t('log.forceLogout') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 8">{{ $t('log.genCode') }}</el-tag>
          <el-tag v-else-if="viewData.businessType === 9">{{ $t('log.clean') }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('log.method')">{{ viewData.method }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.requestMethod')">{{ viewData.requestMethod }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.requestUrl')">{{ viewData.operUrl }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.operName')">{{ viewData.operName }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.operIp')">{{ viewData.operIp }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.operLocation')">{{ viewData.operLocation }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.requestParam')">
          <pre style="max-height: 200px; overflow: auto">{{ viewData.operParam }}</pre>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('log.responseParam')">
          <pre style="max-height: 200px; overflow: auto">{{ viewData.jsonResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('log.status')">
          <el-tag :type="viewData.status === 0 ? 'success' : 'danger'">
            {{ viewData.status === 0 ? $t('log.statusSuccess') : $t('log.statusFail') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('log.errorMsg')" v-if="viewData.errorMsg">
          <pre style="max-height: 200px; overflow: auto; color: red">{{ viewData.errorMsg }}</pre>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('log.operTime')">{{ viewData.operTime }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.costTime')">{{ viewData.costTime }}ms</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { operLogApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

const queryForm = reactive({
  title: '',
  operName: '',
  status: '',
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const viewVisible = ref(false)
const viewData = ref({})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await operLogApi.list(queryForm)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.title = ''
  queryForm.operName = ''
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const handleView = (row) => {
  viewData.value = row
  viewVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该操作日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await operLogApi.delete([row.id])
      if (res.code === 200) {
        ElMessage.success('删除成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleClean = () => {
  ElMessageBox.confirm('确认清空所有操作日志吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await operLogApi.clean()
      if (res.code === 200) {
        ElMessage.success('清空成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('清空失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.operlog-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>