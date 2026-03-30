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
              <div class="stat-label">{{ $t('dashboard.userCount') }}</div>
              <div class="stat-trend">
                <el-icon><TrendCharts /></el-icon>
                <span>{{ $t('dashboard.activeUser') }}</span>
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
              <div class="stat-label">{{ $t('dashboard.menuCount') }}</div>
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
              <div class="stat-label">{{ $t('dashboard.unreadNotice') }}</div>
              <div class="stat-trend">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ $t('dashboard.pending') }}</span>
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
            <h3 class="tool-title">{{ $t('dashboard.apiDoc') }}</h3>
          </div>
          <p class="tool-desc">{{ $t('dashboard.knife4jDesc') }}</p>
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
            <h3 class="tool-title">{{ $t('dashboard.dataMonitor') }}</h3>
          </div>
          <p class="tool-desc">{{ $t('dashboard.druidDesc') }}</p>
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
              <h1 class="welcome-title">{{ $t('dashboard.welcome') }}</h1>
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
            <h3 class="notice-card-title">{{ $t('dashboard.latestNotice') }}</h3>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notice-badge">
              <span></span>
            </el-badge>
          </div>
          <div class="notice-card-body" v-loading="loading">
            <el-empty v-if="sortedNotices.length === 0" :description="$t('dashboard.noNotice')" />
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
    <el-dialog v-model="noticeVisible" width="800px" class="notice-detail-dialog">
      <template #header>
        <div class="notice-detail-header">
          <div class="notice-detail-type" :class="`type-${viewNoticeData.noticeType}`">
            <el-icon v-if="viewNoticeData.noticeType === '1'" :size="24"><ChatDotRound /></el-icon>
            <el-icon v-else-if="viewNoticeData.noticeType === '2'" :size="24"><Document /></el-icon>
            <span class="notice-type-text">{{ viewNoticeData.noticeType === '1' ? $t('dashboard.noticeTypeNotice') : $t('dashboard.noticeTypeAnnouncement') }}</span>
          </div>
          <div class="notice-detail-title">{{ viewNoticeData.noticeTitle }}</div>
        </div>
      </template>
      <div class="notice-detail-body">
        <div class="notice-detail-meta">
                    <div class="meta-item">
                      <el-icon class="meta-icon"><User /></el-icon>
                      <span class="meta-label">{{ $t('dashboard.creator') }}：</span>
                      <span class="meta-value">{{ viewNoticeData.createBy }}</span>
                    </div>
                    <div class="meta-item">
                      <el-icon class="meta-icon"><Clock /></el-icon>
                      <span class="meta-label">{{ $t('dashboard.createTime') }}：</span>
                      <span class="meta-value">{{ viewNoticeData.createTime }}</span>
                    </div>
                  </div>        <div class="notice-divider"></div>
        <div class="notice-detail-content">
          <div class="content-wrapper" v-html="viewNoticeData.noticeContent"></div>
        </div>
      </div>
      <template #footer>
        <el-button @click="noticeVisible = false">{{ $t('dashboard.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
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

const { t } = useI18n()
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
  if (!notices.value?.length) return 0
  if (!unreadNoticeStatus.value?.length) return notices.value.length
  return notices.value.filter(notice => isNoticeUnread(notice.id)).length
})

// 判断是否是超级管理员
const isAdmin = computed(() => {
  const userInfo = userStore.userInfo
  return userInfo?.username === 'admin'
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
    return true
  }

  const status = unreadNoticeStatus.value.find(s => s.noticeId === noticeId)
  return !status || status.readStatus === '0'
}

const loadNotices = async () => {
  loading.value = true
  try {
    const res = await noticeApi.latest()
    if (res.code === 200) {
      notices.value = Array.isArray(res.data) ? res.data :
                      res.data?.records || res.data?.list || []
    } else {
      notices.value = []
    }
  } catch (error) {
    console.error(t('dashboard.loadNoticesFailed'), error)
    notices.value = []
  } finally {
    loading.value = false
  }
}

const loadUserNoticeStatus = async () => {
  unreadCountLoading.value = true
  try {
    const res = await noticeApi.userStatus()
    if (res.code === 200) {
      unreadNoticeStatus.value = Array.isArray(res.data) ? res.data :
                                 res.data?.records || res.data?.list || []
    } else {
      unreadNoticeStatus.value = []
    }
  } catch (error) {
    console.error(t('dashboard.loadUserStatusFailed'), error)
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
      unreadNoticeStatus.value.push({ noticeId, readStatus: '1' })
    }
  } catch (error) {
    console.error(t('dashboard.markAsReadFailed'), error)
  }
}

