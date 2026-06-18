/* prettier-ignore-start */
import * as Types from './interface/newFtpBaseInfoApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // ftp主表列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/new/ftp/base/info/list', data, { mock }),

  // ftp文字描述信息列表
  postInfoDesclist: (data: Types.InfoDesclistRequest): Promise<Types.InfoDesclistResponse> =>
    http.post('/new/ftp/base/info/desclist', data, { mock }),

  // ftp文字描述信息更新
  postInfoDescmodify: (data: Types.InfoDescmodifyRequest): Promise<Types.InfoDescmodifyResponse> =>
    http.post('/new/ftp/base/info/descmodify', data, { mock }),

  // ftp计算
  postInfoCalculate: (data: Types.InfoCalculateRequest): Promise<Types.InfoCalculateResponse> =>
    http.post('/new/ftp/base/info/calculate', data, { mock }),

  // 提交审批
  postInfoSubmit: (data: Types.InfoSubmitRequest): Promise<Types.InfoSubmitResponse> =>
    http.post('/new/ftp/base/info/submit', data, { mock }),
  // 主表信息
  postInfoDetail: (data: Types.InfoSubmitRequest): Promise<Types.InfoSubmitResponse> =>
    http.post('/new/ftp/base/info/detail', data, { mock }),

  // 新增ftp主表
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/new/ftp/base/info/add', data, { mock }),
  // 导出
  postPricingExport: (params: Types.InfoSubmitRequest): Promise<any> =>
    http('/new/ftp/base/pricing/downLoad', { params, mock, type: 'download' }),
  // 测试版本比对
  postInfoTestCompare: (
    data: Types.InfoTestCompareRequest
  ): Promise<Types.InfoTestCompareResponse> =>
    http.post('/new/ftp/base/info/testCompare', data, { mock }),

  // 测试生成版本
  postInfoTestProcessEnd: (
    data: Types.InfoTestProcessEndRequest
  ): Promise<Types.InfoTestProcessEndResponse> =>
    http.post('/new/ftp/base/info/testProcessEnd', data, { mock }),

  // 版本日志
  postInfoVersions: (data: Types.InfoVersionsRequest): Promise<Types.InfoVersionsResponse> =>
    http.post('/new/ftp/base/info/versions', data, { mock }),

  // 版本比对
  postFtpPreVersion: (data: Types.FtpPreVersionRequest): Promise<Types.FtpPreVersionResponse> =>
    http.post('/new/ftp/preVersion', data, { mock }),

  // 流程详情页面内比对接口-季度指导
  postCompareQuarterly: (
    data: Types.CompareQuarterlyRequest
  ): Promise<Types.CompareQuarterlyResponse> =>
    http.post('/new/ftp/flow/detail/compare/quarterly', data, { mock }),

  // 流程详情页面内比对接口-月度指导
  postCompareMonthly: (data: Types.CompareMonthlyRequest): Promise<Types.CompareMonthlyResponse> =>
    http.post('/new/ftp/flow/detail/compare/monthly', data, { mock }),
  // 流程详情页面内比对接口-描述信息
  postCompareDescription: (
    data: Types.CompareMonthlyRequest
  ): Promise<Types.CompareMonthlyResponse> =>
    http.post('/new/ftp/flow/detail/compare/description', data, { mock }),
  // 流程详情页面内比对接口-月度推导表
  postCompareDeduction: (
    data: Types.CompareMonthlyRequest
  ): Promise<Types.CompareMonthlyResponse> =>
    http.post('/new/ftp/flow/detail/compare/deduction', data, { mock }),
  // 流程详情页面内比对接口-月度指导扩展
  postCompareExt: (data: Types.CompareMonthlyRequest): Promise<Types.CompareMonthlyResponse> =>
    http.post('/new/ftp/flow/detail/compare/ext', data, { mock }),
  // 流程详情页面内比对接口-月度指导扩展
  postRefresh: (data: any): Promise<any> =>
    http.post('/new/ftp/monthly/deduction/refresh', data, { mock }),
}

/* prettier-ignore-end */
