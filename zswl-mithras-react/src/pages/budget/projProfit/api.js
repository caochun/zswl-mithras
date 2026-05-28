import { http } from '@zswl/admin'

export default {
  postFtpInterestLastMonth: (params) =>
    http.post('/ftp/interest/latest/month', params, {
      headers: {
        functionCode: 'profitFtpInterestLatestMonth',
      },
    }),
  postFinanceProjectprofitConfirm: (params) =>
    http.post('/finance/projectprofit/confirm', params, {}),
}
