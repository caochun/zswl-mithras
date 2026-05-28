/* prettier-ignore-start */
import * as Types from './interface/creditInformationApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改征信报告-未结清信贷及授信信息表
  postSummaryModify: (data: Types.SummaryModifyRequest): Promise<Types.SummaryModifyResponse> =>
    http.post('/credit/report/unsettled/summary/modify', data, { mock }),

  // 删除征信报告-未结清信贷及授信信息表
  postSummaryRemove: (data: Types.SummaryRemoveRequest): Promise<Types.SummaryRemoveResponse> =>
    http.post('/credit/report/unsettled/summary/remove', data, { mock }),

  // 征信报告-未结清信贷及授信信息表列表
  postSummaryList: (data: Types.SummaryListRequest): Promise<Types.SummaryListResponse> =>
    http.post('/credit/report/unsettled/summary/list', data, { mock }),
}

/* prettier-ignore-end */
