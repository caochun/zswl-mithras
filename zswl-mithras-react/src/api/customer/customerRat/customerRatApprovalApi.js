import { http } from '@zswl/admin'

export default {
  passProcess: (params) => http.post('/flow/execution/pass', params, { timeout: 0 }),
}
