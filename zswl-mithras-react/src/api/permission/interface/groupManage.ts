/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [菜单列表↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11184) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/menu/list`
 * @更新时间 `2022-12-09 09:50:53`
 */
export interface MenuListRequest {}

/**
 * 接口 [菜单列表↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11184) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/menu/list`
 * @更新时间 `2022-12-09 09:50:53`
 */
export type MenuListResponse = {
  icon: string
  groupName: string
  groupId: number
  code: string
  enName: string | null
  sortNo: number
  parentId: null
  gmtCreate: string
  children: {
    sortNo: number
    path: string
    code: string
    menuId: number
    menuName: string
    gmtCreate: number
    enName: string
    icon: string
  }[]
}[]

/**
 * 接口 [分组修改↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11211) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `POST /functionGroup/modify`
 * @更新时间 `2022-12-12 14:55:36`
 */
export interface FunctionGroupModifyRequest {
  menuId: number
  code: string
  name: string
  describe: string
  functionIds: number[]
}

/**
 * 接口 [分组修改↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11211) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `POST /functionGroup/modify`
 * @更新时间 `2022-12-12 14:55:36`
 */
export type FunctionGroupModifyResponse = null

/**
 * 接口 [分组列表↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11192) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/list`
 * @更新时间 `2022-12-09 10:21:14`
 */
export interface FunctionGroupListRequest {
  menuId: string
}

/**
 * 接口 [分组列表↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11192) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/list`
 * @更新时间 `2022-12-09 10:21:14`
 */
export type FunctionGroupListResponse = {
  id?: number
  code?: string
  name?: string
  describe?: string
}[]

/**
 * 接口 [分组删除↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11212) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `POST /functionGroup/delete`
 * @更新时间 `2022-12-12 14:56:40`
 */
export interface FunctionGroupDeleteRequest {
  id: number
}

/**
 * 接口 [分组删除↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11212) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `POST /functionGroup/delete`
 * @更新时间 `2022-12-12 14:56:40`
 */
export type FunctionGroupDeleteResponse = null

/**
 * 接口 [分组新增↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11210) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `POST /functionGroup/add`
 * @更新时间 `2022-12-12 13:50:10`
 */
export interface FunctionGroupAddRequest {
  menuId: number
  code: string
  name: string
  describe: string
  functionIds: number[]
}

/**
 * 接口 [分组新增↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11210) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `POST /functionGroup/add`
 * @更新时间 `2022-12-12 13:50:10`
 */
export type FunctionGroupAddResponse = null

/**
 * 接口 [分组详情↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11209) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/detail`
 * @更新时间 `2022-12-12 13:41:40`
 */
export interface FunctionGroupDetailRequest {
  groupId: string
}

/**
 * 接口 [分组详情↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11209) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/detail`
 * @更新时间 `2022-12-12 13:41:40`
 */
export interface FunctionGroupDetailResponse {
  id?: number
  code?: string
  name?: string
  describe?: string
  functions?: {
    funcId: number
    name: string
    code: string
    enName: string | null
    type: null
    hasPermission: boolean
  }[]
}

/**
 * 接口 [未分组功能列表↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11213) 的 **请求类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/functions`
 * @更新时间 `2022-12-12 17:30:31`
 */
export interface FunctionGroupFunctionsRequest {
  menuId: string
}

/**
 * 接口 [未分组功能列表↗](http://api-dev.zswl.cn:3011/project/15/interface/api/11213) 的 **返回类型**
 *
 * @分类 [功能分组↗](http://api-dev.zswl.cn:3011/project/15/interface/api/cat_1958)
 * @请求头 `GET /functionGroup/functions`
 * @更新时间 `2022-12-12 17:30:31`
 */
export type FunctionGroupFunctionsResponse = {
  funcId: number
  name: string
  code: string
  enName: string | null
  type: null
  hasPermission: boolean
}[]

/* prettier-ignore-end */
