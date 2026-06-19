import { http } from '@zswl/admin'

export default {
  postOnlyOfficeParams: (params) => http.post('/onlyoffice/docDetail', params),
  postMaterialsPreview: (params) => http.post('/materials/preview', params),
  callback: (params) => http.post('/onlyoffice/callback', params),
}
