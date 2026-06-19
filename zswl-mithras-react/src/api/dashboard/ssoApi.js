import { http } from '@zswl/admin'

export default {
  postDashboardSsoLogin: (data) => http.post('/user/dashboard/ssoLogin', data),
  postOaAuth: (data, config) => http.post('/message/oa/auth', data, config),
}
