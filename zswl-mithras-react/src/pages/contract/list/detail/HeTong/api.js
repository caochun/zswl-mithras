import { http } from '@zswl/admin'

export default {
  postProjectDataDetail: (params) => http.post('/contract/file/list', params),
  postGenerateFile: (params) => http.post('/contract/file/generate', params, {}),
  postDataUpload: (params, config) =>
    http.post('/contract/file/upload', params, {
      type: 'upload',
      ...config,
      timeout: 0,
      transformResult: (res) => res.data,

      headers: {
        functionCode: 'contractfileupload',
      },
    }),

  postRemoveFile: (params) =>
    http.post('/contract/file/remove', params, {
      transformResult: (res) => res.data,
    }),

  postDataDownload: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
      headers: {
        functionCode: 'contractfiledownload',
      },
    }),
}
