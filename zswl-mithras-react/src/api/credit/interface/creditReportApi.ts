/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [征信查询批量导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/37885) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/export`
 * @更新时间 `2025-10-27 08:46:52`
 */
export interface BaseExportRequest {
  sourceScene?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
  /**
   * 查询编号
   */
  creditCode?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 统一社会信用代码
   */
  cscCode?: string
  /**
   * 申请人
   */
  applyUser?: string
  /**
   * 申请部门
   */
  applyOrg?: string
  /**
   * 查询完成时间-开始
   */
  createFrom?: string
  /**
   * 查询完成时间-结束
   */
  createTo?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 查询目的
   */
  selectGoal?: string
  /**
   * 项目id
   */
  projId?: number
  /**
   * 客户id
   */
  clientId?: number
  targetClientIds?: number[]
  searchTimeFrom?: string
  searchTimeTo?: string
}

/**
 * 接口 [征信查询批量导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/37885) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/export`
 * @更新时间 `2025-10-27 08:46:52`
 */
export interface BaseExportResponse {}

/**
 * 接口 [客户比对承租人及担保人工商信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37879) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/creditSearch/client/compare/business`
 * @更新时间 `2025-10-27 08:46:52`
 */
export interface CompareBusinessRequest {
  /**
   * 征信查询id
   */
  creditSearchId?: number
  flowId?: string
}

/**
 * 接口 [客户比对承租人及担保人工商信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37879) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/creditSearch/client/compare/business`
 * @更新时间 `2025-10-27 08:46:52`
 */
export type CompareBusinessResponse = {
  /**
   * 变更标识 0无变更，1变更
   */
  changeFlag?: number
  /**
   * 客户类型，承租人or担保人
   */
  clientType?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * tyc客户名称
   */
  clientTycName?: string
  /**
   * 比对客户名称
   */
  clientNameCompare?: string
  /**
   * 法人代表
   */
  corpRepresent?: string
  /**
   * tyc法人代表
   */
  corpTycRepresent?: string
  /**
   * 比对法人代表
   */
  corpRepresentCompare?: string
  /**
   * 股东信息
   */
  shareHolderInfo?: string[]
  /**
   * tyc股东信息
   */
  shareHolderTycInfo?: string[]
  /**
   * 比对股东信息
   */
  shareHolderInfoCompare?: string[]
}[]

/**
 * 接口 [征信报告查询列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37849) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/list`
 * @更新时间 `2025-10-27 08:46:50`
 */
export interface BaseListRequest {
  sourceScene?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
  /**
   * 查询编号
   */
  creditCode?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 统一社会信用代码
   */
  cscCode?: string
  /**
   * 申请人
   */
  applyUser?: string
  /**
   * 申请部门
   */
  applyOrg?: string
  /**
   * 查询完成时间-开始
   */
  createFrom?: string
  /**
   * 查询完成时间-结束
   */
  createTo?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 查询目的
   */
  selectGoal?: string
  /**
   * 项目id
   */
  projId?: number
  /**
   * 客户id
   */
  clientId?: number
  targetClientIds?: number[]
  searchTimeFrom?: string
  searchTimeTo?: string
}

/**
 * 接口 [征信报告查询列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37849) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/list`
 * @更新时间 `2025-10-27 08:46:50`
 */
export interface BaseListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 查询编号
     */
    creditCode?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 统一社会信用代码
     */
    cscCode?: string
    /**
     * 中征码
     */
    zhongZhengCode?: string
    /**
     * 申请人id
     */
    applyUser?: number
    /**
     * 申请部门id
     */
    applyOrg?: number
    /**
     * 申请人
     */
    applyUserName?: string
    /**
     * 申请部门
     */
    applyOrgName?: string
    /**
     * 申请状态
     */
    applyStatus?: string
    /**
     * 申请通过时间
     */
    applyTime?: string
    /**
     * 查询状态
     */
    selectStatus?: string
    /**
     * 查询完成时间
     */
    selectTime?: string
    /**
     * 项目编号
     */
    projCode?: number
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 查询版本
     */
    selectVersion?: string
    /**
     * 查询目的
     */
    selectGoal?: string
    /**
     * 信用报告封装格式
     */
    reportFormat?: string
    /**
     * 统一社会信用代码-集
     */
    cscCodeList?: string[]
    /**
     * 中征码-集
     */
    zhongZhengCodeList?: string[]
    /**
     * 查询目的-集
     */
    selectGoalList?: string[]
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
    key?: {}
  }
}

/**
 * 接口 [征信报告查询提交↗](http://yapi.zswltec.com:3000/project/11/interface/api/37867) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/submit`
 * @更新时间 `2025-10-27 08:46:51`
 */
export interface BaseSubmitRequest {
  /**
   * 征信报告id
   */
  id?: number
  /**
   * 客户id
   */
  clientId?: number[]
}

/**
 * 接口 [征信报告查询提交↗](http://yapi.zswltec.com:3000/project/11/interface/api/37867) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/submit`
 * @更新时间 `2025-10-27 08:46:51`
 */
export type BaseSubmitResponse = {
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 资料
   */
  creditReportFiles?: {
    /**
     * 资料子类型
     */
    materialsTypeName?: string
  }[]
}[]

/**
 * 接口 [征信报告查询详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37855) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/detail`
 * @更新时间 `2025-10-27 08:46:50`
 */
export interface BaseDetailRequest {
  creditCode?: string
}

