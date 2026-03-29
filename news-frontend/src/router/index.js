import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue') },
  { path: '/list', component: () => import('@/views/NewsList.vue') },
  { path: '/detail/:id', component: () => import('@/views/NewsDetail.vue') },
  { path: '/publish', component: () => import('@/views/Publish.vue'), meta: { auth: true, permission: 'news:content:list' } },
  { path: '/login', component: () => import('@/views/Login.vue') },
  { path: '/register', component: () => import('@/views/Register.vue') },
  { path: '/profile', component: () => import('@/views/Profile.vue'), meta: { auth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.auth && !getToken()) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }

  if (to.meta.permission) {
    if (userStore.permissions.length === 0) {
      await userStore.fetchUserMenus()
    }
    if (!userStore.hasPermission(to.meta.permission)) {
      ElMessage.error('您没有权限访问该页面')
      next('/')
      return
    }
  }

  next()
})

export default router
