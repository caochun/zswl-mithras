/* prettier-ignore-start */
import * as Types from './interface/newFtpMonthlyDeductionApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改月度计价指导
  postDeductionModify: (
    data: Types.DeductionModifyRequest,
  ): Promise<Types.DeductionModifyResponse> =>
    http.post('/new/ftp/monthly/deduction/modify', data, { mock }),

  // 月度计价指导列表
  postDeductionList: (data: Types.DeductionListRequest): Promise<Types.DeductionListResponse> =>
    http.post('/new/ftp/monthly/deduction/list', data, { mock }),
}

/* prettier-ignore-end */
