import { http } from '@zswl/admin'

export default {
  depostInfo: (id) => http.post(`/contract/depost/depostInfo?id=${id}`),
  // 基本信息
  getBaseInfo: (params) => http.get('/contract/base/info/detail', { params }),
  // 抵扣租金信息列表
  rentList: (params) => http.post('/contract/depost/rentList', params, {}),
  // 抵扣租金信息新增
  rentAdd: (params) => http.post('/contract/depost/rentAdd', params, {}),
  // 抵扣租金信息删除
  rentDel: (params) => http.post('/contract/depost/rentDel', params, {}),
  // 现金流编号
  getFlowNum: (params) => http.post('/contract/depost/getByContractId', params),
  // 现金流编号选中后回显
  getFlowNumDetail: (collectionId) => http.post(`/contract/depost/getByCollectionId?collectionId=${collectionId}`),
  // 保证金退抵信息保存
  refundSchemeSave: (params) => http.post('/contract/depost/depostSave', params, {}),
  // 提交审批
  submit: (params) => http.post('/contract/depost/submit', params, {}),
  // 模板下载
  downloadTemplete: () => http.get('/contract/depost/downloadtemplete'),
  getCurTaskDefKey: (params) => http.post('/filingMaterial/getCurTaskDefKey', params),
}
