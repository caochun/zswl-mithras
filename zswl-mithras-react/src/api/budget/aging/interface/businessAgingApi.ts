/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [关闭帐龄主表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21589) 的 **请求类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/close`
 * @更新时间 `2024-09-11 18:28:34`
 */
export interface InfoCloseRequest {
  /**
   * 主键id
   */
  id?: number
}

/**
 * 接口 [关闭帐龄主表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21589) 的 **返回类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/close`
 * @更新时间 `2024-09-11 18:28:34`
 */
export interface InfoCloseResponse {
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
 * 接口 [删除帐龄主表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21601) 的 **请求类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/remove`
 * @更新时间 `2024-09-11 18:28:41`
 */
export interface InfoRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除帐龄主表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21601) 的 **返回类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/remove`
 * @更新时间 `2024-09-11 18:28:41`
 */
export interface InfoRemoveResponse {
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
 * 接口 [帐龄主表-完成↗](http://yapi.zswltec.com:3000/project/11/interface/api/21607) 的 **请求类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/effect`
 * @更新时间 `2024-09-11 18:28:46`
 */
export interface InfoEffectRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [帐龄主表-完成↗](http://yapi.zswltec.com:3000/project/11/interface/api/21607) 的 **返回类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/effect`
 * @更新时间 `2024-09-11 18:28:46`
 */
export interface InfoEffectResponse {
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
 * 接口 [帐龄主表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21595) 的 **请求类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/list`
 * @更新时间 `2024-09-11 18:48:38`
 */
export interface InfoListRequest {
  /**
   * 状态
   */
  status?: string
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
 * 接口 [帐龄主表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21595) 的 **返回类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/list`
 * @更新时间 `2024-09-11 18:48:38`
 */
export interface InfoListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 截止日期
     */
    deadline?: string
    /**
     * 核算组织编码 默认 10000396
     */
    accountancyOrganizationNumber?: string
    /**
     * 核算组织名称 默认 浙江浙商融资租赁有限公司
     */
    accountancyOrganizationName?: string
    /**
     * 状态 financialAccountAgeRecordStatus
     */
    status?: string
    /**
     * 逻辑删除，0-未删除，1-已删除
     */
    deleted?: number
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 创建人
     */
    createBy?: number
    createByName?: string
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

/**
 * 接口 [帐龄主表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/21649) 的 **请求类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/detail`
 * @更新时间 `2024-09-11 19:04:17`
 */
export interface InfoDetailRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [帐龄主表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/21649) 的 **返回类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/detail`
 * @更新时间 `2024-09-11 19:04:17`
 */
export interface InfoDetailResponse {
  /**
   * 主键id
   */
  id?: number
  /**
   * 截止日期
   */
  deadline?: string
  /**
   * 核算组织编码 默认 10000396
   */
  accountancyOrganizationNumber?: string
  /**
   * 核算组织名称 默认 浙江浙商融资租赁有限公司
   */
  accountancyOrganizationName?: string
  /**
   * 状态 financialAccountAgeRecordStatus
   */
  status?: string
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 创建人
   */
  createBy?: number
  createByName?: string
}

/**
 * 接口 [新增帐龄主表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21583) 的 **请求类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/add`
 * @更新时间 `2024-09-11 18:28:26`
 */
export interface InfoAddRequest {
  /**
   * 截止日期
   */
  deadline: string
  /**
   * 核算组织编码 默认 10000396
   */
  accountancyOrganizationNumber?: string
  /**
   * 核算组织名称 默认 浙江浙商融资租赁有限公司
   */
  accountancyOrganizationName?: string
  /**
   * 状态
   */
  status?: string
}

/**
 * 接口 [新增帐龄主表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21583) 的 **返回类型**
 *
 * @分类 [帐龄主表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3128)
 * @请求头 `POST /finance/account/age/base/info/add`
 * @更新时间 `2024-09-11 18:28:26`
 */
export type InfoAddResponse = number

/* prettier-ignore-end */
