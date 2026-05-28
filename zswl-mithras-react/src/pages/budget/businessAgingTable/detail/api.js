import { http } from '@zswl/admin'

export default {
  postBaseInfoCount: (params) => http.post('/finance/account/age/base/info/count', params),
  postAccountModify: (params) => http.post('/finance/account/age/item/modify', params),
}
