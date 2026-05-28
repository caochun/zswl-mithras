/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [苍穹接口调用记录-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6085) 的 **请求类型**
 *
 * @分类 [苍穹接口调用记录-人工处理相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1592)
 * @请求头 `POST /third/cq/record/pagelist`
 * @更新时间 `2024-08-01 15:03:19`
 */
export interface RecordPagelistRequest {
  /**
   * 唯一标识编号
   */
  businessId?: string
  /**
   * 是否已操作
   */
  isDone?: number
  /**
   * 苍穹单据类型
   */
  billType?: string
  /**
   * 关联id
   */
  businessTitle?: string
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
 * 接口 [苍穹接口调用记录-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6085) 的 **返回类型**
 *
 * @分类 [苍穹接口调用记录-人工处理相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1592)
 * @请求头 `POST /third/cq/record/pagelist`
 * @更新时间 `2024-08-01 15:03:19`
 */
export interface RecordPagelistResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * ID
     */
    id?: number
    /**
     * 苍穹单据类型
     */
    billType?: string
    /**
     * 唯一标识编号
     */
    businessId?: string
    /**
     * 请求参数
     */
    reqJson?: string
    /**
     * 返回参数
     */
    resJson?: string
    /**
     * 是否已操作
     */
    isDone?: number
    /**
     * 情况说明
     */
    situationDescription?: string
    /**
     * 关联流水类型
     */
    source?: string
    /**
     * 关联流水类型名称
     */
    sourceName?: string
    /**
     * 关联id
     */
    businessTitle?: string
    /**
     * 接口状态
     */
    status?: string
    /**
     * 失败原因
     */
    withdrawFailMessage?: string
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
 * 接口 [苍穹接口调用记录-忽略↗](http://yapi.zswltec.com:3000/project/11/interface/api/6073) 的 **请求类型**
 *
 * @分类 [苍穹接口调用记录-人工处理相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1592)
 * @请求头 `POST /third/cq/record/ignore`
 * @更新时间 `2024-07-17 11:34:06`
 */
export interface RecordIgnoreRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [苍穹接口调用记录-忽略↗](http://yapi.zswltec.com:3000/project/11/interface/api/6073) 的 **返回类型**
 *
 * @分类 [苍穹接口调用记录-人工处理相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1592)
 * @请求头 `POST /third/cq/record/ignore`
 * @更新时间 `2024-07-17 11:34:06`
 */
export interface RecordIgnoreResponse {
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
 * 接口 [苍穹接口调用记录-推送↗](http://yapi.zswltec.com:3000/project/11/interface/api/6079) 的 **请求类型**
 *
 * @分类 [苍穹接口调用记录-人工处理相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1592)
 * @请求头 `POST /third/cq/record/push`
 * @更新时间 `2024-07-17 11:34:06`
 */
export interface RecordPushRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [苍穹接口调用记录-推送↗](http://yapi.zswltec.com:3000/project/11/interface/api/6079) 的 **返回类型**
 *
 * @分类 [苍穹接口调用记录-人工处理相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1592)
 * @请求头 `POST /third/cq/record/push`
 * @更新时间 `2024-07-17 11:34:06`
 */
export interface RecordPushResponse {
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
