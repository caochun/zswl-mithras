import { http } from '@zswl/admin'

export default {
  //自然人账号信息
  addNormalBankAccount: (params) => http.post('/normal/bank/account/add', params),
  removeNormalBankAccount: (params) => http.post('/normal/bank/account/remove', params),
  editNormalBankAccount: (params) => http.post('/normal/bank/account/modify', params),
  getNormalBankAccountList: (params) => http.post('/normal/bank/account/list', params),
  //法人账号信息
  getBackAccountList: (params) => http.post('/corp/bank/account/list', params),
  addCorpBankAccount: (params) => http.post('/corp/bank/account/add', params),
  removeCorpBankAccount: (params) => http.post('/corp/bank/account/remove', params),
  editCorpBankAccount: (params) => http.post('/corp/bank/account/modify', params),

  //审批流基本信息 变更日志的对比
  getApprovalNormalBankAccountList: (params) =>
    http.post('/normal/bank/account/list/compare', params),
  getApprovalCorpBankAccount: (params) => http.post('/corp/bank/account/list/compare', params),
}