/**
 * 接口 [征信报告查询详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37855) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/detail`
 * @更新时间 `2025-10-27 08:46:50`
 */
export interface BaseDetailResponse {
  /**
   * 征信报告id
   */
  id?: number
  /**
   * 查询编号
   */
  creditCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 统一社会信用代码
   */
  cscCode?: string
  /**
   * 中征码
   */
  zhongZhengCode?: string
  /**
   * 查询目的
   */
  selectGoal?: string
  /**
   * 关联项目编号
   */
  projCode?: string
  /**
   * 关联项目名称
   */
  projName?: string
  /**
   * 查询版本
   */
  selectVersion?: string
  /**
   * 信用报告封装格式
   */
  reportFormat?: string
  /**
   * 客户信息列表
   */
  clientInfos?: {
    /**
     * 征信报告详情id
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
     * 统一社会信用代码
     */
    cscCode?: string
    /**
     * 中征码
     */
    zhongZhengCode?: string
    /**
     * 查询目的
     */
    selectGoal?: string
    /**
     * 征信报告id
     */
    reportId?: number
  }[]
}

/**
 * 接口 [征信报告查询详情保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/37861) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/save`
 * @更新时间 `2025-10-27 08:46:51`
 */
export interface BaseSaveRequest {
  /**
   * 征信报告id
   */
  id: number
  /**
   * 征信报告查询客户信息列表
   */
  clientInfos?: {
    /**
     * 征信报告详情id
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
     * 统一社会信用代码
     */
    cscCode?: string
    /**
     * 中征码
     */
    zhongZhengCode?: string
    /**
     * 查询目的
     */
    selectGoal?: string
    /**
     * 征信报告id
     */
    reportId?: number
  }[]
  /**
   * 关联项目id
   */
  projId?: number
  /**
   * 关联项目名称
   */
  projName?: string
  /**
   * 关联项目编号
   */
  projCode?: string
  /**
   * 查询版本
   */
  selectVersion?: string
  /**
   * 信用报告封装格式
   */
  reportFormat?: string
}

/**
 * 接口 [征信报告查询详情保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/37861) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/save`
 * @更新时间 `2025-10-27 08:46:51`
 */
export type BaseSaveResponse = null

/**
 * 接口 [征信查询删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37873) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/delete`
 * @更新时间 `2025-10-27 08:46:51`
 */
export interface BaseDeleteRequest {
  id?: string
}

/**
 * 接口 [征信查询删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37873) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/delete`
 * @更新时间 `2025-10-27 08:46:51`
 */
export type BaseDeleteResponse = null

/**
 * 接口 [新增征信报告查询↗](http://yapi.zswltec.com:3000/project/11/interface/api/37843) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/add`
 * @更新时间 `2025-10-27 08:46:49`
 */
export interface BaseAddRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName: string
  /**
   * 统一社会信用代码
   */
  cscCode: string
  /**
   * 中征码
   */
  zhongZhengCode?: string
  /**
   * 关联项目id
   */
  projId?: number
  /**
   * 关联项目名称
   */
  projName?: string
  /**
   * 关联项目编号
   */
  projCode?: string
  /**
   * 查询版本
   */
  selectVersion?: string
  /**
   * 查询目的
   */
  selectGoal: string
  /**
   * 信用报告封装格式
   */
  reportFormat?: string
}

/**
 * 接口 [新增征信报告查询↗](http://yapi.zswltec.com:3000/project/11/interface/api/37843) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `POST /creditreport/base/add`
 * @更新时间 `2025-10-27 08:46:49`
 */
export type BaseAddResponse = null

/**
 * 接口 [查询有征信报告查询权限的客户列表信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37831) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/clientInfo`
 * @更新时间 `2025-10-27 08:46:48`
 */
export interface BaseClientInfoRequest {
  clientName?: string
}

/**
 * 接口 [查询有征信报告查询权限的客户列表信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37831) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/clientInfo`
 * @更新时间 `2025-10-27 08:46:48`
 */
export type BaseClientInfoResponse = {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户类型
   */
  clientType?: string
  /**
   * 租赁物文件类型
   */
  leaseItemFileType?: string
  /**
   * 存量风险敝口
   */
  stockRiskExposure?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 境内or境外
   */
  domesticOrAbroad?: string
  belongSponsorId?: number
  orgType?: string
}[]

/**
 * 接口 [根据客户id反显客户信息和关联项目信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37837) 的 **请求类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/showCreditReportByClientId`
 * @更新时间 `2025-10-27 08:46:49`
 */
export interface BaseShowCreditReportByClientIdRequest {
  clientId?: string
}

/**
 * 接口 [根据客户id反显客户信息和关联项目信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37837) 的 **返回类型**
 *
 * @分类 [CreditReportController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5384)
 * @请求头 `GET /creditreport/base/showCreditReportByClientId`
 * @更新时间 `2025-10-27 08:46:49`
 */
export interface BaseShowCreditReportByClientIdResponse {
  /**
   * 客户编号
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 查询版本
   */
  selectVersion?: string
  /**
   * 信用报告封装格式
   */
  reportFormat?: string
  /**
   * 统一社会信用代码
   */
  cscCode?: string
  /**
   * 中征码
   */
  zhongZhengCode?: string
  /**
   * 关联项目列表
   */
  projectInfos?: {
    /**
     * 项目id
     */
    projId?: number
    /**
     * 项目编码
     */
    projCode?: string
    /**
     * 项目名称
     */
    projName?: string
  }[]
}

/* prettier-ignore-end */
