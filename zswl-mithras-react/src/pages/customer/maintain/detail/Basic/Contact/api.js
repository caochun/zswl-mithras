import { http } from '@zswl/admin'

export default {
  addContact: (params) => http.post('/corp/contact/add', params),
  removeContact: (params) => http.post('/corp/contact/remove', params),
  editContact: (params) => http.post('/corp/contact/modify', params),
  getContactList: (params) => http.post('/corp/contact/list', params),

  //审批流基本信息 变更日志的对比
  getApprovalContactList: (params) => http.post('/corp/contact/list/compare', params),
}
