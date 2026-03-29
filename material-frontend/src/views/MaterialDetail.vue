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

    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="收银台" width="420px" :close-on-click-modal="false" @close="handlePayDialogClose">
      <div v-if="payStep === 'select'" class="payment-select">
        <div class="payment-amount">
          <span class="amount-label">支付金额</span>
          <span class="amount-value">¥{{ detail?.price || '0.00' }}</span>
        </div>
        <div class="payment-methods">
          <div 
            class="payment-method-item"
            :class="{ active: payType === 'WECHAT' }"
            @click="selectPayType('WECHAT')"
          >
            <div class="payment-icon wechat-icon">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="#09BB07" d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178A1.17 1.17 0 014.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178 1.17 1.17 0 01-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 01.598.082l1.584.926a.272.272 0 00.14.047c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 01-.023-.156.49.49 0 01.201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-6.656-6.088V8.89c-.135-.01-.269-.027-.407-.03zm-2.53 3.274c.535 0 .969.44.969.982a.976.976 0 01-.969.983.976.976 0 01-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 01-.969.983.976.976 0 01-.969-.983c0-.542.434-.982.969-.982z"/>
              </svg>
            </div>
            <span class="payment-name">微信支付</span>
          </div>
          <div 
            class="payment-method-item"
            :class="{ active: payType === 'ALIPAY' }"
            @click="selectPayType('ALIPAY')"
          >
            <div class="payment-icon alipay-icon">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="#1678FF" d="M19.5 3h-15A1.5 1.5 0 003 4.5v15A1.5 1.5 0 004.5 21h15a1.5 1.5 0 001.5-1.5v-15A1.5 1.5 0 0019.5 3zM7.5 9h9v1.5h-9V9zm0 3h9v1.5h-9V12zm0 3h6v1.5h-6V15zm9 3h-9v-1.5h9V18z"/>
              </svg>
            </div>
            <span class="payment-name">支付宝</span>
          </div>
        </div>
      </div>

      <div v-else-if="payStep === 'processing'" class="payment-processing">
        <div v-if="payType === 'WECHAT'" class="wechat-payment">
          <div class="qrcode-container">
            <div ref="qrcodeRef" class="qrcode"></div>
            <div v-if="qrCodeLoading" class="qrcode-loading">
              <el-icon class="is-loading" :size="32"><Loading /></el-icon>
            </div>
          </div>
          <div class="payment-tip">
            <el-icon><Cellphone /></el-icon>
            <span>请使用微信扫描二维码完成支付</span>
          </div>
        </div>

        <div v-else-if="payType === 'ALIPAY'" class="alipay-payment">
          <div class="alipay-container">
            <div v-html="alipayFormHtml" v-if="alipayFormHtml"></div>
            <div v-else-if="alipayPayUrl" class="alipay-link">
              <p>即将跳转到支付宝完成支付...</p>
              <el-button type="primary" @click="openAlipayUrl" :loading="alipayLoading">
                立即跳转
              </el-button>
            </div>
          </div>
        </div>

        <div class="payment-status">
          <el-icon v-if="paymentStatus === 'pending'" class="is-loading" :size="20"><Loading /></el-icon>
          <el-icon v-else-if="paymentStatus === 'success'" color="#67C23A" :size="20"><CircleCheck /></el-icon>
          <el-icon v-else-if="paymentStatus === 'failed'" color="#F56C6C" :size="20"><CircleClose /></el-icon>
          <span class="status-text">{{ getStatusText() }}</span>
        </div>
      </div>

      <template #footer>
        <el-button v-if="payStep === 'select'" @click="handlePayDialogClose">取消</el-button>
        <el-button v-if="payStep === 'select'" type="primary" @click="initiatePayment" :loading="payLoading">
          确认支付
        </el-button>
        <el-button v-if="payStep === 'processing'" @click="handlePayDialogClose">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, Cellphone, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { createOrder, getMaterialDetail, payOrder, getMyOrderDetail } from '@/api/material'
import { useUserStore } from '@/store/user'
import QRCode from 'qrcode'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const detail = ref(null)
const payDialogVisible = ref(false)
const payStep = ref('select') // select, processing
const payType = ref('WECHAT')
const currentOrderNo = ref('')
const payLoading = ref(false)
const qrCodeLoading = ref(false)
const alipayLoading = ref(false)
const qrcodeRef = ref(null)
const alipayFormHtml = ref('')
const alipayPayUrl = ref('')
const paymentStatus = ref('pending') // pending, success, failed
const pollingTimer = ref(null)

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
  try {
    payLoading.value = true
    const res = await createOrder({ materialId: detail.value.id })
    currentOrderNo.value = res.data.orderNo
    payDialogVisible.value = true
  } catch (error) {
    ElMessage.error(error.message || '创建订单失败')
  } finally {
    payLoading.value = false
  }
}

