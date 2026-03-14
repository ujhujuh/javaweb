<template>
  <div class="operlog-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-button type="danger" @click="handleClean">清空</el-button>
        </div>
      </template>
      <el-form :model="queryForm" inline>
        <el-form-item label="模块标题">
          <el-input v-model="queryForm.title" placeholder="请输入模块标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="queryForm.operName" placeholder="请输入操作人员" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="正常" value="0" />
            <el-option label="异常" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="title" label="模块标题" min-width="150" />
        <el-table-column prop="businessType" label="业务类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.businessType === 0" type="info">其它</el-tag>
            <el-tag v-else-if="row.businessType === 1" type="success">新增</el-tag>
            <el-tag v-else-if="row.businessType === 2" type="warning">修改</el-tag>
            <el-tag v-else-if="row.businessType === 3" type="danger">删除</el-tag>
            <el-tag v-else-if="row.businessType === 4" type="primary">授权</el-tag>
            <el-tag v-else-if="row.businessType === 5">导出</el-tag>
            <el-tag v-else-if="row.businessType === 6">导入</el-tag>
            <el-tag v-else-if="row.businessType === 7">强退</el-tag>
            <el-tag v-else-if="row.businessType === 8">生成代码</el-tag>
            <el-tag v-else-if="row.businessType === 9">清空数据</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="method" label="方法名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="requestMethod" label="请求方式" width="100" />
        <el-table-column prop="operName" label="操作人员" width="120" />
        <el-table-column prop="operUrl" label="请求URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operIp" label="主机地址" width="130" />
        <el-table-column prop="operLocation" label="操作地点" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operTime" label="操作时间" width="180" />
        <el-table-column prop="costTime" label="消耗时间" width="100">
          <template #default="{ row }">
            {{ row.costTime }}ms
          </template>
        </el-table-column>
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
    <el-dialog v-model="viewVisible" title="操作日志详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模块标题">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">
          <el-tag v-if="viewData.businessType === 0" type="info">其它</el-tag>
          <el-tag v-else-if="viewData.businessType === 1" type="success">新增</el-tag>
          <el-tag v-else-if="viewData.businessType === 2" type="warning">修改</el-tag>
          <el-tag v-else-if="viewData.businessType === 3" type="danger">删除</el-tag>
          <el-tag v-else-if="viewData.businessType === 4" type="primary">授权</el-tag>
          <el-tag v-else-if="viewData.businessType === 5">导出</el-tag>
          <el-tag v-else-if="viewData.businessType === 6">导入</el-tag>
          <el-tag v-else-if="viewData.businessType === 7">强退</el-tag>
          <el-tag v-else-if="viewData.businessType === 8">生成代码</el-tag>
          <el-tag v-else-if="viewData.businessType === 9">清空数据</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="方法名称">{{ viewData.method }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ viewData.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ viewData.operUrl }}</el-descriptions-item>
        <el-descriptions-item label="操作人员">{{ viewData.operName }}</el-descriptions-item>
        <el-descriptions-item label="主机地址">{{ viewData.operIp }}</el-descriptions-item>
        <el-descriptions-item label="操作地点">{{ viewData.operLocation }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre style="max-height: 200px; overflow: auto">{{ viewData.operParam }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回参数">
          <pre style="max-height: 200px; overflow: auto">{{ viewData.jsonResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 0 ? 'success' : 'danger'">
            {{ viewData.status === 0 ? '正常' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="错误消息" v-if="viewData.errorMsg">
          <pre style="max-height: 200px; overflow: auto; color: red">{{ viewData.errorMsg }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ viewData.operTime }}</el-descriptions-item>
        <el-descriptions-item label="消耗时间">{{ viewData.costTime }}ms</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { operLogApi } from '@/api/system'

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