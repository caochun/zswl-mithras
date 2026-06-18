import { http } from '@zswl/admin'

export default {
  checkMaterialsDesc: (params) => http.post('/other/filingMaterial/checkMaterialsDesc', params),
}
