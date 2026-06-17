import { http } from '@zswl/admin'

export default {
  postAllocationExport: (params) =>
    http.post('/kpi/projectdistribution/export', params, {
      type: 'download',
      timeout: 0,
    }),

  postProjectdistributionGetProcess: (params) =>
    http.post('/kpi/projectdistribution/baseinfo/get/process', params, {}),

  // 绩效考核-项目分配-基本信息-比对
  postProjectdistributionCompare: (params) =>
    http.post('/kpi/projectdistribution/compare', params, {}),
  // /kpi/projectdistribution/prev
  postProjectPrev: (params) => http.post('/kpi/projectdistribution/prev', params, {}),

  // 历史部门分润比
  postDeptWeightPrev: (params) =>
    http.post('/kpi/project/distribution/dept/weight/prev', params, {}),
  // 历史部门投放分配比
  postDeptLaunchWeightPrev: (params) =>
    http.post('/kpi/project/distribution/dept/weight/launchPrev', params, {}),
}
