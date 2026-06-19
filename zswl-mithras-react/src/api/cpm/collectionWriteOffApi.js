import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/collection/list', params),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-4',
        },
      }
    ),
  exportList: (params) =>
    http.post('/collection/list/export', params, {
      type: 'download',
      timeout: 0,
    }),

  getCollectionWriteOffDetail: (params) => http.post('/collection/detail', params),
  postCollectionWriteOffAdd: (params) =>
    http.post('/collection/add/record', params, {
      transformResult: (res) => res.data,
    }),
  postCollectionWriteOffModify: (params) =>
    http.post('/collection/update/record', params, {
      transformResult: (res) => res.data,
    }),
  postCollectionWriteOffDetailModify: (params) =>
    http.post('/collection/modify/record', params, {
      transformResult: (res) => res.data,
    }),
  postCollectionWriteOffList: (params) => http.post('/collection/record/list', params),
  postCollectionWriteoff: (params) =>
    http.post('/collection/writeoff', params, {
      transformResult: (res) => res.data,
    }),
  postCollectionWriteoffUnDo: (params) =>
    http.post('/collection/undowriteoff', params, {
      transformResult: (res) => res.data,
    }),
  postCollectionWriteoffHistory: (params) => http.post('/collection/writeoff/list', params),

  getClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-3',
      },
    }),
  postCollectionWriteoffDetail: (params) => http.post('/collection/record/detail', params),
  postPenaltyInterestModify: (params) =>
    http.post('/collection/update/penaltyInterest/record', params, {
      transformResult: (res) => res.data,
    }),
  postPenaltyInterestRecord: (params) => http.post('/collection/penaltyInterest/detail', params),
  postPenaltyInterestRecordHistory: (params) =>
    http.post('/collection/penaltyInterest/record/list', params, {
      headers: {
        functionCode: 'collectionpenaltyInterestrecordlist',
      },
    }),
}
