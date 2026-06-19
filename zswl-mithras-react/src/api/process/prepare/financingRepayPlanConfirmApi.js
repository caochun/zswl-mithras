import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/process/prepare/detail/repay/list', params),
  postWriteOffModify: (params) => http.post('/repayActual/writeOff/modify', params),
  postPlanModify: (params) => http.post('/repayActual/plan/modify', params),
}
