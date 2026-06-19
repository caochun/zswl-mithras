import { http } from '@zswl/admin'

export default {
  depostInfo: (id) => http.post(`/contract/depost/depostInfo?id=${id}`),
}
