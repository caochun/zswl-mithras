/* prettier-ignore-start */
import * as Types from './interface/fundCostDetailApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改费用
  postFeeModify: (data: Types.FeeModifyRequest): Promise<Types.FeeModifyResponse> =>
    http.post('/fund/financing/fee/modify', data, { mock }),

  // 删除费用项
  postFeeRemove: (data: Types.FeeRemoveRequest): Promise<Types.FeeRemoveResponse> =>
    http.post('/fund/financing/fee/remove', data, { mock }),

  // 新增费用项
  postFeeAdd: (data: Types.FeeAddRequest): Promise<Types.FeeAddResponse> =>
    http.post('/fund/financing/fee/add', data, { mock }),

  // 费用项列表
  postFeeList: (data: Types.FeeListRequest): Promise<Types.FeeListResponse> =>
    http.post('/fund/financing/fee/list', data, { mock }),
}

/* prettier-ignore-end */
