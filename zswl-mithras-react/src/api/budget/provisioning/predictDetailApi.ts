/* prettier-ignore-start */
import * as Types from './interface/predictDetailApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改资产减值预测详情记录表
  postRecordModify: (data: Types.RecordModifyRequest): Promise<Types.RecordModifyResponse> =>
    http.post('/ecl/execute/predict/record/modify', data, { mock }),

  // 删除资产减值预测详情记录表
  postRecordRemove: (data: Types.RecordRemoveRequest): Promise<Types.RecordRemoveResponse> =>
    http.post('/ecl/execute/predict/record/remove', data, { mock }),

  // 新增资产减值预测导入
  postRecordImport: (data: Types.RecordImportRequest): Promise<Types.RecordImportResponse> =>
    http.post('/ecl/execute/predict/record/import', data, { mock, type: 'upload' }),

  // 新增资产减值预测导入检查
  postRecordCheck: (data: Types.RecordCheckRequest): Promise<Types.RecordCheckResponse> =>
    http.post('/ecl/execute/predict/record/check', data, { mock, type: 'upload' }),

  // 新增资产减值预测检查
  postRecordAddCheck: (data: Types.RecordAddCheckRequest): Promise<Types.RecordAddCheckResponse> =>
    http.post('/ecl/execute/predict/record/addCheck', data, { mock }),

  // 新增资产减值预测详情记录表
  postRecordAdd: (data: Types.RecordAddRequest): Promise<Types.RecordAddResponse> =>
    http.post('/ecl/execute/predict/record/add', data, { mock }),

  // 资产减值预测详情记录表列表
  postRecordList: (data: Types.RecordListRequest): Promise<Types.RecordListResponse> =>
    http.post('/ecl/execute/predict/record/list', data, { mock }),
}

/* prettier-ignore-end */
