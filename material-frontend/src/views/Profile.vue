<template>
  <div class="page-wrap">
    <div class="panel" style="margin-bottom: 16px;">
      <div class="spread">
        <div>
          <h3 style="margin: 0;">个人中心</h3>
          <p class="muted" style="margin: 8px 0 0;">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</p>
        </div>
        <div class="flex-row">
          <el-button @click="go('/')">首页</el-button>
          <el-button @click="go('/list')">资料列表</el-button>
          <el-button type="danger" plain @click="logoutAction">退出登录</el-button>
        </div>
      </div>
    </div>

    <div class="panel" style="margin-bottom: 16px;">
      <el-tabs v-model="activeTab" @tab-change="loadData">
        <el-tab-pane label="我的订单" name="orders" />
        <el-tab-pane label="已购资源" name="purchased" />
      </el-tabs>

      <template v-if="activeTab === 'orders'">
        <div class="flex-row" style="margin-bottom: 12px;">
          <el-select v-model="orderStatus" placeholder="订单状态" clearable style="width: 180px;">
            <el-option label="待支付" value="PENDING" />
            <el-option label="已支付" value="PAID" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
          <el-button type="primary" @click="loadOrders">查询</el-button>
        </div>
        <el-table :data="orders" stripe>
          <el-table-column prop="orderNo" label="订单号" min-width="230" />
          <el-table-column prop="materialTitle" label="资料" min-width="220" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="payType" label="支付方式" width="120" />
          <el-table-column prop="payAmount" label="金额" width="100" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button v-if="scope.row.status === 'PENDING'" link type="primary" @click="pay(scope.row, 'WECHAT')">微信支付</el-button>
              <el-button v-if="scope.row.status === 'PENDING'" link type="primary" @click="pay(scope.row, 'ALIPAY')">支付宝</el-button>
              <el-button v-if="scope.row.status === 'COMPLETED'" link type="success" @click="go('/detail/' + scope.row.materialId)">查看资料</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>

      <template v-else>
        <el-table :data="purchased" stripe>
          <el-table-column prop="orderNo" label="订单号" min-width="230" />
          <el-table-column prop="materialTitle" label="资料" min-width="240" />
          <el-table-column prop="purchaseTime" label="购买时间" width="180" />
          <el-table-column prop="downloadCount" label="已下载" width="90" />
          <el-table-column prop="maxDownloadCount" label="可下载上限" width="100" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-link type="primary" :href="scope.row.fileUrl" target="_blank">下载资料</el-link>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getMyOrders, getMyPurchased, payOrder } from '@/api/material'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('orders')
const orderStatus = ref(undefined)
const orders = ref([])
const purchased = ref([])

const go = (path) => router.push(path)

const loadOrders = async () => {
  const res = await getMyOrders({ current: 1, size: 100, status: orderStatus.value })
  orders.value = res.data.records || []
}

const loadPurchased = async () => {
  const res = await getMyPurchased({ current: 1, size: 100 })
  purchased.value = res.data.records || []
}

const loadData = async () => {
  if (activeTab.value === 'orders') {
    await loadOrders()
  } else {
    await loadPurchased()
  }
}

const pay = async (row, payType) => {
  await payOrder(row.orderNo, { payType })
  ElMessage.success('支付成功，已自动发货')
  await loadOrders()
}

const logoutAction = async () => {
  await userStore.logoutAction()
  router.push('/login')
}

onMounted(async () => {
  await userStore.fetchUserInfo()
  await loadData()
})
</script>
