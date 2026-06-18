import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/assetclassify/client/pagelist', params, {}),
  getBaseInfo: (params) => http.post('/assetclassify/client/detail', params, {}),
  modifyBaseInfo: (params) => http.post('/assetclassify/client/modify', params, {}),
  getQuarterSelect: (params) => http.post('/assetclassify/quarter/select', params, { mock: false }),
  getProcess: (params) => http.post('/assetclassify/grade/process', params, { mock: false }),
  getHistory: (params) => http.post('/assetclassify/client/history', params, { mock: false }),
  getWorkDay: (params) => http.post('/assetclassify/residue/workday', params, { mock: false }),
  getCheckReport: (params) =>
    http.post('/assetclassify/client/check/report', params, { mock: false }),
  modifyCheckReport: (params) =>
    http.post('/assetclassify/client/check/report/modify', params, { mock: false }),
  saveClassifyresult: (params) =>
    http.post('/assetclassify/client/classifyresult/save', params, { mock: false }),
  submitReview: (params) =>
    http.post('/assetclassify/client/review/submit', params, { mock: false }),
  submitReviewCheck: (params) =>
    http.post('/assetclassify/flow/review/submit', params, { mock: false }),
  submitReviewmeeting: (params) =>
    http.post('/assetclassify/process/reviewmeeting/submit', params, { mock: false }),
  submitRiskmeeting: (params) =>
    http.post('/assetclassify/process/riskmeeting/submit', params, { mock: false }),
  submitBoardmeeting: (params) =>
    http.post('/assetclassify/process/boardmeeting/submit', params, { mock: false }),
  postContentGet: (data) => http.post('/afterlease/check/project/report/summary/get', data),
  postContentSave: (data) => http.post('/afterlease/check/project/report/summary/save', data),
  postFileList: (data) => http.post('/file/list', data),
  postGroupFileList: (data) =>
    http.post('/file/list/group', data, {
      headers: {
        functionCode: 'filelistgroupassetclass',
      },
    }),
  postFileRemove: (data) =>
    http.post('/file/remove', data, {
      transformResult: (res) => res.data,
    }),
  postFileUpload: (data) =>
    http.post('/file/upload', data, {
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
    }),
  postReviewFileUpload: (data) =>
    http.post('/file/upload', data, {
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'assetClassifyReviewFileUpload',
      },
    }),
  postFileDown: (data) =>
    http.post('/file/download', data, {
      type: 'download',
    }),
  postFileBatchDown: (data) =>
    http.post('/file/batch/download', data, {
      type: 'download',
    }),
  postRiskFactor: (data) => http.post('/assetclassify/client/risk/factor', data, {}),
  postRiskFactorModify: (data) => http.post('/assetclassify/client/risk/factor/modify', data, {}),
  postWithdrawalRatio: (data) => http.post('/assetclassify/client/withdrawal/ratio', data, {}),
  postWithdrawalRatioModify: (data) =>
    http.post('/assetclassify/client/withdrawal/ratio/modify', data, {}),
  postFileLastVersion: (data) => http.post('/assetclassify/last/version', data, {}),
  firstPart: (data) =>
    http.post('/assetclassify/manual/initial/division', data, {
      timeout: 60000,
    }),
  remove: (data) => http.post('/assetclassify/client/remove', data, {}),
  postExportSummaryFile: (data) =>
    http.post('/assetclassify/summaryfile/download', data, {
      type: 'download',
    }),
  postClientList: (data) => http.post('/assetclassify/client/clientList', data, {}),
  postMidQuarterDivision: (data) =>
    http.post('/assetclassify/manual/initial/midQuarterDivision', data, {}),
}
