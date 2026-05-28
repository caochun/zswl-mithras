/* prettier-ignore-start */
import * as Types from './interface/guaranteedFinancialFlow'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 保融流水表列表
  postRecordList: (data: Types.RecordListRequest): Promise<Types.RecordListResponse> =>
    http.post('/br/flow/record/list', data, { mock }),

  // 保融流水表统计
  postRecordCount: (data: Types.RecordCountRequest): Promise<Types.RecordCountResponse> =>
    http.post('/br/flow/record/count', data, { mock }),

  // 删除保融流水表
  postRecordRemove: (data: Types.RecordRemoveRequest): Promise<Types.RecordRemoveResponse> =>
    http.post('/br/flow/record/remove', data, { mock }),

  // 忽略保融流水表
  postRecordIgnore: (data: Types.RecordIgnoreRequest): Promise<Types.RecordIgnoreResponse> =>
    http.post('/br/flow/record/ignore', data, { mock }),
}

/* prettier-ignore-end */
