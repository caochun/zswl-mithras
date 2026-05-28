/* prettier-ignore-start */
import * as Types from './interface/approvalControlApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // \/black\/gray\/approval\/awaiting\/list
  getAwaitingList: (params: Types.AwaitingListRequest): Promise<Types.AwaitingListResponse> =>
    http.get('/black/gray/approval/awaiting/list', { params, mock }),

  // \/black\/gray\/approval\/batchApprove
  postApprovalBatchApprove: (
    data: Types.ApprovalBatchApproveRequest,
  ): Promise<Types.ApprovalBatchApproveResponse> =>
    http.post('/black/gray/approval/batchApprove', data, { mock }),

  // \/black\/gray\/approval\/batchWithdraw
  postApprovalBatchWithdraw: (
    data: Types.ApprovalBatchWithdrawRequest,
  ): Promise<Types.ApprovalBatchWithdrawResponse> =>
    http.post('/black/gray/approval/batchWithdraw', data, { mock }),

  // \/black\/gray\/approval\/list
  getApprovalList: (params: Types.ApprovalListRequest): Promise<Types.ApprovalListResponse> =>
    http.get('/black/gray/approval/list', { params, mock }),

  // 黑灰名单主任务-审批查询
  getTaskAuditList: (params: Types.TaskAuditListRequest): Promise<Types.TaskAuditListResponse> =>
    http.get('/black/gray/approval/manual/task/auditList', { params, mock }),

  // 黑灰名单主任务审批
  postManualTask: (data: Types.ManualTaskRequest): Promise<Types.ManualTaskResponse> =>
    http.post('/black/gray/approval/manual/task', data, { mock }),

  // 黑灰名单入库-审批查询
  getWarehouseAuditList: (
    params: Types.WarehouseAuditListRequest,
  ): Promise<Types.WarehouseAuditListResponse> =>
    http.get('/black/gray/approval/warehouse/auditList', { params, mock }),

  // 黑灰名单出库-审批查询
  getOutboundAuditList: (
    params: Types.OutboundAuditListRequest,
  ): Promise<Types.OutboundAuditListResponse> =>
    http.get('/black/gray/approval/manual/outbound/auditList', { params, mock }),

  // 黑灰名单库入库审批
  postWarehouseSubmit: (
    data: Types.WarehouseSubmitRequest,
  ): Promise<Types.WarehouseSubmitResponse> =>
    http.post('/black/gray/approval/warehouse/submit', data, { mock }),

  // 黑灰名单库出库审批
  postManualOutbound: (data: Types.ManualOutboundRequest): Promise<Types.ManualOutboundResponse> =>
    http.post('/black/gray/approval/manual/outbound', data, { mock }),

  // 黑灰名单库突破审批
  postBreakBusiness: (data: Types.BreakBusinessRequest): Promise<Types.BreakBusinessResponse> =>
    http.post('/black/gray/approval/break/business', data, { mock }),

  // 黑灰名单突破-审批查询
  getBusinessAuditList: (
    params: Types.BusinessAuditListRequest,
  ): Promise<Types.BusinessAuditListResponse> =>
    http.get('/black/gray/approval/break/business/auditList', { params, mock }),
}

/* prettier-ignore-end */
