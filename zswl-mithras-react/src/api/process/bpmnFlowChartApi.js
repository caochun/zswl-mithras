import { http } from '@zswl/admin'

export default {
  getXml: (url, params) =>
    http.get(url, {
      params,
      transformResult: (r) => r.data,
    }),
  getHighLight: (url, params) => http.get(url, { params }),
}
