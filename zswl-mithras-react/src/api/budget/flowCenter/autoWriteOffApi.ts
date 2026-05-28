/* prettier-ignore-start */
import * as Types from './interface/autoWriteOffApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // \/capital\/write\/off\/release\/bank\/flow
  postBankFlow: (data: Types.ReleasebankFlowRequest): Promise<Types.ReleasebankFlowResponse> =>
    http.post('/capital/write/off/release/bank/flow', data, { mock }),

  // 修改业务流水
  postBusinessFlowUpdate: (
    data: Types.UpdatebusinessFlowRequest
  ): Promise<Types.UpdatebusinessFlowResponse> =>
    http.post('/capital/write/off/update/business/flow', data, { mock }),

  // 删除业务流水
  postBusinessFlowDelete: (
    data: Types.DeletebusinessFlowRequest
  ): Promise<Types.DeletebusinessFlowResponse> =>
    http.post('/capital/write/off/delete/business/flow', data, { mock }),

  // 删除单个Tab
  postDeleteTab: (data: Types.OffdeleteTabRequest): Promise<Types.OffdeleteTabResponse> =>
    http.post('/capital/write/off/delete/tab', data, { mock }),

  // 删除流水
  postBankFlowDelete: (data: Types.DeletebankFlowRequest): Promise<Types.DeletebankFlowResponse> =>
    http.post('/capital/write/off/delete/bank/flow', data, { mock }),

  // 增加流水
  postBankFlowAdd: (data: Types.AddbankFlowRequest): Promise<Types.AddbankFlowResponse> =>
    http.post('/capital/write/off/add/bank/flow', data, { mock }),

  // 导入流水前的校验
  postBeforeImport: (
    data: Types.CheckbeforeImportRequest
  ): Promise<Types.CheckbeforeImportResponse> =>
    http.post('/capital/write/off/check/before/import', data, { mock }),

  // 手动核销
  postWriteOff: (data: Types.ManualwriteOffRequest): Promise<Types.ManualwriteOffResponse> =>
    http.post('/capital/write/off/manual/write/off', data, { mock, timeout: 0 }),

  // 新增业务流水
  postBusinessFlowAdd: (
    data: Types.AddbusinessFlowRequest
  ): Promise<Types.AddbusinessFlowResponse> =>
    http.post('/capital/write/off/add/business/flow', data, { mock }),

  // 获取单个Tab信息
  postSingleTab: (data: Types.OffsingleTabRequest): Promise<Types.OffsingleTabResponse> =>
    http.post('/capital/write/off/single/tab', data, { mock }),

  // 选择流水之后的排序结果
  postMatchResult: (data: Types.FlowmatchResultRequest): Promise<Types.FlowmatchResultResponse> =>
    http.post('/capital/write/off/flow/match/result', data, { mock }),

  // 重新匹配单个Tab的信息
  postRematchTab: (data: Types.OffrematchTabRequest): Promise<Types.OffrematchTabResponse> =>
    http.post('/capital/write/off/rematch/tab', data, { mock }),
}

/* prettier-ignore-end */
