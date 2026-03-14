<template>
  <div class="loginlog-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>登录日志</span>
          <el-button type="danger" @click="handleClean">清空</el-button>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item label="登录地址">
          <el-input v-model="queryForm.ipaddr" placeholder="请输入登录地址" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="用户名称">
          <el-input v-model="queryForm.username" placeholder="请输入用户名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="username" label="用户名称" width="120" />
        <el-table-column prop="ipaddr" label="登录地址" width="130" />
        <el-table-column prop="loginLocation" label="登录地点" width="150" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="msg" label="提示消息" min-width="150" show-overflow-tooltip />
        <el-table-column prop="loginTime" label="访问时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="viewVisible" title="登录日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名称">{{ viewData.username }}</el-descriptions-item>
        <el-descriptions-item label="登录状态">
          <el-tag :type="viewData.status === '0' ? 'success' : 'danger'">
            {{ viewData.status === '0' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="登录地址">{{ viewData.ipaddr }}</el-descriptions-item>
        <el-descriptions-item label="登录地点">{{ viewData.loginLocation }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{ viewData.browser }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{ viewData.os }}</el-descriptions-item>
        <el-descriptions-item label="提示消息" :span="2">{{ viewData.msg }}</el-descriptions-item>
        <el-descriptions-item label="访问时间" :span="2">{{ viewData.loginTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { loginLogApi } from '@/api/system'

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