import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/assetclassify/client/pagelist', params, {}),
  getBaseInfo: (params) => http.post('/assetclassify/client/detail', params, {}),
  modifyBaseInfo: (params) => http.post('/assetclassify/client/modify', params, {}),

  // 季度选择
  getQuarterSelect: (params) => http.post('/assetclassify/quarter/select', params, { mock: false }),
  // 定级流程
  getProcess: (params) => http.post('/assetclassify/grade/process', params, { mock: false }),
  // 审批历史
  getHistory: (params) => http.post('/assetclassify/client/history', params, { mock: false }),

  //流程剩余工作日
  getWorkDay: (params) => http.post('/assetclassify/residue/workday', params, { mock: false }),

  // 检查内容
  getCheckReport: (params) =>
    http.post('/assetclassify/client/check/report', params, { mock: false }),

  // 修改检查内容
  modifyCheckReport: (params) =>
    http.post('/assetclassify/client/check/report/modify', params, { mock: false }),

  // 保存客户分类结果
  saveClassifyresult: (params) =>
    http.post('/assetclassify/client/classifyresult/save', params, { mock: false }),

  //发起单个客户复合审批
  submitReview: (params) =>
    http.post('/assetclassify/client/review/submit', params, { mock: false }),

  // 发起复合流程
  submitReviewCheck: (params) =>
    http.post('/assetclassify/flow/review/submit', params, { mock: false }),

  // 发起项目评审会流程
  submitReviewmeeting: (params) =>
    http.post('/assetclassify/process/reviewmeeting/submit', params, { mock: false }),
  // 发起风委会流程
  submitRiskmeeting: (params) =>
    http.post('/assetclassify/process/riskmeeting/submit', params, { mock: false }),
  // 发起董事会流程
  submitBoardmeeting: (params) =>
    http.post('/assetclassify/process/boardmeeting/submit', params, { mock: false }),

  // 获取项目检查报告检查内容
  postContentGet: (data) => http.post('/afterlease/check/project/report/summary/get', data),
  postContentSave: (data) => http.post('/afterlease/check/project/report/summary/save', data),

  // 文件
  postFileList: (data) => http.post('/file/list', data),
  postGroupFileList: (data) =>
    http.post('/file/list/group', data, {
      headers: {
        functionCode: 'filelistgroupassetclass',
      },
    }),
  postFileRemove: (data) => {
    return http.post('/file/remove', data, {
      transformResult: (res) => res.data,
    })
  },
  postFileUpload: (data) =>
    http.post('/file/upload', data, {
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
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
  postClientList: (data) =>
    http.post('/assetclassify/client/clientList', data, {
    }),
  postMidQuarterDivision: (data) =>
    http.post('/assetclassify/manual/initial/midQuarterDivision', data, {
    }),
}
