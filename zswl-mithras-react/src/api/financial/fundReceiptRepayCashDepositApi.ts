/* prettier-ignore-start */
import * as Types from './interface/fundReceiptRepayCashDepositApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 保证金明细列表
  postDepositList: (data: Types.DepositListRequest): Promise<Types.DepositListResponse> =>
    http.post('/fund/receipt/repay/cash/deposit/list', data, { mock }),

  // 修改保证金明细
  postDepositModify: (data: Types.DepositModifyRequest): Promise<Types.DepositModifyResponse> =>
    http.post('/fund/receipt/repay/cash/deposit/modify', data, { mock }),

  // 修改保证金明细
  postDepositListCompare: (
    data: Types.DepositModifyRequest
  ): Promise<Types.DepositModifyResponse> =>
    http.post('/fund/receipt/repay/cash/deposit/list/compare', data, { mock }),
}

/* prettier-ignore-end */
