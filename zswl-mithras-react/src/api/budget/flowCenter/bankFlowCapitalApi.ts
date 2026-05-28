/* prettier-ignore-start */
import * as Types from './interface/bankFlowCapitalApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 机构\/产品列表
  postOrgList: (data: Types.OrgListRequest): Promise<Types.OrgListResponse> =>
    http.post('/bank/center/finance/org/list', data, { mock }),

  // 根据机构ID查询融资编号
  postCodeList: (data: Types.CodeListRequest): Promise<Types.CodeListResponse> =>
    http.post('/bank/center/finance/code/list', data, { mock }),

  // 根据融资ID和期项和现金流项目查询金额
  postAmountDetail: (data: Types.AmountDetailRequest): Promise<Types.AmountDetailResponse> =>
    http.post('/bank/center/finance/amount/detail', data, { mock }),

  // 根据融资id和现金流项目查期项
  postPhaseList: (data: Types.PhaseListRequest): Promise<Types.PhaseListResponse> =>
    http.post('/bank/center/finance/phase/list', data, { mock }),

  // 现金流项目信息
  postCashflowList: (data: Types.CashflowListRequest): Promise<Types.CashflowListResponse> =>
    http.post('/bank/center/finance/cashflow/list', data, { mock }),

  // 获取子列表信息
  postSubList: (data: Types.SubListRequest): Promise<Types.SubListResponse> =>
    http.post('/bank/center/finance/sub/list', data, { mock }),

  // 融资列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/bank/center/finance/info/list', data, { mock }),

  // 资金端的付款核销
  postPaymentWriteoff: (data) => http.post('/bank/center/finance/payment/writeoff', data, { mock }),

  // 付款现金流项目信息
  postCashFlowList: (data) =>
    http.post('/bank/center/finance/payment/cashflow/list', data, { mock }),
  // /bank/center/finance/repay/split/list
  postRepaySplitList: (data) => http.post('/bank/center/finance/repay/split/list', data, { mock }),
}

/* prettier-ignore-end */
