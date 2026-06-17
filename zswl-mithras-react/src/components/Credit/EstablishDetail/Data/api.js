import { http } from '@zswl/admin'

export default {
  postProjectDataDetail: (params) => http.post('/materials/proj/list', params),
  postProjectDataUpload: (params) =>
    http.post('/materials/upload', params, {
      transformResult: (res) => res.data,
      timeout: 0,
      headers: {
        functionCode: 'materialsupload-1',
      },
      type: 'upload',
    }),
  postProjectDataRemove: (params) =>
    http.post('/materials/remove', params, {
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'materialsremove-2',
      },
    }),
  postProjectDataDownload: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      fileName: params.filename,
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'materialsdownload-creditestablish',
      },
      timeout: 0,
    }),
  getProjectDataDownload: (params) =>
    http('/materials/proj/download', {
      params,
      type: 'download',
      // timeout: 0,
      timeout: 0,
      fileName: '资料清单.zip',
      transformResult: (res) => res.data,
      // headers: {
      //   functionCode: 'materialsdownload-4',
      // },
    }),
}
