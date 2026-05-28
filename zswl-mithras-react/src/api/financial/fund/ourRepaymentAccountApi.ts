/* prettier-ignore-start */
import * as Types from './interface/ourRepaymentAccountApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改账户
  postAccountModify: (data: Types.AccountModifyRequest): Promise<Types.AccountModifyResponse> =>
    http.post('/fund/financing/pay/account/modify', data, { mock }),

  // 创建账户
  postAccountCreate: (data: Types.AccountCreateRequest): Promise<Types.AccountCreateResponse> =>
    http.post('/fund/financing/pay/account/create', data, { mock }),

  // 删除账户
  postAccountDelete: (data: Types.AccountDeleteRequest): Promise<Types.AccountDeleteResponse> =>
    http.post('/fund/financing/pay/account/delete', data, { mock }),

  // 查询银行名称
  postAccountBank: (data: Types.AccountBankRequest): Promise<Types.AccountBankResponse> =>
    http.post('/fund/financing/pay/account/bank', data, { mock }),

  // 查询银行账号等信息
  postBankInfo: (data: Types.BankInfoRequest): Promise<Types.BankInfoResponse> =>
    http.post('/fund/financing/pay/account/bank/info', data, { mock }),

  // 获取账户列表
  postAccountList: (data: Types.AccountListRequest): Promise<Types.AccountListResponse> =>
    http.post('/fund/financing/pay/account/list', data, { mock }),
}

/* prettier-ignore-end */
