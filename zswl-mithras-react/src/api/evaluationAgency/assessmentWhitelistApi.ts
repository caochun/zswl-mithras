import { http } from '@zswl/admin'

const mock = false

export default {
  postWhitelistPagelist: (data: any): Promise<any> =>
    http.post('/appraisalcompany/whitelist/pagelist', data, { mock }),
}
