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
}