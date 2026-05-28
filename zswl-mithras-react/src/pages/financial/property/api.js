import { http } from '@zswl/admin'

export default {
  // 投放资产台账
  postList: (params) => http.post('/financing/property/list', params),
  // 投放资产台账（excel导出）
  postListExport: (params) =>
    http.post('/financing/property/list/download', params, {
      type: 'download',
      timeout: 0,
    }),
}
