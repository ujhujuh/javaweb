import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, removeToken, setToken } from '@/utils/auth'
import { getUserInfo, login, logout } from '@/api/auth'

export const useUserStore = defineStore('material-user', () => {
  const token = ref(getToken())
  const userInfo = ref(null)

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

  const logoutAction = async () => {
    if (token.value) {
      await logout()
    }
    token.value = ''
    userInfo.value = null
    removeToken()
  }

  return { token, userInfo, isLogin, loginAction, fetchUserInfo, logoutAction }
})
