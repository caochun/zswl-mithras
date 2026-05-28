import { http } from '@zswl/admin'

export default {
  getOperationsDirDict: (id) => http.post('/other/filingMaterial/getOperationsDirDict', { id }),
  getMaterialsDesc: (id) => http.post('/other/filingMaterial/getMaterialsDesc', { id }),
  saveMaterialsDesc: (params) =>
    http.post('/other/filingMaterial/saveMaterialsDesc', params, {
      transformRequest: [(data) => {
        return JSON.stringify({ ...data, materialsDesc: data.materialsDesc || '' })
      }],
      headers: { 'Content-Type': 'application/json' },
    }),
  checkMaterialsDesc: (params) => http.post('/other/filingMaterial/checkMaterialsDesc', params),
  remove: (params) =>
    http.post('/other/filingMaterial/file/remove', params, { transformResult: (res) => res.data }),
  batchDownload: (params) =>
    http.post(
      '/other/filingMaterial/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),

}
