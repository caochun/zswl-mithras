import { http } from '@zswl/admin'

const mock = false

export default {
  postActionDetail: (data) => http.post('/overduecollection/action/detail', data, { mock }),
  postOverduecollectionDetail: (data) =>
    http.post('/overduecollection/detail', data, { mock }),
}
