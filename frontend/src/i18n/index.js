import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS
}

// 标准化语言值
const storedLanguage = localStorage.getItem('language')
const normalizedLanguage = storedLanguage 
  ? (storedLanguage.toLowerCase().includes('en') ? 'en-US' : 'zh-CN')
  : 'zh-CN'

// 确保localStorage中存储的是标准化后的值
if (storedLanguage !== normalizedLanguage) {
  localStorage.setItem('language', normalizedLanguage)
}

const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: normalizedLanguage, // 默认语言
  fallbackLocale: 'zh-CN', // 回退语言
  messages
})

export default i18n