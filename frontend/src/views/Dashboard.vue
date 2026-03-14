<template>
  <div class="dashboard">
    <el-row :gutter="20" v-if="isAdmin">
      <el-col :span="8">
        <div class="stat-card user-stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon user-icon">
              <el-icon :size="36"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value" v-loading="userCountLoading">{{ userCount }}</div>
              <div class="stat-label">用户总数</div>
              <div class="stat-trend">
                <el-icon><TrendCharts /></el-icon>
                <span>活跃用户</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card menu-stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon menu-icon">
              <el-icon :size="36"><Menu /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value" v-loading="menuCountLoading">{{ menuCount }}</div>
              <div class="stat-label">菜单总数</div>
              <div class="stat-trend">
                <el-icon><Operation /></el-icon>
                <span>系统菜单</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card notice-stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon notice-icon">
              <el-icon :size="36"><Bell /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value" v-loading="unreadCountLoading">{{ unreadCount }}</div>
              <div class="stat-label">未读公告</div>
              <div class="stat-trend">
                <el-icon><ChatDotRound /></el-icon>
                <span>待处理</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px" v-if="isAdmin">
      <el-col :span="12">
        <div class="tool-card" @click="openDoc">
          <div class="tool-card-header">
            <div class="tool-icon doc-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <h3 class="tool-title">接口文档</h3>
          </div>
          <p class="tool-desc">Knife4j 在线接口文档，查看和管理所有API接口</p>
          <div class="tool-footer">
            <span class="tool-status">在线</span>
            <el-icon class="tool-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="tool-card" @click="openDruid">
          <div class="tool-card-header">
            <div class="tool-icon monitor-icon">
              <el-icon :size="40"><Monitor /></el-icon>
            </div>
            <h3 class="tool-title">数据监控</h3>
          </div>
          <p class="tool-desc">Druid 数据库连接池监控，查看SQL执行和数据库性能</p>
          <div class="tool-footer">
            <span class="tool-status">在线</span>
            <el-icon class="tool-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <div class="welcome-card">
          <div class="welcome-header">
            <div class="welcome-icon">
              <el-icon :size="48"><House /></el-icon>
            </div>
            <div class="welcome-title-section">
              <h1 class="welcome-title">欢迎使用JavaWeb管理系统</h1>
              <p class="welcome-subtitle">Enterprise Management System</p>
            </div>
          </div>
          <div class="welcome-content">
            <p class="welcome-desc">
              这是一个基于 <span class="tech-tag">Spring Boot</span> + <span class="tech-tag">MyBatis-Plus</span> + <span class="tech-tag">Shiro</span> + <span class="tech-tag">Vue 3</span> 的企业级Web应用框架，为您提供全面、高效的系统管理解决方案。
            </p>
            <div class="feature-grid">
              <div class="feature-item">
                <div class="feature-icon user-feature">
                  <el-icon :size="24"><User /></el-icon>
                </div>
                <span class="feature-text">用户管理</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon role-feature">
                  <el-icon :size="24"><Lock /></el-icon>
                </div>
                <span class="feature-text">角色管理</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon menu-feature">
                  <el-icon :size="24"><Menu /></el-icon>
                </div>
                <span class="feature-text">菜单管理</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon dict-feature">
                  <el-icon :size="24"><Document /></el-icon>
                </div>
                <span class="feature-text">字典管理</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon config-feature">
                  <el-icon :size="24"><Setting /></el-icon>
                </div>
                <span class="feature-text">参数设置</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon notice-feature">
                  <el-icon :size="24"><Bell /></el-icon>
                </div>
                <span class="feature-text">通知公告</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon log-feature">
                  <el-icon :size="24"><Tickets /></el-icon>
                </div>
                <span class="feature-text">操作日志</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon login-feature">
                  <el-icon :size="24"><Monitor /></el-icon>
                </div>
                <span class="feature-text">登录日志</span>
              </div>
              <div class="feature-item">
                <div class="feature-icon online-feature">
                  <el-icon :size="24"><User /></el-icon>
                </div>
                <span class="feature-text">在线用户</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="notice-card">
          <div class="notice-card-header">
            <div class="notice-header-icon">
              <el-icon :size="24"><Bell /></el-icon>
            </div>
            <h3 class="notice-card-title">最新公告</h3>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notice-badge">
              <span></span>
            </el-badge>
          </div>
          <div class="notice-card-body" v-loading="loading">
            <el-empty v-if="sortedNotices.length === 0" description="暂无公告" />
            <div v-else class="notice-list">
              <div v-for="notice in sortedNotices" :key="notice.id"
                   class="notice-item"
                   :class="{ 'unread': isNoticeUnread(notice.id) }"
                   @click="viewNotice(notice)">
                <div class="notice-item-header">
                  <div class="notice-type-tag" :class="`type-${notice.noticeType}`">
                    <el-icon v-if="notice.noticeType === '1'"><ChatDotRound /></el-icon>
                    <el-icon v-else-if="notice.noticeType === '2'"><Document /></el-icon>
                  </div>
                  <span class="notice-item-title">{{ notice.noticeTitle }}</span>
                  <el-badge v-if="isNoticeUnread(notice.id)" is-dot type="danger" />
                </div>
                <div class="notice-item-meta">
                  <el-icon class="time-icon"><Clock /></el-icon>
                  <span class="notice-item-time">{{ notice.createTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 公告详情对话框 -->
    <el-dialog v-model="noticeVisible" :title="viewNoticeData.noticeTitle" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="公告类型">
          <el-tag v-if="viewNoticeData.noticeType === '1'" type="primary">通知</el-tag>
          <el-tag v-else-if="viewNoticeData.noticeType === '2'" type="success">公告</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="公告内容">
          <div v-html="viewNoticeData.noticeContent"></div>
        </el-descriptions-item>
        <el-descriptions-item label="创建者">{{ viewNoticeData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewNoticeData.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { noticeApi, userApi, menuApi } from '@/api/system'
import { useUserStore } from '@/store/user'
import {
  Document,
  Monitor,
  ArrowRight,
  House,
  User,
  Lock,
  Menu,
  Setting,
  Bell,
  Tickets,
  TrendCharts,
  Operation,
  ChatDotRound,
  Clock
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const notices = ref([])
const unreadNoticeStatus = ref([])
const noticeVisible = ref(false)
const viewNoticeData = ref({})
const userCount = ref(0)
const userCountLoading = ref(false)
const menuCount = ref(0)
const menuCountLoading = ref(false)
const unreadCountLoading = ref(false)
const unreadCount = computed(() => {
  // 从公告列表中统计未读公告
  if (!notices.value || !Array.isArray(notices.value)) {
    console.log('公告列表为空，未读数量为 0')
    return 0
  }

  // 如果用户公告状态为空，则认为所有公告都是未读的
  if (!unreadNoticeStatus.value || !Array.isArray(unreadNoticeStatus.value) || unreadNoticeStatus.value.length === 0) {
    console.log('用户公告状态为空，所有公告都认为未读，未读数量:', notices.value.length)
    return notices.value.length
  }

  // 否则根据用户公告状态统计未读公告
  const count = notices.value.filter(notice => isNoticeUnread(notice.id)).length
  console.log('未读公告数量计算:', count, '公告总数:', notices.value.length, '用户状态总数:', unreadNoticeStatus.value.length)
  return count
})

// 判断是否是超级管理员
const isAdmin = computed(() => {
  const userInfo = userStore.userInfo
  console.log('判断管理员 - 用户信息:', userInfo)
  if (!userInfo) {
    console.log('用户信息为空，不是管理员')
    return false
  }
  const result = userInfo.username === 'admin'
  console.log('判断管理员结果:', result, '用户名:', userInfo.username)
  return result
})

// 排序后的公告列表，未读的在前面
const sortedNotices = computed(() => {
  return [...notices.value].sort((a, b) => {
    const aUnread = isNoticeUnread(a.id)
    const bUnread = isNoticeUnread(b.id)
    if (aUnread && !bUnread) return -1
    if (!aUnread && bUnread) return 1
    return 0
  })
})

const isNoticeUnread = (noticeId) => {
  if (!unreadNoticeStatus.value || !Array.isArray(unreadNoticeStatus.value)) {
    console.log('用户公告状态未初始化，默认认为所有公告未读')
    return true
  }

  const status = unreadNoticeStatus.value.find(s => s.noticeId === noticeId)

  // 如果找不到对应的用户公告状态，认为该公告未读
  if (!status) {
    console.log('公告', noticeId, '未找到用户状态，认为未读')
    return true
  }

  // 如果找到状态且 readStatus 为 '0'，则认为未读
  const result = status.readStatus === '0'

  console.log('检查公告未读状态:', {
    noticeId,
    readStatus: status.readStatus,
    isUnread: result
  })

  return result
}

const loadNotices = async () => {
  loading.value = true
  try {
    console.log('开始加载最新公告...')
    const res = await noticeApi.latest()
    console.log('公告API返回结果:', res)
    if (res.code === 200) {
      // 处理数据格式
      if (Array.isArray(res.data)) {
        notices.value = res.data
        console.log('公告列表（数组）:', notices.value)
      } else if (res.data && res.data.records && Array.isArray(res.data.records)) {
        notices.value = res.data.records
        console.log('公告列表（分页）:', notices.value)
      } else if (res.data && res.data.list && Array.isArray(res.data.list)) {
        notices.value = res.data.list
        console.log('公告列表（list）:', notices.value)
      } else {
        console.warn('未知的公告数据格式:', res.data)
        notices.value = []
      }
    } else {
      console.error('公告API返回错误:', res)
      notices.value = []
    }
  } catch (error) {
    console.error('加载公告失败', error)
    notices.value = []
  } finally {
    loading.value = false
  }
}

const loadUserNoticeStatus = async () => {
  unreadCountLoading.value = true
  try {
    console.log('开始加载用户公告状态...')
    const res = await noticeApi.userStatus()
    console.log('用户公告状态API返回结果:', res)
    if (res.code === 200) {
      // 处理数据格式
      if (Array.isArray(res.data)) {
        unreadNoticeStatus.value = res.data
        console.log('用户公告状态（数组）:', unreadNoticeStatus.value)
      } else if (res.data && res.data.records && Array.isArray(res.data.records)) {
        unreadNoticeStatus.value = res.data.records
        console.log('用户公告状态（分页）:', unreadNoticeStatus.value)
      } else if (res.data && res.data.list && Array.isArray(res.data.list)) {
        unreadNoticeStatus.value = res.data.list
        console.log('用户公告状态（list）:', unreadNoticeStatus.value)
      } else {
        console.warn('未知的用户公告状态数据格式:', res.data)
        unreadNoticeStatus.value = []
      }
    } else {
      console.error('用户公告状态API返回错误:', res)
      unreadNoticeStatus.value = []
    }
  } catch (error) {
    console.error('加载用户公告状态失败', error)
    unreadNoticeStatus.value = []
  } finally {
    unreadCountLoading.value = false
  }
}

const markNoticeAsRead = async (noticeId) => {
  try {
    await noticeApi.markAsRead(noticeId)
    const status = unreadNoticeStatus.value.find(s => s.noticeId === noticeId)
    if (status) {
      status.readStatus = '1'
    } else {
      unreadNoticeStatus.value.push({
        noticeId: noticeId,
        readStatus: '1'
      })
    }
  } catch (error) {
    console.error('标记公告已读失败', error)
  }
}

const loadUserCount = async () => {
  userCountLoading.value = true
  try {
    console.log('开始加载用户总数...')
    const res = await userApi.list({ current: 1, size: 1 })
    console.log('用户API返回结果:', res)
    if (res.code === 200) {
      userCount.value = res.data.total
      console.log('用户总数:', userCount.value)
    } else {
      console.error('用户API返回错误:', res)
      userCount.value = 0
    }
  } catch (error) {
    console.error('加载用户总数失败', error)
    userCount.value = 0
  } finally {
    userCountLoading.value = false
  }
}

const loadMenuCount = async () => {
  menuCountLoading.value = true
  try {
    console.log('开始加载菜单列表...')
    const res = await menuApi.list()
    console.log('菜单API返回结果:', res)
    if (res.code === 200) {
      console.log('菜单数据:', res.data)
      // 如果返回的是分页数据
      if (res.data && res.data.total !== undefined) {
        menuCount.value = res.data.total
        console.log('菜单总数（分页）:', menuCount.value)
      }
      // 如果返回的是列表数据
      else if (Array.isArray(res.data)) {
        menuCount.value = res.data.length
        console.log('菜单总数（列表）:', menuCount.value)
      }
      else {
        console.log('未知的数据格式:', res.data)
        menuCount.value = 0
      }
    } else {
      console.error('菜单API返回错误:', res)
      menuCount.value = 0
    }
  } catch (error) {
    console.error('加载菜单总数失败', error)
    menuCount.value = 0
  } finally {
    menuCountLoading.value = false
  }
}

const viewNotice = async (notice) => {
  viewNoticeData.value = notice
  noticeVisible.value = true
  if (isNoticeUnread(notice.id)) {
    await markNoticeAsRead(notice.id)
  }
}

const openDoc = () => {
  window.open('/doc.html', '_blank')
}

const openDruid = () => {
  window.open('/druid/index.html', '_blank')
}

onMounted(async () => {
  console.log('Dashboard 组件已挂载')
  console.log('当前用户:', userStore.userInfo)
  console.log('是否为管理员:', isAdmin.value)

  // 先加载公告列表
  await loadNotices()
  // 再加载用户公告状态
  await loadUserNoticeStatus()

  if (isAdmin.value) {
    loadUserCount()
    loadMenuCount()
  }

  // 打印最终统计结果
  console.log('=== 最终统计结果 ===')
  console.log('用户总数:', userCount.value)
  console.log('菜单总数:', menuCount.value)
  console.log('未读公告数量:', unreadCount.value)
  console.log('公告列表:', notices.value)
  console.log('用户公告状态:', unreadNoticeStatus.value)
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  border-radius: 16px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card-inner {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.user-stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

.menu-stat-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 16px rgba(240, 147, 251, 0.3);
}

.notice-stat-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 4px 16px rgba(79, 172, 254, 0.3);
}

.stat-icon {
  width: 72px;
  height: 72px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  color: white;
}

.stat-label {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.stat-trend .el-icon {
  font-size: 16px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-20px, 20px);
  }
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.welcome-title-section {
  flex: 1;
}

.welcome-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: white;
  letter-spacing: 0.5px;
}

.welcome-subtitle {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 300;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.welcome-content {
  position: relative;
  z-index: 1;
}

.welcome-desc {
  margin: 0 0 24px 0;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.95);
  line-height: 1.8;
  text-align: justify;
}

.tech-tag {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  font-weight: 600;
  margin: 0 2px;
  backdrop-filter: blur(5px);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  cursor: default;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: transform 0.3s ease;
}

.feature-item:hover .feature-icon {
  transform: scale(1.1);
}

.user-feature {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.role-feature {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.menu-feature {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.dict-feature {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.config-feature {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.notice-feature {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.log-feature {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.login-feature {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.online-feature {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.feature-text {
  font-size: 13px;
  color: white;
  font-weight: 500;
  text-align: center;
}

ul {
  margin-left: 20px;
  line-height: 2;
}

.notice-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  height: 100%;
}

.notice-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-bottom: 1px solid #ebeef5;
}

.notice-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.notice-card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.notice-card-body {
  padding: 16px 24px;
}

.notice-list {
  max-height: 480px;
  overflow-y: auto;
}

.notice-item {
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 12px;
  border: 1px solid transparent;
}

.notice-item:last-child {
  margin-bottom: 0;
}

.notice-item:hover {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-color: #4facfe;
  transform: translateX(4px);
}

.notice-item.unread {
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-color: #409eff;
}

.notice-item.unread:hover {
  background: linear-gradient(135deg, #e0f2ff 0%, #d0edff 100%);
  border-color: #66b1ff;
}

.notice-item-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.notice-type-tag {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  flex-shrink: 0;
}

.type-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.type-2 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.notice-item-title {
  flex: 1;
  font-weight: 500;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-item.unread .notice-item-title {
  font-weight: 600;
  color: #409eff;
}

.notice-item-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-icon {
  font-size: 12px;
  color: #909399;
}

.notice-item-time {
  font-size: 12px;
  color: #909399;
}

.tool-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  position: relative;
  overflow: hidden;
}

.tool-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tool-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.tool-card:hover::before {
  opacity: 1;
}

.tool-card:nth-child(2) {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 4px 12px rgba(17, 153, 142, 0.15);
}

.tool-card:nth-child(2):hover {
  box-shadow: 0 8px 24px rgba(17, 153, 142, 0.3);
}

.tool-card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.tool-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.tool-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: white;
}

.tool-desc {
  margin: 0 0 20px 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  min-height: 42px;
}

.tool-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.tool-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
}

.tool-status::before {
  content: '';
  width: 8px;
  height: 8px;
  background-color: #4ade80;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.1);
  }
}

.tool-arrow {
  font-size: 20px;
  color: white;
  transition: transform 0.3s ease;
}

.tool-card:hover .tool-arrow {
  transform: translateX(6px);
}
</style>