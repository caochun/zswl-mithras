/* prettier-ignore-start */
import * as Types from './interface/interfaceManageApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 苍穹接口调用记录-分页列表
  postRecordPagelist: (data: Types.RecordPagelistRequest): Promise<Types.RecordPagelistResponse> =>
    http.post('/third/cq/record/pagelist', data, { mock }),

  // 苍穹接口调用记录-忽略
  postRecordIgnore: (data: Types.RecordIgnoreRequest): Promise<Types.RecordIgnoreResponse> =>
    http.post('/third/cq/record/ignore', data, { mock }),

  // 苍穹接口调用记录-推送
  postRecordPush: (data: Types.RecordPushRequest): Promise<Types.RecordPushResponse> =>
    http.post('/third/cq/record/push', data, { mock }),
}

/* prettier-ignore-end */
