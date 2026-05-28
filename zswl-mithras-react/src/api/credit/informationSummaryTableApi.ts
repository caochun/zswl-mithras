/* prettier-ignore-start */
import * as Types from './interface/informationSummaryTableApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改征信报告-信息概要表
  postSummaryModify: (data: Types.SummaryModifyRequest): Promise<Types.SummaryModifyResponse> =>
    http.post('/credit/report/summary/modify', data, { mock }),

  // 删除征信报告-信息概要表
  postSummaryRemove: (data: Types.SummaryRemoveRequest): Promise<Types.SummaryRemoveResponse> =>
    http.post('/credit/report/summary/remove', data, { mock }),

  // 征信报告-信息概要表列表
  postSummaryList: (data: Types.SummaryListRequest): Promise<Types.SummaryListResponse> =>
    http.post('/credit/report/summary/list', data, { mock }),

  // 征信报告-信息概要表详情
  postSummaryDetail: (data: Types.SummaryDetailRequest): Promise<Types.SummaryDetailResponse> =>
    http.post('/credit/report/summary/detail', data, { mock }),

  // 新增征信报告-信息概要表
  postSummaryAdd: (data: Types.SummaryAddRequest): Promise<Types.SummaryAddResponse> =>
    http.post('/credit/report/summary/add', data, { mock }),
}

/* prettier-ignore-end */
