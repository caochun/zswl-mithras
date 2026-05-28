import { http } from '@zswl/admin'

export default {
  getBondList: (params) => http.post('/corp/bond/info/list', params),
  addBondInfo: (params) => http.post('/corp/bond/info/add', params),
  removeBondInfo: (params) => http.post('/corp/bond/info/remove', params),
  editBondInfo: (params) => http.post('/corp/bond/info/modify', params),
  //审批流基本信息 变更日志的对比
  getApprovalBondList: (params) => http.post('/corp/bond/info/list/compare', params),
}
