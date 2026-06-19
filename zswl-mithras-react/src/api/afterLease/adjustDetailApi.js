import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.post('/after/lease/adjust/info/detail', params),
  modifyInfo: (params) => http.post('/after/lease/adjust/info/modify', params, {}),
  submit: (params) => http.post('/after/lease/adjust/effect', params, {}),
  cancelFlow: (params) => http.post('/after/lease/adjust/cancel', params, {}),
}
