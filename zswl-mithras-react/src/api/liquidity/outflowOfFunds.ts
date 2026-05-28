/* prettier-ignore-start */
import * as Types from './interface/outflowOfFunds'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 压力测试-流出
  postOutflowList: (data: Types.OutflowListRequest): Promise<Types.OutflowListResponse> =>
    http.post('/stress/testing/outflow/list', data, { mock }),

  // 现金流流出明细-导出
  postOutflowExport: (data: Types.OutflowExportRequest): Promise<Types.OutflowExportResponse> =>
    http.post('/cash/outflow/export', data, { mock, type: 'download' }),

  // 现金流流出明细-资产端
  postAssetsOutflowList: (data: Types.OutflowListRequest): Promise<Types.OutflowListResponse> =>
    http.post('/assets/cash/outflow/list', data, { mock }),

  // 现金流流出明细-资金端
  postFundsOutflowList: (data: Types.OutflowListRequest): Promise<Types.OutflowListResponse> =>
    http.post('/funds/cash/outflow/list', data, { mock }),

  // 预估现金流流出
  postEstimateOutflowList: (data: Types.OutflowListRequest): Promise<Types.OutflowListResponse> =>
    http.post('/estimate/cash/outflow/list', data, { mock }),
}

/* prettier-ignore-end */
