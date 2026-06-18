import { http } from '@zswl/admin'

export default {
  getRelatedList: (params) => http.post('/corp/related/enterprise/list', params),
  addEnterprise: (params) => http.post('/corp/related/enterprise/add', params),
  removeEnterprise: (params) => http.post('/corp/related/enterprise/remove', params),
  editEnterprise: (params) => http.post('/corp/related/enterprise/modify', params),

  //审批流基本信息 变更日志的对比
  getApprovalEnterpriseList: (params) => http.post('/corp/related/enterprise/list/compare', params),
}
