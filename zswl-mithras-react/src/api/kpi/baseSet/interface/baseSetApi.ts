/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [删除绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22771) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/remove`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22771) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/remove`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseRemoveResponse {
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

/**
 * 接口 [删除绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22777) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/remove`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseRemoveRequest {
  /**
   * id
   */
  id: number
  /**
   * 生效月份
   */
  effectMonth?: string
}

/**
 * 接口 [删除绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22777) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/remove`
 * @更新时间 `2024-09-29 14:47:58`
 */
export type BaseRemoveResponse = number

/**
 * 接口 [复制到绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22819) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/copy`
 * @更新时间 `2024-09-29 16:20:56`
 */
export interface BaseCopyRequest {
  /**
   * id
   */
  id: number
  /**
   * 生效月份
   */
  effectMonth?: string
}

/**
 * 接口 [复制到绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22819) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/copy`
 * @更新时间 `2024-09-29 16:20:56`
 */
export type BaseCopyResponse = number

/**
 * 接口 [新增绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22753) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/add`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseAddRequest {
  /**
   * 生效月份
   */
  effectMonth?: string
  /**
   * 参数状态
   */
  parameterStatus?: string
}

/**
 * 接口 [新增绩效考核-参数设置基本表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22753) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/add`
 * @更新时间 `2024-09-29 14:47:58`
 */
export type BaseAddResponse = number

/**
 * 接口 [绩效考核-参数设置基本表-关闭↗](http://yapi.zswltec.com:3000/project/11/interface/api/22747) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/close`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseCloseRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [绩效考核-参数设置基本表-关闭↗](http://yapi.zswltec.com:3000/project/11/interface/api/22747) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/close`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseCloseResponse {
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

/**
 * 接口 [绩效考核-参数设置基本表-生效↗](http://yapi.zswltec.com:3000/project/11/interface/api/22759) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/effect`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseEffectRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [绩效考核-参数设置基本表-生效↗](http://yapi.zswltec.com:3000/project/11/interface/api/22759) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/effect`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseEffectResponse {
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

/**
 * 接口 [绩效考核-参数设置基本表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22765) 的 **请求类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/list`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseListRequest {
  /**
   * 参数状态
   */
  parameterStatus?: string
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [绩效考核-参数设置基本表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22765) 的 **返回类型**
 *
 * @分类 [绩效考核-参数设置基本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3284)
 * @请求头 `POST /kpi/parameter/base/list`
 * @更新时间 `2024-09-29 14:47:58`
 */
export interface BaseListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 生效月份 RecordStatus
     */
    effectMonth?: string
    /**
     * 参数状态
     */
    parameterStatus?: string
  }[]
  /**
   * 数据总记录数。
   */
  total?: number
  /**
   * 总页数
   */
  pages?: number
  /**
   * 页大小
   */
  pageSize?: number
  /**
   * 当前页
   */
  currentPage?: number
  /**
   * 其他携带参数
   */
  others?: {
    KEY?: {}
  }
}

/* prettier-ignore-end */
