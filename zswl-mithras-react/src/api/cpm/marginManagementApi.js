import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/margin/list', params),
  exportList: (params) =>
    http.post('/margin/list/export', params, {
      type: 'download',
      timeout: 0,
    }),
  marginDetail: (params) => http.post('/margin/detail', params),

  marginRecordList: (params) => http.post('/margin/record/list', params),
  backList: (params) => http.post('/margin/record/list', params),
  writeoffList: (params) => http.post('/margin/writeoff/list', params),
  updateRecord: (params) => http.post('/margin/update/record', params),
  addRecord: (params) => http.post('/margin/add/record', params),
  addBackRecord: (params) => http.post('/margin/add/back/record', params),
  addDeductRecord: (params) => http.post('/margin/add/deduct/record', params),
  marginRecordDetail: (params) => http.post('/margin/record/detail', params),
  marginCollectionDetail: (params) => http.post('/margin/collection/detail', params),

  getListClient: (params) => http.post('/client/list', { scene: 'query', ...params, effected: true }),
  getOurClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-4',
      },
    }),
  getClientBankList: (params) => http.post('/corp/versioned/bank/account/list', params),
  getNormalBankAccountList: (params) => http.post('/normal/bank/account/list', params),
  phaseList: (params) => http.post('/margin/phase/select', params),
  phaseInfo: (params) => http.post('/margin/phase/info', params),
}
