import { http } from '@zswl/admin'

export default {
  // 预算管理-预算考核
  getExamineList: (params) => http.post('/budget/examine/list', params),
  getExamineAdd: (params) => http.post('/budget/examine/add', params),
  getExamineDetail: (params) => http.post('/budget/examine/detail', params),
  getExamineModify: (params) => http.post('/budget/examine/modify', params),
  getExamineRemove: (params) => http.post('/budget/examine/remove', params),
  // 预算管理-预算考核-效益考核表
  getExamineBenefitList: (params) => http.post('/budget/examine/benefit/list', params),
  getExamineBenefitAdd: (params) => http.post('/budget/examine/benefit/add', params),
  getExamineBenefitModify: (params) => http.post('/budget/examine/benefit/modify', params),
  getExamineBenefitRemove: (params) => http.post('/budget/examine/benefit/remove', params),

  // 预算管理-预算考核-预算执行情况表
  getExamineExecuteModify: (params) => http.post('/budget/examine/budget/execute/modify', params),
  getExamineExecuteAdd: (params) => http.post('/budget/examine/budget/execute/add', params),
  getExamineExecuteRemove: (params) => http.post('/budget/examine/budget/execute/remove', params),
  getExamineExecuteList: (params) => http.post('/budget/examine/budget/execute/list', params),

  // 预算管理-预算考核-投放计划执行情况表列表
  getExamineBenefitRemove: (params) => http.post('/budget/examine/pay/plan/execute/list', params),
  postExamineSubmit: (params) => http.post('/budget/examine/submit', params),
  // 预算管理-预算考核-投放计划执行情况表
  getExaminePayPlanExecuteList: (params) =>
    http.post('/budget/examine/pay/plan/execute/list', params),
  getExaminePayPlanExecuteModify: (params) =>
    http.post('/budget/examine/pay/plan/execute/modify', params),
  getExaminePayPlanExecuteAdd: (params) =>
    http.post('/budget/examine/pay/plan/execute/add', params),
  getExaminePayPlanExecuteRemove: (params) =>
    http.post('/budget/examine/pay/plan/execute/remove', params),

  // /budget/examine/detail
  postExamineDetail: (params) => http.post('/budget/examine/detail', params),
}
