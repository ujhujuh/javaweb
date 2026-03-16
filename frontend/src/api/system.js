import request from '@/utils/request'

// 用户管理API
export const userApi = {
  // 分页查询用户
  list(params) {
    return request({
      url: '/system/user/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询用户
  getById(id) {
    return request({
      url: `/system/user/${id}`,
      method: 'get'
    })
  },

  // 新增用户
  add(data) {
    return request({
      url: '/system/user',
      method: 'post',
      data
    })
  },

  // 修改用户
  update(data) {
    return request({
      url: '/system/user',
      method: 'put',
      data
    })
  },

  // 删除用户
  delete(ids) {
    return request({
      url: `/system/user/${ids.join(',')}`,
      method: 'delete'
    })
  },

  // 重置密码
  resetPwd(data) {
    return request({
      url: '/system/user/resetPwd',
      method: 'put',
      data
    })
  },

  // 修改状态
  changeStatus(data) {
    return request({
      url: '/system/user/changeStatus',
      method: 'put',
      data
    })
  },

  // 角色授权
  authRole(userId, roleIds) {
    return request({
      url: '/system/user/authRole',
      method: 'put',
      params: { userId },
      data: roleIds
    })
  },

  // 导出用户
  export(data) {
    return request({
      url: '/system/user/export',
      method: 'post',
      data,
      responseType: 'blob'
    })
  }
}

// 角色管理API
export const roleApi = {
  // 分页查询角色
  list(params) {
    return request({
      url: '/system/role/list',
      method: 'get',
      params
    })
  },

  // 查询所有角色
  all() {
    return request({
      url: '/system/role/all',
      method: 'get'
    })
  },

  // 根据ID查询角色
  getById(id) {
    return request({
      url: `/system/role/${id}`,
      method: 'get'
    })
  },

  // 新增角色
  add(data) {
    return request({
      url: '/system/role',
      method: 'post',
      data
    })
  },

  // 修改角色
  update(data) {
    return request({
      url: '/system/role',
      method: 'put',
      data
    })
  },

  // 删除角色
  delete(ids) {
    return request({
      url: `/system/role/${ids.join(',')}`,
      method: 'delete'
    })
  },

  // 菜单授权
  authMenu(roleId, menuIds) {
    return request({
      url: '/system/role/authMenu',
      method: 'put',
      params: { roleId },
      data: menuIds
    })
  },

  // 查询角色菜单ID
  getMenuIds(roleId) {
    return request({
      url: `/system/role/menuIds/${roleId}`,
      method: 'get'
    })
  },

  // 分配用户
  authUser(roleId, userIds) {
    return request({
      url: '/system/role/authUser',
      method: 'put',
      params: { roleId },
      data: userIds
    })
  },

  // 查询角色用户
  getUsers(roleId) {
    return request({
      url: `/system/role/users/${roleId}`,
      method: 'get'
    })
  },

  // 导出角色
  export(data) {
    return request({
      url: '/system/role/export',
      method: 'post',
      data,
      responseType: 'blob'
    })
  }
}

// 菜单管理API
export const menuApi = {
  // 查询菜单列表
  list(params) {
    return request({
      url: '/system/menu/list',
      method: 'get',
      params
    })
  },

  // 查询当前用户菜单
  userMenus() {
    return request({
      url: '/system/menu/user',
      method: 'get'
    })
  },

  // 查询菜单树
  tree(params) {
    return request({
      url: '/system/menu/tree',
      method: 'get',
      params
    })
  },

  // 根据ID查询菜单
  getById(id) {
    return request({
      url: `/system/menu/${id}`,
      method: 'get'
    })
  },

  // 新增菜单
  add(data) {
    return request({
      url: '/system/menu',
      method: 'post',
      data
    })
  },

  // 修改菜单
  update(data) {
    return request({
      url: '/system/menu',
      method: 'put',
      data
    })
  },

  // 删除菜单
  delete(id) {
    return request({
      url: `/system/menu/${id}`,
      method: 'delete'
    })
  },

  // 刷新菜单缓存
  refresh() {
    return request({
      url: '/system/menu/refresh',
      method: 'post'
    })
  }
}

// 字典管理API
export const dictApi = {
  // 分页查询字典类型
  typeList(params) {
    return request({
      url: '/system/dict/type/list',
      method: 'get',
      params
    })
  },

  // 查询所有字典类型
  typeAll() {
    return request({
      url: '/system/dict/type/all',
      method: 'get'
    })
  },

  // 根据ID查询字典类型
  getTypeById(id) {
    return request({
      url: `/system/dict/type/${id}`,
      method: 'get'
    })
  },

  // 新增字典类型
  addType(data) {
    return request({
      url: '/system/dict/type',
      method: 'post',
      data
    })
  },

  // 修改字典类型
  updateType(data) {
    return request({
      url: '/system/dict/type',
      method: 'put',
      data
    })
  },

  // 删除字典类型
  deleteType(ids) {
    return request({
      url: `/system/dict/type/${ids.join(',')}`,
      method: 'delete'
    })
  },

  // 分页查询字典数据
  dataList(params) {
    return request({
      url: '/system/dict/data/list',
      method: 'get',
      params
    })
  },

  // 查询字典数据列表
  dataByType(dictType) {
    return request({
      url: `/system/dict/data/list/${dictType}`,
      method: 'get'
    })
  },

  // 根据ID查询字典数据
  getDataById(id) {
    return request({
      url: `/system/dict/data/${id}`,
      method: 'get'
    })
  },

  // 新增字典数据
  addData(data) {
    return request({
      url: '/system/dict/data',
      method: 'post',
      data
    })
  },

  // 修改字典数据
  updateData(data) {
    return request({
      url: '/system/dict/data',
      method: 'put',
      data
    })
  },

  // 删除字典数据
  deleteData(ids) {
    return request({
      url: `/system/dict/data/${ids.join(',')}`,
      method: 'delete'
    })
  }
}

// 参数管理API
export const configApi = {
  // 分页查询参数配置
  list(params) {
    return request({
      url: '/system/config/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询参数配置
  getById(id) {
    return request({
      url: `/system/config/${id}`,
      method: 'get'
    })
  },

  // 新增参数配置
  add(data) {
    return request({
      url: '/system/config',
      method: 'post',
      data
    })
  },

  // 修改参数配置
  update(data) {
    return request({
      url: '/system/config',
      method: 'put',
      data
    })
  },

  // 删除参数配置
  delete(ids) {
    return request({
      url: `/system/config/${ids.join(',')}`,
      method: 'delete'
    })
  },

  // 刷新参数缓存
  refresh() {
    return request({
      url: '/system/config/refresh',
      method: 'delete'
    })
  }
}

// 操作日志API
export const operLogApi = {
  // 分页查询操作日志
  list(params) {
    return request({
      url: '/system/operlog/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询操作日志
  getById(id) {
    return request({
      url: `/system/operlog/${id}`,
      method: 'get'
    })
  },

  // 删除操作日志
  delete(ids) {
    return request({
      url: `/system/operlog/${ids.join(',')}`,
      method: 'delete'
    })
  },

  // 清空操作日志
  clean() {
    return request({
      url: '/system/operlog/clean',
      method: 'delete'
    })
  }
}

// 登录日志API
export const loginLogApi = {
  // 分页查询登录日志
  list(params) {
    return request({
      url: '/system/loginlog/list',
      method: 'get',
      params
    })
  },

  // 根据ID查询登录日志
  getById(id) {
    return request({
      url: `/system/loginlog/${id}`,
      method: 'get'
    })
  },

  // 删除登录日志
  delete(ids) {
    return request({
      url: `/system/loginlog/${ids.join(',')}`,
      method: 'delete'
    })
  },

  // 清空登录日志
  clean() {
    return request({
      url: '/system/loginlog/clean',
      method: 'delete'
    })
  }
}

// 通知公告API
export const noticeApi = {
  // 分页查询公告
  list(params) {
    return request({
      url: '/system/notice/list',
      method: 'get',
      params
    })
  },

  // 查询最新公告
  latest() {
    return request({
      url: '/system/notice/latest',
      method: 'get'
    })
  },

  // 查询用户公告状态
  userStatus() {
    return request({
      url: '/system/notice/user/status',
      method: 'get'
    })
  },

  // 标记公告为已读
  markAsRead(id) {
    return request({
      url: `/system/notice/${id}/read`,
      method: 'put'
    })
  },

  // 根据ID查询公告
  getById(id) {
    return request({
      url: `/system/notice/${id}`,
      method: 'get'
    })
  },

  // 新增公告
  add(data) {
    return request({
      url: '/system/notice',
      method: 'post',
      data
    })
  },

  // 修改公告
  update(data) {
    return request({
      url: '/system/notice',
      method: 'put',
      data
    })
  },

  // 删除公告
  delete(ids) {
    return request({
      url: `/system/notice/${ids.join(',')}`,
      method: 'delete'
    })
  }
}

// 在线用户API
export const onlineApi = {
  // 查询在线用户
  list(params) {
    return request({
      url: '/system/online/list',
      method: 'get',
      params
    })
  },

  // 强退用户
  forceLogout(sessionId) {
    return request({
      url: `/system/online/forceLogout/${sessionId}`,
      method: 'delete'
    })
  }
}

// 用户配置API
export const userConfigApi = {
  // 获取用户配置
  get(configKey) {
    return request({
      url: `/system/user/config/${configKey}`,
      method: 'get'
    })
  },

  // 设置用户配置
  set(configKey, configValue) {
    return request({
      url: '/system/user/config',
      method: 'post',
      data: {
        configKey,
        configValue
      }
    })
  },

  // 删除用户配置
  delete(configKey) {
    return request({
      url: `/system/user/config/${configKey}`,
      method: 'delete'
    })
  }
}

// 美股情绪指标监控API
export const usSentimentApi = {
  // 分页查询美股情绪指标
  list(params) {
    return request({
      url: '/toolbox/us-sentiment/list',
      method: 'get',
      params
    })
  },

  // 查询最新美股情绪指标
  latest() {
    return request({
      url: '/toolbox/us-sentiment/latest',
      method: 'get'
    })
  },

  // 根据ID查询美股情绪指标
  getById(id) {
    return request({
      url: `/toolbox/us-sentiment/${id}`,
      method: 'get'
    })
  },

  // 手动收集美股情绪指标
  collect() {
    return request({
      url: '/toolbox/us-sentiment/collect',
      method: 'post'
    })
  },

  // 删除美股情绪指标
  delete(ids) {
    return request({
      url: `/toolbox/us-sentiment/${ids.join(',')}`,
      method: 'delete'
    })
  }
}

// 文件上传API
export const fileApi = {
  // 上传图片（用于富文本编辑器）
  uploadImage(file) {
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
  },

  // 通用文件上传
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除文件
  delete(bucketName, fileName) {
    return request({
      url: '/file/delete',
      method: 'delete',
      params: {
        bucketName,
        fileName
      }
    })
  }
}