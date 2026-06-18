/* prettier-ignore-start */
import * as Types from './interface/ftpQuarterlyGuidance'
import moment from 'moment'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 季度指导模板下载
  postTemplateDownload: (
    data: Types.TemplateDownloadRequest
  ): Promise<Types.TemplateDownloadResponse> =>
    http.post('/ftp/quarterly/guidance/template/download', data, {
      mock,
      type: 'download',
      fileName: `季度最低收益率指导模版.xlsx`,
      transformResult: (res) => res.data,
    }),

  // excel导出
  postExcelExport: (data: Types.ExcelExportRequest): Promise<Types.ExcelExportResponse> =>
    http.post('/ftp/quarterly/guidance/excel/export', data, {
      mock,
      type: 'download',
      transformResult: (res) => res.data,
    }),

  // excel导入
  postExcelImport: (data: Types.ExcelImportRequest): Promise<Types.ExcelImportResponse> =>
    http.post('/ftp/quarterly/guidance/excel/import', data, {
      mock,
      type: 'upload',
      timeout: 0,
      // transformResult: (res) => res.data,
    }),

  // 季度指导列表
  postGuidanceList: (data: Types.GuidanceListRequest): Promise<Types.GuidanceListResponse> =>
    http.post('/ftp/quarterly/guidance/list', data, { mock }),

  // 季度指导详情
  postGuidanceDetail: (data: { id: number }): Promise<any> =>
    http.post('/ftp/quarterly/guidance/detail', data, { mock }),

  // 季度指导详情-sheet1列表
  postPricingBase: (data: Types.PricingBaseRequest): Promise<Types.PricingBaseResponse> =>
    http.post('/ftp/quarterly/guidance/detail/pricing/base', data, { mock }),

  // 季度指导详情-sheet2-表1-右
  postPricingCuntomer: (
    data: Types.PricingCuntomerRequest
  ): Promise<Types.PricingCuntomerResponse> =>
    http.post('/ftp/quarterly/guidance/detail/pricing/cuntomer', data, { mock }),

  // 季度指导详情-sheet2-表1-左
  postPricingMonth: (data: Types.PricingMonthRequest): Promise<Types.PricingMonthResponse> =>
    http.post('/ftp/quarterly/guidance/detail/pricing/month', data, { mock }),

  // 季度指导详情-sheet2-表2
  postPricingEnterprise: (
    data: Types.PricingEnterpriseRequest
  ): Promise<Types.PricingEnterpriseResponse> =>
    http.post('/ftp/quarterly/guidance/detail/pricing/enterprise', data, { mock }),

  // 提交审批
  postGuidanceSubmit: (data: Types.GuidanceSubmitRequest): Promise<Types.GuidanceSubmitResponse> =>
    http.post('/ftp/quarterly/guidance/submit', data, { mock }),

  // 新增季度指导列表
  postGuidanceAdd: (data: Types.GuidanceAddRequest): Promise<Types.GuidanceAddResponse> =>
    http.post('/ftp/quarterly/guidance/add', data, { mock }),

  // 版本比较详情（与上一版本比较）
  postComparePreVersion: (
    data: Types.ComparePreVersionRequest
  ): Promise<Types.ComparePreVersionResponse> =>
    http.post('/ftp/quarterly/guidance/compare/preVersion', data, { mock }),

  // 版本表列
  postVersionList: (data: Types.VersionListRequest): Promise<Types.VersionListResponse> =>
    http.post('/ftp/quarterly/guidance/version/list', data, { mock }),
}

/* prettier-ignore-end */
