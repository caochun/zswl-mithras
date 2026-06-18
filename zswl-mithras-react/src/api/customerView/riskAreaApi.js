import { http } from '@zswl/admin'

const mock = false

export default {
  postAreaRating: (data) => http.post('/customer/view/detail/areaRating', data, { mock }),
  postDetailAreaEconomy: (data) =>
    http.post('/customer/view/detail/areaEconomy', data, { mock }),
  postDetailCtzReginEconomy: (data) =>
    http.post('/customer/view/detail/ctzReginEconomy', data, { mock }),
}
