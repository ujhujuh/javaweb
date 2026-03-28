import request from '@/utils/request'

export function getHomeData() {
  return request({ url: '/portal/news/home', method: 'get' })
}

export function getCategories() {
  return request({ url: '/portal/news/categories', method: 'get' })
}

export function getNewsList(params) {
  return request({ url: '/portal/news/list', method: 'get', params })
}

export function getNewsDetail(id) {
  return request({ url: `/portal/news/detail/${id}`, method: 'get' })
}

export function publishNews(data) {
  return request({ url: '/portal/news/publish', method: 'post', data })
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function likeNews(id) {
  return request({ url: `/portal/news/${id}/like`, method: 'post' })
}

export function unlikeNews(id) {
  return request({ url: `/portal/news/${id}/like`, method: 'delete' })
}

export function favoriteNews(id) {
  return request({ url: `/portal/news/${id}/favorite`, method: 'post' })
}

export function unfavoriteNews(id) {
  return request({ url: `/portal/news/${id}/favorite`, method: 'delete' })
}

export function getMyFavorites(params) {
  return request({ url: '/portal/news/my/favorites', method: 'get', params })
}

export function getMyHistory(params) {
  return request({ url: '/portal/news/my/history', method: 'get', params })
}

export function clearMyHistory() {
  return request({ url: '/portal/news/my/history', method: 'delete' })
}

export function getComments(newsId) {
  return request({ url: `/portal/news/${newsId}/comments`, method: 'get' })
}

export function addComment(newsId, data) {
  return request({ url: `/portal/news/${newsId}/comments`, method: 'post', data })
}
