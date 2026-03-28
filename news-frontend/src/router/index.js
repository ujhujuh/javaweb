import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue') },
  { path: '/list', component: () => import('@/views/NewsList.vue') },
  { path: '/detail/:id', component: () => import('@/views/NewsDetail.vue') },
  { path: '/publish', component: () => import('@/views/Publish.vue'), meta: { auth: true } },
  { path: '/login', component: () => import('@/views/Login.vue') },
  { path: '/register', component: () => import('@/views/Register.vue') },
  { path: '/profile', component: () => import('@/views/Profile.vue'), meta: { auth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.auth && !getToken()) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }
  next()
})

export default router
