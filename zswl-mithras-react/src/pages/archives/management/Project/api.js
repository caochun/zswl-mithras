import { http } from '@zswl/admin'

export default {
  // 项目端归档资料-列表查询
  getList: (params) => http.post('/documentManagementLedger/proj/list/query', params),
}
