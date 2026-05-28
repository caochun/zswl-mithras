/* prettier-ignore-start */
import * as Types from './interface/creditLimitApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 征信报告-信用额度表列表
  postLimitList: (data: Types.LimitListRequest): Promise<Types.LimitListResponse> =>
    http.post('/credit/report/limit/list', data, { mock }),
}

/* prettier-ignore-end */
