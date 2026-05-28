/* prettier-ignore-start */
import * as Types from './interface/billManagement'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改票据管理表
  postManagementModify: (
    data: Types.ManagementModifyRequest,
  ): Promise<Types.ManagementModifyResponse> =>
    http.post('/bill/management/modify', data, { mock }),

  // 删除票据管理表
  postManagementRemove: (
    data: Types.ManagementRemoveRequest,
  ): Promise<Types.ManagementRemoveResponse> =>
    http.post('/bill/management/remove', data, { mock }),

  // 新增票据管理表
  postManagementAdd: (data: Types.ManagementAddRequest): Promise<Types.ManagementAddResponse> =>
    http.post('/bill/management/add', data, { mock }),

  // 票据管理表列表
  postManagementList: (data: Types.ManagementListRequest): Promise<Types.ManagementListResponse> =>
    http.post('/bill/management/list', data, { mock }),
}

/* prettier-ignore-end */
