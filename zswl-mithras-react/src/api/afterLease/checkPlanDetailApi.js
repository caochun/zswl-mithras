import { http } from '@zswl/admin'

export default {
  // 获取计划详情
  getCheckPlanDetail: (params) => http.post('/afterlease/checkplan/detail', params, {}),
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

  // 提交完结审批
  processFinish: (params) => http.post('/afterlease/checkplan/process/finish', params, {}),

  // 一键催办
  postCuiBan: (params) => http.post('/afterlease/checkplan/cuiban', params, {}),

  // 下载指定部门报告
  downDeptReport: (params) =>
    http.get('/afterlease/check/project/report/dept/download', {
      params,
      timeout: 0,
      type: 'download',
    }),

  // 下载指定的项目报告
  downDeptChooseReport: (params) =>
    http.get('/afterlease/check/project/report/batch/download', {
      params,
      type: 'download',
      timeout: 0,
    }),
  // 风控经理
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-1',
      },
    }),
  postRiskManagerList: (params) => http.post('/afterlease/checkplan/riskmanager/list', params, {}),
  postProjectModify: (params) =>
    http.post('/afterlease/checkplan/quarter/project/modify', params, {}),
}
