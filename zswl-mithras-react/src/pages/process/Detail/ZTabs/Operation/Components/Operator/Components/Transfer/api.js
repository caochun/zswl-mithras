import { http } from '@zswl/admin'

export default {
  queryCanTransferUser: (body) => http.post('/flow/process/queryCanTransferUser', body),
}
