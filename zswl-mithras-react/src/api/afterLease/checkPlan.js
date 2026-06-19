import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.post('/process/prepare/detail', params),
  getPageList: (params) => http.post('/afterlease/checkplan/pagelist', params),
  postLedgerList: (params) => http.post('/afterlease/checkplan/ledger/list', params),
  getExternalList: (params) => http.post('/afterlease/check/external/query/list', params),
  getStatistics: (params) =>
    http.post('/afterlease/check/external/query/list/statistics', params, {}),
  addCheckPlan: (params) => http.post('/afterlease/checkplan/add', params),
  closeCheckPlan: (params) => http.post('/afterlease/checkplan/close', params),
  postRiskManagerList: (params) => http.post('/afterlease/checkplan/riskmanager/list', params, {}),
  postCheckPlanClient: (params) => http.post('/afterlease/checkplan/client', params, {}),
  postCheckPlanCommonlyDetail: (params) =>
    http.post('/afterlease/checkplan/commonly/detail', params, {}),
  postCheckChangeRecordList: (params) =>
    http.post('/after/lease/check/change/record/list', params, {}),
  postCheckPlanAssetStrategy: (params) =>
    http.post('/afterlease/checkplan/asset/strategy', params, {}),
  postCheckPlanAssetStrategyModify: (params) =>
    http.post('/afterlease/checkplan/asset/strategy/modify', params, {}),
}