const selectPayType = (type) => {
  payType.value = type
}

const initiatePayment = async () => {
  try {
    payLoading.value = true
    paymentStatus.value = 'pending'
    const res = await payOrder(currentOrderNo.value, { payType: payType.value })
    
    payStep.value = 'processing'
    
    if (payType.value === 'WECHAT') {
      // 微信支付 - 显示二维码
      if (res.data.codeUrl) {
        await generateQRCode(res.data.codeUrl)
        startPolling()
      } else {
        throw new Error('微信支付二维码获取失败')
      }
    } else if (payType.value === 'ALIPAY') {
      // 支付宝 - 显示表单或跳转
      if (res.data.formHtml) {
        alipayFormHtml.value = res.data.formHtml
        // 自动提交表单
        nextTick(() => {
          const form = document.querySelector('#alipay-form')
          if (form) {
            form.submit()
          }
        })
      } else if (res.data.payUrl) {
        alipayPayUrl.value = res.data.payUrl
        openAlipayUrl()
      } else {
        throw new Error('支付宝支付链接获取失败')
      }
    }
  } catch (error) {
    ElMessage.error(error.message || '发起支付失败')
    payStep.value = 'select'
  } finally {
    payLoading.value = false
  }
}

const generateQRCode = async (text) => {
  try {
    qrCodeLoading.value = true
    await nextTick()
    const canvas = await QRCode.toCanvas(text, {
      width: 200,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#ffffff'
      }
    })
    if (qrcodeRef.value) {
      qrcodeRef.value.innerHTML = ''
      qrcodeRef.value.appendChild(canvas)
    }
  } catch (error) {
    ElMessage.error('二维码生成失败')
  } finally {
    qrCodeLoading.value = false
  }
}

const openAlipayUrl = () => {
  if (alipayPayUrl.value) {
    alipayLoading.value = true
    window.open(alipayPayUrl.value, '_blank')
    startPolling()
  }
}

const startPolling = () => {
  stopPolling()
  pollingTimer.value = setInterval(async () => {
    try {
      const res = await getMyOrderDetail(currentOrderNo.value)
      const order = res.data
      
      if (order.status === 'PAID' || order.status === 'COMPLETED') {
        paymentStatus.value = 'success'
        stopPolling()
        ElMessage.success('支付成功，资料已自动发货')
        setTimeout(() => {
          handlePayDialogClose()
          fetchDetail()
          router.push('/profile')
        }, 1500)
      } else if (order.status === 'CLOSED') {
        paymentStatus.value = 'failed'
        stopPolling()
        ElMessage.error('订单已关闭')
      }
    } catch (error) {
      console.error('轮询支付状态失败:', error)
    }
  }, 2000)
}

const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value)
    pollingTimer.value = null
  }
}

const handlePayDialogClose = () => {
  stopPolling()
  payDialogVisible.value = false
  payStep.value = 'select'
  paymentStatus.value = 'pending'
  alipayFormHtml.value = ''
  alipayPayUrl.value = ''
  currentOrderNo.value = ''
}

const getStatusText = () => {
  switch (paymentStatus.value) {
    case 'pending':
      return '等待支付...'
    case 'success':
      return '支付成功'
    case 'failed':
      return '支付失败'
    default:
      return ''
  }
}

onMounted(fetchDetail)
onBeforeUnmount(stopPolling)
</script>

<style scoped>
.payment-select {
  padding: 8px 0;
}

.payment-amount {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.amount-label {
  display: block;
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.amount-value {
  display: block;
  color: #f56c6c;
  font-size: 32px;
  font-weight: bold;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-method-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.payment-method-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.payment-icon {
  width: 32px;
  height: 32px;
  margin-right: 12px;
  flex-shrink: 0;
}

.payment-name {
  font-size: 16px;
  font-weight: 500;
}

.payment-processing {
  padding: 20px 0;
}

.wechat-payment,
.alipay-payment {
  text-align: center;
}

.qrcode-container {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.qrcode {
  padding: 10px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.qrcode-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
}

.payment-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
  margin-top: 16px;
}

.alipay-container {
  padding: 20px;
}

.alipay-link {
  padding: 40px 20px;
}

.alipay-link p {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.payment-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.status-text {
  font-size: 14px;
  color: #666;
}

:deep(#alipay-form) {
  display: none;
}
</style>
