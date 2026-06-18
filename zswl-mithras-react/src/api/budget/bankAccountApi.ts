/* prettier-ignore-start */
import * as Types from './interface/bankAccountApi'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // 保存我方账户
  postBankaccountSave: (
    data: Types.BankaccountSaveRequest
  ): Promise<Types.BankaccountSaveResponse> =>
    http.post('/basedata/bankaccount/save', data, { mock }),

  // 删除我方账户
  postBankaccountDelete: (
    data: Types.BankaccountDeleteRequest
  ): Promise<Types.BankaccountDeleteResponse> =>
    http.post('/basedata/bankaccount/delete', data, { mock }),

  // 我方账户列表
  postBankaccountList: (
    data: Types.BankaccountListRequest
  ): Promise<Types.BankaccountListResponse> =>
    http.post('/basedata/bankaccount/list', data, {
      mock,
      headers: { functionCode: 'basedatabankaccountlist' },
    }),

  // 我方账户详情
  postBankaccountDetail: (
    data: Types.BankaccountDetailRequest
  ): Promise<Types.BankaccountDetailResponse> =>
    http.post('/basedata/bankaccount/detail', data, { mock }),
}

/* prettier-ignore-end */
