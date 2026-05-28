import { http } from '@zswl/admin'

export default {
  submitApproval: (params) => http.post('/contract/flow/effect/submit', { ...params }, {}),
  submitChangeFlow: (params) => http.post('/contract/flow/change/submit', params, {}),
  priceIrrSave: (params) => http.post('/contract/price/irr/save', params, {}),

  // 计算合同平均IRR
  calculateCombinedIrr: (params) =>
    http.post('/contract/rent/combined/irr/calculate', { ...params }, {}),

  // 取消
  cancelFlow: (params) =>
    http.post('/contract/flow/change/cancel', params, {
      // transformResult: (res) => res.data,
    }),

  // 基本信息
  getBaseInfo: (params) => http.get('/contract/base/info/detail', { params }),
  // 基本信息对比
  getBaseInfoCompare: (params) => http.post('/contract/base/info/detail/compare', params),
  // 修改基本
  postBaseInfoModify: (params) => http.post('/contract/base/info/modify', params, {}),
  // 修改变更详情
  postChangeremarkModify: (params) =>
    http.post('/contract/base/info/changeremark/save', params, {}),
  // 检查合同加权平均IRR是否低于最低IRR要求
  checkIrr: (params) => http.post('/contract/operation/checkIrr', params, {}),
}
