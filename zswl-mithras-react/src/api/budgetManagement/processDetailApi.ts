import { http } from '@zswl/admin'

export default {
  getProcessDetail: (params: any): Promise<any> => http.post('/flow/task/process/detail', params),
}
