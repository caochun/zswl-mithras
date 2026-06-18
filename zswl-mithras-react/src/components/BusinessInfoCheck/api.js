import { http } from '@zswl/admin'

export default {
  // 合同比对承租人及担保人工商信息
  contractClientBusinessCompare: (params) =>
    http.post('/contract/client/compare/business', params, {}),

  // 付款比对承租人及担保人工商信息
  paymentClientBusinessCompare: (params) =>
    http.post('/payment/client/compare/business', params, {}),

  // 征信查询比对承租人及担保人工商信息
  creditSearchClientBusinessCompare: (params) =>
    http.post('/creditreport/base/creditSearch/client/compare/business', params, {}),

  // 新增客户工商信息处理意见表
  clientBusinessOpinionAdd: (params) => http.post('/client/business/opinion/add', params, {}),
  // 客户工商信息处理意见表列表
  clientBusinessOpinionList: (params) => http.post('/client/business/opinion/list', params, {}),
}
