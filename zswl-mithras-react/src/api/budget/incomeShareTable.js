import { http } from '@zswl/admin'

export default {
  postList: (params) => http.post('/incomeSharing/list', params),
  postDetailList: (params) => http.post('/incomeSharing/detail', params),
  postDownloadList: (params) =>
    http.post('/incomeSharing/list/download', params, { type: 'download' }),
  postDownloadDetailList: (params) =>
    http.post('/incomeSharing/detail/download', params, { type: 'download' }),
}
