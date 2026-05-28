import { http } from '@zswl/admin'

export default {
  getBankAccountList: (params) =>
    http.post('/basedata/bankaccount/list', params, {
      headers: {
        functionCode: 'basedatabankaccountlist',
      },
    }),
  postBankAccountModify: (params) =>
    http.post('/basedata/bankaccount/save', params, {
      transformResult: (res) => res.data,
    }),
  postBankAccountDelete: (params) =>
    http.post('/basedata/bankaccount/delete', params, {
      transformResult: (res) => res.data,
    }),
}
