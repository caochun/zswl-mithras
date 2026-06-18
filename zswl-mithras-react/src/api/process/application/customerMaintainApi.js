import { http } from '@zswl/admin'

export default {
  postClientApplyOccupy: (data) => http.post('/client/apply/occupy', data),
}
