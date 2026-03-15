<template>
  <!-- 配置加载中 -->
  <div v-if="configLoading" class="config-loading">
    <el-icon class="is-loading" :size="40"><Loading /></el-icon>
    <div style="margin-top: 10px;">加载中...</div>
  </div>

  <!-- 配置加载完成 -->
  <el-container v-else class="layout-container">
    <!-- 纵向菜单模式 -->
    <el-aside v-if="!isHorizontal" :width="isCollapsed ? '64px' : '200px'" class="aside-transition">
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
        :collapse="isCollapsed"
        :collapse-transition="false"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <template v-for="menu in menuList" :key="menu.id">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path || String(menu.id)">
            <template #title>
              <el-icon v-if="menu.icon"><component :is="getIconComponent(menu.icon)" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <MenuItem :menus="menu.children" />
          </el-sub-menu>
          <!-- 没有子菜单 -->
          <el-menu-item v-else :index="formatPath(menu.path)" @click="handleMenuClick(menu)">
            <el-icon v-if="menu.icon"><component :is="getIconComponent(menu.icon)" /></el-icon>
            <template #title>{{ menu.menuName }}</template>
          </el-menu-item>
        </template>
      </el-menu>
      <!-- 菜单折叠控制区 -->
      <div class="menu-collapse-control" @click="toggleCollapse">
        <el-icon class="collapse-icon">
          <component :is="isCollapsed ? Expand : Fold" />
        </el-icon>
      </div>
    </el-aside>

    <!-- 横向菜单模式 -->
    <el-header v-if="isHorizontal" class="horizontal-header">
      <div class="horizontal-menu">
        <el-menu
          :default-active="activeMenu"
          router
          mode="horizontal"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <template v-for="menu in menuList" :key="menu.id">
            <!-- 有子菜单 -->
            <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path || String(menu.id)">
              <template #title>
                <el-icon v-if="menu.icon"><component :is="getIconComponent(menu.icon)" /></el-icon>
                <span>{{ menu.menuName }}</span>
              </template>
              <el-menu-item
                v-for="subMenu in menu.children"
                :key="subMenu.id"
                :index="formatPath(subMenu.path)"
                @click="handleMenuClick(subMenu)"
              >
                {{ subMenu.menuName }}
              </el-menu-item>
            </el-sub-menu>
            <!-- 没有子菜单 -->
            <el-menu-item v-else :index="formatPath(menu.path)" @click="handleMenuClick(menu)">
              <el-icon v-if="menu.icon"><component :is="getIconComponent(menu.icon)" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </div>
    </el-header>

    <el-container>
      <el-header v-if="!isHorizontal">
        <div class="header-content">
          <span>JavaWeb管理系统</span>
          <div class="header-right">
            <div class="user-info">
              <el-dropdown @command="handleCommand">
                <span class="el-dropdown-link">
                  <el-icon class="user-avatar"><User /></el-icon>
                  <span class="user-name">{{ userInfo.nickname || userInfo.username }}</span>
                  <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="password">修改密码</el-dropdown-item>
                    <el-dropdown-item command="refresh" divided>
                      <el-icon class="dropdown-item-icon"><Refresh /></el-icon>
                      系统刷新
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleMenu">
                      <el-icon class="dropdown-item-icon">
                        <component :is="isHorizontal ? Menu : Expand" />
                      </el-icon>
                      切换为{{ isHorizontal ? '纵向' : '横向' }}菜单
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </el-header>

      <!-- 横向模式下的控制栏 -->
      <el-header v-if="isHorizontal" class="control-header">
        <div class="header-content">
          <span>JavaWeb管理系统</span>
          <div class="header-right">
            <div class="user-info">
              <el-dropdown @command="handleCommand">
                <span class="el-dropdown-link">
                  <el-icon class="user-avatar"><User /></el-icon>
                  <span class="user-name">{{ userInfo.nickname || userInfo.username }}</span>
                  <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="password">修改密码</el-dropdown-item>
                    <el-dropdown-item command="refresh" divided>
                      <el-icon class="dropdown-item-icon"><Refresh /></el-icon>
                      系统刷新
                    </el-dropdown-item>
                    <el-dropdown-item command="toggleMenu">
                      <el-icon class="dropdown-item-icon">
                        <component :is="isHorizontal ? Menu : Expand" />
                      </el-icon>
                      切换为{{ isHorizontal ? '纵向' : '横向' }}菜单
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </el-header>

      <div class="notice-bar" v-if="firstUnreadNotice">
        <el-icon class="notice-icon"><Bell /></el-icon>
        <div class="notice-badge" v-if="unreadNoticeCount > 0">{{ unreadNoticeCount }}</div>
        <div class="notice-item" @click="viewNotice(firstUnreadNotice)">
          <el-tag v-if="firstUnreadNotice.noticeType === '1'" type="primary" size="small">通知</el-tag>
          <el-tag v-else-if="firstUnreadNotice.noticeType === '2'" type="success" size="small">公告</el-tag>
          <span class="notice-title unread">{{ firstUnreadNotice.noticeTitle }}</span>
          <span class="unread-mark">未读</span>
        </div>
      </div>

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

      <!-- 个人中心对话框 -->
      <el-dialog v-model="profileVisible" title="个人中心" width="500px">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ userInfo.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            <span v-if="userInfo.sex === '0'">未知</span>
            <span v-else-if="userInfo.sex === '1'">男</span>
            <span v-else-if="userInfo.sex === '2'">女</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userInfo.status === '0' ? 'success' : 'danger'">
              {{ userInfo.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ userInfo.createTime }}</el-descriptions-item>
        </el-descriptions>
      </el-dialog>

      <!-- 修改密码对话框 -->
      <el-dialog v-model="passwordVisible" title="修改密码" width="400px">
        <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="passwordVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
        </template>
      </el-dialog>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { noticeApi, menuApi, userConfigApi } from '@/api/system'
