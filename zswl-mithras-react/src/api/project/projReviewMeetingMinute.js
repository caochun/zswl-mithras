import { http } from '@zswl/admin'

const mock = false

export default {
  postInfoModify: (params, functionCode) =>
    http.post('/proj/review/meet/minute/base/info/modify', params, {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteBaseInfoModify',
      },
    }),
  postInfoDetail: (params, functionCode) =>
    http.post('/proj/review/meet/minute/base/info/detail', params, {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteBaseInfoDetail',
      },
    }),
  postRelatedCustomers: (params, functionCode) =>
    http.post('/proj/review/meet/minute/get/related/customers', params, {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteGetRelatedCustomers',
      },
    }),
  postInfoSubmit: (params, functionCode) =>
    http.post('/proj/review/meet/minute/base/info/submit', params, {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteBaseInfoSubmit',
      },
    }),
  postTrackEventList: (params, functionCode) =>
    http.post('/proj/review/meet/minute/trackEvent/list', params, {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteTrackEventList',
      },
    }),
  getTrackEventClose: (params, functionCode) =>
    http.get('/trackEvent/close', params, {
      headers: {
        functionCode: functionCode || 'projReviewTrackEventClose',
      },
    }),
  postCashflowplanUpload: (params, functionCode) =>
    http.post('/proj/review/quotation/proposal/cashflowplan/upload', params, {
      mock,
      type: 'upload',
      headers: {
        functionCode: functionCode || 'projReviewQuotationProposalCashflowplanUpload',
      },
    }),
  postCashflowplanList: (params, functionCode) =>
    http.post('/proj/review/quotation/proposal/cashflowplan/list', params, {
      mock,
      headers: {
        functionCode: functionCode || 'projReviewQuotationProposalCashflowplanList',
      },
    }),
  postRentExport: (params, functionCode) =>
    http.post('/proj/review/quotation/proposal/cashflowplan/rent/export', params, {
      mock,
      type: 'download',
      headers: {
        functionCode: functionCode || 'projReviewQuotationProposalCashflowplanRentExport',
      },
    }),
  postCashflowExport: (params, functionCode) =>
    http.post('/proj/review/quotation/proposal/cashflowplan/cashflow/export', params, {
      mock,
      type: 'download',
      headers: {
        functionCode: functionCode || 'selectorgs-groupCreditEstablish',
      },
    }),
}
