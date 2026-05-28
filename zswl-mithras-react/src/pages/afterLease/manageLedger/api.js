import { http } from '@zswl/admin'

export default {
  // 租后检查管理台账
  postList: (params) => http.post('/afterlease/checkplan/ledger/list', params),
  // 保单台账-列表（excel导出）
  postListExport: (params) =>
    http.post('/policy/ledger/list/export', params, {
      type: 'download',
      timeout: 0,
      // transformResult: (res) => res.data,
    }),
  // 合同保单导出
  postPolicyExport: (params) =>
    http.post('/policy/ledger/contract/policy/export', params, {
      type: 'download',
      timeout: 0,
    }),

  // 工作台保单导出
  postMaintenancePolicyExport: (params) =>
    http.get('/maintenance/policy/proj/export', { params, type: 'download', timeout: 0 }),
  getPaymentDetail: (params) => http.post('/payment/detail', params),
}
