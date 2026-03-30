<template>
  <div class="online-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('system.onlineUsers') }}</span>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item :label="$t('online.loginAccount')" :label-width="isEnglish ? '130px' : 'auto'">
          <el-input v-model="queryForm.loginName" :placeholder="$t('online.loginAccount')" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item :label="$t('online.loginAddress')" :label-width="isEnglish ? '130px' : 'auto'">
          <el-input v-model="queryForm.ipaddr" :placeholder="$t('online.loginAddress')" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item :label="$t('online.loginLocation')" :label-width="isEnglish ? '130px' : 'auto'">
          <el-input v-model="queryForm.loginLocation" :placeholder="$t('online.loginLocation')" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
          <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="sessionId" :label="$t('online.sessionId')" width="200" show-overflow-tooltip />
        <el-table-column prop="loginName" :label="$t('online.loginAccount')" width="120" />
        <el-table-column prop="ipaddr" :label="$t('online.loginAddress')" width="130" />
        <el-table-column prop="loginLocation" :label="$t('online.loginLocation')" width="150" />
        <el-table-column prop="browser" :label="$t('online.browser')" width="120" />
        <el-table-column prop="os" :label="$t('online.os')" width="120" />
        <el-table-column prop="status" :label="$t('online.onlineStatus')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'on_line' ? 'success' : 'info'">
              {{ row.status === 'on_line' ? $t('online.online') : $t('online.offline') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTimestamp" :label="$t('online.createTime')" width="180" />
        <el-table-column prop="lastAccessTime" :label="$t('online.lastAccessTime')" width="180" />
        <el-table-column :label="$t('common.operation')" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleForceLogout(row)" :disabled="row.status !== 'on_line'">
              {{ $t('online.forceLogout') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { onlineApi } from '@/api/system'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

const queryForm = reactive({
  loginName: '',
  ipaddr: '',
  loginLocation: ''
})

const tableData = ref([])
const loading = ref(false)

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await onlineApi.list(queryForm)
    if (res.code === 200) {
      tableData.value = res.data
    }
  } catch (error) {
    ElMessage.error(t('common.queryFailed'))
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.loginName = ''
  queryForm.ipaddr = ''
  queryForm.loginLocation = ''
  handleQuery()
}

const handleForceLogout = (row) => {
  ElMessageBox.confirm(
    `${t('online.confirmForceLogout')} "${row.loginName}"?`,
    t('common.tip'),
    {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await onlineApi.forceLogout(row.sessionId)
      if (res.code === 200) {
        ElMessage.success(t('online.forceLogoutSuccess'))
        handleQuery()
      }
    } catch (error) {
      ElMessage.error(t('online.forceLogoutFailed'))
    }
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.online-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>