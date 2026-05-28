import { http } from '@zswl/admin'

export default {
  // 列表信息
  postList: (params) => http.post('/incomeSharing/list', params),
  // 收入分摊详细列表
  postDetailList: (params) => http.post('/incomeSharing/detail', params),
  // 列表下载
  postDownloadList: (params) =>
    http.post('/incomeSharing/list/download', params, { type: 'download' }),
  // 列表下载
  postDownloadDetailList: (params) =>
    http.post('/incomeSharing/detail/download', params, { type: 'download' }),
}
