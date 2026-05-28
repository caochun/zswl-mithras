/* prettier-ignore-start */
import { http } from '@zswl/admin'

const mock = false
export default {
  generateToken: (data) =>
    http.post('/qccApi/generateToken', data, {
      mock,
    }),
}