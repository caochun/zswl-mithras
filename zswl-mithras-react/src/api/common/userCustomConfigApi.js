import { http } from '@zswl/admin'

const mock = false

export default {
  saveCustomConfig: (params) => http.post('/user/custom/config/save', params, mock),
  queryCustomConfig: (params) => http.post('/user/custom/config/query', params, mock),
}
