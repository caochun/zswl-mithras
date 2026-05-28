import { http } from '@zswl/admin'

export default {
  backList: (params) => http.post('/margin/record/list', params),
  //新增退款
  addBackRecord: (params) => http.post('/margin/add/back/record', params),
  //新增抵扣
  addDeductRecord: (params) => http.post('/margin/add/deduct/record', params),

  updateRecord: (params) => http.post('/margin/update/record', params),

  getList: (params) => http.post('/client/list', { scene: 'query', ...params, effected: true }),

  getClientBankList: (params) => http.post('/corp/versioned/bank/account/list', params),
  getOurClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-4',
      },
    }),
  getNormalBankAccountList: (params) => http.post('/normal/bank/account/list', params),
  phaseList: (params) => http.post('/margin/phase/select', params),
  phaseInfo: (params) => http.post('/margin/phase/info', params),

  //保证金抵扣记录详情

  // marginDeductDetail: (params) => http.post('/margin/deduct/detail', params),
  // 弹窗
  marginRecordDetail: (params) => http.post('/margin/record/detail', params),
}
