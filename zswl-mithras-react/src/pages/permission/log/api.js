/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  postLogList: (params) => http.post('/system/operatelog', params),
}
