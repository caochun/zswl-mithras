import { http } from '@zswl/admin'

export default {
  postInfoDetail: (params, functionCode) =>
    http.post('/proj/review/meet/minute/base/info/detail', params, {
      headers: {
        functionCode: functionCode || 'projReviewMeetMinuteBaseInfoDetail',
      },
    }),
}
