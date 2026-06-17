import { http } from '@zswl/admin'

export default {
  postProjectDataDetail: (params) => http.post('/materials/group/credit/review/list', params),
  postProjectDataUpload: (params) =>
    http.post('/materials/upload', params, {
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'materialsupload-creditreview',
      },
      type: 'upload',
      timeout: 0,
    }),
  postProjectDataRemove: (params) =>
    http.post('/materials/remove', params, {
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'materialsremove-creditreview',
      },
    }),
  postProjectDataDownload: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      headers: {
        functionCode: 'materialsdownload-creditreview',
      },
      timeout: 0,
    }),
}
