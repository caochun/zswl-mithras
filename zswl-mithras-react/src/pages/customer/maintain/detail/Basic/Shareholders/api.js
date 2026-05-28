import { http } from '@zswl/admin'

export default {
  getShareholderList: (params) => http.post('/corp/shareholder/info/list', params),
  addShareholder: (params) => http.post('/corp/shareholder/info/add', params),
  removeShareholder: (params) => http.post('/corp/shareholder/info/remove', params),
  editShareholder: (params) => http.post('/corp/shareholder/info/modify', params),

  //审批流基本信息 变更日志的对比
  getApprovalShareholderList: (params) => http.post('/corp/shareholder/info/list/compare', params),
}
