/* prettier-ignore-start */
import * as Types from './interface/fundDayReportApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 日结指标
  postIndicatorList: (data: Types.IndicatorListRequest): Promise<Types.IndicatorListResponse> =>
    http.post('/fundDayReport/indicator/list', data, { mock }),

  // 租金流入
  postRentIncome: (data: Types.RentIncomeRequest): Promise<Types.RentIncomeResponse> =>
    http.post('/fundDayReport/rent/income', data, { mock }),

  // 账户余额
  postAccountBalance: (data: Types.AccountBalanceRequest): Promise<Types.AccountBalanceResponse> =>
    http.post('/fundDayReport/account/balance', data, { mock }),

  // 还本付息
  postRepayPrincipalInterest: (
    data: Types.RepayPrincipalInterestRequest,
  ): Promise<Types.RepayPrincipalInterestResponse> =>
    http.post('/fundDayReport/repay/principalInterest', data, { mock }),
}

/* prettier-ignore-end */
