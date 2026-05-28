import { http } from '@zswl/admin'

export default {
  /*************** 合同列表 *******************/
  postContractList: (params, functionCode = 'contractbaseinfolist') =>
    http.post('/contract/base/info/list', params, {
      headers: {
        functionCode,
      },
    }),
  removeContract: (params) =>
    http.post('/contract/cancel', params, {
      transformResult: (res) => res.data,
    }),

  /*************** 合同创建 *******************/
  // 项目列表
  postProjList: (params, functionCode = 'contractreviewquery') =>
    http.post('/contract/review/query', params, { headers: { functionCode } }),
  // 创建合同
  postCreateContract: (params) =>
    http.post('/contract/base/info/add', params, {
      // transformResult: (res) => res.data,
    }),
  //  合同变更 状态校验
  changeCheck: (params) =>
    http.post('/contract/flow/change/check', params, {
      transformResult: (res) => res.data,
    }),

  // 报价信息
  getContractQSDetail: (params) => http.post('/contract/price/detail', params),
  // 合同变更、展期、提前还款约束
  changeConstraint: (params) => http.post('/contract/flow/change/constraint', params, {}),

  // 检查是否可进行合同变更
  // candochangeCheck: (params) => http.post('/contract/candochange/check', params),
  //准备进行合同操作
  contractOptPre: (params) => http.post('/contract/operation/prepare', params),
  // 保证金抵扣check
  marginRefundCheck: (params) => http.post('/contract/depost/check', params),
}
