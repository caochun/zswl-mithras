/* prettier-ignore-start */
import * as Types from './interface/overdueListController'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 票据逾期名单导入
  getOverdueListImport: (
    data: Types.OverdueListImportRequest
  ): Promise<Types.OverdueListImportResponse> =>
    http.post('/overdueList/import', data, { mock, type: 'upload' }),

  // 票据逾期名单查询
  postOverdueListList: (
    data: Types.OverdueListListRequest
  ): Promise<Types.OverdueListListResponse> => http.post('/overdueList/list', data, { mock }),
  // 待办中的导入接口：/overdueList/draft/import
  // 待办中的查询接口：/overdueList/draft/list
  getOverdueListImportTodo: (
    data: Types.OverdueListImportRequest
  ): Promise<Types.OverdueListImportResponse> =>
    http.post('/overdueList/draft/import', data, { mock, type: 'upload' }),
  // 待办中的查询接口：/overdueList/draft/list
  getOverdueListListTodo: (
    data: Types.OverdueListListRequest
  ): Promise<Types.OverdueListListResponse> => http.post('/overdueList/draft/list', data, { mock }),
}

/* prettier-ignore-end */
