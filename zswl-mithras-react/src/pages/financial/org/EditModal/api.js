import { http } from '@zswl/admin'

export default {
  getFundInstitutionCode: (data) => http.post('/fund/institutionCode', data),
}