const loadUserCount = async () => {
  userCountLoading.value = true
  try {
    const res = await userApi.list({ current: 1, size: 1 })
    if (res.code === 200) {
      userCount.value = res.data.total || 0
    } else {
      userCount.value = 0
    }
  } catch (error) {
    console.error(t('dashboard.loadUserCountFailed'), error)
    userCount.value = 0
  } finally {
    userCountLoading.value = false
  }
}

const loadMenuCount = async () => {
  menuCountLoading.value = true
  try {
    const res = await menuApi.list()
    if (res.code === 200) {
      menuCount.value = res.data?.total ?? (Array.isArray(res.data) ? res.data.length : 0)
    } else {
      menuCount.value = 0
    }
  } catch (error) {
    console.error(t('dashboard.loadMenuCountFailed'), error)
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
  await loadNotices()
  await loadUserNoticeStatus()

  if (isAdmin.value) {
    loadUserCount()
    loadMenuCount()
  }
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
  padding-right: 8px;
}

.notice-list::-webkit-scrollbar {
  width: 6px;
}

.notice-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.notice-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.notice-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
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

/* 公告详情对话框样式 */
.notice-detail-dialog :deep(.el-dialog__header) {
  padding: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-radius: 12px 12px 0 0;
}

.notice-detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.notice-detail-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #ebeef5;
}

.notice-detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
}

.notice-detail-type {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  color: white;
  font-weight: 600;
  flex-shrink: 0;
}

.type-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.type-2 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.notice-type-text {
  font-size: 14px;
}

.notice-detail-title {
  flex: 1;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-detail-body {
  padding: 24px;
}

.notice-detail-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.meta-icon {
  font-size: 16px;
  color: #909399;
}

.meta-label {
  color: #909399;
}

.meta-value {
  font-weight: 500;
  color: #303133;
}

.notice-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #dcdfe6, transparent);
  margin-bottom: 20px;
}

.notice-detail-content {
  background: #fafafa;
  border-radius: 8px;
  padding: 20px;
  min-height: 200px;
  max-height: 500px;
  overflow-y: auto;
}

.notice-detail-content::-webkit-scrollbar {
  width: 6px;
}

.notice-detail-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.notice-detail-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.notice-detail-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.content-wrapper {
  line-height: 1.8;
  color: #303133;
}

.content-wrapper :deep(p) {
  margin-bottom: 12px;
}

.content-wrapper :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 12px 0;
}

.content-wrapper :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
}

.content-wrapper :deep(table td),
.content-wrapper :deep(table th) {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
  text-align: left;
}

.content-wrapper :deep(table th) {
  background-color: #f5f7fa;
  font-weight: 600;
}

.content-wrapper :deep(ul),
.content-wrapper :deep(ol) {
  margin: 12px 0;
  padding-left: 24px;
}

.content-wrapper :deep(li) {
  margin-bottom: 6px;
}

.content-wrapper :deep(blockquote) {
  border-left: 4px solid #409eff;
  padding-left: 16px;
  margin: 12px 0;
  color: #606266;
  background: #f5f7fa;
  padding: 12px 16px;
}

.content-wrapper :deep(code) {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  color: #e6a23c;
}

.content-wrapper :deep(pre) {
  background: #282c34;
  color: #abb2bf;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 12px 0;
}

.content-wrapper :deep(pre code) {
  background: none;
  color: inherit;
  padding: 0;
}
</style>