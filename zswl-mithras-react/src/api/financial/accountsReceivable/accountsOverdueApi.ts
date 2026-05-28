/* prettier-ignore-start */
import * as Types from './interface/accountsOverdueApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改应收逾期结算表
  postSettlementModify: (
    data: Types.SettlementModifyRequest,
  ): Promise<Types.SettlementModifyResponse> =>
    http.post('/finance/overdue/settlement/modify', data, { mock }),

  // 删除应收逾期结算表
  postSettlementRemove: (
    data: Types.SettlementRemoveRequest,
  ): Promise<Types.SettlementRemoveResponse> =>
    http.post('/finance/overdue/settlement/remove', data, { mock }),

  // 应收逾期结算表列表
  postSettlementList: (data: Types.SettlementListRequest): Promise<Types.SettlementListResponse> =>
    http.post('/finance/overdue/settlement/list', data, { mock }),

  // 推送应收逾期结算
  postSettlementPush: (data: Types.SettlementPushRequest): Promise<Types.SettlementPushResponse> =>
    http.post('/finance/overdue/settlement/push', data, { mock }),

  // 新增应收逾期结算表
  postSettlementAdd: (data: Types.SettlementAddRequest): Promise<Types.SettlementAddResponse> =>
    http.post('/finance/overdue/settlement/add', data, { mock }),

  // 逾期查询客户下合同信息
  postContractRelation: (
    data: Types.ContractRelationRequest,
  ): Promise<Types.ContractRelationResponse> =>
    http.post('/finance/overdue/settlement/contract/relation', data, { mock }),
}

/* prettier-ignore-end */
