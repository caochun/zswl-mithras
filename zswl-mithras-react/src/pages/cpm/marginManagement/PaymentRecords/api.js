import { http } from '@zswl/admin'

export default {
  marginRecordList: (params) => http.post('/margin/record/list', params),
  updateRecord: (params) => http.post('/margin/update/record', params),
  getList: (params) => http.post('/client/list', { scene: 'query', ...params, effected: true }),
  getOurClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-4',
      },
    }),

  getClientBankList: (params) => http.post('/corp/versioned/bank/account/list', params),
  getNormalBankAccountList: (params) => http.post('/normal/bank/account/list', params),

  addRecord: (params) => http.post('/margin/add/record', params),
  // 收款详情
  // marginRecordDetail: (params) => http.post('/margin/record/detail', params),
  marginRecordDetail: (params) => http.post('/margin/collection/detail', params),
}
