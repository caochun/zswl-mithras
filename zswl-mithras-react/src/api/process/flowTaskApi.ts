import { http } from '@zswl/admin'

export default {
  myReceiveCount: (params) => http.get('/flow/task/myReceive/count', { params }),
  getTodoList: (params) => http.post('/flow/task/myReceive/todo/list', params),
  getDoneList: (params) => http.post('/flow/task/myReceive/done/list', params),
  getCCList: (params) => http.post('/flow/task/myReceive/cc/list', params),
  getReturnableNodes: (params) => http.post('/flow/task/returnable/nodes', params),
  randomReturn: (params) => http.post('/flow/execution/randomReturn', params),
  getProcessDetail: (params) => http.post('/flow/task/process/detail', params),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),
  batchPass: (body) => http.post('/flow/execution/batch/pass', body),
}
