/* prettier-ignore-start */
import * as Types from './interface/groupManage'
import { http } from '@zswl/admin'

const mock = false
// const mock= { delay: 800 }
export default {
  // 菜单列表
  getMenuList: (params: Types.MenuListRequest): Promise<Types.MenuListResponse> =>
    http.get('/functionGroup/menu/list', { params, mock }),

  // 分组修改
  postFunctionGroupModify: (
    data: Types.FunctionGroupModifyRequest,
  ): Promise<Types.FunctionGroupModifyResponse> =>
    http.post('/functionGroup/modify', data, { mock }),

  // 分组列表
  getFunctionGroupList: (
    params: Types.FunctionGroupListRequest,
  ): Promise<Types.FunctionGroupListResponse> => http.get('/functionGroup/list', { params, mock }),

  // 分组删除
  postFunctionGroupDelete: (
    data: Types.FunctionGroupDeleteRequest,
  ): Promise<Types.FunctionGroupDeleteResponse> =>
    http.post('/functionGroup/delete', data, { mock }),

  // 分组新增
  postFunctionGroupAdd: (
    data: Types.FunctionGroupAddRequest,
  ): Promise<Types.FunctionGroupAddResponse> => http.post('/functionGroup/add', data, { mock }),

  // 分组详情
  getFunctionGroupDetail: (
    params: Types.FunctionGroupDetailRequest,
  ): Promise<Types.FunctionGroupDetailResponse> =>
    http.get('/functionGroup/detail', { params, mock }),

  // 未分组功能列表
  getFunctionGroupFunctions: (
    params: Types.FunctionGroupFunctionsRequest,
  ): Promise<Types.FunctionGroupFunctionsResponse> =>
    http.get('/functionGroup/functions', { params, mock }),
}

/* prettier-ignore-end */
