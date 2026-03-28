<template>
  <div class="page-wrap" v-loading="loading">
    <div class="panel" style="margin-bottom: 16px;">
      <div class="spread">
        <el-button @click="go('/list')">返回列表</el-button>
        <div class="flex-row">
          <el-button v-if="userStore.isLogin()" @click="go('/profile')">个人中心</el-button>
          <el-button v-else type="primary" @click="go('/login')">登录</el-button>
        </div>
      </div>
    </div>

    <div v-if="detail" class="panel">
      <h2 style="margin-top: 0;">{{ detail.title }}</h2>
      <div class="muted" style="margin-bottom: 12px;">分类：{{ detail.category }} | 发布时间：{{ detail.publishTime }}</div>
      <el-image v-if="detail.coverImage" :src="detail.coverImage" fit="cover" style="width: 100%; max-height: 320px; border-radius: 10px; margin-bottom: 12px;" />
      <p>{{ detail.summary }}</p>
      <div v-html="detail.detailContent" style="line-height: 1.8;"></div>
      <div class="flex-row" style="margin: 14px 0;">
        <el-tag type="info">格式：{{ detail.fileFormat || '-' }}</el-tag>
        <el-tag type="info">大小：{{ detail.fileSize || '-' }}</el-tag>
        <el-tag type="success">销量：{{ detail.salesCount }}</el-tag>
      </div>
      <div class="spread" style="margin-top: 16px;">
        <div>
          <span class="muted">价格：</span>
          <span class="price" style="font-size: 24px;">¥{{ detail.price }}</span>
        </div>
        <div class="flex-row">
          <el-button v-if="detail.purchased" type="success" @click="go('/profile')">已购买，去下载</el-button>
          <el-button v-else type="primary" @click="createOrderAction">立即购买</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="payDialogVisible" title="选择支付方式" width="360px">
      <el-radio-group v-model="payType">
        <el-radio label="WECHAT">微信支付</el-radio>
        <el-radio label="ALIPAY">支付宝</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确认支付（模拟）</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { createOrder, getMaterialDetail, payOrder } from '@/api/material'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref(null)
const payDialogVisible = ref(false)
const payType = ref('WECHAT')
const currentOrderNo = ref('')

const go = (path) => router.push(path)

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getMaterialDetail(route.params.id)
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

const createOrderAction = async () => {
  if (!userStore.isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  const res = await createOrder({ materialId: detail.value.id })
  currentOrderNo.value = res.data.orderNo
  payDialogVisible.value = true
}

const confirmPay = async () => {
  await payOrder(currentOrderNo.value, { payType: payType.value })
  payDialogVisible.value = false
  ElMessage.success('支付成功，资料已自动发货')
  await fetchDetail()
  router.push('/profile')
}

onMounted(fetchDetail)
</script>
