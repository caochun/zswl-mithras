/* prettier-ignore-start */
import * as Types from './interface/projMeetingApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 导出现金流表
  postCashflowExport: (data: Types.CashflowExportRequest,functionCode:string): Promise<Types.CashflowExportResponse> =>
    http.post('/proj/review/quotation/proposal/cashflowplan/cashflow/export', data, {
      mock,
      type: 'download',
      headers: {
        functionCode: functionCode
      }
    }),

  // 导出租金表
  postRentExport: (data: Types.RentExportRequest,functionCode:string): Promise<Types.RentExportResponse> =>
    http.post('/proj/review/quotation/proposal/cashflowplan/rent/export', data, {
      mock,
      type: 'download',
      headers: {
        functionCode: functionCode
      }
    }),

  // 上传现金流计划表
  postCashflowplanUpload: (
    data: Types.CashflowplanUploadRequest,
    functionCode:string
  ): Promise<Types.CashflowplanUploadResponse> =>
    http.post('/proj/review/quotation/proposal/cashflowplan/upload', data, {
      mock,
      type: 'upload',
      headers: {
        functionCode: functionCode
      }
    }),

  // 获取现金流计划表
  postCashflowplanList: (
    data: Types.CashflowplanListRequest,
    functionCode:string
  ): Promise<Types.CashflowplanListResponse> =>
    http.post('/proj/review/quotation/proposal/cashflowplan/list', data, { 
      mock,
      headers: {
        functionCode: functionCode
      }
     }),
}

/* prettier-ignore-end */
