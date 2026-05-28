/* prettier-ignore-start */
import * as Types from './interface/payableLoansApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 征信报告-信贷记录明细表列表
  postDetailsList: (data: Types.DetailsListRequest): Promise<Types.DetailsListResponse> =>
    http.post('/credit/report/record/details/list', data, { mock }),
}

/* prettier-ignore-end */
