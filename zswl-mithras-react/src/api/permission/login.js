import { http } from '@zswl/admin'

export default {
  getAuthCode: (data) => http.post('/user/getAuthCode', data, { type: 'formData' }),
  login: (data) => http.post('/user/login', data, { type: 'formData' }),
  saveChangePwd: (params) => http.put('/userCenter/changePwd', params, { type: 'formData' }),
}
