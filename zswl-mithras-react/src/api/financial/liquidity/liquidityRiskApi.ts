/* prettier-ignore-start */
import * as Types from './interface/liquidityRiskApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 流动性指标
  postManageIndex: (data: Types.ManageIndexRequest): Promise<Types.ManageIndexResponse> =>
    http.post('/liquidity/manage/index', data, { mock, timeout: 0 }),

  // 流动性看板
  postManageBoard: (data: Types.ManageBoardRequest): Promise<Types.ManageBoardResponse> =>
    http.post('/liquidity/manage/board', data, { mock, timeout: 0 }),

  // 租金流入
  postRentIncome: (data: Types.RentIncomeRequest): Promise<Types.RentIncomeResponse> =>
    http.post('/liquidity/manage/rent/income', data, { mock, timeout: 0 }),

  // 还本付息
  postManageRepay: (data: Types.ManageRepayRequest): Promise<Types.ManageRepayResponse> =>
    http.post('/liquidity/manage/repay', data, { mock, timeout: 0 }),

  // 错配明细
  postManageMismatch: (data: Types.ManageMismatchRequest): Promise<Types.ManageMismatchResponse> =>
    http.post('/liquidity/manage/mismatch', data, { mock, timeout: 0 }),
}

/* prettier-ignore-end */
