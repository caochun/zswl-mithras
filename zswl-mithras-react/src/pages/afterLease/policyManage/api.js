import { http } from '@zswl/admin'

export default {
  // 合同管理-资料清单列表
  postContractList: (data) => http.post('/kpi/proj/guess/contract/list', data),

  // 保单台账-合同信息
  postContractDetail: (params) => http.post('/policy/ledger/contract/detail', params),
  // 保单台账-保单详情
  postLengerDetail: (params) => http.post('/policy/ledger/detail', params),
  // 保单台账-合同保单信息
  postContractPolicy: (params) => http.post('/policy/ledger/contract/policy', params),

  // 保单台账-保单信息列表
  postList: (params) => http.post('/policy/ledger/list', params),
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
