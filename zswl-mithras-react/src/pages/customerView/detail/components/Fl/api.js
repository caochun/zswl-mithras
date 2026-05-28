const { http } = require('@zswl/admin')

// 法律诉讼
export const getDatabaseList = (params) => {
  return http.post('/customer/view/detail/queryLitigation', params)
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
