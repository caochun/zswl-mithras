/* prettier-ignore-start */
import * as Types from './interface/peerComparisonController'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 获取最新财年
  postComparisonLatestYear: (
    data: Types.ComparisonLatestYearRequest,
  ): Promise<Types.ComparisonLatestYearResponse> =>
    http.post('/peer/comparison/latestYear', data, { mock }),

  // 企业状态
  postComparisonEnterpriseState: (
    data: Types.ComparisonEnterpriseStateRequest,
  ): Promise<Types.ComparisonEnterpriseStateResponse> =>
    http.post('/peer/comparison/enterpriseState', data, { mock }),

  // 同业比较分析
  postPeerComparison: (data: Types.PeerComparisonRequest): Promise<Types.PeerComparisonResponse> =>
    http.post('/peer/comparison', data, { mock }),

  // 明细数据集合
  postComparisonList: (data: Types.ComparisonListRequest): Promise<Types.ComparisonListResponse> =>
    http.post('/peer/comparison/list', data, { mock }),

  // 用户对标企业配置
  postComparisonConfig: (
    data: Types.ComparisonConfigRequest,
  ): Promise<Types.ComparisonConfigResponse> =>
    http.post('/peer/comparison/config', data, { mock }),

  // 财年数据刷新
  postComparisonRefresh: (
    data: Types.ComparisonRefreshRequest,
  ): Promise<Types.ComparisonRefreshResponse> =>
    http.post('/peer/comparison/refresh', data, { mock }),
}

/* prettier-ignore-end */
