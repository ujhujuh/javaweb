import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import enUs from 'element-plus/dist/locale/en.mjs'
import App from './App.vue'
import router from './router'
import i18n from './i18n'
import { useThemeStore } from './store/theme'

// 引入主题样式
import './styles/themes.css'

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(i18n)

// Element Plus 国际化 - 使用响应式配置
app.use(ElementPlus, {
  locale: zhCn // 默认中文
})

// 初始化主题
app.mount('#app')
const themeStore = useThemeStore()
themeStore.loadTheme()