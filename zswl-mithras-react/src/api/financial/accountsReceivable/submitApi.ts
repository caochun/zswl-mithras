/* prettier-ignore-start */
import * as Types from './interface/submitApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 提交应收逾期集成单
  postOverdueSubmit: (data: Types.OverdueSubmitRequest): Promise<Types.OverdueSubmitResponse> =>
    http.post('/finance/overdue/submit', data, { mock }),
}

/* prettier-ignore-end */
