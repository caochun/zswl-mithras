/* prettier-ignore-start */
import * as Types from './interface/sealForDocumentsApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 文书用印列表
  postPrintingPageList: (
    data: Types.PrintingPageListRequest,
  ): Promise<Types.PrintingPageListResponse> => http.post('/printing/pageList', data, { mock }),

  // 文书用印删除
  postPrintingRemove: (data: Types.PrintingRemoveRequest): Promise<Types.PrintingRemoveResponse> =>
    http.post('/printing/remove', data, { mock }),

  // 文书用印提交审批
  postPrintingSubmit: (data: Types.PrintingSubmitRequest): Promise<Types.PrintingSubmitResponse> =>
    http.post('/printing/submit', data, { mock }),

  // 文书用印新增
  postPrintingAdd: (data: Types.PrintingAddRequest): Promise<Types.PrintingAddResponse> =>
    http.post('/printing/add', data, { mock }),

  // 文书用印编辑
  postPrintingSave: (data: Types.PrintingSaveRequest): Promise<Types.PrintingSaveResponse> =>
    http.post('/printing/save', data, { mock }),

  // 文书用印详情
  postPrintingDetail: (data: Types.PrintingDetailRequest): Promise<Types.PrintingDetailResponse> =>
    http.post('/printing/detail', data, { mock }),
}

/* prettier-ignore-end */