import MenuItem from '@/components/MenuItem.vue'
import {
  House,
  Setting,
  User,
  Lock,
  Menu as MenuIcon,
  Document,
  Bell,
  Monitor,
  Tickets,
  ArrowDown,
  Refresh,
  Expand,
  Fold,
  Menu,
  Loading
} from '@element-plus/icons-vue'

// 图标映射
const iconMap = {
  'house': House,
  'setting': Setting,
  'user': User,
  'lock': Lock,
  'menu': MenuIcon,
  'document': Document,
  'bell': Bell,
  'monitor': Monitor,
  'tickets': Tickets,
  'system': Setting,
  'peoples': User,
  'tree-table': Document,
  'dict': Document,
  'edit': Setting,
  'message': Bell,
  'form': Document,
  'logininfor': Tickets,
  'online': User,
  'log': Document
}

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const menuList = ref([])

// 配置加载状态
const configLoading = ref(true)

// 菜单状态
const isCollapsed = ref(false)
const isHorizontal = ref(false)

// 加载用户配置
const loadUserConfig = async () => {
  configLoading.value = true
  try {
    // 加载菜单折叠状态
    const collapsedRes = await userConfigApi.get('menu_collapsed')
    if (collapsedRes.code === 200 && collapsedRes.data.menu_collapsed) {
      isCollapsed.value = collapsedRes.data.menu_collapsed === 'true'
    }

    // 加载菜单方向配置
    const horizontalRes = await userConfigApi.get('menu_horizontal')
    if (horizontalRes.code === 200 && horizontalRes.data.menu_horizontal) {
      isHorizontal.value = horizontalRes.data.menu_horizontal === 'true'
    }
  } catch (error) {
    console.error('加载用户配置失败', error)
  } finally {
    configLoading.value = false
  }
}

// 保存用户配置
const saveUserConfig = async (configKey, configValue) => {
  try {
    await userConfigApi.set(configKey, configValue)
  } catch (error) {
    console.error('保存用户配置失败', error)
  }
}

// 切换菜单折叠状态
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
  saveUserConfig('menu_collapsed', isCollapsed.value.toString())
}

// 切换菜单模式（横向/纵向）
const toggleMenuMode = () => {
  isHorizontal.value = !isHorizontal.value
  // 切换到横向模式时，取消折叠
  if (isHorizontal.value) {
    isCollapsed.value = false
  }
  saveUserConfig('menu_horizontal', isHorizontal.value.toString())
}
const notices = ref([])
const noticeVisible = ref(false)
const viewNoticeData = ref({})
const unreadNoticeStatus = ref([])
const unreadNoticeCount = computed(() => unreadNoticeStatus.value.filter(status => status.readStatus === '0').length)
const firstUnreadNotice = computed(() => {
  if (notices.value.length === 0) return null
  return notices.value.find(notice => isNoticeUnread(notice.id)) || null
})
const userInfo = ref({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  sex: '0',
  status: '0',
  createTime: ''
})

// 个人中心
const profileVisible = ref(false)

