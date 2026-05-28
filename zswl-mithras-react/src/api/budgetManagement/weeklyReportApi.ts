/* prettier-ignore-start */
import * as Types from './interface/weeklyReportApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 预算管理-投放计划-项目周报列表
  postReportList: (data: Types.ReportListRequest): Promise<Types.ReportListResponse> =>
    http.post('/budget/plan/pay/weekly/report/list', data, { mock }),

  // 预算管理-预算计划-投放计划-信息
  postReportInfo: (data: Types.ReportInfoRequest): Promise<Types.ReportInfoResponse> =>
    http.post('/budget/plan/pay/weekly/report/info', data, { mock }),
}

/* prettier-ignore-end */
