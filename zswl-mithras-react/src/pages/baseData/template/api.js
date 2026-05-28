import { http } from '@zswl/admin'

export default {
  postFileTemplateUpdate: (params) => http.post('/file/template/update', params),
}
