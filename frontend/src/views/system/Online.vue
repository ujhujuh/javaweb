<template>
  <div class="online-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>在线用户</span>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item label="登录账号">
          <el-input v-model="queryForm.loginName" placeholder="请输入登录账号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="登录地址">
          <el-input v-model="queryForm.ipaddr" placeholder="请输入登录地址" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="登录地点">
          <el-input v-model="queryForm.loginLocation" placeholder="请输入登录地点" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="sessionId" label="会话编号" width="200" show-overflow-tooltip />
        <el-table-column prop="loginName" label="登录账号" width="120" />
        <el-table-column prop="ipaddr" label="登录地址" width="130" />
        <el-table-column prop="loginLocation" label="登录地点" width="150" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="120" />
        <el-table-column prop="status" label="在线状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'on_line' ? 'success' : 'info'">
              {{ row.status === 'on_line' ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTimestamp" label="创建时间" width="180" />
        <el-table-column prop="lastAccessTime" label="最后访问时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleForceLogout(row)" :disabled="row.status !== 'on_line'">
              强退
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onlineApi } from '@/api/system'

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
    ElMessage.error('查询失败')
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
  ElMessageBox.confirm(`确认强制退出用户"${row.loginName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await onlineApi.forceLogout(row.sessionId)
      if (res.code === 200) {
        ElMessage.success('强退成功')
        handleQuery()
      }
    } catch (error) {
      ElMessage.error('强退失败')
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