/* prettier-ignore-start */
import * as Types from './interface/policyTemporaryStorageApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 保单暂存表c列表
  postTmpList: (data: Types.TmpListRequest): Promise<Types.TmpListResponse> =>
    http.post('/policy/info/tmp/list', data, { mock }),

  // 保单暂存表c导出
  postTmpExport: (data: Types.TmpExportRequest): Promise<Types.TmpExportResponse> =>
    http.post('/policy/info/tmp/export', data, { mock, type: 'download' }),

  // 修改保单暂存表c
  postTmpModify: (data: Types.TmpModifyRequest): Promise<Types.TmpModifyResponse> =>
    http.post('/policy/info/tmp/modify', data, { mock }),

  // 删除保单暂存表c
  postTmpRemove: (data: Types.TmpRemoveRequest): Promise<Types.TmpRemoveResponse> =>
    http.post('/policy/info/tmp/remove', data, { mock }),

  // 导入保单信息
  postTmpImport: (data: Types.TmpImportRequest): Promise<Types.TmpImportResponse> =>
    http.post('/policy/info/tmp/import', data, { mock, type: 'upload' }),

  // 新增保单暂存表c
  postTmpAdd: (data: Types.TmpAddRequest): Promise<Types.TmpAddResponse> =>
    http.post('/policy/info/tmp/add', data, { mock }),
}

/* prettier-ignore-end */
