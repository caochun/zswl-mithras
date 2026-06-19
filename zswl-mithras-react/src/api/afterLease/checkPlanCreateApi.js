import { http } from '@zswl/admin'

export default {
  // 获取部门
  getProjDeptList: (params) =>
    http.post('/afterlease/checkplan/project/listGroupByDept', params, {
      transformResult: (res) => res.data,
    }),
  // 获取季度检查项目列表
  getQuarterProjList: (params) =>
    http.post('/afterlease/checkplan/quarter/project/list', params, {}),
  // 获取非季度检查项目列表
  getnotQuarterProjList: (params) =>
    http.post('/afterlease/checkplan/notquarter/project/list', params, {}),
  // 保存项目列表
  saveProjList: (params) => http.post('/afterlease/checkplan/project/save', params),
  // 移除
  removeProjItem: (params) => http.post('/afterlease/checkplan/project/remove', params, {}),

  // 获取计划详情
  getCheckPlanDetail: (params) => http.post('/afterlease/checkplan/detail', params, {}),
  // 发布计划
  processPublish: (params) => http.post('/afterlease/checkplan/process/publish', params, {}),

  // 取消审批
  processCancel: (params) => http.post('/afterlease/checkplan/cancel', params, {}),
  // 修改检查计划
  modifyCheckPlan: (params) => http.post('/afterlease/checkplan/modify', params, {}),

  // 非季度项目查询
  getProjectQuery: (params) => http.post('/afterlease/checkplan/project/query', params, {}),
  // 新增需检查项目
  addProject: (params) => http.post('/afterlease/checkplan/project/add', params, {}),
  postRiskManagerList: (params) => http.post('/afterlease/checkplan/riskmanager/list', params, {}),
}
