const { http } = require('@zswl/admin')

// 新增
export const addData = (params) => {
  return http.post('/file/task/add', params)
}
// 修改
export const saveModify = (params) => {
  return http.post('/file/task/modify', params)
}

// 删除
export const deleteDatabase = (params) => {
  return http.post('/file/task/remove', params)
}

// 列表
export const getDatabaseList = (params) => {
  return http.post('/file/task/list', params)
}

// 执行
export const dispatchExecute = (params) => {
  return http.get(`/file/task/execute?id=${params.id}`, params)
}

//详情
export const getDetailTask = (params) => {
  return http.get('/file/task/detail', params)
}

// 数据字段快捷映射
export const autoMap = (params) => {
  return http.post('/file/task/auto/map', params)
}

// 数据字段快捷映射
export const getPublicEnums = (params) => {
  return http.get('/common/dict/enums', params)
}

// 实例列表
export const getInstanceList = (params) => {
  return http.post('/file/task/log/list', params)
}
