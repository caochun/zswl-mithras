/* prettier-ignore-start */
import * as Types from './interface/litigationRegistrationApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 客户逾期信息展示
  getClientOverdueinfo: (
    params: Types.ClientOverdueinfoRequest,
  ): Promise<Types.ClientOverdueinfoResponse> =>
    http.get('/litigation/client/overdueinfo', { params, mock }),

  // 新增诉讼登记
  postLitigationAdd: (data: Types.LitigationAddRequest): Promise<Types.LitigationAddResponse> =>
    http.post('/litigation/add', data, { mock }),

  // 诉讼登记-合同相关全量客户
  postContractClient: (data: Types.ContractClientRequest): Promise<Types.ContractClientResponse> =>
    http.post('/litigation/contract/client', data, { mock }),

  // 诉讼登记-客户相关合同下拉
  getContractPulldown: (
    params: Types.ContractPulldownRequest,
  ): Promise<Types.ContractPulldownResponse> =>
    http.get('/litigation/contract/pulldown', { params, mock }),

  // 诉讼登记保存
  postLitigationSave: (data: Types.LitigationSaveRequest): Promise<Types.LitigationSaveResponse> =>
    http.post('/litigation/save', data, { mock }),

  // 诉讼登记列表
  postLitigationPageList: (
    data: Types.LitigationPageListRequest,
  ): Promise<Types.LitigationPageListResponse> => http.post('/litigation/pageList', data, { mock }),

  // 诉讼登记删除被告
  postDefendantRemove: (
    data: Types.DefendantRemoveRequest,
  ): Promise<Types.DefendantRemoveResponse> =>
    http.post('/litigation/defendant/remove', data, { mock }),

  // 诉讼登记新增被告
  postDefendantAdd: (data: Types.DefendantAddRequest): Promise<Types.DefendantAddResponse> =>
    http.post('/litigation/defendant/add', data, { mock }),

  // 诉讼登记新增进展
  postProgressAdd: (data: Types.ProgressAddRequest): Promise<Types.ProgressAddResponse> =>
    http.post('/litigation/progress/add', data, { mock }),

  // 诉讼登记详情
  getLitigationDetail: (
    params: Types.LitigationDetailRequest,
  ): Promise<Types.LitigationDetailResponse> => http.get('/litigation/detail', { params, mock }),
}

/* prettier-ignore-end */
