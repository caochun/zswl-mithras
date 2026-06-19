import { http } from '@zswl/admin'

export default {
  getLPRList: (params) => http.post('/basedata/lpr/pagelist', params),
  postLPRModify: (params) =>
    http.post('/basedata/lpr/save', params, {
      transformResult: (res) => res.data,
    }),
  postLPRDelete: (params) =>
    http.post('/basedata/lpr/delete', params, {
      transformResult: (res) => res.data,
    }),
  postLPRImport: (params) =>
    http.post('/basedata/lpr/import', params, {
      transformResult: (res) => res.data,
    }),
  postLPRTemplateDownload: (params) =>
    http('/basedata/lpr/template/download', {
      params,
      type: 'download',
      fileName: params.filename,
      transformResult: (res) => res.data,
      timeout: 0,
    }),
}
