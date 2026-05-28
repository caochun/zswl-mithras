/* prettier-ignore-start */
import * as Types from './interface/allot'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // 绩效考核-项目分配-分页列表
  postProjectdistributionPagelist: (
    data: Types.ProjectdistributionPagelistRequest
  ): Promise<Types.ProjectdistributionPagelistResponse> =>
    http.post('/kpi/projectdistribution/pagelist', data, { mock }),

  // 绩效考核-项目分配-提交审批
  postProjectdistributionSubmit: (
    data: Types.ProjectdistributionSubmitRequest
  ): Promise<Types.ProjectdistributionSubmitResponse> =>
    http.post('/kpi/projectdistribution/submit', data, { mock }),

  // 绩效考核-项目分配-查看历史
  postProjectdistributionHistory: (
    data: Types.ProjectdistributionHistoryRequest
  ): Promise<Types.ProjectdistributionHistoryResponse> =>
    http.post('/kpi/projectdistribution/history', data, { mock }),
}

/* prettier-ignore-end */
