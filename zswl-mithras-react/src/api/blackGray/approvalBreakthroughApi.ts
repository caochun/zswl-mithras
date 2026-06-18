/* prettier-ignore-start */
import * as Types from './interface/approvalBreakthroughApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改审批突破
  postBusinessModify: (data: Types.BusinessModifyRequest): Promise<Types.BusinessModifyResponse> =>
    http.post('/black/gray/break/business/modify', data, { mock }),

  // 审批突破列表
  postBusinessList: (data: Types.BusinessListRequest): Promise<Types.BusinessListResponse> =>
    http.post('/black/gray/break/business/list', data, { mock }),

  // 审批突破详情
  postBusinessDetail: (data: Types.BusinessDetailRequest): Promise<Types.BusinessDetailResponse> =>
    http.post('/black/gray/break/business/detail', data, { mock }),

  // 新增审批突破
  postBusinessAdd: (data: Types.BusinessAddRequest): Promise<Types.BusinessAddResponse> =>
    http.post('/black/gray/break/business/add', data, { mock }),
}

/* prettier-ignore-end */
