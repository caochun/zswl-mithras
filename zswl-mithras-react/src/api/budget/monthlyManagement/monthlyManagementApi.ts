/* prettier-ignore-start */
import * as Types from './interface/monthlyManagementApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // Excel导出
  postMonthlyDownload: (
    data: Types.MonthlyDownloadRequest,
  ): Promise<Types.MonthlyDownloadResponse> =>
    http.post('/monthly/download', data, { mock, type: 'download' }),

  // 关账校验
  postCloseValidate: (data: Types.CloseValidateRequest): Promise<Types.CloseValidateResponse> =>
    http.post('/monthly/close/validate', data, { mock }),

  // 列表
  postListPage: (data: Types.ListPageRequest): Promise<Types.ListPageResponse> =>
    http.post('/monthly/list/page', data, { mock }),

  // 刷新数据
  postMonthlyFresh: (data: Types.MonthlyFreshRequest): Promise<Types.MonthlyFreshResponse> =>
    http.post('/monthly/fresh', data, { mock }),

  // 印花税计提-资金端
  postFinPage: (data: Types.FinPageRequest): Promise<Types.FinPageResponse> =>
    http.post('/monthly/stampDuty/fin/page', data, { mock }),

  // 印花税计提-项目端
  postProjPage: (data: Types.ProjPageRequest): Promise<Types.ProjPageResponse> =>
    http.post('/monthly/stampDuty/proj/page', data, { mock }),

  // 推送单条数据到苍穹
  postPushSingle: (data: Types.PushSingleRequest): Promise<Types.PushSingleResponse> =>
    http.post('/monthly/push/single', data, { mock }),

  // 提交
  postMonthlySubmit: (data: Types.MonthlySubmitRequest): Promise<Types.MonthlySubmitResponse> =>
    http.post('/monthly/submit', data, { mock }),

  // 收入确认-实际利率法列表
  postAirPage: (data: Types.AirPageRequest): Promise<Types.AirPageResponse> =>
    http.post('/monthly/air/page', data, { mock }),

  // 收入计提-剩余本金法列表
  postRpPage: (data: Types.RpPageRequest): Promise<Types.RpPageResponse> =>
    http.post('/monthly/rp/page', data, { mock }),

  // 新增月结
  postBaseAdd: (data: Types.BaseAddRequest): Promise<Types.BaseAddResponse> =>
    http.post('/monthly/base/add', data, { mock }),

  // 更新单条数据
  postUpdateSingle: (data: Types.UpdateSingleRequest): Promise<Types.UpdateSingleResponse> =>
    http.post('/monthly/update/single', data, { mock }),

  // 更新记录的生效状态
  postUpdateStatus: (data: Types.UpdateStatusRequest): Promise<Types.UpdateStatusResponse> =>
    http.post('/monthly/update/status', data, { mock }),

  // 月结关账
  postMonthlyClose: (data: Types.MonthlyCloseRequest): Promise<Types.MonthlyCloseResponse> =>
    http.post('/monthly/close', data, { mock }),

  // 校验
  postMonthlyValidate: (
    data: Types.MonthlyValidateRequest,
  ): Promise<Types.MonthlyValidateResponse> => http.post('/monthly/validate', data, { mock }),

  // 每月成本计提
  postCostList: (data: Types.CostListRequest): Promise<Types.CostListResponse> =>
    http.post('/monthly/cost/list', data, { mock }),
}

/* prettier-ignore-end */
