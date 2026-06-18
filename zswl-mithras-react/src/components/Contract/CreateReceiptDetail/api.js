import { http } from '@zswl/admin'

export default {
  //  新增借据
  submitStart: (params) => http.post('/contract/flow/receipt/submit', params, {}),

  // 新增借据
  addReceipt: (params) =>
    http.post('/contract/rent/actual/import', params, {
      type: 'upload',
      timeout: 0,
    }),
  // 取消
  cancelFlow: (params) => http.post('/contract/flow/receipt/cancel', params, {}),
  //付款申请-获取没有关联借据的付款申请
  getPaymentList: (params) =>
    http.post('/payment/noreceipt/list', params, {
      transformResult: (res) => res.data,
    }),

  //编辑区-更改借据的实际起租日期
  updateEditActualStartDate: (params) =>
    http.post('/contract/receipt/edit/updateActualStartDate', params, {}),

  //流程中-更改借据的实际起租日
  updateProcessActualStartDate: (params) =>
    http.post('/contract/receipt/process/updateActualStartDate', params, {}),
  // 检查合同加权平均IRR是否低于最低IRR要求
  checkIrr: (params) => http.post('/contract/operation/checkIrr', params, {}),
}
