/* prettier-ignore-start */
import * as Types from './interface/businessFundApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 业务流水-资金端-保存核销明细
  postDetailSave: (data: Types.DetailSaveRequest): Promise<Types.DetailSaveResponse> =>
    http.post('/business/flow/finance/detail/save', data, { mock }),

  // 业务流水-资金端-核销明细列表
  postDetailList: (data: Types.DetailListRequest): Promise<Types.DetailListResponse> =>
    http.post('/business/flow/finance/detail/list', data, { mock }),

  // 业务流水-资金端列表
  postFinanceList: (data: Types.FinanceListRequest): Promise<Types.FinanceListResponse> =>
    http.post('/business/flow/finance/list', data, { mock }),

  // 导出业务流水-资金端列表
  postFinanceListExport: (data: Types.FinanceListRequest): Promise<any> =>
    http.post('/business/flow/finance/list/export', data, { mock, type: 'download', timeout: 0 }),

  // 业务流水-资金端-人工核销完毕推送单据
  postManualPush: (data: Types.FinanceListRequest): Promise<Types.FinanceListResponse> =>
    http.post('/business/flow/finance/manual/push', data, { mock }),
}

/* prettier-ignore-end */
