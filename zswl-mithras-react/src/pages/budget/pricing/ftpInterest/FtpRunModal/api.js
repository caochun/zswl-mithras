import { http } from '@zswl/admin'

export default {
  postFtpInterestRecalculate: (params) => http.post('/ftp/interest/recalculate', params, {}),
  postFtpInterestLastMonth: (params) =>
    http.post('/ftp/interest/latest/month', params, {
      headers: {
        functionCode: 'ftpInterestLatestMonth',
      },
    }),
}
