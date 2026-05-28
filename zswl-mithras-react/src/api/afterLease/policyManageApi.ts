/* prettier-ignore-start */
import * as Types from './interface/policyManageApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 新增项目列表
  getProjList: (params: Types.ProjListRequest): Promise<Types.ProjListResponse> =>
    http.get('/policy/add/proj/list', { params, mock }),

  // 保单信息列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/policy/info/list', data, { mock }),

  // 保单详情-已废弃
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/policy/info/detail', data, { mock }),

  // 保单资料清单
  postMaterialsList: (data: Types.MaterialsListRequest): Promise<Types.MaterialsListResponse> =>
    http.post('/policy/info/materials/list', data, { mock }),

  // 修改保单信息
  postInfoModify: (data: Types.InfoModifyRequest): Promise<Types.InfoModifyResponse> =>
    http.post('/policy/info/modify', data, { mock }),

  // 删除保单信息
  postInfoRemove: (data: Types.InfoRemoveRequest): Promise<Types.InfoRemoveResponse> =>
    http.post('/policy/info/remove', data, { mock }),

  // 合同列表
  getContractList: (params: Types.ContractListRequest): Promise<Types.ContractListResponse> =>
    http.get('/policy/add/contract/list', { params, mock }),

  // 导入保单信息
  postPolicyImport: (data: Types.PolicyImportRequest): Promise<Types.PolicyImportResponse> =>
    http.post('/policy/import', data, { mock, type: 'upload' }),

  // 新增保单信息
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/policy/info/add', data, { mock }),
}

/* prettier-ignore-end */
