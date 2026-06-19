import { http } from '@zswl/admin'

export default {
  postPaymentPolicyList: (params) => http.post('/payment/policy/info/list', params),
  postPolicyLedgerRenewInsurance: (params) => http.post('/policy/ledger/renew/insurance', params),
  postPaymentPolicyModify: (params) =>
    http.post('/payment/policy/info/modify', params, {
      type: 'upload',
      timeout: 0,
    }),
  postPaymentPolicyAdd: (params) =>
    http.post('/payment/policy/info/add', params, {
      type: 'upload',
      timeout: 0,
    }),
  postPaymentPolicyRemove: (params) => http.post('/payment/policy/info/remove', params, {}),
  postPaymentPolicyImport: (params, uploadConfig) =>
    http.post('/payment/policy/import', params, { type: 'upload', ...uploadConfig, timeout: 0 }),
  postChecked: (params) => http.post('/payment/policy/info/modify/flag', params, {}),
  postPolicyProjList: (params) => http.post('/maintenance/policy/proj/list', params, {}),
  postPolicyList: (params) => http.post('/policy/info/list', params),
  postPolicyModify: (params) =>
    http.post('/policy/info/modify', params, {
      type: 'upload',
      timeout: 0,
    }),
  postPolicyAdd: (params) =>
    http.post('/policy/info/add', params, {
      type: 'upload',
      timeout: 0,
    }),
  postPolicyRemove: (params) => http.post('/policy/info/remove', params, {}),
  postPolicyImport: (params, uploadConfig) =>
    http.post('/policy/import', params, { type: 'upload', ...uploadConfig, timeout: 0 }),
  postPaymentRemoveBatch: (params) => http.post('/payment/policy/info/removeBatch', params, {}),
  postPaymentPolicyBatchExport: (params) =>
    http('/payment/policy/info/export', {
      params,
      type: 'download',
      timeout: 0,
    }),
}
