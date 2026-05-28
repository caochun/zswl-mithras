/* prettier-ignore-start */
import * as Types from './interface/thirdCanqiongApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 付款记录明细
  postPaymentRecord: (data: Types.PaymentRecordRequest): Promise<Types.PaymentRecordResponse> =>
    http.post('/third/payment/record', data, { mock }),

  // 保证金管理-内扣\/退回
  postMarginRecord: (data: Types.MarginRecordRequest): Promise<Types.MarginRecordResponse> =>
    http.post('/third/margin/record', data, { mock }),

  // 收款记录明细
  postCollectionRecord: (
    data: Types.CollectionRecordRequest,
  ): Promise<Types.CollectionRecordResponse> =>
    http.post('/third/collection/record', data, { mock }),

  // 苍穹接口撤回
  postFinancialWithdraw: (
    data: Types.FinancialWithdrawRequest,
  ): Promise<Types.FinancialWithdrawResponse> =>
    http.post('/third/financial/withdraw', data, { mock }),
}

/* prettier-ignore-end */
