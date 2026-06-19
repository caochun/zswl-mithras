import { http } from '@zswl/admin'
import { re } from 'mathjs'

export default {
  postProjectDataDetail: (params) => http.post('/contract/file/list', params),
  postExchangeFile: (params) => http.post('/contract/file/exchange/list', params, {}),
  postDataUpload: (params) =>
    http.post('/contract/file/upload', params, {
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'contractfileupload',
      },
    }),

  postRemoveFile: (params) =>
    http.post('contract/file/remove', params, {
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
