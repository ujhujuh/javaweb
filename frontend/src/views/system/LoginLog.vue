<template>
  <div class="loginlog-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.loginLog') }}</span>
          <el-button type="danger" @click="handleClean">{{ $t('log.clean') }}</el-button>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('log.loginIp')" :label-width="isEnglish ? '120px' : 'auto'">
          <el-input v-model="queryForm.ipaddr" :placeholder="$t('log.loginIp')" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item :label="$t('log.operName')" :label-width="isEnglish ? '120px' : 'auto'">
          <el-input v-model="queryForm.username" :placeholder="$t('log.operName')" clearable style="width: 150px" />
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
        <el-table-column prop="username" :label="$t('log.operName')" width="120" />
        <el-table-column prop="ipaddr" :label="$t('log.loginIp')" width="130" />
        <el-table-column prop="loginLocation" :label="$t('log.loginLocation')" width="150" />
        <el-table-column prop="browser" :label="$t('log.browserType')" width="120" />
        <el-table-column prop="os" :label="$t('log.osType')" width="120" />
        <el-table-column prop="status" :label="$t('log.status')" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? $t('log.statusSuccess') : $t('log.statusFail') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="msg" :label="$t('log.msg')" min-width="150" show-overflow-tooltip />
        <el-table-column prop="loginTime" :label="$t('log.loginTime')" width="180" />
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
    <el-dialog v-model="viewVisible" :title="$t('log.loginLogDetail')" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item :label="$t('log.username')">{{ viewData.username }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.loginStatus')">
          <el-tag :type="viewData.status === '0' ? 'success' : 'danger'">
            {{ viewData.status === '0' ? $t('log.statusSuccess') : $t('log.statusFail') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('log.loginAddress')">{{ viewData.ipaddr }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.loginLocation')">{{ viewData.loginLocation }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.browser')">{{ viewData.browser }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.os')">{{ viewData.os }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.msg')" :span="2">{{ viewData.msg }}</el-descriptions-item>
        <el-descriptions-item :label="$t('log.accessTime')" :span="2">{{ viewData.loginTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loginLogApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

const queryForm = reactive({
  ipaddr: '',
  username: '',
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
    const res = await loginLogApi.list(queryForm)
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
  queryForm.ipaddr = ''
  queryForm.username = ''
  queryForm.status = ''
  queryForm.current = 1
  handleQuery()
}

const handleView = (row) => {
  viewData.value = row
  viewVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该登录日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await loginLogApi.delete([row.id])
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
  ElMessageBox.confirm('确认清空所有登录日志吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await loginLogApi.clean()
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
.loginlog-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>