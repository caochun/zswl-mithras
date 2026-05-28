import { http } from '@zswl/admin'

export default {
  // 获取详情
  getExternalDetail: (params) => http.post('/afterlease/check/external/query/detail', params, {}),
  // 修改外部查询承租人/担保人信息
  modifyClientInfo: (params) =>
    http.post('/afterlease/check/external/query/clientinfo/modify', params, {}),

  // 修改修改查询任务结论
  modifyConclusion: (params) =>
    http.post('/afterlease/check/external/query/modifyConclusion', params, {}),
  // 修改查询任务
  modifyQuery: (params) => http.post('/afterlease/check/external/query/modify', params, {}),
  // 下载报告
  downReport: (params) =>
    http.post('/afterlease/check/external/query/report', params, {
      type: 'download',
      timeout: 0,
    }),
  // 查询任务提交审批
  submitExternal: (params) => http.post('/afterlease/check/external/query/submit', params, {}),
}
