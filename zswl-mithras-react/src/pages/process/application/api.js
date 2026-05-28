import { http } from '@zswl/admin'

export default {
  myProcessCount: (params) => http.get('/flow/task/myProcess/count', { params }),
}
