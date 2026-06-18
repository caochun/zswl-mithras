import { http } from '@zswl/admin'

export default {
  checkResult: (params) => http.post('/proj/review/client/subjectitem/checkresult', params),
}
