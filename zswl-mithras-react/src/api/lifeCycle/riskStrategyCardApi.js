import { http } from '@zswl/admin'

export default {
  getCardId: (params) => http.post('/risk/control/score/card/change/card', params),
  saveCardData: (params) => http.post('/risk/control/score/card/calculate/save', params),
  getCardData: (params) => http.post('/risk/control/score/card/calculate/detail', params),
  postTryCalc: (params) => http.post('/risk/control/score/card/try/calculate', params),
}
