import { http } from '@zswl/admin'

export default {
  checkResult: (params) => http.post('/proj/review/client/subjectitem/checkresult', params),
  checkResultList: (params) =>
    http.post('/proj/review/client/subjectitem/checkresult/list', params),
  saveReason: (params) =>
    http.post('/proj/review/client/subjectitem/checkresult/reason/save', params),
}
