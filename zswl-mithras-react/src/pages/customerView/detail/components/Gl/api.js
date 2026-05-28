const { http } = require('@zswl/admin')

// list 列表 外层

export const getList = (params) => {
  return http.post('/client/unified/view/list', params)
}
//  获取客户授信信息
export const getCredit = (data) => {
  return http.post('/client/unified/view/apply/credit', data)
}
// 五级分类
export const getClassic = (data) => {
  return http.post('/client/unified/view/apply/classification', data)
}
//获取客户授信历史
export const getCreditHistory = (data) => {
  return http.post('/client/unified/view/apply/credit/history', data)
}

// 项目阶段
export const projectStatistics = (params, functionCode) => {
  return http.post('/dashboard/project/stage/statistics', params, {
    headers: {
      functionCode,
    },
  })
}

// 项目列表
export const projList = (params) => {
  return http.post('/client/unified/view/proj/list', params)
}

// 合同列表
export const contractList = (params) => {
  return http.post('/client/unified/view/contract/list', params)
}
export const selectAll = (params) => {
  return http.get('/select/all', params)
}

// 外部
export const getExternalList = (params) => {
  return http.post('/customer/view/detail/outerRating', params)
}
// 内部
export const getRatinghistory = (params) => {
  return http.post('/client/unified/view/rating/history', params)
}

// 修改
export const saveModify = (params) => {
  return http.post('/htp/auth/info/modify', params)
}

// 删除
export const deleteDatabase = (params) => {
  return http.post('/htp/auth/info/remove', params)
}

// 列表
export const getDatabaseList = (params) => {
  return http.post('/htp/auth/info/list', params)
}

// 通用枚举
export const getPublicEnums = (params) => {
  return http.get('/common/dict/enums', params)
}

// 弹窗 - 数据源列表
export const getDloagDataSourceList = (params) => {
  return http.post('/htp/auth/field/list', params)
}

// 弹窗 - 新增
export const DloagDataSaveAdd = (params) => {
  return http.post('/htp/auth/field/add', params)
}

// 弹窗 - 修改
export const DloagDataSaveModify = (params) => {
  return http.post('/htp/auth/field/modify', params)
}

export const DloagDeleted = (params) => {
  return http.post('/htp/auth/field/remove', params)
}
