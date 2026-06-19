import { http } from '@zswl/admin'

export default {
  //  提交审核
  submitStart: (params) => http.post('/contract/flow/start/submit', params, {}),
  // 取消
  cancelFlow: (params) => http.post('/contract/flow/start/cancel', params, {}),
  // 更新合同实际起租日期
  updateDate: (params) => http.post('/contract/base/actualleasedate/update', params, {}),
   // 检查合同加权平均IRR是否低于最低IRR要求
  checkIrr: (params) => http.post('/contract/operation/checkIrr', params, {}),
}
