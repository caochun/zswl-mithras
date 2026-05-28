import { http } from '@zswl/admin'

export default {
  searchAbbreviation: (data) => http.post('/fund/organization/abbreviation', data),
}
