/* prettier-ignore-start */
import * as Types from './interface/publicInfoApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 公开信息-修改表格内容
  postTableContent: (data: Types.TableContentRequest): Promise<Types.TableContentResponse> =>
    http.post('/public/info/modify/table/content', data, { mock }),

  // 公开信息-删除指定区间表格
  postDeleteIntervalTable: (
    data: Types.IntervalTableRequest
  ): Promise<Types.IntervalTableResponse> =>
    http.post('/public/info/delete/interval/table', data, { mock }),

  // 公开信息-客户列表查询
  postClientList: (data: Types.ClientListRequest): Promise<Types.ClientListResponse> =>
    http.post('/public/info/client/list', data, { mock }),

  // 公开信息-导出
  postInfoExport: (data: Types.InfoExportRequest): Promise<Types.InfoExportResponse> =>
    http.post('/public/info/export', data, { mock, type: 'download', timeout: 0 }),

  // 公开信息-新建
  postCreateIntervalTable: (
    data: Types.IntervalTableRequest
  ): Promise<Types.IntervalTableResponse> =>
    http.post('/public/info/create/interval/table', data, { mock }),

  // 公开信息-查询指定区间表格
  postQueryIntervalTable: (
    data: Types.IntervalTableRequest
  ): Promise<Types.IntervalTableResponse> =>
    http.post('/public/info/query/interval/table', data, { mock }),

  // 公开信息-项目经理提交前校验
  postSubmitCheck: (data: Types.SubmitCheckRequest): Promise<Types.SubmitCheckResponse> =>
    http.post('/public/info/submit/check', data, { mock }),

  // 公开信息-重新取数
  postQueryOuterPublic: (data: Types.IntervalTableRequest): Promise<Types.IntervalTableResponse> =>
    http.post('/outer/public/query', data, { mock }),
}

/* prettier-ignore-end */
