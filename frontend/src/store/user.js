import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import { setToken, getToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(null)

  const loginAction = async (loginForm) => {
    const res = await login(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.user
    setToken(res.data.token)
  }

  const logoutAction = async () => {
    await logout()
    token.value = ''
    removeToken()
    userInfo.value = null
  }

  const getUserInfoAction = async () => {
    const res = await getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
    }
    return res
  }

  return { token, userInfo, loginAction, logoutAction, getUserInfoAction }
})