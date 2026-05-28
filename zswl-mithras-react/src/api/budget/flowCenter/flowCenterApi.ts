/* prettier-ignore-start */
import * as Types from './interface/flowCenterApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 导出收款业务流水
  postBusinessExport: (data: Types.BusinessExportRequest): Promise<Types.BusinessExportResponse> =>
    http.post('/collection/flow/center/business/export', data, { mock, type: 'download' }),

  // 付款业务流水列表
  postPaymentList: (data: Types.PaymentListRequest): Promise<Types.PaymentListResponse> =>
    http.post('/collection/flow/center/business/payment/list', data, { mock }),

  // 付款业务流水结算明细
  postSettleDetail: (data: Types.SettleDetailRequest): Promise<Types.SettleDetailResponse> =>
    http.post('/collection/flow/center/business/payment/settle/detail', data, { mock }),

  // 付款手工核销
  postManualRecord: (data: Types.ManualRecordRequest): Promise<Types.ManualRecordResponse> =>
    http.post('/collection/flow/center/business/payment/manual/record', data, { mock }),

  // 导出收款业务流水列表详情
  postExportList: (data: Types.ExportListRequest): Promise<Types.ExportListResponse> =>
    http.post('/collection/flow/center/business/export/list', data, { mock }),

  // 收款业务流水列表
  postCollectionList: (data: Types.CollectionListRequest): Promise<Types.CollectionListResponse> =>
    http.post('/collection/flow/center/business/collection/list', data, { mock }),

  // 收款业务流水结算明细
  postCollectionSettleDetail: (
    data: Types.SettleDetailRequest
  ): Promise<Types.SettleDetailResponse> =>
    http.post('/collection/flow/center/business/collection/settle/detail', data, { mock }),

  // 收款手工核销
  postCollectionManualRecord: (
    data: Types.ManualRecordRequest
  ): Promise<Types.ManualRecordResponse> =>
    http.post('/collection/flow/center/business/collection/manual/record', data, { mock }),

  // 流水中心统计
  postCenterCount: (data: Types.CenterCountRequest): Promise<Types.CenterCountResponse> =>
    http.post('/collection/flow/center/count', data, { mock }),
}

/* prettier-ignore-end */
