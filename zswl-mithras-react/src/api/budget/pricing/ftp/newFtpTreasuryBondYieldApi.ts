/* prettier-ignore-start */
import * as Types from './interface/newFtpTreasuryBondYieldApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 10年期国债收益率列表
  postYieldList: (data: Types.YieldListRequest): Promise<Types.YieldListResponse> =>
    http.post('/new/ftp/treasury/bond/yield/list', data, { mock }),

  // 10年期国债收益率定价列表
  postYieldListpricing: (
    data: Types.YieldListpricingRequest
  ): Promise<Types.YieldListpricingResponse> =>
    http.post('/new/ftp/treasury/bond/yield/listpricing', data, { mock }),

  // 导入10年期国债收益率
  postYieldImport: (data: Types.YieldImportRequest): Promise<Types.YieldImportResponse> =>
    http.post('/new/ftp/treasury/bond/yield/import', data, {
      mock,
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
    }),
}

/* prettier-ignore-end */
