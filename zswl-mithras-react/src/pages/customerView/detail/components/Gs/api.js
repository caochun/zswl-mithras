const { http } = require('@zswl/admin')

//外部搜索
export const queryCompanyBasicInfoByKeyword = (params) => {
  return http.post('/customer/view/detail/queryCustomerViewInfo', params)
}
// 工商信息查询
export const queryCompanyBasicInfo = (params) => {
  return http.post('/customer/view/detail/queryCompanyBasicInfo', params)
}
// 主要股东
export const detailqueryStockHolders = (params) => {
  return http.post('/customer/view/detail/queryStockHolders', params)
}

// 发债信息查询
export const deleteDatabase = (params) => {
  return http.post('/customer/view/detail/queryBondBasicInfo', params)
}
