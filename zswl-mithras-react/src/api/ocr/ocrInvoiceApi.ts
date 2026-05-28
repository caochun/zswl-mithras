/* prettier-ignore-start */
import * as Types from './interface/ocrInvoiceApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 增值税发票上传
  postVatInvoiceUpload: (
    data: Types.VatInvoiceUploadRequest,
    { onUploadProgress }
  ): Promise<Types.VatInvoiceUploadResponse> =>
    http.post('/lease/vatInvoice/upload', data, {
      mock,
      type: 'upload',
      timeout: 0,
      onUploadProgress,
    }),

  // 增值税发票下载
  postVatInvoiceExportExcel: (
    data: Types.VatInvoiceExportExcelRequest
  ): Promise<Types.VatInvoiceExportExcelResponse> =>
    http.post('/lease/vatInvoice/exportExcel', data, { mock, type: 'download' }),

  // 增值税发票分页列表
  postVatInvoiceList: (data: Types.VatInvoiceListRequest): Promise<Types.VatInvoiceListResponse> =>
    http.post('/lease/vatInvoice/list', data, { mock }),

  // 增值税发票批量修改
  postVatInvoiceUpdate: (
    data: Types.VatInvoiceUpdateRequest
  ): Promise<Types.VatInvoiceUpdateResponse> =>
    http.post('/lease/vatInvoice/update', data, { mock }),

  // 增值税发票批量删除
  postVatInvoiceDelete: (
    data: Types.VatInvoiceDeleteRequest
  ): Promise<Types.VatInvoiceDeleteResponse> =>
    http.post('/lease/vatInvoice/delete', data, { mock }),

  // 增值税发票重新上传
  postVatInvoiceAnewUpload: (
    data: Types.VatInvoiceAnewUploadRequest,
    { onUploadProgress }
  ): Promise<Types.VatInvoiceAnewUploadResponse> =>
    http.post('/lease/vatInvoice/anewUpload', data, {
      mock,
      type: 'upload',
      timeout: 0,
      onUploadProgress,
    }),

  // 增值税发票重新验真
  postVatInvoiceRetest: (
    data: Types.VatInvoiceRetestRequest
  ): Promise<Types.VatInvoiceRetestResponse> =>
    http.post('/lease/vatInvoice/retest', data, { mock }),

  // 增值税发票金额校验
  postVatInvoiceAmountCheckout: (
    data: Types.VatInvoiceAmountCheckoutRequest
  ): Promise<Types.VatInvoiceAmountCheckoutResponse> =>
    http.post('/lease/vatInvoice/amountCheckout', data, { mock }),

  // 统计各状态发票数量
  postVatInvoiceCount: (
    data: Types.VatInvoiceCountRequest
  ): Promise<Types.VatInvoiceCountResponse> => http.post('/lease/vatInvoice/count', data, { mock }),
  //  /lease/ocr/fileNameComparison
  // 增值税发票文件名对比
  postVatInvoiceFileNameComparison: (data) =>
    http.post('/lease/ocr/fileNameComparison', data, { mock }),
}

/* prettier-ignore-end */
