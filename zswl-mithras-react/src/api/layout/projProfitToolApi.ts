/* prettier-ignore-start */
import * as Types from './interface/projProfitToolApi'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // 导出
  postProfitcalculateExport: (
    data: Types.ProfitcalculateExportRequest
  ): Promise<Types.ProfitcalculateExportResponse> =>
    http.post('/profitcalculate/export', data, { mock, type: 'download' }),

  // 分页列表
  postProfitcalculatePagelist: (
    data: Types.ProfitcalculatePagelistRequest
  ): Promise<Types.ProfitcalculatePagelistResponse> =>
    http.post('/profitcalculate/pagelist', data, { mock }),
}

/* prettier-ignore-end */
