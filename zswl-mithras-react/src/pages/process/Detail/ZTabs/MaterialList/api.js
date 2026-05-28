import { http } from '@zswl/admin'

export default {
  commentGuideLine: (params) => http.post('/materials/proj/review/comments', params),
}
