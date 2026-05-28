/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [客户逾期信息展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/25717) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `GET /litigation/client/overdueinfo`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface ClientOverdueinfoRequest {
  clientId: string
}

/**
 * 接口 [客户逾期信息展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/25717) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `GET /litigation/client/overdueinfo`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface ClientOverdueinfoResponse {
  clientId?: number
  clientName?: string
  overdueDays?: number
  overdueAmount?: number
  riskExposure?: number
}

/**
 * 接口 [新增诉讼登记↗](http://yapi.zswltec.com:3000/project/11/interface/api/25489) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/add`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface LitigationAddRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
}

/**
 * 接口 [新增诉讼登记↗](http://yapi.zswltec.com:3000/project/11/interface/api/25489) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/add`
 * @更新时间 `2024-11-07 14:34:38`
 */
export type LitigationAddResponse = number

/**
 * 接口 [诉讼登记-合同相关全量客户↗](http://yapi.zswltec.com:3000/project/11/interface/api/25675) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/contract/client`
 * @更新时间 `2024-11-06 16:43:40`
 */
export interface ContractClientRequest {
  /**
   * 合同id集合
   */
  contractIds?: number[]
}

/**
 * 接口 [诉讼登记-合同相关全量客户↗](http://yapi.zswltec.com:3000/project/11/interface/api/25675) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/contract/client`
 * @更新时间 `2024-11-06 16:43:40`
 */
export type ContractClientResponse = {
  clientId?: number
  name?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同中的角色
   */
  role?: string
  /**
   * 证件类型
   */
  certificateType?: string
  /**
   * 证件号码
   */
  certificateNumber?: string
}[]

/**
 * 接口 [诉讼登记-客户相关合同下拉↗](http://yapi.zswltec.com:3000/project/11/interface/api/25669) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `GET /litigation/contract/pulldown`
 * @更新时间 `2024-11-06 16:28:47`
 */
export interface ContractPulldownRequest {
  clientId: string
}

/**
 * 接口 [诉讼登记-客户相关合同下拉↗](http://yapi.zswltec.com:3000/project/11/interface/api/25669) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `GET /litigation/contract/pulldown`
 * @更新时间 `2024-11-06 16:28:47`
 */
export interface ContractPulldownResponse {}

/**
 * 接口 [诉讼登记保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/25507) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/save`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface LitigationSaveRequest {
  /**
   * 主键
   */
  id?: number
  /**
   * 合同id
   */
  contractIds?: number[]
  /**
   * 审判信息
   */
  trialInfo?: {
    id?: number
    /**
     * 诉讼登记id
     */
    lrId?: number
    /**
     * 一审案件编号
     */
    fcaseNo?: string
    /**
     * 一审立案时间
     */
    ffilingDate?: string
    /**
     * 一审受理法院
     */
    facceptingCourt?: string
    /**
     * 一开庭日期
     */
    fhearingDate?: string
    /**
     * 一审判决日期
     */
    fjudgmentDate?: string
    /**
     * 二审案件编号
     */
    scaseNo?: string
    /**
     * 二审立案时间
     */
    sfilingDate?: string
    /**
     * 二审受理法院
     */
    sacceptingCourt?: string
    /**
     * 二审开庭日期
     */
    shearingDate?: string
    /**
     * 二审判决日期
     */
    sjudgmentDate?: string
    /**
     * 再审案件编号
     */
    tcaseNo?: string
    /**
     * 再审立案时间
     */
    tfilingDate?: string
    /**
     * 再审受理法院
     */
    tacceptingCourt?: string
    /**
     * 再审开庭日期
     */
    thearingDate?: string
    /**
     * 再审判决日期
     */
    tjudgmentDate?: string
    /**
     * 执行案号
     */
    executionNo?: string
    /**
     * 执行日期
     */
    executionDate?: string
    /**
     * 保全完成日期
     */
    preservationCompletionDate?: string
    /**
     * 查封到期日期
     */
    sealingExpirationDate?: string
  }
}

/**
 * 接口 [诉讼登记保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/25507) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/save`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface LitigationSaveResponse {
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
 * 接口 [诉讼登记列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25495) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/pageList`
 * @更新时间 `2024-11-06 16:28:47`
 */
export interface LitigationPageListRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 诉讼状态 (PREPARE: 诉前准备, LITIGATION: 诉讼中, EXECUTION: 执行中, EXECUTION_END: 执行终本, END: 已结案)
   */
  status?: string
  /**
   * 诉讼登记编号
   */
  code?: string
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
 * 接口 [诉讼登记列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25495) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/pageList`
 * @更新时间 `2024-11-06 16:28:47`
 */
export interface LitigationPageListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 诉讼登记编号
     */
    code?: string
    /**
     * 合同编号
     */
    contractCodes?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 诉讼登记状态
     */
    status?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 创建人
     */
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
  others?: {}
}

/**
 * 接口 [诉讼登记删除被告↗](http://yapi.zswltec.com:3000/project/11/interface/api/25525) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/defendant/remove`
 * @更新时间 `2024-11-06 16:28:48`
 */
