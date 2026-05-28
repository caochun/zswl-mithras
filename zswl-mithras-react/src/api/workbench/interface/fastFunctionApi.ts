/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [新增首页工作台-快捷功能-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11977) 的 **请求类型**
 *
 * @分类 [首页工作台-快捷功能-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2238)
 * @请求头 `POST /workbench/shortcuts/list`
 * @更新时间 `2023-03-17 14:47:23`
 */
export interface ShortcutsListRequest {}

/**
 * 接口 [新增首页工作台-快捷功能-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11977) 的 **返回类型**
 *
 * @分类 [首页工作台-快捷功能-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2238)
 * @请求头 `POST /workbench/shortcuts/list`
 * @更新时间 `2023-03-17 14:47:23`
 */
export type ShortcutsListResponse = {
  /**
   * 路径
   */
  path?: string
  /**
   * 菜单id
   */
  menuId?: number
  /**
   * 菜单名称
   */
  menuName?: string
  /**
   * 常用功能标识 1常用，0 其他
   */
  isCollect?: number
}[]

/**
 * 接口 [新增首页工作台-快捷功能-维护↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11976) 的 **请求类型**
 *
 * @分类 [首页工作台-快捷功能-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2238)
 * @请求头 `POST /workbench/shortcuts/maintain`
 * @更新时间 `2023-03-17 14:47:23`
 */
export interface ShortcutsMaintainRequest {
  /**
   * 常用功能id
   */
  menuIds?: number[]
}

/**
 * 接口 [新增首页工作台-快捷功能-维护↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11976) 的 **返回类型**
 *
 * @分类 [首页工作台-快捷功能-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2238)
 * @请求头 `POST /workbench/shortcuts/maintain`
 * @更新时间 `2023-03-17 14:47:23`
 */
export interface ShortcutsMaintainResponse {
  /**
   * 是否成功
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 提示信息
   */
  msg?: string
  /**
   * 异常时返回的异常信息
   */
  description?: string
  /**
   * 不阻断操作流程的toast提示
   */
  toast?: string
}

/* prettier-ignore-end */
