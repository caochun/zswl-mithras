import { http } from '@zswl/admin'

const mock = false

export default {
  postSubmitCheck: (data: any): Promise<any> =>
    http.post('/public/info/submit/check', data, { mock }),
}
