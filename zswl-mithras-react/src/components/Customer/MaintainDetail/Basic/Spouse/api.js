import { http } from '@zswl/admin'

export default {
  //配偶信息
  getSpouseList: (params) => http.post('/normal/spouse/detail', params),
  deleteSpouse: (params) => http.post('/normal/spouse/remove', params),
  editSpouse: (params) => http.post('/normal/spouse/modify', params),
  spouseSelect: (params) => http.post('/normal/spouse/select', params),
  addSpouse: (params) => http.post('/normal/spouse/add', params),
  naturalDetail: (params) => http.post('/normal/base/info/detail', params),

  //审批流基本信息 变更日志的对比
  getApprovalSpouseList: (params) => http.post('/normal/spouse/detail/compare', params),
}
