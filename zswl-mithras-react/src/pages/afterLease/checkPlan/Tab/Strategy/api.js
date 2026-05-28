import { http } from '@zswl/admin'

export default {
  getPageList: (params) => http.post('/afterlease/checkplan/pagelist', params),
  addCheckPlan: (params) => http.post('/afterlease/checkplan/add', params),
  closeCheckPlan: (params) => http.post('/afterlease/checkplan/close', params),
  postRiskManagerList: (params) => http.post('/afterlease/checkplan/riskmanager/list', params, {}),
  postCheckChangeRecordList: (params) =>
    http.post('/after/lease/check/change/record/list', params, {}),
  postCheckPlanAssetStrategy: (params) =>
    http.post('/afterlease/checkplan/asset/strategy', params, {}),
  postCheckPlanAssetStrategyModify: (params) =>
    http.post('/afterlease/checkplan/asset/strategy/modify', params, {}),
}
