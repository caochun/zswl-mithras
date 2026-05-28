import { http } from '@zswl/admin'

export default {
  postFtpPriceList: (params) => http.post('/ftp/price/list', params, {}),
  postFtpPriceUpdate: (params) => http.post('/ftp/price/update', params, {}),
  postFtpPriceCheck: (params) => http.post('/ftp/price/check', params, {}),
}
