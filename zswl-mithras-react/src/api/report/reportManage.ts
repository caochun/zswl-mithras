/* prettier-ignore-start */
import * as Types from './interface/reportManage'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 获取管报列表
  getReportList: (params: Types.ReportListRequest): Promise<Types.ReportListResponse> =>
    http.get('/management/report/list', { params, mock }),
}

/* prettier-ignore-end */
