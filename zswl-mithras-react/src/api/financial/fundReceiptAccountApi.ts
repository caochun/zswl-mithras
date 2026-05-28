/* prettier-ignore-start */
import * as Types from './interface/fundReceiptAccountApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改资金管理-融资管理-对方收款账户
  postAccountModify: (data: Types.AccountModifyRequest): Promise<Types.AccountModifyResponse> =>
    http.post('/fund/receipt/account/modify', data, { mock }),

  // 删除资金管理-融资管理-对方收款账户
  postAccountRemove: (data: Types.AccountRemoveRequest): Promise<Types.AccountRemoveResponse> =>
    http.post('/fund/receipt/account/remove', data, { mock }),

  // 新增资金管理-融资管理-对方收款账户
  postAccountAdd: (data: Types.AccountAddRequest): Promise<Types.AccountAddResponse> =>
    http.post('/fund/receipt/account/add', data, { mock }),

  // 资金管理-融资管理-对方收款账户列表
  postAccountList: (data: Types.AccountListRequest): Promise<Types.AccountListResponse> =>
    http.post('/fund/receipt/account/list', data, { mock }),

  // 资金管理-融资管理-对方收款账户列表
  postAccountListCompare: (data: Types.AccountListRequest): Promise<Types.AccountListResponse> =>
    http.post('/fund/receipt/account/list/compare', data, { mock }),
}

/* prettier-ignore-end */
