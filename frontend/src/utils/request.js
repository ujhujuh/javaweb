import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = token
    }

    // 添加语言请求头
    const language = localStorage.getItem('language') || 'zh-CN'
    config.headers['Accept-Language'] = language === 'en-US' ? 'en' : 'zh'

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    // 如果是 blob 类型响应（如导出文件），直接返回 response.data
    if (response.config.responseType === 'blob') {
      return response
    }

    const res = response.data

    // 特殊处理：wangEditor 格式（用于富文本编辑器图片上传）
    // wangEditor 格式: { errno: 0, message: '...', data: {...} }
    if (res.hasOwnProperty('errno')) {
      if (res.errno !== 0) {
        ElMessage.error(res.message || '上传失败')
        return Promise.reject(new Error(res.message || '上传失败'))
      }
      return res
    }

    // 标准格式: { code: 200, message: '...', data: {...} }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        removeToken()
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service