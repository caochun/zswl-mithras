import { http } from '@zswl/admin'

export default {
  // 收入确认-实际利率法列表
  postMonthlyAirPage: (params) => http.post('/monthly/air/page', params),
  // 收入计提-剩余本金法列表
  postMonthlyRpPage: (params) => http.post('/monthly/rp/page', params),
  // 印花税计提-资金端
  postMonthlyStampDutyFinPage: (params) => http.post('/monthly/stampDuty/fin/page', params),
  // 印花税计提-项目端
  postMonthlyStampDutyProjPage: (params) => http.post('/monthly/stampDuty/proj/page', params),
  // Excel导出
  postMonthlyDownload: (params) =>
    http.post('/monthly/download', params, { type: 'download', timeout: 0 }),
  // 提交
  postMonthlySubmit: (params) => http.post('/monthly/submit', params),
  // 计提成本
  postMonthlyCostList: (params) => http.post('/monthly/cost/list', params, { timeout: 0 }),
  // 列表
  postMonthlyList: (params) => http.post('/monthly/list/page', params),
  // 校验
  postMonthlyValidate: (params) => http.post('/monthly/validate', params),
}
