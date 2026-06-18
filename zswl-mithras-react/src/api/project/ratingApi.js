import { http } from '@zswl/admin'

export default {
  postEstablishUpdate: (data) => http.post('/proj/establish/base/info/updateRating', data),
  postReviewUpdate: (data) => http.post('/proj/review/base/info/updateRating', data),
}
