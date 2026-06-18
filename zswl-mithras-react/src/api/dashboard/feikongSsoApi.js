import { http } from '@zswl/admin'

export default {
  getOauthAuthorize: () => http.get('/oauth/authorize', {}),
}
