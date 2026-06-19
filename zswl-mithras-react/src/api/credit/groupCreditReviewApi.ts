import { http } from '@zswl/admin'

const mock = false

export default {
  postInfoList: (data?: any): Promise<any> =>
    http.post('/group/credit/review/base/info/list', data, { mock }),

  postInfoAdd: (data?: any): Promise<any> =>
    http.post('/group/credit/review/base/info/add', data, { mock }),

  postEstablishQuery: (data?: any): Promise<any> =>
    http.post('/group/credit/review/establish/query', data, { mock }),

  getClientList: (data?: any): Promise<any> =>
    http.post(
      '/client/list',
      { scene: 'query', ...data },
      {
        timeout: 0,
        headers: {
          functionCode: 'clientlist-9',
        },
      }
    ),
}
