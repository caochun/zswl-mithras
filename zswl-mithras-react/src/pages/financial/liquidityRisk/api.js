/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  // 流动性统计
  postCashInOutStat: (params) => http.post('/cash/inOut/stat', params),
  postCashInOutDownLoad: (params) =>
    http.post('/cash/inOut/stat/download', params, {
      type: 'download',
      timeout: 0,
    }),
}
