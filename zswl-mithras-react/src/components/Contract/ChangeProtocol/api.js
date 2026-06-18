import { http } from '@zswl/admin'

export default {
  postDataList: (params) => http.post('/contract/flow/change/down', params),
  postDataUpload: (params) =>
    http.post('/contract/flow/change/upload', params, {
      type: 'upload',
      timeout: 0,
    }),
  postExtraMaterialsDownload: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      timeout: 0,
      headers: {
        functionCode: 'contractsettleextramaterialsdownload',
      },
    }),
}
