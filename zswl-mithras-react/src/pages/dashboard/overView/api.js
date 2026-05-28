import { http } from '@zswl/admin'

export default {
  // 获取当前用户可查看的看板列表
  postDashboardList: (data) => http.post('/dashboard/list', data),
  // 资产余额总览
  postDashboardAssetsBalanceOverview: (data) =>
    http.post('/dashboard/assets/balance/overview', data),
  // 投放总览
  postDashboardAssetsLoanOverview: (data) => http.post('/dashboard/assets/loan/overview', data),

  // 资产和投放明细-按地域（资产地图复用）
  postDashboardAssetsDetailByProvince: (data) =>
    http.post('/dashboard/assets/detail/byprovince', data),

  // 资产和投放明细-下载
  postDashboardAssetsDetailByProvinceExport: (data) =>
    http.post('/dashboard/assets/detail/byprovince/export', data, { type: 'download', timeout: 0 }),

  // 资产和投放明细-按经济圈
  postDashboardAssetsDetailByArea: (data) => http.post('/dashboard/assets/detail/byarea', data),

  // 资产行业分布
  postDashboardDistributionAssetsindustry: (data) =>
    http.post('/dashboard/distribution/assetsindustry', data),

  // 客户部门分布
  postDashboardDistributionClientdepartment: (data) =>
    http.post('/dashboard/distribution/clientdepartment', data),

  // 客户统计
  postDashboardDistributionClientstatistics: (data) =>
    http.post('/dashboard/distribution/clientstatistics', data),

  // 各阶段转化数据
  postDashboardConversionStatistics: (data) => http.post('/dashboard/conversion/statistics', data),

  // 项目运营效率统计
  postDashboardOperationefficiencyStatistics: (data) =>
    http.post('/dashboard/operationefficiency/statistics', data),

  // 项目运营效率明细
  postDashboardOperationefficiencyList: (data) =>
    http.post('/dashboard/operationefficiency/list', data),

  // 资产五级分类统计表
  postDashboardAssetfiveclassifyStatistics: (data) =>
    http.post('/dashboard/assetfiveclassify/statistics', data),

  // 逾期项目信息
  postDashboardOverdueprojectList: (data) => http.post('/dashboard/overdueproject/list', data),
}
