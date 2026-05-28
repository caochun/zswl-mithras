/* prettier-ignore-start */
import * as Types from './interface/workbenchLittleChartApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 工作台-五级分类饼图
  postPieFivelevel: (data: Types.PieFivelevelRequest): Promise<Types.PieFivelevelResponse> =>
    http.post('/workbench/little/chart/pie/fivelevel', data, { mock }),

  // 工作台-当前角色table列表
  postChartTabs: (data: Types.ChartTabsRequest): Promise<Types.ChartTabsResponse> =>
    http.post('/workbench/little/chart/tabs', data, { mock }),

  // 工作台-融资成本饼图
  postPieFinancecost: (data: Types.PieFinancecostRequest): Promise<Types.PieFinancecostResponse> =>
    http.post('/workbench/little/chart/pie/financecost', data, { mock }),

  // 工作台-雷达图指标
  postChartRadar: (data: Types.ChartRadarRequest): Promise<Types.ChartRadarResponse> =>
    http.post('/workbench/little/chart/radar', data, { mock }),

  // 工作台-项目立项列表
  postListProjestablish: (
    data: Types.ListProjestablishRequest,
  ): Promise<Types.ListProjestablishResponse> =>
    http.post('/workbench/little/chart/list/projestablish', data, { mock }),

  // 工作台-项目评审列表
  postListProjreview: (data: Types.ListProjreviewRequest): Promise<Types.ListProjreviewResponse> =>
    http.post('/workbench/little/chart/list/projreview', data, { mock }),
}

/* prettier-ignore-end */
