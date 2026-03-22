import request from '@/utils/request'

// 用户画像API
export const userProfileApi = {
  // 分页查询用户画像
  list(params) {
    return request({
      url: '/userprofile/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询用户画像
  getById(id) {
    return request({
      url: `/userprofile/${id}`,
      method: 'get'
    })
  },

  // 查询用户指定日期的画像
  getByDate(userId, date) {
    return request({
      url: '/userprofile/byDate',
      method: 'get',
      params: {
        userId,
        date
      }
    })
  },

  // 查询用户历史画像列表
  getHistory(userId) {
    return request({
      url: `/userprofile/history/${userId}`,
      method: 'get'
    })
  },

  // 查询用户日期范围内的画像
  getByDateRange(userId, startDate, endDate) {
    return request({
      url: '/userprofile/range',
      method: 'get',
      params: {
        userId,
        startDate,
        endDate
      }
    })
  },

  // 生成用户画像
  generate(userId, date) {
    return request({
      url: '/userprofile/generate',
      method: 'post',
      params: {
        userId,
        date
      }
    })
  },

  // 批量生成用户画像
  batchGenerate(date) {
    return request({
      url: '/userprofile/generate/batch',
      method: 'post',
      params: {
        date
      }
    })
  },

  // 删除用户画像
  delete(id) {
    return request({
      url: `/userprofile/${id}`,
      method: 'delete'
    })
  }
}

// 软件使用记录API
export const softwareUsageApi = {
  // 分页查询软件使用记录
  list(params) {
    return request({
      url: '/userprofile/software/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询软件使用记录
  getById(id) {
    return request({
      url: `/userprofile/software/${id}`,
      method: 'get'
    })
  },

  // 新增软件使用记录
  add(data) {
    return request({
      url: '/userprofile/software',
      method: 'post',
      data
    })
  },

  // 修改软件使用记录
  update(data) {
    return request({
      url: '/userprofile/software',
      method: 'put',
      data
    })
  },

  // 删除软件使用记录
  delete(id) {
    return request({
      url: `/userprofile/software/${id}`,
      method: 'delete'
    })
  },

  // 查询用户指定日期的软件使用记录
  getByUserIdAndDate(userId, date) {
    return request({
      url: '/userprofile/software/byDate',
      method: 'get',
      params: {
        userId,
        date
      }
    })
  }
}

// 文件操作记录API
export const fileOperationApi = {
  // 分页查询文件操作记录
  list(params) {
    return request({
      url: '/userprofile/file/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询文件操作记录
  getById(id) {
    return request({
      url: `/userprofile/file/${id}`,
      method: 'get'
    })
  },

  // 新增文件操作记录
  add(data) {
    return request({
      url: '/userprofile/file',
      method: 'post',
      data
    })
  },

  // 修改文件操作记录
  update(data) {
    return request({
      url: '/userprofile/file',
      method: 'put',
      data
    })
  },

  // 删除文件操作记录
  delete(id) {
    return request({
      url: `/userprofile/file/${id}`,
      method: 'delete'
    })
  },

  // 查询用户指定日期的文件操作记录
  getByUserIdAndDate(userId, date) {
    return request({
      url: '/userprofile/file/byDate',
      method: 'get',
      params: {
        userId,
        date
      }
    })
  }
}

// 浏览器行为记录API
export const browserBehaviorApi = {
  // 分页查询浏览器行为记录
  list(params) {
    return request({
      url: '/userprofile/browser/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询浏览器行为记录
  getById(id) {
    return request({
      url: `/userprofile/browser/${id}`,
      method: 'get'
    })
  },

  // 新增浏览器行为记录
  add(data) {
    return request({
      url: '/userprofile/browser',
      method: 'post',
      data
    })
  },

  // 修改浏览器行为记录
  update(data) {
    return request({
      url: '/userprofile/browser',
      method: 'put',
      data
    })
  },

  // 删除浏览器行为记录
  delete(id) {
    return request({
      url: `/userprofile/browser/${id}`,
      method: 'delete'
    })
  },

  // 查询用户指定日期的浏览器行为记录
  getByUserIdAndDate(userId, date) {
    return request({
      url: '/userprofile/browser/byDate',
      method: 'get',
      params: {
        userId,
        date
      }
    })
  }
}