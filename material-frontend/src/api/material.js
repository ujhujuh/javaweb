import request from '@/utils/request'

export function getCategories() {
  return request({ url: '/portal/material/categories', method: 'get' })
}

export function getMaterialList(params) {
  return request({ url: '/portal/material/list', method: 'get', params })
}

export function getMaterialDetail(id) {
  return request({ url: `/portal/material/detail/${id}`, method: 'get' })
}

export function createOrder(data) {
  return request({ url: '/portal/material/orders/create', method: 'post', data })
}

export function payOrder(orderNo, data) {
  return request({ url: `/portal/material/orders/${orderNo}/pay`, method: 'post', data })
}

export function getMyOrders(params) {
  return request({ url: '/portal/material/orders/my', method: 'get', params })
}

export function getMyPurchased(params) {
  return request({ url: '/portal/material/purchased', method: 'get', params })
}
