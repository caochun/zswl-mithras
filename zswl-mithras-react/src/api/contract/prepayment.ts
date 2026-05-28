/* prettier-ignore-start */
import * as Types from './interface/prepayment'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改合同-提前还款表
  postPrepaymentModify: (
    data: Types.ContractprepaymentModifyRequest,
  ): Promise<Types.ContractprepaymentModifyResponse> =>
    http.post('/contract/prepayment/modify', data, { mock }),

  // 合同-提前还款表列表
  postPrepaymentList: (
    data: Types.ContractprepaymentListRequest,
  ): Promise<Types.ContractprepaymentListResponse> =>
    http.post('/contract/prepayment/list', data, { mock }),

  // 提前还款表-计算
  postPrepaymentCalculation: (
    data: Types.ContractprepaymentCalculationRequest,
  ): Promise<Types.ContractprepaymentCalculationResponse> =>
    http.post('/contract/prepayment/calculation', data, { mock }),

  // 新增合同-提前还款表
  postPrepaymentAdd: (
    data: Types.ContractprepaymentAddRequest,
  ): Promise<Types.ContractprepaymentAddResponse> =>
    http.post('/contract/prepayment/add', data, { mock }),
}

/* prettier-ignore-end */
