import { http } from '@zswl/admin'

export default {
  postInfoSave: (params) => http.post('/contract/text/info/save', params),
  postInfoGet: (params) => http.post('/contract/text/info/get', params),
}
