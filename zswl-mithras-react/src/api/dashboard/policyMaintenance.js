import { http } from '@zswl/admin'

export default {
  // 待维护保单项目列表
  postPolicyProjList: (params) => http.post('/maintenance/policy/proj/list', params, {}),

  // 工作台保单导出
  postMaintenancePolicyExport: (params) =>
    http.get('/maintenance/policy/proj/export', { params, type: 'download', timeout: 0 }),

  // 保单提交
  policySubmit: (params) => http.post('/policy/info/submit', params),
}
