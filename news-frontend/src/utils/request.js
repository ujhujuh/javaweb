import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data

    // 兼容 wangEditor 上传响应格式: { errno, message, data }
    if (Object.prototype.hasOwnProperty.call(res, 'errno')) {
      if (res.errno !== 0) {
        ElMessage.error(res.message || '上传失败')
        return Promise.reject(new Error(res.message || '上传失败'))
      }
      return res
    }

    if (res.code !== 200) {
      if (res.code === 401) {
        removeToken()
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
