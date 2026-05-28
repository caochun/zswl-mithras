/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改授信↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11234) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/modify`
 * @更新时间 `2022-12-23 09:25:53`
 */
export interface CreditModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 资金用途
   */
  fundUsage?: string
  /**
   * 增信方式
   */
  enhanceCreditMethod?: string[]
  /**
   * 额度是否可循环
   */
  recyclable?: number
  /**
   * 担保明细
   */
  guaranteeDetail?: {
    /**
     * id
     */
    id?: number
    /**
     * 所属授信id
     */
    creditId?: number
    /**
     * 担保机构id
     */
    guaranteeAgencyId?: number
    /**
     * 担保金额
     */
    guaranteeAmount?: number
    /**
     * 剩余担保金额
     */
    remainingLimit?: number
  }[]
  /**
   * 授信生效时间
   */
  effectiveDateFrom?: string
  /**
   * 授信生效时间
   */
  effectiveDateTo?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改授信↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11234) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/modify`
 * @更新时间 `2022-12-23 09:25:53`
 */
export interface CreditModifyResponse {
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
 * 接口 [删除授信↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11236) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/remove`
 * @更新时间 `2022-12-19 19:48:32`
 */
export interface CreditRemoveRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [删除授信↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11236) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/remove`
 * @更新时间 `2022-12-19 19:48:32`
 */
export interface CreditRemoveResponse {
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
 * 接口 [授信列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11235) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/list`
 * @更新时间 `2022-12-26 10:36:32`
 */
export interface CreditListRequest {
  /**
   * 授信机构
   */
  organizationId?: number
  /**
   * 授信机构类型
   */
  organizationType?: string
  /**
   * 授信额度From
   */
  creditLimitFrom?: number
  /**
   * 授信额度To
   */
  creditLimitTo?: number
  /**
   * 授信日期From
   */
  creditDateFrom?: string
  /**
   * 授信日期To
   */
  creditDateTo?: string
  /**
   * 创建人
   */
  createBy?: number
  /**
   * 创建时间From
   */
  createTimeFrom?: string
  /**
   * 创建时间To
   */
  createTimeTo?: string
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
}

/**
 * 接口 [授信列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11235) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/list`
 * @更新时间 `2022-12-26 10:36:32`
 */
export interface CreditListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 授信编号
     */
    creditCode?: string
    /**
     * 授信机构
     */
    organizationId?: number
    /**
     * 授信机构名称
     */
    organizationName?: string
    /**
     * 总授信额度
     */
    totalCreditLimit?: number
    /**
     * 信用额度
     */
    creditLimit?: number
    /**
     * 已使用总授信额度（元）
     */
    usedTotalCreditAmount?: number
    /**
     * 已使用信用额度（元）
     */
    usedCreditAmount?: number
    /**
     * 已使用担保额度（元）
     */
    usedGuaranteeAmount?: number
    /**
     * 剩余授信时间
     */
    remainingDays?: number
    /**
     * 创建人id
     */
    createBy?: number
    /**
     * 创建人name
     */
    createByName?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
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
}

/**
 * 接口 [授信详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11232) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/detail`
 * @更新时间 `2022-12-23 13:30:22`
 */
export interface CreditDetailRequest {
  id?: number
}

/**
 * 接口 [授信详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11232) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/detail`
 * @更新时间 `2022-12-23 13:30:22`
 */
export interface CreditDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 授信机构
   */
  organizationId?: number
  /**
   * 授信机构名称
   */
  organizationName?: string
  /**
   * 银行联号或统一社会信用代码
   */
  organizationCode?: string
  /**
   * 授信额度
   */
  totalCreditLimit?: number
  /**
   * 授信编号
   */
  creditCode?: string
  /**
   * 资金用途
   */
  fundUsage?: string
  /**
   * 增信方式
   */
  enhanceCreditMethod?: string[]
  /**
   * 额度是否可循环
   */
  recyclable?: number
  /**
   * 担保明细
   */
  guaranteeDetail?: {
    /**
     * id
     */
    id?: number
    /**
     * 所属授信id
     */
    creditId?: number
    /**
     * 担保机构id
     */
    guaranteeAgencyId?: number
    /**
     * 担保金额
     */
    guaranteeAmount?: number
    /**
     * 剩余担保金额
     */
    remainingLimit?: number
  }[]
  /**
   * 授信生效时间
   */
  effectiveDateFrom?: string
  /**
   * 授信生效时间
   */
  effectiveDateTo?: string
  /**
   * 资金经理
   */
  fundManager?: number
  /**
   * 资金经理姓名
   */
  fundManagerName?: string
  /**
   * 资金经理部门
   */
  fundManagerDept?: string
  /**
   * 资金经理部门负责人
   */
  fundManagerDeptLeader?: string
  /**
   * 资金经理分管领导
   */
  fundManagerDivisionLeader?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [文件上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11307) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/file/upload`
 * @更新时间 `2022-12-26 10:36:33`
 */
export interface FileUploadRequest {
  file: File
  belongId: string
}

/**
 * 接口 [文件上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11307) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/file/upload`
 * @更新时间 `2022-12-26 10:36:33`
 */
export interface FileUploadResponse {
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
 * 接口 [文件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11304) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/file/list`
 * @更新时间 `2022-12-26 10:36:32`
 */
export interface FileListRequest {
  id?: number
}

/**
 * 接口 [文件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11304) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/file/list`
 * @更新时间 `2022-12-26 10:36:32`
 */
export type FileListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 文件名
   */
  fileName?: string
}[]

/**
 * 接口 [文件删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11313) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/file/remove`
 * @更新时间 `2022-12-26 13:49:42`
 */
export interface FileRemoveRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [文件删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11313) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/file/remove`
 * @更新时间 `2022-12-26 13:49:42`
 */
export interface FileRemoveResponse {
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
 * 接口 [新增授信↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11233) 的 **请求类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/add`
 * @更新时间 `2022-12-23 09:27:32`
 */
export interface CreditAddRequest {
  /**
   * 授信机构
   */
  organizationId: number
  /**
   * 授信金额
   */
  totalCreditLimit: number
}

/**
 * 接口 [新增授信↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11233) 的 **返回类型**
 *
 * @分类 [授信管理-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1964)
 * @请求头 `POST /fund/credit/add`
 * @更新时间 `2022-12-23 09:27:32`
 */
export interface CreditAddResponse {
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
