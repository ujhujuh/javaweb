import request from '@/utils/request'

export function getUserMenus() {
  return request({ url: '/system/menu/user', method: 'get' })
}

export function refreshUserPermissions() {
  return request({ url: '/system/menu/refresh', method: 'post' })
}

export function hasPermission(permission) {
  return request({ url: '/system/hasPermission', method: 'get', params: { permission } })
}