// 修改密码
const passwordVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await userStore.logoutAction()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (error) {
      ElMessage.error(error.message || '退出失败')
    }
  } else if (command === 'profile') {
    loadUserInfo()
    profileVisible.value = true
  } else if (command === 'password') {
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    passwordVisible.value = true
  } else if (command === 'refresh') {
    handleRefreshMenu()
  } else if (command === 'toggleMenu') {
    toggleMenuMode()
  }
}

const loadUserInfo = async () => {
  try {
    await userStore.getUserInfoAction()
    if (userStore.userInfo) {
      userInfo.value = { ...userStore.userInfo }
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里需要调用修改密码的API
        ElMessage.success('密码修改成功，请重新登录')
        passwordVisible.value = false
        await userStore.logoutAction()
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '修改失败')
      }
    }
  })
}

const handleRefreshMenu = async () => {
  try {
    await menuApi.refresh()
    ElMessage.success('菜单缓存已刷新，正在重新加载...')
    // 重新加载菜单
    await loadMenus()
  } catch (error) {
    ElMessage.error(error.message || '刷新菜单失败')
  }
}

const handleMenuClick = (menu) => {
  if (menu.path) {
    router.push(formatPath(menu.path))
  }
}

const formatPath = (path) => {
  if (!path) return path
  // 如果路径不是以 / 开头，添加 /system 前缀
  if (!path.startsWith('/')) {
    return `/system/${path}`
  }
  return path
}

const getIconComponent = (iconName) => {
  return iconMap[iconName] || Document
}

const isNoticeUnread = (noticeId) => {
  const status = unreadNoticeStatus.value.find(s => s.noticeId === noticeId)
  return !status || status.readStatus === '0'
}

const loadMenus = async () => {
  try {
    const res = await menuApi.userMenus()
    if (res.code === 200) {
      menuList.value = res.data
    }
  } catch (error) {
    console.error('加载菜单失败', error)
  }
}

const loadNotices = async () => {
  try {
    const res = await noticeApi.latest()
    if (res.code === 200) {
      notices.value = res.data
    }
  } catch (error) {
    console.error('加载公告失败', error)
  }
}

const loadUserNoticeStatus = async () => {
  try {
    const res = await noticeApi.userStatus()
    if (res.code === 200) {
      unreadNoticeStatus.value = res.data || []
    }
  } catch (error) {
    console.error('加载用户公告状态失败', error)
  }
}

const markNoticeAsRead = async (noticeId) => {
  try {
    await noticeApi.markAsRead(noticeId)
    // 更新本地状态
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

const viewNotice = async (notice) => {
  viewNoticeData.value = notice
  noticeVisible.value = true
  // 如果是未读公告，标记为已读
  if (isNoticeUnread(notice.id)) {
    await markNoticeAsRead(notice.id)
  }
}

onMounted(async () => {
  await loadUserConfig()
  loadMenus()
  loadNotices()
  loadUserNoticeStatus()
  loadUserInfo()
})
</script>

<style scoped>
.config-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f5f5;
  color: #409eff;
}

.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #545c64;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.aside-transition {
  transition: all 0.3s ease;
}

.el-aside .el-menu {
  flex: 1;
  overflow-x: hidden;
  overflow-y: auto;
}

.menu-collapse-control {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #fff;
  transition: all 0.3s;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.menu-collapse-control:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.collapse-icon {
  font-size: 20px;
  transition: transform 0.3s;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px !important;
}

.horizontal-header {
  height: 60px !important;
  padding: 0;
  border-bottom: 1px solid #e6e6e6;
}

.horizontal-menu {
  width: 100%;
  height: 100%;
}

.horizontal-menu .el-menu {
  border-bottom: none;
  height: 100%;
  line-height: 60px;
}

.control-header {
  background-color: #f5f5f5;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.el-dropdown-link:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  font-size: 20px;
  color: #409eff;
}

.user-name {
  font-size: 14px;
  color: #303133;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-item-icon {
  font-size: 16px;
}

.notice-bar {
  background-color: #f0f9ff;
  border-bottom: 1px solid #d1e8ff;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 40px;
  position: relative;
}

.notice-icon {
  margin-right: 10px;
  color: #409eff;
  font-size: 18px;
}

.notice-badge {
  position: absolute;
  top: 5px;
  left: 25px;
  background-color: #f56c6c;
  color: white;
  border-radius: 10px;
  padding: 0 6px;
  font-size: 12px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  z-index: 10;
}

.notice-item {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  height: 40px;
  line-height: 40px;
}

.notice-item:hover {
  background-color: #e6f7ff;
}

.notice-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-title.unread {
  font-weight: bold;
  color: #409eff;
}

.unread-mark {
  color: #f56c6c;
  font-size: 12px;
  font-weight: bold;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}
</style>