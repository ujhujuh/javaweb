import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { login, logout, getUserInfo } from '@/api/auth'
import { getUserMenus } from '@/api/system'

export const useUserStore = defineStore('news-user', () => {
  const token = ref(getToken())
  const userInfo = ref(null)
  const permissions = ref([])
  const menus = ref([])

  const isLogin = () => !!token.value

  const loginAction = async (form) => {
    const res = await login(form)
    token.value = res.data.token
    userInfo.value = res.data.user
    setToken(res.data.token)
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    const res = await getUserInfo()
    userInfo.value = res.data
  }

  const fetchUserMenus = async () => {
    if (!token.value) return
    try {
      const res = await getUserMenus()
      menus.value = res.data || []
      permissions.value = extractPermissions(menus.value)
    } catch (error) {
      console.error('获取用户权限失败:', error)
      permissions.value = []
      menus.value = []
    }
  }

  const refreshPermissions = async () => {
    if (!token.value) return
    try {
      const { refreshUserPermissions } = await import('@/api/system')
      await refreshUserPermissions()
      await fetchUserMenus()
      console.log('权限刷新成功')
    } catch (error) {
      console.error('刷新权限失败:', error)
    }
  }

  const extractPermissions = (menuList) => {
    const perms = []
    const traverse = (menus) => {
      menus.forEach(menu => {
        if (menu.perms) {
          perms.push(menu.perms)
        }
        if (menu.children && menu.children.length > 0) {
          traverse(menu.children)
        }
      })
    }
    traverse(menuList)
    return perms
  }

  const hasPermission = (permission) => {
    if (!permission) return true
    return permissions.value.includes(permission)
  }

  const logoutAction = async () => {
    if (token.value) {
      await logout()
    }
    token.value = ''
    userInfo.value = null
    permissions.value = []
    menus.value = []
    removeToken()
  }

  return {
    token,
    userInfo,
    permissions,
    menus,
    isLogin,
    loginAction,
    fetchUserInfo,
    fetchUserMenus,
    hasPermission,
    refreshPermissions,
    logoutAction
  }
})
