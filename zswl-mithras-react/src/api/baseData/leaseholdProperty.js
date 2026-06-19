import { http } from '@zswl/admin'

export default {
  list: (params) => http.post('/leaseholdproperty/list', params),
  import: (params) => http.post('/leaseholdproperty/import', params, { type: 'upload', timeout: 0 }),
}
