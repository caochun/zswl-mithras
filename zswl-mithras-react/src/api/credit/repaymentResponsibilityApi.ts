/* prettier-ignore-start */
import * as Types from './interface/repaymentResponsibilityApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改征信报告-相关还款责任信息概要表
  postResponsibilityModify: (
    data: Types.ResponsibilityModifyRequest,
  ): Promise<Types.ResponsibilityModifyResponse> =>
    http.post('/credit/report/repayment/responsibility/modify', data, { mock }),

  // 删除征信报告-相关还款责任信息概要表
  postResponsibilityRemove: (
    data: Types.ResponsibilityRemoveRequest,
  ): Promise<Types.ResponsibilityRemoveResponse> =>
    http.post('/credit/report/repayment/responsibility/remove', data, { mock }),

  // 征信报告-相关还款责任信息概要表列表
  postResponsibilityList: (
    data: Types.ResponsibilityListRequest,
  ): Promise<Types.ResponsibilityListResponse> =>
    http.post('/credit/report/repayment/responsibility/list', data, { mock }),
}

/* prettier-ignore-end */
