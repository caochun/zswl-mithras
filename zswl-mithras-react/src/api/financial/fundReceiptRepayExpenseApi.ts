/* prettier-ignore-start */
import * as Types from './interface/fundReceiptRepayExpenseApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改费用一览表
  postExpenseModify: (data: Types.ExpenseModifyRequest): Promise<Types.ExpenseModifyResponse> =>
    http.post('/fund/receipt/repay/expense/modify', data, { mock }),

  // 费用一览表列表
  postExpenseList: (data: Types.ExpenseListRequest): Promise<Types.ExpenseListResponse> =>
    http.post('/fund/receipt/repay/expense/list', data, { mock }),

  // 费用一览表列表
  postExpenseListCompare: (data: Types.ExpenseListRequest): Promise<Types.ExpenseListResponse> =>
    http.post('/fund/receipt/repay/expense/list/compare', data, { mock }),
}

/* prettier-ignore-end */
