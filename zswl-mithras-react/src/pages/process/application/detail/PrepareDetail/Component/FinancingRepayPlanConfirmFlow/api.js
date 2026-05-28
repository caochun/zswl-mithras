import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/process/prepare/detail/repay/list', params),
  // 还款核销修改
  postWriteOffModify: (params) => http.post('/repayActual/writeOff/modify', params),
  // 还款计划修改
  postPlanModify: (params) => http.post('/repayActual/plan/modify', params),
}
