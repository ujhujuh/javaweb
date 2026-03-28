import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/User.vue')
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('@/views/system/Role.vue')
      },
      {
        path: '/system/menu',
        name: 'Menu',
        component: () => import('@/views/system/Menu.vue')
      },
      {
        path: '/system/dict',
        name: 'Dict',
        component: () => import('@/views/system/Dict.vue')
      },
      {
        path: '/system/config',
        name: 'Config',
        component: () => import('@/views/system/Config.vue')
      },
      {
        path: '/system/notice',
        name: 'Notice',
        component: () => import('@/views/system/Notice.vue')
      },
      {
        path: '/system/operlog',
        name: 'OperLog',
        component: () => import('@/views/system/OperLog.vue')
      },
      {
        path: '/system/loginlog',
        name: 'LoginLog',
        component: () => import('@/views/system/LoginLog.vue')
      },
      {
        path: '/system/online',
        name: 'Online',
        component: () => import('@/views/system/Online.vue')
      },
      {
        path: '/toolbox/us-sentiment',
        name: 'UsSentiment',
        component: () => import('@/views/toolbox/UsSentiment.vue')
      },
      {
        path: '/news/content',
        name: 'NewsContent',
        component: () => import('@/views/news/Content.vue')
      },
      {
        path: '/news/category',
        name: 'NewsCategory',
        component: () => import('@/views/news/Category.vue')
      },
      {
        path: '/news/comment',
        name: 'NewsComment',
        component: () => import('@/views/news/Comment.vue')
      },
      {
        path: '/news/interaction',
        name: 'NewsInteraction',
        component: () => import('@/views/news/Interaction.vue')
      },
      {
        path: '/sale/material',
        name: 'SaleMaterial',
        component: () => import('@/views/sale/Material.vue')
      },
      {
        path: '/sale/category',
        name: 'SaleCategory',
        component: () => import('@/views/sale/Category.vue')
      },
      {
        path: '/sale/order',
        name: 'SaleOrder',
        component: () => import('@/views/sale/Order.vue')
      },
      {
        path: '/userprofile',
        name: 'UserProfile',
        component: () => import('@/views/userprofile/UserProfile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
