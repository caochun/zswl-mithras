/* prettier-ignore-start */
import * as Types from './interface/ftpMonthlyGuidance'
import { http } from '@zswl/admin'
import moment from 'moment'

const mock = false
// const mock= { mode:2 }
export default {
  // 季度指导模板下载
  postTemplateDownload: (
    data: Types.TemplateDownloadRequest
  ): Promise<Types.TemplateDownloadResponse> =>
    http.post('/ftp/monthly/guidance/template/download', data, {
      mock,
      type: 'download',
      fileName: `月度FTP指导模版.xlsx`,
      transformResult: (res) => res.data,
    }),

  // excel导出
  postExcelExport: (data: Types.ExcelExportRequest): Promise<Types.ExcelExportResponse> =>
    http.post('/ftp/monthly/guidance/excel/export', data, {
      mock,
      type: 'download',
      transformResult: (res) => res.data,
    }),

  // excel导入
  postExcelImport: (data: Types.ExcelImportRequest): Promise<Types.ExcelImportResponse> =>
    http.post('/ftp/monthly/guidance/excel/import', data, {
      mock,
      type: 'upload',
      timeout: 0,
      // transformResult: (res) => res.data,
    }),

  // 提交审批
  postGuidanceSubmit: (data: Types.GuidanceSubmitRequest): Promise<Types.GuidanceSubmitResponse> =>
    http.post('/ftp/monthly/guidance/submit', data, { mock }),

  // 新增月度指导
  postGuidanceAdd: (data: Types.GuidanceAddRequest): Promise<Types.GuidanceAddResponse> =>
    http.post('/ftp/monthly/guidance/add', data, { mock }),

  // 月度指导列表
  postGuidanceList: (data: Types.GuidanceListRequest): Promise<Types.GuidanceListResponse> =>
    http.post('/ftp/monthly/guidance/list', data, { mock }),

  // 月度指导详情
  postGuidanceDetail: (data: Types.GuidanceDetailRequest): Promise<Types.GuidanceDetailResponse> =>
    http.post('/ftp/monthly/guidance/detail', data, { mock }),

  // 月度指导详情-定价指导列表
  postDetailPricing: (data: Types.DetailPricingRequest): Promise<Types.DetailPricingResponse> =>
    http.post('/ftp/monthly/guidance/detail/pricing', data, { mock }),

  // 月度指导详情-计价列表
  postDetailValuation: (
    data: Types.DetailValuationRequest
  ): Promise<Types.DetailValuationResponse> =>
    http.post('/ftp/monthly/guidance/detail/valuation', data, { mock }),

  // 版本比较详情（与上一版本比较）
  postComparePreVersion: (
    data: Types.ComparePreVersionRequest
  ): Promise<Types.ComparePreVersionResponse> =>
    http.post('/ftp/monthly/guidance/compare/preVersion', data, { mock }),

  // 版本表列
  postVersionList: (data: Types.VersionListRequest): Promise<Types.VersionListResponse> =>
    http.post('/ftp/monthly/guidance/version/list', data, { mock }),
}

/* prettier-ignore-end */
