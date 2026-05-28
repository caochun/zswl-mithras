/* prettier-ignore-start */
import * as Types from './interface/irrGenerationApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // /user/custom/config/save
  saveCustomConfig: (params) => http.post('/user/custom/config/save', params, mock),
  // /user/custom/config/query
  queryCustomConfig: (params) => http.post('/user/custom/config/query', params, mock),
  /** 获取 OAuth 授权码，用于费控系统 SSO（响应体即为 code） */
  getOauthAuthorize: () => http.get('/oauth/authorize', {}),
}

/* prettier-ignore-end */
