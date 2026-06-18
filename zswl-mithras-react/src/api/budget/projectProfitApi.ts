/* prettier-ignore-start */
import * as Types from './interface/projectProfitApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 利润测算
  postProfitCalculation: (
    data: Types.ProfitCalculationRequest
  ): Promise<Types.ProfitCalculationResponse> =>
    http.post('/finance/projectprofit/profit/calculation', data, { mock, timeout: 0 }),

  // 财务管理-项目利润-分页列表
  postProjectprofitPagelist: (
    data: Types.ProjectprofitPagelistRequest
  ): Promise<Types.ProjectprofitPagelistResponse> =>
    http.post('/finance/projectprofit/pagelist', data, { mock }),

  // 财务管理-项目利润-详情-分页列表
  postDetailPagelist: (data: Types.DetailPagelistRequest): Promise<Types.DetailPagelistResponse> =>
    http.post('/finance/projectprofit/detail/pagelist', data, { mock }),
}

/* prettier-ignore-end */
