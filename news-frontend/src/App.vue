<template>
  <div>
    <header class="topbar">
      <div class="inner">
        <RouterLink to="/" class="logo">资讯站</RouterLink>
        <nav>
          <RouterLink to="/">首页</RouterLink>
          <RouterLink to="/list">分类资讯</RouterLink>
          <RouterLink to="/publish">发布内容</RouterLink>
          <RouterLink to="/profile">个人中心</RouterLink>
        </nav>
        <div class="actions">
          <el-input
            v-model="keyword"
            size="small"
            placeholder="搜索标题/摘要/标签"
            @keyup.enter="toSearch"
            style="width: 220px"
          />
          <template v-if="userStore.isLogin()">
            <span class="nickname">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            <el-button size="small" @click="logout">退出</el-button>
          </template>
          <template v-else>
            <RouterLink to="/login">登录</RouterLink>
            <RouterLink to="/register">注册</RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main>
      <RouterView />
    </main>

    <footer class="footer">
      <div>© 2026 资讯站 | 隐私政策 | 用户协议 | 免责声明</div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

const toSearch = () => {
  router.push({ path: '/list', query: { keyword: keyword.value } })
}

const logout = async () => {
  await userStore.logoutAction()
  router.push('/login')
}

onMounted(async () => {
  if (userStore.token) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      await userStore.logoutAction()
    }
  }
})
</script>

<style scoped>
.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e5e7eb;
  backdrop-filter: blur(8px);
}

.inner {
  max-width: 1180px;
  height: 64px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: var(--brand);
}

nav {
  display: flex;
  gap: 16px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nickname {
  color: var(--muted);
  font-size: 13px;
}

main {
  min-height: calc(100vh - 120px);
}

.footer {
  border-top: 1px solid #e5e7eb;
  padding: 16px;
  text-align: center;
  color: var(--muted);
  font-size: 12px;
  background: #fff;
}

@media (max-width: 768px) {
  .inner {
    height: auto;
    flex-wrap: wrap;
    padding: 10px 12px;
  }

  nav {
    order: 3;
    width: 100%;
  }
}
</style>
