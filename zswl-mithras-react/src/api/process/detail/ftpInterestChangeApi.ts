import { http } from '@zswl/admin'

const mock = false

export default {
  postApplyDetail: (data: any): Promise<any> =>
    http.post('/new/ftp/interest/change/apply/detail', data, { mock }),
}
