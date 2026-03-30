<template>
  <div class="sale-order-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('sale.saleOrderManagement') }}</span>
        </div>
      </template>

      <el-form :model="queryForm">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item :label="$t('sale.orderNo')" :label-width="isEnglish ? '100px' : 'auto'">
              <el-input v-model="queryForm.orderNo" :placeholder="$t('sale.orderNo')" clearable style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item :label="$t('sale.username')" :label-width="isEnglish ? '90px' : 'auto'">
              <el-input v-model="queryForm.username" :placeholder="$t('sale.username')" clearable style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item :label="$t('sale.orderStatus')" :label-width="isEnglish ? '110px' : 'auto'">
              <el-select v-model="queryForm.status" clearable :placeholder="$t('common.query')" style="width: 100%">
                <el-option :label="$t('sale.pendingPayment')" value="PENDING" />
                <el-option :label="$t('sale.paid')" value="PAID" />
                <el-option :label="$t('sale.completed')" value="COMPLETED" />
                <el-option :label="$t('sale.cancelled')" value="CLOSED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item :label="$t('sale.paymentMethod')" :label-width="isEnglish ? '120px' : 'auto'">
              <el-select v-model="queryForm.payType" clearable :placeholder="$t('common.query')" style="width: 100%">
                <el-option :label="$t('sale.wechat')" value="WECHAT" />
                <el-option :label="$t('sale.alipay')" value="ALIPAY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="margin-bottom: 16px">
          <el-col :span="24" style="text-align: right">
            <el-button type="primary" @click="handleQuery">{{ $t('common.query') }}</el-button>
            <el-button @click="handleReset">{{ $t('common.reset') }}</el-button>
          </el-col>
        </el-row>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="orderNo" :label="$t('sale.orderNo')" min-width="220" />
        <el-table-column prop="username" :label="$t('sale.username')" width="120" />
        <el-table-column prop="materialTitle" :label="$t('sale.materialTitle')" min-width="220" show-overflow-tooltip />
        <el-table-column prop="payAmount" :label="$t('sale.orderAmount')" width="100" />
        <el-table-column prop="payType" :label="$t('sale.paymentMethod')" width="110" />
        <el-table-column prop="status" :label="$t('sale.orderStatus')" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('user.createTime')" width="180" />
        <el-table-column prop="payTime" :label="$t('sale.payTime')" width="180" />
        <el-table-column prop="deliverTime" :label="$t('sale.deliverTime')" width="180" />
        <el-table-column :label="$t('common.operation')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PAID'" size="small" type="success" @click="handleResend(row)">{{ $t('sale.viewMaterial') }}</el-button>
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
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saleManageApi } from '@/api/system'
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()
const isEnglish = computed(() => locale.value === 'en-US')

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
