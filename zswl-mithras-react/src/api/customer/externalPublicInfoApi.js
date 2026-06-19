import { http } from '@zswl/admin'

export default {
  getMortgageInfoList: (params) => http.post('/tyc/mortgageInfo/list', params),
  getequityInfoList: (params) => http.post('/tyc/equityInfo/list', params),
  getPunishmentInfoList: (params) => http.post('/tyc/punishmentInfo/list', params),
  getPenaltyList: (params) => http.post('/environment/penalty/list', params),
  addEnvironment: (params) => http.post('/environment/penalty/add', params),
  deleteEnvironment: (params) => http.post('/environment/penalty/remove', params),
  editEnvironment: (params) => http.post('/environment/penalty/modify', params),
  getAbnormalList: (params) => http.post('/tyc/abnormal/list', params),
  getJudicialList: (params) => http.post('/tyc/judicial/list', params),
  getLawSuitList: (params) => http.post('/tyc/lawSuit/list', params),
  getConsumptionRestrictionList: (params) => http.post('/tyc/consumptionRestriction/list', params),
  getZhixingInfoList: (params) => http.post('/tyc/zhixingInfo/list', params),
  getDishonestList: (params) => http.post('/tyc/dishonest/list', params),
  getZhongdengInfoList: (params) => http.post('/zhongdengInfo/list', params),
  addZhongdengInfo: (params) => http.post('/zhongdengInfo/add', params),
  deleteZhongdengInfo: (params) => http.post('/zhongdengInfo/remove', params),
  editZhongdengInfo: (params) => http.post('/zhongdengInfo/modify', params),
  syncExternal: (params) => http.post('/tyc/external/sync', params),
  clientEffect: (params) => http.post('/client/effect', params),
}