export interface DefendantRemoveRequest {
  /**
   * 诉讼登记id
   */
  lrId?: number
  /**
   * 主键列表
   */
  ids?: number[]
}

/**
 * 接口 [诉讼登记删除被告↗](http://yapi.zswltec.com:3000/project/11/interface/api/25525) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/defendant/remove`
 * @更新时间 `2024-11-06 16:28:48`
 */
export interface DefendantRemoveResponse {
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
 * 接口 [诉讼登记新增被告↗](http://yapi.zswltec.com:3000/project/11/interface/api/25519) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/defendant/add`
 * @更新时间 `2024-11-06 16:28:48`
 */
export interface DefendantAddRequest {
  /**
   * 诉讼登记id
   */
  lrId?: number
  /**
   * 被告名称
   */
  name?: string
  /**
   * 合同地位
   */
  role?: string
  /**
   * 证件类型
   */
  certificateType?: string
  /**
   * 证件号码
   */
  certificateNumber?: string
}

/**
 * 接口 [诉讼登记新增被告↗](http://yapi.zswltec.com:3000/project/11/interface/api/25519) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/defendant/add`
 * @更新时间 `2024-11-06 16:28:48`
 */
export interface DefendantAddResponse {
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
 * 接口 [诉讼登记新增进展↗](http://yapi.zswltec.com:3000/project/11/interface/api/25513) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/progress/add`
 * @更新时间 `2024-11-04 10:51:27`
 */
export interface ProgressAddRequest {
  /**
   * 诉讼登记id
   */
  lrId?: number
  /**
   * 阶段
   */
  stage?: string
  /**
   * 状态
   */
  status?: string
}

/**
 * 接口 [诉讼登记新增进展↗](http://yapi.zswltec.com:3000/project/11/interface/api/25513) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `POST /litigation/progress/add`
 * @更新时间 `2024-11-04 10:51:27`
 */
export interface ProgressAddResponse {
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
 * 接口 [诉讼登记详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25501) 的 **请求类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `GET /litigation/detail`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface LitigationDetailRequest {
  id: string
}

/**
 * 接口 [诉讼登记详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25501) 的 **返回类型**
 *
 * @分类 [诉讼登记管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3542)
 * @请求头 `GET /litigation/detail`
 * @更新时间 `2024-11-07 14:34:38`
 */
export interface LitigationDetailResponse {
  /**
   * 主键
   */
  id?: number
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同id
   */
  contractIds?: number[]
  /**
   * 合同编号
   */
  contractCodes?: string[]
  /**
   * 被告列表
   */
  defendants?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 被告名称
     */
    name?: string
    /**
     * 被告角色
     */
    role?: string
    /**
     * 证件类型
     */
    certificateType?: string
    /**
     * 证件号码
     */
    certificateNumber?: string
  }[]
  /**
   * 进展列表
   */
  caseProgresses?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 案件阶段
     */
    stage?: string
    /**
     * 案件状态
     */
    status?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 创建人
     */
    processPerson?: string
  }[]
  /**
   * 诉讼登记信息
   */
  trialInfo?: {
    id?: number
    /**
     * 诉讼登记id
     */
    lrId?: number
    /**
     * 一审案件编号
     */
    fcaseNo?: string
    /**
     * 一审立案时间
     */
    ffilingDate?: string
    /**
     * 一审受理法院
     */
    facceptingCourt?: string
    /**
     * 一开庭日期
     */
    fhearingDate?: string
    /**
     * 一审判决日期
     */
    fjudgmentDate?: string
    /**
     * 二审案件编号
     */
    scaseNo?: string
    /**
     * 二审立案时间
     */
    sfilingDate?: string
    /**
     * 二审受理法院
     */
    sacceptingCourt?: string
    /**
     * 二审开庭日期
     */
    shearingDate?: string
    /**
     * 二审判决日期
     */
    sjudgmentDate?: string
    /**
     * 再审案件编号
     */
    tcaseNo?: string
    /**
     * 再审立案时间
     */
    tfilingDate?: string
    /**
     * 再审受理法院
     */
    tacceptingCourt?: string
    /**
     * 再审开庭日期
     */
    thearingDate?: string
    /**
     * 再审判决日期
     */
    tjudgmentDate?: string
    /**
     * 执行案号
     */
    executionNo?: string
    /**
     * 执行日期
     */
    executionDate?: string
    /**
     * 保全完成日期
     */
    preservationCompletionDate?: string
    /**
     * 查封到期日期
     */
    sealingExpirationDate?: string
  }
}

/* prettier-ignore-end */
