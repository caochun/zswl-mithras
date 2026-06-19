import { http } from '@zswl/admin'

const mock = false

export const generateToken = (data) =>
  http.post('/qccApi/generateToken', data, {
    mock,
  })

export const getHeadBody = (params) =>
  http.post('/customer/view/detail/headBody', params, { timeout: 0 })

export const getUnifiedViewDetail = (params) =>
  http.post('/client/unified/view/detail', params)

export const getCustomerViewList = (params) => http.post('/client/unified/view/list', params)

export const getCredit = (data) => http.post('/client/unified/view/apply/credit', data)

export const getClassic = (data) =>
  http.post('/client/unified/view/apply/classification', data)

export const getCreditHistory = (data) =>
  http.post('/client/unified/view/apply/credit/history', data)

export const projectStatistics = (params, functionCode) =>
  http.post('/dashboard/project/stage/statistics', params, {
    headers: {
      functionCode,
    },
  })

export const projList = (params) => http.post('/client/unified/view/proj/list', params)

export const contractList = (params) => http.post('/client/unified/view/contract/list', params)

export const selectAll = (params) => http.get('/select/all', params)

export const getExternalList = (params) =>
  http.post('/customer/view/detail/outerRating', params)

export const getRatinghistory = (params) =>
  http.post('/client/unified/view/rating/history', params)

export const getAuthInfoList = (params) => http.post('/htp/auth/info/list', params)

export const saveAuthInfoModify = (params) => http.post('/htp/auth/info/modify', params)

export const deleteAuthInfo = (params) => http.post('/htp/auth/info/remove', params)

export const getPublicEnums = (params) => http.get('/common/dict/enums', params)

export const getAuthFieldList = (params) => http.post('/htp/auth/field/list', params)

export const addAuthField = (params) => http.post('/htp/auth/field/add', params)

export const modifyAuthField = (params) => http.post('/htp/auth/field/modify', params)

export const removeAuthField = (params) => http.post('/htp/auth/field/remove', params)

export const queryLitigation = (params) =>
  http.post('/customer/view/detail/queryLitigation', params)

export const judicialAssistanceDetail = (params) =>
  http.post('/customer/view/detail/judicialAssistanceDetail', params)

export const judgementDocumentDetail = (params) =>
  http.post('/customer/view/detail/judgementDocumentDetail', params)

export const limitHighConsumeDetail = (params) =>
  http.post('/customer/view/detail/limitHighConsumeDetail', params)

export const queryBizRisk = (params) => http.post('/customer/view/detail/queryBizRisk', params)

export const majorTaxDetail = (params) => http.post('/customer/view/detail/majortax', params)

export const guaranteeEventDetail = (params) =>
  http.post('/customer/view/detail/queryGuaranteeEventDetail', params)

export const chattelMortgageDetail = (params) =>
  http.post('/customer/view/detail/queryChattelMortageDetail', params)

export const queryCustomerViewInfo = (params) =>
  http.post('/customer/view/detail/queryCustomerViewInfo', params)

export const queryCompanyBasicInfo = (params) =>
  http.post('/customer/view/detail/queryCompanyBasicInfo', params)

export const queryStockHolders = (params) =>
  http.post('/customer/view/detail/queryStockHolders', params)

export const queryBondBasicInfo = (params) =>
  http.post('/customer/view/detail/queryBondBasicInfo', params)

export const addFileTask = (params) => http.post('/file/task/add', params)

export const modifyFileTask = (params) => http.post('/file/task/modify', params)

export const removeFileTask = (params) => http.post('/file/task/remove', params)

export const getFileTaskList = (params) => http.post('/file/task/list', params)

export const executeFileTask = (params) => http.get(`/file/task/execute?id=${params.id}`, params)

export const getFileTaskDetail = (params) => http.get('/file/task/detail', params)

export const autoMapFileTask = (params) => http.post('/file/task/auto/map', params)

export const getFileTaskLogList = (params) => http.post('/file/task/log/list', params)

export const getOpinionStatistics = (params) =>
  http.post('/customer/view/detail/opiStatistc', params)

export const getOpinionInfo = (params) => http.post('/customer/view/detail/opiInfo', params)

export const getRelation = (params) => http.post('/customer/view/detail/relation', params)

export const getOverdueRent = (params) => http.post('/client/unified/view/overdue/rent', params)

export default {
  generateToken,
  getHeadBody,
  getUnifiedViewDetail,
  getCustomerViewList,
  getCredit,
  getClassic,
  getCreditHistory,
  projectStatistics,
  projList,
  contractList,
  selectAll,
  getExternalList,
  getRatinghistory,
  getAuthInfoList,
  saveAuthInfoModify,
  deleteAuthInfo,
  getPublicEnums,
  getAuthFieldList,
  addAuthField,
  modifyAuthField,
  removeAuthField,
  queryLitigation,
  judicialAssistanceDetail,
  judgementDocumentDetail,
  limitHighConsumeDetail,
  queryBizRisk,
  majorTaxDetail,
  guaranteeEventDetail,
  chattelMortgageDetail,
  queryCustomerViewInfo,
  queryCompanyBasicInfo,
  queryStockHolders,
  queryBondBasicInfo,
  addFileTask,
  modifyFileTask,
  removeFileTask,
  getFileTaskList,
  executeFileTask,
  getFileTaskDetail,
  autoMapFileTask,
  getFileTaskLogList,
  getOpinionStatistics,
  getOpinionInfo,
  getRelation,
  getOverdueRent,
}
