/* prettier-ignore-start */
import * as Types from './interface/riskControlStrategyApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 付款申请拦截
  postPaymentapplyIntercept: (
    data: Types.PaymentapplyInterceptRequest,
  ): Promise<Types.PaymentapplyInterceptResponse> =>
    http.post('/risk/control/strategy/paymentapply/intercept', data, { mock }),

  // 修改预警监控指标
  postStrategyModify: (data: Types.StrategyModifyRequest): Promise<Types.StrategyModifyResponse> =>
    http.post('/risk/control/strategy/modify', data, { mock }),

  // 快照重计算
  postSnapshotRecalculate: (
    data: Types.SnapshotRecalculateRequest,
  ): Promise<Types.SnapshotRecalculateResponse> =>
    http.post('/risk/control/strategy/snapshot/recalculate', data, { mock }),

  // 立项拦截
  postProreviewIntercept: (
    data: Types.ProreviewInterceptRequest,
  ): Promise<Types.ProreviewInterceptResponse> =>
    http.post('/risk/control/strategy/proreview/intercept', data, { mock }),

  // 预警监控指标列表
  postStrategyList: (data: Types.StrategyListRequest): Promise<Types.StrategyListResponse> =>
    http.post('/risk/control/strategy/list', data, { mock }),

  // 预警监控指标详情
  postStrategyDetail: (data: Types.StrategyDetailRequest): Promise<Types.StrategyDetailResponse> =>
    http.post('/risk/control/strategy/detail', data, { mock }),
}

/* prettier-ignore-end */
