/* prettier-ignore-start */
import * as Types from './interface/policyLedgerApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 保单台账-保单信息列表
  postLedgerList: (data: Types.LedgerListRequest): Promise<Types.LedgerListResponse> =>
    http.post('/policy/ledger/list', data, { mock }),

  // 保单台账-保单详情
  postLedgerDetail: (data: Types.LedgerDetailRequest): Promise<Types.LedgerDetailResponse> =>
    http.post('/policy/ledger/detail', data, { mock }),

  // 保单台账-列表（excel导出）
  postListExport: (data: Types.ListExportRequest): Promise<Types.ListExportResponse> =>
    http.post('/policy/ledger/list/export', data, { mock, type: 'download' }),

  // 保单台账-合同保单信息
  postContractPolicy: (data: Types.ContractPolicyRequest): Promise<Types.ContractPolicyResponse> =>
    http.post('/policy/ledger/contract/policy', data, { mock }),

  // 保单台账-合同保单信息导出
  postPolicyExport: (data: Types.PolicyExportRequest): Promise<Types.PolicyExportResponse> =>
    http.post('/policy/ledger/contract/policy/export', data, { mock, type: 'download' }),

  // 保单台账-合同信息
  postContractDetail: (data: Types.ContractDetailRequest): Promise<Types.ContractDetailResponse> =>
    http.post('/policy/ledger/contract/detail', data, { mock }),

  // 保单台账-确认保单信息
  postTmpSync: (data: Types.TmpSyncRequest): Promise<Types.TmpSyncResponse> =>
    http.post('/policy/ledger/tmp/sync', data, { mock }),

  // 待维护保单项目列表
  postProjList: (data: Types.ProjListRequest): Promise<Types.ProjListResponse> =>
    http.post('/maintenance/policy/proj/list', data, { mock }),

  // 待维护保单项目导出
  getProjExport: (params: Types.ProjExportRequest): Promise<Types.ProjExportResponse> =>
    http.get('/maintenance/policy/proj/export', { params, mock, type: 'download' }),
}

/* prettier-ignore-end */
