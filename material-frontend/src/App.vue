<template>
  <div class="app-container">
    <header class="topbar">
      <div class="inner">
        <RouterLink to="/" class="logo">
          <span class="logo-icon">📚</span>
          <span class="logo-text">资料商城</span>
        </RouterLink>
        <nav class="nav-links">
          <RouterLink to="/" class="nav-link">
            <span class="nav-icon">🏠</span>
            <span>首页</span>
          </RouterLink>
          <RouterLink to="/list" class="nav-link">
            <span class="nav-icon">🛒</span>
            <span>资料列表</span>
          </RouterLink>
        </nav>
        <div class="actions">
          <template v-if="userStore.isLogin()">
            <div class="user-info">
              <div class="avatar">{{ userStore.userInfo?.nickname?.[0] || userStore.userInfo?.username?.[0] || 'U' }}</div>
              <span class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            </div>
            <el-button size="small" @click="go('/profile')" class="profile-btn">个人中心</el-button>
            <el-button size="small" @click="logout" class="logout-btn">退出</el-button>
          </template>
          <template v-else>
            <RouterLink to="/login" class="auth-link">登录</RouterLink>
            <RouterLink to="/register" class="auth-link primary">注册</RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main class="main-content">
      <RouterView />
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-links">
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/list">资料列表</RouterLink>
          <RouterLink to="/profile">个人中心</RouterLink>
        </div>
        <div class="footer-divider"></div>
        <div class="footer-text">
          © 2026 资料商城 | 隐私政策 | 用户协议 | 免责声明
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const go = (path) => router.push(path)

const logout = async () => {
  await userStore.logoutAction()
  router.push('/login')
}

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      await userStore.logoutAction()
    }
  }
})
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.inner {
  max-width: 1240px;
  height: 70px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  font-weight: 800;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.logo-icon {
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(37, 99, 235, 0.3));
}

.nav-links {
  display: flex;
  gap: 8px;
  align-items: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-link:hover {
  color: var(--brand);
  background: rgba(37, 99, 235, 0.08);
  transform: translateY(-2px);
}

.nav-link.router-link-active {
  color: var(--brand);
  background: rgba(37, 99, 235, 0.12);
  font-weight: 600;
}

.nav-icon {
  font-size: 18px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba(37, 99, 235, 0.08);
  border: 1px solid rgba(37, 99, 235, 0.2);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(37, 99, 235, 0.12);
  transform: translateY(-2px);
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--brand-gradient);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.nickname {
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
}

.profile-btn {
  border-radius: 20px;
  border: 1px solid rgba(37, 99, 235, 0.3);
  color: var(--brand);
  background: rgba(37, 99, 235, 0.05);
  transition: all 0.3s ease;
}

.profile-btn:hover {
  background: rgba(37, 99, 235, 0.12);
  transform: translateY(-2px);
}

.logout-btn {
  border-radius: 20px;
  border: 1px solid rgba(239, 68, 68, 0.3);
  color: #ef4444;
  background: rgba(239, 68, 68, 0.05);
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  transform: translateY(-2px);
}

.auth-link {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 1px solid rgba(37, 99, 235, 0.3);
  color: var(--brand);
  background: rgba(37, 99, 235, 0.05);
}

.auth-link:hover {
  background: rgba(37, 99, 235, 0.12);
  transform: translateY(-2px);
}

.auth-link.primary {
  background: var(--brand-gradient);
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.auth-link.primary:hover {
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
  transform: translateY(-2px);
}

.main-content {
  flex: 1;
  min-height: calc(100vh - 140px);
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px 32px;
}

.footer {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-top: 1px solid rgba(226, 232, 240, 0.6);
  padding: 24px 32px;
}

.footer-inner {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.footer-links {
  display: flex;
  gap: 20px;
}

.footer-links a {
  color: var(--text-secondary);
  font-size: 14px;
  transition: color 0.2s ease;
}

.footer-links a:hover {
  color: var(--brand);
}

.footer-divider {
  width: 1px;
  height: 20px;
  background: var(--muted);
  opacity: 0.3;
}

.footer-text {
  color: var(--muted);
  font-size: 13px;
}

@media (max-width: 1024px) {
  .inner {
    height: auto;
    flex-wrap: wrap;
    padding: 12px 16px;
  }

  .nav-links {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 12px;
  }

  .main-content {
    padding: 20px 24px;
  }

  .footer {
    padding: 20px 24px;
  }
}

@media (max-width: 768px) {
  .footer-inner {
    flex-direction: column;
    text-align: center;
  }

  .footer-divider {
    display: none;
  }

  .nav-link {
    padding: 6px 10px;
    font-size: 13px;
  }

  .nav-icon {
    display: none;
  }

  .logo-text {
    font-size: 20px;
  }

  .logo-icon {
    font-size: 24px;
  }

  .user-info {
    padding: 4px 8px;
  }

  .nickname {
    font-size: 12px;
  }

  .avatar {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }

  .profile-btn,
  .logout-btn {
    padding: 6px 12px;
    font-size: 12px;
  }

  .auth-link {
    padding: 6px 14px;
    font-size: 13px;
  }

  .main-content {
    padding: 16px 16px;
  }

  .footer {
    padding: 16px 16px;
  }
}

@media (max-width: 480px) {
  .inner {
    padding: 10px 12px;
    gap: 12px;
  }

  .nav-links {
    gap: 4px;
  }

  .nav-link {
    padding: 6px 8px;
    font-size: 12px;
  }

  .actions {
    gap: 8px;
  }

  .user-info {
    display: none;
  }

  .profile-btn,
  .logout-btn {
    padding: 5px 10px;
    font-size: 11px;
  }

  .auth-link {
    padding: 5px 12px;
    font-size: 12px;
  }

  .main-content {
    padding: 12px 12px;
  }

  .footer {
    padding: 12px 12px;
  }
}
</style>
