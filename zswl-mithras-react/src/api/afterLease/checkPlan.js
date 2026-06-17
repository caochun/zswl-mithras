import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.post('/process/prepare/detail', params),
  getPageList: (params) => http.post('/afterlease/checkplan/pagelist', params),
  addCheckPlan: (params) => http.post('/afterlease/checkplan/add', params),
  closeCheckPlan: (params) => http.post('/afterlease/checkplan/close', params),
  postRiskManagerList: (params) => http.post('/afterlease/checkplan/riskmanager/list', params, {}),
  postCheckPlanClient: (params) => http.post('/afterlease/checkplan/client', params, {}),
  postCheckPlanCommonlyDetail: (params) =>
    http.post('/afterlease/checkplan/commonly/detail', params, {}),
}
