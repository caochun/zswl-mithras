/* prettier-ignore-start */
import * as Types from './interface/creditSearchClientApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 征信报告查询列表
  postClientList: (data: Types.ClientListRequest): Promise<Types.ClientListResponse> =>
    http.post('/creditreport/client/list', data, { mock }),

  // 征信查询删除
  getClientDelete: (params: Types.ClientDeleteRequest): Promise<Types.ClientDeleteResponse> =>
    http.get('/creditreport/client/delete', { params, mock }),
}

/* prettier-ignore-end */
