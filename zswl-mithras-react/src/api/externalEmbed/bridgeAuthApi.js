import { http } from '@zswl/admin'

export default {
  getAuthCode: (data) => http.post('/birdge/user/auth', data),
  login: (data) => http.post('birdge/user/login', data),
}
