/* prettier-ignore-start */
import * as Types from './interface/workbenchChartMetricApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 工作台-不良项目列表
  postUndesirableProj: (
    data: Types.UndesirableProjRequest,
  ): Promise<Types.UndesirableProjResponse> =>
    http.post('/workbench/chart/list/undesirable/proj', data, { mock }),

  // 工作台-各行业存量本金柱状图
  postChartStockprincipal: (
    data: Types.ChartStockprincipalRequest,
  ): Promise<Types.ChartStockprincipalResponse> =>
    http.post('/workbench/chart/stockprincipal', data, { mock }),

  // 工作台-大可视化新增投放表格
  postListLaunch: (data: Types.ListLaunchRequest): Promise<Types.ListLaunchResponse> =>
    http.post('/workbench/chart/list/launch', data, { mock }),

  // 工作台-大可视化新增评审表格
  postListReview: (data: Types.ListReviewRequest): Promise<Types.ListReviewResponse> =>
    http.post('/workbench/chart/list/review', data, { mock }),

  // 工作台-当前角色table列表
  postChartTabs: (data: Types.ChartTabsRequest): Promise<Types.ChartTabsResponse> =>
    http.post('/workbench/chart/tabs', data, { mock }),

  // 工作台-本年\/本月新增立项列表
  postYearProj: (data: Types.YearProjRequest): Promise<Types.YearProjResponse> =>
    http.post('/workbench/chart/list/year/proj', data, { mock }),

  // 工作台-本年\/本月累计投放金额列表
  postYearPayment: (data: Types.YearPaymentRequest): Promise<Types.YearPaymentResponse> =>
    http.post('/workbench/chart/list/year/payment', data, { mock }),

  // 工作台-本年新增投放客户列表
  postYearClient: (data: Types.YearClientRequest): Promise<Types.YearClientResponse> =>
    http.post('/workbench/chart/list/year/client', data, { mock }),

  // 工作台-本年新增项目评审列表
  postYearReview: (data: Types.YearReviewRequest): Promise<Types.YearReviewResponse> =>
    http.post('/workbench/chart/list/year/review', data, { mock }),

  // 工作台-柱状图指标
  postChartBar: (data: Types.ChartBarRequest): Promise<Types.ChartBarResponse> =>
    http.post('/workbench/chart/bar', data, { mock }),

  // 工作台-资金流动性分析曲线图
  postChartFundsliquidity: (
    data: Types.ChartFundsliquidityRequest,
  ): Promise<Types.ChartFundsliquidityResponse> =>
    http.post('/workbench/chart/fundsliquidity', data, { mock }),

  // 工作台-逾期项目列表
  postOverdueProj: (data: Types.OverdueProjRequest): Promise<Types.OverdueProjResponse> =>
    http.post('/workbench/chart/list/overdue/proj', data, { mock }),

  // 工作台-项目投放\/回款情况曲线图
  postChartReleasecollection: (
    data: Types.ChartReleasecollectionRequest,
  ): Promise<Types.ChartReleasecollectionResponse> =>
    http.post('/workbench/chart/releasecollection', data, { mock }),

  // 工作台-项目整体收益率曲线图（行业对比）
  postProjecttypeReturnrate: (
    data: Types.ProjecttypeReturnrateRequest,
  ): Promise<Types.ProjecttypeReturnrateResponse> =>
    http.post('/workbench/chart/projecttype/returnrate', data, { mock }),

  // 工作台-项目整体收益率曲线图（部门对比）
  postDeptReturnrate: (data: Types.DeptReturnrateRequest): Promise<Types.DeptReturnrateResponse> =>
    http.post('/workbench/chart/dept/returnrate', data, { mock }),
}

/* prettier-ignore-end */
