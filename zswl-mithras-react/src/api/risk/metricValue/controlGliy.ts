/* prettier-ignore-start */
import * as Types from './interface/controlGliy'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改关联交易记录；如果是已报送记录，高度提示
  postReportModify: (data: Types.ReportModifyRequest): Promise<Types.ReportModifyResponse> =>
    http.post('/risk/control/gljy/report/modify', data, { mock }),

  // 关联交易关联方查询；返回最多50条
  postRelatedClients: (data: Types.RelatedClientsRequest): Promise<Types.RelatedClientsResponse> =>
    http.post('/risk/control/gljy/report/related/clients', data, { mock }),

  // 关联交易批量报送
  postReportSubmit: (data: Types.ReportSubmitRequest): Promise<Types.ReportSubmitResponse> =>
    http.post('/risk/control/gljy/report/submit', data, { mock }),

  // 关联交易记录列表
  postReportList: (data: Types.ReportListRequest): Promise<Types.ReportListResponse> =>
    http.post('/risk/control/gljy/report/list', data, { mock }),

  // 删除关联交易记录
  postReportRemove: (data: Types.ReportRemoveRequest): Promise<Types.ReportRemoveResponse> =>
    http.post('/risk/control/gljy/report/remove', data, { mock }),

  // 手动新增关联交易记录
  postReportAdd: (data: Types.ReportAddRequest): Promise<Types.ReportAddResponse> =>
    http.post('/risk/control/gljy/report/add', data, { mock }),
}

/* prettier-ignore-end */
