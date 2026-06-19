import { http } from '@zswl/admin'

export default {
  upload: (params) =>
    http.post('/contract/flow/startRent/upload', params, {
      type: 'upload',
      transformResult: (res) => res.data,
    }),
  generateActualRentFile: (params) =>
    http.post('/contract/receipt/generateActualRentFile ', params, {}),
}
