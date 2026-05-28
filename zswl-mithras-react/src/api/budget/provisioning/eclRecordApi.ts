/* prettier-ignore-start */
import * as Types from './interface/eclRecordApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改资产减值记录表
  postRecordModify: (data: Types.RecordModifyRequest): Promise<Types.RecordModifyResponse> =>
    http.post('/ecl/execute/record/modify', data, { mock }),

  // 删除资产减值全量记录表
  postAllRemove: (data: Types.AllRemoveRequest): Promise<Types.AllRemoveResponse> =>
    http.post('/ecl/execute/record/all/remove', data, { mock }),

  // 删除资产减值记录表
  postRecordRemove: (data: Types.RecordRemoveRequest): Promise<Types.RecordRemoveResponse> =>
    http.post('/ecl/execute/record/remove', data, { mock }),

  // 新增资产减值记录表
  postRecordAdd: (data: Types.RecordAddRequest): Promise<Types.RecordAddResponse> =>
    http.post('/ecl/execute/record/add', data, { mock }),

  // 新增资产减值记录表导入
  postRecordImport: (data: Types.RecordImportRequest): Promise<Types.RecordImportResponse> =>
    http.post('/ecl/execute/record/import', data, { mock, type: 'upload' }),

  // 新增资产减值记录表导入检查
  postImportCheck: (data: Types.ImportCheckRequest): Promise<Types.ImportCheckResponse> =>
    http.post('/ecl/execute/record/import/check', data, { mock, type: 'upload' }),

  // 新增资产减值记录表检查
  postRecordAddCheck: (data: Types.RecordAddCheckRequest): Promise<Types.RecordAddCheckResponse> =>
    http.post('/ecl/execute/record/addCheck', data, { mock }),

  // 资产减值记录表列表
  postRecordList: (data: Types.RecordListRequest): Promise<Types.RecordListResponse> =>
    http.post('/ecl/execute/record/list', data, { mock }),

  // 资产减值记录表比对列表
  postListCompare: (data: Types.ListCompareRequest): Promise<Types.ListCompareResponse> =>
    http.post('/ecl/execute/record/list/compare', data, { mock }),
}

/* prettier-ignore-end */
