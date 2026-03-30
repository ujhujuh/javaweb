import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import { setToken, getToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(null)
  
  // 初始化语言时进行标准化处理
  const storedLanguage = localStorage.getItem('language')
  const normalizedLanguage = storedLanguage 
    ? (storedLanguage.toLowerCase().includes('en') ? 'en-US' : 'zh-CN')
    : 'zh-CN'
  const language = ref(normalizedLanguage)

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
      // 如果用户有语言偏好，且 localStorage 中没有保存语言，才从后端获取
      if (res.data.language && !localStorage.getItem('language')) {
        const userLang = res.data.language.toLowerCase()
        language.value = userLang.includes('en') ? 'en-US' : 'zh-CN'
        localStorage.setItem('language', language.value)
      }
    }
    return res
  }

  const setLanguage = (lang) => {
    // 标准化语言值
    const langLower = lang.toLowerCase()
    const normalizedLang = langLower.includes('en') ? 'en-US' : 'zh-CN'
    language.value = normalizedLang
    localStorage.setItem('language', normalizedLang)
  }

  return { token, userInfo, language, loginAction, logoutAction, getUserInfoAction, setLanguage }
})