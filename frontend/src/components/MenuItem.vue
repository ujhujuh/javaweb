<template>
  <template v-for="menu in menus" :key="menu.id">
    <!-- 有子菜单 -->
    <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path || String(menu.id)">
      <template #title>
        <el-icon v-if="menu.icon"><component :is="getIconComponent(menu.icon)" /></el-icon>
        <span>{{ menu.menuName }}</span>
      </template>
      <MenuItem :menus="menu.children" />
    </el-sub-menu>
    <!-- 没有子菜单 -->
    <el-menu-item v-else :index="formatPath(menu.path)">
      <el-icon v-if="menu.icon"><component :is="getIconComponent(menu.icon)" /></el-icon>
      <span>{{ menu.menuName }}</span>
    </el-menu-item>
  </template>
</template>

<script setup>
import {
  House,
  Setting,
  User,
  Lock,
  Menu as MenuIcon,
  Document,
  Bell,
  Monitor,
  Tickets
} from '@element-plus/icons-vue'

const props = defineProps({
  menus: {
    type: Array,
    default: () => []
  }
})

// 图标映射
const iconMap = {
  'house': House,
  'setting': Setting,
  'user': User,
  'lock': Lock,
  'menu': MenuIcon,
  'document': Document,
  'bell': Bell,
  'monitor': Monitor,
  'tickets': Tickets,
  'system': Setting,
  'peoples': User,
  'tree-table': Document,
  'dict': Document,
  'edit': Setting,
  'message': Bell,
  'form': Document,
  'logininfor': Tickets,
  'online': User,
  'log': Document
}

const formatPath = (path) => {
  if (!path) return path
  // 如果路径不是以 / 开头，添加 /system 前缀
  if (!path.startsWith('/')) {
    return `/system/${path}`
  }
  return path
}

const getIconComponent = (iconName) => {
  return iconMap[iconName] || Document
}
</script>