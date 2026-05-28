/* prettier-ignore-start */
import * as Types from './interface/financialManageOrg'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改资金管理-机构表
  postOrganizationModify: (
    data: Types.OrganizationModifyRequest
  ): Promise<Types.OrganizationModifyResponse> =>
    http.post('/fund/organization/modify', data, { mock }),

  // 删除资金管理-机构表
  postOrganizationRemove: (
    data: Types.OrganizationRemoveRequest
  ): Promise<Types.OrganizationRemoveResponse> =>
    http.post('/fund/organization/remove', data, { mock }),

  // 新增资金管理-机构表
  postOrganizationAdd: (
    data: Types.OrganizationAddRequest
  ): Promise<Types.OrganizationAddResponse> => http.post('/fund/organization/add', data, { mock }),

  // 资金管理-机构表列表
  postOrganizationList: (
    data: Types.OrganizationListRequest
  ): Promise<Types.OrganizationListResponse> =>
    http.post('/fund/organization/list', data, { mock }),

  // 资金管理-机构表详情
  postOrganizationDetail: (
    data: Types.OrganizationDetailRequest
  ): Promise<Types.OrganizationDetailResponse> =>
    http.post('/fund/organization/detail', data, { mock }),
}

/* prettier-ignore-end */
