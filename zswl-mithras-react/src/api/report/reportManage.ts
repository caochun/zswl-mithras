/* prettier-ignore-start */
import * as Types from './interface/reportManage'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 获取管报列表
  getReportList: (params: Types.ReportListRequest): Promise<Types.ReportListResponse> =>
    http.get('/management/report/list', { params, mock }),

  // 刷新管报
  getRefresh: (params: any): Promise<any> => http.get('/management/report/refresh', { params, mock }),

  // 是否展示刷新按钮
  showRefreshBtn: (params: any): Promise<any> =>
    http.get('/management/report/showRefreshBtn', { params, mock }),

  // 获取管报分组列表
  getGroupList: (params?: any): Promise<any> =>
    http.get('/management/report/group/list', { params, mock }),
}

/* prettier-ignore-end */
