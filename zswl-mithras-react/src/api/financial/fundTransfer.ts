/* prettier-ignore-start */
import { http } from '@zswl/admin'
import * as Types from './interface/fundTransfer'

const mock = false
// const mock= { mode:2 }
export default {
  // 监管户待转资金列表展示
  postAccountList: (
    data: Types.FundTransferaccountListRequest
  ): Promise<Types.FundTransferaccountListResponse> =>
    http.post('/fundTransfer/account/list', data, { mock }),

  // 监管户待转资金列表每日情况展示
  postAccountDaily: (
    data: Types.FundTransferaccountDailyRequest
  ): Promise<Types.FundTransferaccountDailyResponse> =>
    http.post('/fundTransfer/account/daily', data, { mock }),

  // 监管户待转资金图表展示
  postGraphList: (
    data: Types.FundTransfergraphListRequest
  ): Promise<Types.FundTransfergraphListResponse> =>
    http.post('/fundTransfer/graph/list', data, { mock }),

  // 监管户待转资金图表当日情况展示
  postCurrentDaily: (
    data: Types.FundTransfercurrentDailyRequest
  ): Promise<Types.FundTransfercurrentDailyResponse> =>
    http.post('/fundTransfer/current/daily', data, { mock }),

  // 监管户待转资金图表每日情况展示
  postGraphDaily: (
    data: Types.FundTransfergraphDailyRequest
  ): Promise<Types.FundTransfergraphDailyResponse> =>
    http.post('/fundTransfer/graph/daily', data, { mock }),

  // 监管户待转资金账户详情
  postDetailList: (
    data: Types.FundTransferdetailListRequest
  ): Promise<Types.FundTransferdetailListResponse> =>
    http.post('/fundTransfer/detail/list', data, { mock }),

  // 账户列表
  postBankAccountList: (
    data: Types.FundTransferbankAccountListRequest
  ): Promise<Types.FundTransferbankAccountListResponse> =>
    http.post('/fundTransfer/bankAccount/list', data, { mock }),

  // 沉淀资金明细列表
  postcurrentDailytList: (
    data: Types.FundTransferbankAccountListRequest
  ): Promise<Types.FundTransferbankAccountListResponse> =>
    http.post('/fundTransfer/account/current/daily', data, { mock }),
}

/* prettier-ignore-end */
