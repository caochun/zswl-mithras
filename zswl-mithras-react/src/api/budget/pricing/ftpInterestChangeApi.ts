/* prettier-ignore-start */
import * as Types from './interface/ftpInterestChangeApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // FTP计息变更-申请信息-保存
  postApplySave: (data: Types.ApplySaveRequest): Promise<Types.ApplySaveResponse> =>
    http.post('/new/ftp/interest/change/apply/save', data, { mock }),

  // FTP计息变更-申请信息-提交审批
  postApplySubmit: (data: Types.ApplySubmitRequest): Promise<Types.ApplySubmitResponse> =>
    http.post('/new/ftp/interest/change/apply/submit', data, { mock }),

  // FTP计息变更-申请信息-详情
  postApplyDetail: (data: Types.ApplyDetailRequest): Promise<Types.ApplyDetailResponse> =>
    http.post('/new/ftp/interest/change/apply/detail', data, { mock }),
  // /receipt/list/bycontract
  postReceiptListByContract: (
    data: { contractId: string },
    functionCode: string = 'receiptlisbycontract'
  ): Promise<any> =>
    http.post('/receipt/list/bycontract', data, { mock, headers: { functionCode } }),
}

/* prettier-ignore-end */
