/* prettier-ignore-start */
import * as Types from './interface/fundRepayAccountApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改资金管理-融资管理-我方付款账户
  postAccountModify: (data: Types.AccountModifyRequest): Promise<Types.AccountModifyResponse> =>
    http.post('/fund/repay/account/modify', data, { mock }),

  // 删除资金管理-融资管理-我方付款账户
  postAccountRemove: (data: Types.AccountRemoveRequest): Promise<Types.AccountRemoveResponse> =>
    http.post('/fund/repay/account/remove', data, { mock }),

  // 新增资金管理-融资管理-我方付款账户
  postAccountAdd: (data: Types.AccountAddRequest): Promise<Types.AccountAddResponse> =>
    http.post('/fund/repay/account/add', data, { mock }),

  // 资金管理-融资管理-我方付款账户列表
  postAccountList: (data: Types.AccountListRequest): Promise<Types.AccountListResponse> =>
    http.post('/fund/repay/account/list', data, { mock }),

  // 资金管理-融资管理-我方付款账户列表
  postAccountListCompare: (data: Types.AccountListRequest): Promise<Types.AccountListResponse> =>
    http.post('/fund/repay/account/list/compare', data, { mock }),
}

/* prettier-ignore-end */
