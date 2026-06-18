/* prettier-ignore-start */
import * as Types from './interface/fundReceiptRepayBaseInfoApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改收付款
  postInfoModify: (data: Types.InfoModifyRequest): Promise<Types.InfoModifyResponse> =>
    http.post('/fund/receipt/repay/base/info/modify', data, { mock }),
  // 直融的详情
  directDetailBaseInfo: (params: any) =>
    http.post('/fund/receipt/repay/base/info/directDetail', params),

  directDetailBaseInfoCompare: (params: any) =>
    http.post('/fund/receipt/repay/base/info/directCompare', params),

  // 获取 financingType
  postFinancingType: (params: any): Promise<any> =>
    http.post('/fund/receipt/repay/base/info/financingType', params),
  // 收付款列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/fund/receipt/repay/base/info/list', data, { mock }),
  // 关闭还款
  postRepayBatchClose: (data: any): Promise<any> =>
    http.post('/file/batch/remove', data, {
      mock,
      headers: {
        functionCode: undefined,
      },
      transformResult: (res) => res.data,
    }),

  // 收付款详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/fund/receipt/repay/base/info/detail', data, { mock }),

  // 收付款详情
  postInfoDetailCompare: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/fund/receipt/repay/base/info/detail/compare', data, { mock }),

  // 质押明细接口
  postPledgeDetail: (data: Types.PledgeDetailRequest): Promise<Types.PledgeDetailResponse> =>
    http.post('/fund/receipt/repay/base/info/pledge/detail', data, { mock }),
  // 质押明细接口
  postPledgeDetailCompare: (data: Types.PledgeDetailRequest): Promise<Types.PledgeDetailResponse> =>
    http.post('/fund/receipt/repay/base/info/pledge/detail/compare', data, { mock }),
}

/* prettier-ignore-end */
