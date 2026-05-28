import { http } from '@zswl/admin'

export default {
  // 待维护保单项目列表
  postPolicyProjList: (params) => http.post('/maintenance/policy/proj/list', params, {}),

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
    }),
  // 合同保单导出
  postPolicyExport: (params) =>
    http.post('/policy/ledger/contract/policy/export', params, {
      type: 'download',
      timeout: 0,
    }),

  // 保单台账-保单信息列表
  postList: (params) => http.post('/policy/ledger/list', params),

  // 工作台保单导出
  postMaintenancePolicyExport: (params) =>
    http.get('/maintenance/policy/proj/export', { params, type: 'download', timeout: 0 }),
  getPaymentDetail: (params) => http.post('/payment/detail', params),

  policySubmit: (params) => http.post('/policy/info/submit', params),
}
