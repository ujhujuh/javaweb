<template>
  <div class="sale-order-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>售卖订单管理</span>
        </div>
      </template>

      <el-form :model="queryForm" inline>
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单号" clearable style="width: 240px" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="queryForm.payType" clearable placeholder="全部方式" style="width: 140px">
            <el-option label="微信" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" min-width="220" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="materialTitle" label="资料" min-width="220" show-overflow-tooltip />
        <el-table-column prop="payAmount" label="金额" width="100" />
        <el-table-column prop="payType" label="支付方式" width="110" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="payTime" label="支付时间" width="180" />
        <el-table-column prop="deliverTime" label="发货时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PAID'" size="small" type="success" @click="handleResend(row)">补发货</el-button>
            <el-button
              v-if="row.status !== 'COMPLETED' && row.status !== 'CLOSED'"
              size="small"
              type="danger"
              @click="handleClose(row)"
            >关闭订单</el-button>
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
import { saleManageApi } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  orderNo: '',
  username: '',
  status: '',
  payType: '',
  current: 1,
  size: 10
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await saleManageApi.listOrders(queryForm)
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
  queryForm.orderNo = ''
  queryForm.username = ''
  queryForm.status = ''
  queryForm.payType = ''
  queryForm.current = 1
  handleQuery()
}

const statusText = (status) => {
  if (status === 'PENDING') return '待支付'
  if (status === 'PAID') return '已支付'
  if (status === 'COMPLETED') return '已完成'
  if (status === 'CLOSED') return '已关闭'
  return status || '-'
}

const statusTagType = (status) => {
  if (status === 'PENDING') return 'info'
  if (status === 'PAID') return 'warning'
  if (status === 'COMPLETED') return 'success'
  if (status === 'CLOSED') return 'danger'
  return ''
}

const handleResend = async (row) => {
  await saleManageApi.resendOrder(row.orderNo)
  ElMessage.success('补发货成功')
  await loadList()
}

const handleClose = async (row) => {
  await ElMessageBox.confirm(`确认关闭订单「${row.orderNo}」吗？`, '提示', { type: 'warning' })
  await saleManageApi.closeOrder(row.orderNo)
  ElMessage.success('订单已关闭')
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
