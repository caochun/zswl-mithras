import { http } from '@zswl/admin'

export default {
  getClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-rentCollection',
      },
    }),
  getRentDetail: (params) => http.post('/rent/collection/overdue/rent/detail', params),
  getRentOverdueDetail: (params) => http.post('/rent/collection/overdue/detail', params),
  getRentCollectionDetail: (params) => http.post('/rent/collection/rent/detail', params),
  getCollectionIndexList: (params) => http.post('/rent/collection/index/list', params),
  postEmailSend: (params) =>
    http.post('/rent/collection/email/send', params, {
      transformResult: (res) => res.data,
    }),
  getEmailPreview: (params) => http.post('/rent/collection/email/genHtml', params),
  getEmailDetail: (params) => http.post('/rent/collection/email/detail', params),

  postPenaltyInterestRecordHistory: (params) =>
    http.post('/collection/penaltyInterest/record/list', params, {
      headers: {
        functionCode: 'collectionpenaltyInterestrecordlist-rentCollection',
      },
    }),
  postReductionList: (params) => http.post('/collection/penalty/reduction/info/list', params),
  postCollectionEffect: (params) =>
    http.post('/receipt/collection/effect', params, {
      transformResult: (res) => res.data,
    }),
  postFileSave: (params) =>
    http.post('/file/upload', params, {
      transformResult: (res) => res.data,
      timeout: 0,
      type: 'upload',
    }),
  postFileRemove: (params, functionCode) =>
    http.post('/file/remove', params, {
      headers: {
        functionCode,
      },
      transformResult: (res) => res.data,
    }),
  postFileDownload: (params, functionCode) =>
    http('/file/download', {
      params,
      type: 'download',
      fileName: params.filename,
      transformResult: (res) => res.data,
      timeout: 0,
      headers: {
        functionCode,
      },
    }),
  postReceiptList: (params) => http.post('/receipt/overdue/collection', params),
  postNotice: (params) =>
    http.post('/receipt/collection/notice', params, {
      transformResult: (res) => res.data,
    }),
  modifyDeduction: (params) =>
    http.post('/receipt/collection/modify', params, {
      transformResult: (res) => res.data,
    }),
  postDeductionInterest: (params) => http.post('/receipt/receipt/interest', params),
  emailDown: (params) => http.get('/rent/collection/email/down', { params, type: 'download' }),
}
