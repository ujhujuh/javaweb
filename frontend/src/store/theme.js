import { defineStore } from 'pinia'
import { ref } from 'vue'

// 主题类型枚举
export const ThemeType = {
  DEFAULT: 'default',  // 默认主题（蓝色系）
  DARK: 'dark',        // 暗色主题（深色系）
  FRESH: 'fresh'       // 清新主题（绿色系）
}

// 主题配置
const themeConfig = {
  [ThemeType.DEFAULT]: {
    name: '默认主题',
    icon: 'Sunny',
    description: '经典的蓝色主题，商务专业'
  },
  [ThemeType.DARK]: {
    name: '暗色主题',
    icon: 'Moon',
    description: '现代深色主题，科技感十足'
  },
  [ThemeType.FRESH]: {
    name: '清新主题',
    icon: 'Sunny',
    description: '清新的绿色主题，活力自然'
  }
}

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref(ThemeType.DEFAULT)

  // 切换主题
  const setTheme = (theme) => {
    if (Object.values(ThemeType).includes(theme)) {
      currentTheme.value = theme
      // 保存到 localStorage
      localStorage.setItem('theme', theme)
      // 应用主题到 document
      applyTheme(theme)
    }
  }

  // 应用主题样式到 document
  const applyTheme = (theme) => {
    const html = document.documentElement
    // 移除所有主题类
    html.classList.remove('theme-default', 'theme-dark', 'theme-fresh')
    // 添加当前主题类
    html.classList.add(`theme-${theme}`)
  }

  // 从 localStorage 加载主题
  const loadTheme = () => {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme && Object.values(ThemeType).includes(savedTheme)) {
      currentTheme.value = savedTheme
      applyTheme(savedTheme)
    } else {
      applyTheme(ThemeType.DEFAULT)
    }
  }

  // 获取当前主题配置
  const getCurrentThemeConfig = () => {
    return themeConfig[currentTheme.value]
  }

  // 获取所有主题列表
  const getAllThemes = () => {
    return Object.entries(themeConfig).map(([key, config]) => ({
      key,
      ...config
    }))
  }

  return {
    currentTheme,
    setTheme,
    loadTheme,
    getCurrentThemeConfig,
    getAllThemes
  }
})