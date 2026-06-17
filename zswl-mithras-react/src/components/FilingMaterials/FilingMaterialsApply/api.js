import { http } from '@zswl/admin'
export default {
  getTab: (params) => http.post('filingMaterial/getTab', params),
  getBasicMaterial: (params) => http.post('/filingMaterial/getCustomerReferenceMaterials', params),
  getNonBasicMaterial: (params) => http.post('/filingMaterial/getNonCustomerReferenceMaterials', params),
  showSynchronizationButton: (params) => http.post('/filingMaterial/synchronizationButtonFlag', params),
  materialsSynchronization: (params) => http.post('/filingMaterial/materialsSynchronization', params),
  batchDownload: (params) =>
    http.post(
      '/filingMaterial/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
  getOperationsDirDict: (id) => http.post('/filingMaterial/getOperationsDirDict', { id }),
  materialsImport: (params) => http.post('/filingMaterial/materialsImport', params),
  materialsRemove: (params) => http.post('/filingMaterial/file/remove', params, { transformResult: (res) => res.data }),
  download: (params) => http.get('/filingMaterial/download', { params }),
  templateDownApi: (params) => http.get('/filingMaterial/template/download', { params, type: 'download', timeout: 0 }, { type: 'download' }),
  getCurTaskDefKey: (params) => http.post('/filingMaterial/getCurTaskDefKey', params),
}
