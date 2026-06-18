/* prettier-ignore-start */
import * as Types from './interface/workbenchCardMetricApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 工作台-卡片指标配置
  getMetricConfig: (params: Types.MetricConfigRequest): Promise<Types.MetricConfigResponse> =>
    http.get('/workbench/card/metric/config', { params, mock }),

  // 工作台-当前用户角色列表
  getMetricListrole: (params: Types.MetricListroleRequest): Promise<Types.MetricListroleResponse> =>
    http.get('/workbench/metric/listrole', { params, mock }),

  // 工作台-卡片指标列表
  postMetricList: (data: Types.MetricListRequest): Promise<Types.MetricListResponse> =>
    http.post('/workbench/card/metric/list', data, { mock }),

  // 工作台-卡片指标配置
  postConfigModify: (data: Types.ConfigModifyRequest): Promise<Types.ConfigModifyResponse> =>
    http.post('/workbench/card/metric/config/modify', data, { mock }),
}

/* prettier-ignore-end */
