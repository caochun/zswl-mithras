/* prettier-ignore-start */
import * as Types from './interface/projectCashflow'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 下载现金流计划表模板
  postCashflowplanDownload: (
    data: Types.CashflowplanDownloadRequest
  ): Promise<Types.CashflowplanDownloadResponse> =>
    http.post('/proj/review/cashflowplan/download', data, { mock, type: 'download' }),

  // 导出现金流表
  postCashflowExport: (data: Types.CashflowExportRequest): Promise<Types.CashflowExportResponse> =>
    http.post('/proj/review/cashflowplan/cashflow/export', data, { mock, type: 'download' }),

  // 导出租金表
  postRentExport: (data: Types.RentExportRequest): Promise<Types.RentExportResponse> =>
    http.post('/proj/review/cashflowplan/rent/export', data, { mock, type: 'download' }),

  // 上传现金流计划表
  postCashflowplanUpload: (
    data: Types.CashflowplanUploadRequest
  ): Promise<Types.CashflowplanUploadResponse> =>
    http.post('/proj/review/cashflowplan/upload', data, { mock, type: 'upload', timeout: 0 }),

  // 自动生成现金流计划表
  postCashflowGenerate: (
    data: Types.CashflowGenerateRequest
  ): Promise<Types.CashflowGenerateResponse> =>
    http.post('/proj/review/cashflowplan/cashflow/generate', data, { mock }),

  // 获取现金流计划表
  postCashflowplanList: (
    data: Types.CashflowplanListRequest
  ): Promise<Types.CashflowplanListResponse> =>
    http.post('/proj/review/cashflowplan/list', data, { mock }),

  // 计算IRR
  postIrrCalculate: (data: Types.IrrCalculateRequest): Promise<Types.IrrCalculateResponse> =>
    http.post('/proj/review/cashflowplan/irr/calculate', data, { mock }),
}

/* prettier-ignore-end */
