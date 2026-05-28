/* prettier-ignore-start */
import * as Types from './interface/fundReceiptRepayBorrowingApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改借款流入
  postBorrowingModify: (
    data: Types.BorrowingModifyRequest
  ): Promise<Types.BorrowingModifyResponse> =>
    http.post('/fund/receipt/repay/borrowing/modify', data, { mock }),

  // 借款流入列表
  postBorrowingList: (data: Types.BorrowingListRequest): Promise<Types.BorrowingListResponse> =>
    http.post('/fund/receipt/repay/borrowing/list', data, { mock }),
  // 借款流入列表
  postBorrowingListCompare: (
    data: Types.BorrowingListRequest
  ): Promise<Types.BorrowingListResponse> =>
    http.post('/fund/receipt/repay/borrowing/list/compare', data, { mock }),
}

/* prettier-ignore-end */
