/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改资金管理-机构表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11217) 的 **请求类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/modify`
 * @更新时间 `2022-12-14 10:36:03`
 */
export interface OrganizationModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 机构名称
   */
  organizationName?: string
  /**
   * 机构编号
   */
  organizationCode?: string
  /**
   * 机构类型 枚举
   */
  organizationType?: string
  /**
   * 联系人 json
   */
  contactInfo?: {
    name?: string
    job?: string
    tel?: string
    email?: string
  }
  /**
   * 地址信息
   */
  addressInfo?: {
    nation?: string
    province?: string
    city?: string
    address?: string
  }
  /**
   * 银行联行号
   */
  interBankNo?: string
  /**
   * 统一社会信用代码
   */
  uscCode?: string
  /**
   * 账户信息
   */
  accountsInfo?: {
    accountName?: string
    depositBank?: string
    account?: string
    mainAccount?: boolean
  }[]
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改资金管理-机构表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11217) 的 **返回类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/modify`
 * @更新时间 `2022-12-14 10:36:03`
 */
export interface OrganizationModifyResponse {
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
 * 接口 [删除资金管理-机构表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11218) 的 **请求类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/remove`
 * @更新时间 `2022-12-14 10:36:03`
 */
export interface OrganizationRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资金管理-机构表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11218) 的 **返回类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/remove`
 * @更新时间 `2022-12-14 10:36:03`
 */
export interface OrganizationRemoveResponse {
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
 * 接口 [新增资金管理-机构表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11216) 的 **请求类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/add`
 * @更新时间 `2022-12-14 10:36:03`
 */
export interface OrganizationAddRequest {
  /**
   * 机构名称
   */
  organizationName?: string
  /**
   * 机构编号
   */
  organizationCode?: string
  /**
   * 机构类型 枚举
   */
  organizationType?: string
  /**
   * 联系人
   */
  contactInfo?: {
    name?: string
    job?: string
    tel?: string
    email?: string
  }
  /**
   * 地址信息
   */
  addressInfo?: {
    nation?: string
    province?: string
    city?: string
    address?: string
  }
  /**
   * 银行联行号, 类型为银行时
   */
  interBankNo?: string
  /**
   * 统一社会信用代码，类型为租赁公司时
   */
  uscCode?: string
  /**
   * 账户信息
   */
  accountsInfo?: {
    accountName?: string
    depositBank?: string
    account?: string
    mainAccount?: boolean
  }[]
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [新增资金管理-机构表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11216) 的 **返回类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/add`
 * @更新时间 `2022-12-14 10:36:03`
 */
export interface OrganizationAddResponse {
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
 * 接口 [资金管理-机构表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11215) 的 **请求类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/list`
 * @更新时间 `2022-12-14 13:41:41`
 */
export interface OrganizationListRequest {
  /**
   * 机构名称
   */
  organizationName?: string
  /**
   * 机构类型 枚举
   */
  organizationType?: string
  /**
   * 创建人
   */
  createBy?: number
  /**
   * 创建时间from
   */
  createDateFrom?: string
  /**
   * 创建时间to
   */
  createDateTo?: string
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
 * 接口 [资金管理-机构表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11215) 的 **返回类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/list`
 * @更新时间 `2022-12-14 13:41:41`
 */
export interface OrganizationListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 机构名称
     */
    organizationName?: string
    /**
     * 机构编号
     */
    organizationCode?: string
    /**
     * 机构类型 枚举
     */
    organizationType?: string
    /**
     * 联系人名称
     */
    contactName?: string
    createBy?: number
    /**
     * 创建人
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
 * 接口 [资金管理-机构表详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11219) 的 **请求类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/detail`
 * @更新时间 `2022-12-14 13:41:41`
 */
export interface OrganizationDetailRequest {
  id?: number
}

/**
 * 接口 [资金管理-机构表详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11219) 的 **返回类型**
 *
 * @分类 [资金管理-机构表-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1960)
 * @请求头 `POST /fund/organization/detail`
 * @更新时间 `2022-12-14 13:41:41`
 */
export interface OrganizationDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 机构名称
   */
  organizationName?: string
  /**
   * 机构编号
   */
  organizationCode?: string
  /**
   * 机构类型 枚举
   */
  organizationType?: string
  /**
   * 联系人 json
   */
  contactInfo?: {
    name?: string
    job?: string
    tel?: string
    email?: string
  }
  /**
   * 地址信息
   */
  addressInfo?: {
    nation?: string
    province?: string
    city?: string
    address?: string
  }
  /**
   * 银行联行号
   */
  interBankNo?: string
  /**
   * 统一社会信用代码
   */
  uscCode?: string
  /**
   * 账户信息
   */
  accountsInfo?: {
    accountName?: string
    depositBank?: string
    account?: string
    mainAccount?: boolean
  }[]
  /**
   * 备注
   */
  remark?: string
}

/* prettier-ignore-end */
