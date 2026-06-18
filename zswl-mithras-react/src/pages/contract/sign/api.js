import { http } from '@zswl/admin'

export default {
  getBaseInfo: (params) => http.get('/contract/base/info/detail', { params }),
  getContractQSDetail: (params) => http.post('/contract/price/detail', params),
  getContractTextFileDownload: (params) =>
    http.get('/file/download', {
      params,
      type: 'download',
      headers: {
        functionCode: 'contractTextManageFileDownload',
      },
    }),
}
