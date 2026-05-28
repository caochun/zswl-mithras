import { http } from '@zswl/admin'

export default {
  getPaymentDetail: (params) => http.post('/payment/detail', params),
  getPaymentPlanList: (params) => http.post('/payment/list/planeddetail', params),
  getClientBankList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist-1',
      },
    }),
  getPaymentDetailModify: (params) => http.post('/payment/modify', params, {}),
  getPaymentMaterialsList: (params) => http.post('/materials/payment/list', params),
  getPaymentMaterialsDelete: (params) => http.post('/materials/payment/remove', params),
  getPaymentMaterialsUpload: (params, config) =>
    http.post('/materials/payment/upload', params, {
      transformResult: (res) => res.data,
      ...config,
      timeout: 0,
      type: 'upload',
    }),
  getPaymentQuestionnaire: (params) => http.post('/payment/questionnaire/list', params),
  postPaymentQuestionnaireModify: (params) =>
    http.post('/payment/questionnaire/modify', params, {
      transformResult: (res) => res.data,
    }),
  postMaterialsDownload: (params) =>
    http(
      '/materials/download',
      // { ...params },
      {
        params,
        type: 'download',
        fileName: params.filename,
        transformResult: (res) => res,
        timeout: 0,
        headers: {
          functionCode: 'materialsdownload-3',
        },
      }
    ),
  loanDownload: (params) =>
    http.post(
      '/payment/template/loan/download',
      { ...params },
      {
        type: 'download',
        fileName: '放款表底稿.docx',
        transformResult: (res) => res,
        timeout: 0,
      }
    ),
  postMaterialsRemove: (params) =>
    http.post('/materials/payment/remove', params, {
      transformResult: (res) => res.data,
    }),
  postPaymentSubmit: (params) => http.post('/payment/effect', params, {}),

  postLeaseitemCheckrepeat: (params) => http.post('/payment/leaseitem/checkrepeat/get', params, {}),
  getMonitorCount: (params) => http.post('/risk/control/opinion/monitor/count', params, {}),
  paymentTimeOutCheck: (params) => http.post('/payment/projreview/timeout/check', params, {}),
  // /payment/sellerInfo
  getSellerInfo: (params) => http.post('/payment/sellerInfo', params, {}),
  loanReviewFile: (params) => http.post('/payment/loanReviewFile', params, {}),
  loanReviewFileList: (params) => http.post('/payment/loanReviewFile/list', params, {}),
  loanReviewDownloadTemplate: (params) =>
    http.post('/payment/loanReviewFile/download/template ', params, {
      type: 'download',
    }),

  // 校验申请金额
  postPayMentCheckApplyAmount: (params) => http.post('/payment/check/apply/amount', params),
  postAppContractPaySign: (params) => http.post('/app/contract/pay/sign', params),
  postAppContractPaySignUpdate: (params) => http.post('/app/contract/sign/update', params),
  postPaymentCheckClientOpinion: (params) => http.post('/payment/check/client/opinion', params),
  postPaymentFinanceCheckClientOpinion: (params) => http.post('/payment/check/finance/project/distribution', params),
  // 公开信息-检查是否存在客户没有维护公开信息(返回空数组即校验通过)
  publicCheck: (params) => http.post('/public/info/check', params),
  ///proj/review/meet/minute/credit/date/check
  checkCreditDate: (params,functionCode) => http.post('/proj/review/meet/minute/credit/date/check', params,
    {
      headers: {
        functionCode: functionCode||'paymentMeetMinuteCreditDateCheck'
      }
    }
  ),
  // /payment/auto/register
  postPaymentAutoRegister: (params) => http.post('/payment/auto/register', params),
  validateAgreen: (params) => http.post('/proj/review/material/comments/check', params),
}
