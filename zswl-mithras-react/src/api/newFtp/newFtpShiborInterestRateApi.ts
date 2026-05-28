/* prettier-ignore-start */
import * as Types from './interface/newFtpShiborInterestRateApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 1年期SHIBOR利率列表
  postRateList: (data: Types.RateListRequest): Promise<Types.RateListResponse> =>
    http.post('/new/ftp/shibor/interest/rate/list', data, { mock }),

  // 一年期shibor利率定价列表
  postRateListprincing: (
    data: Types.RateListprincingRequest
  ): Promise<Types.RateListprincingResponse> =>
    http.post('/new/ftp/shibor/interest/rate/listprincing', data, { mock }),

  // 新增1年期SHIBOR利率
  postRateImport: (data: Types.RateImportRequest): Promise<Types.RateImportResponse> =>
    http.post('/new/ftp/shibor/interest/rate/import', data, {
      mock,
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
    }),
}

/* prettier-ignore-end */
