/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [批量查询下载模版↗](http://yapi.zswltec.com:3000/project/10/interface/api/16909) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `GET /black/gray/batch/query/template/download`
 * @更新时间 `2023-12-12 11:12:05`
 */
export interface TemplateDownloadRequest {}

/**
 * 接口 [批量查询下载模版↗](http://yapi.zswltec.com:3000/project/10/interface/api/16909) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `GET /black/gray/batch/query/template/download`
 * @更新时间 `2023-12-12 11:12:05`
 */
export interface TemplateDownloadResponse {}

/**
 * 接口 [黑灰名单综合查询导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/109) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `GET /black/gray/base/info/export`
 * @更新时间 `2024-01-20 17:32:30`
 */
export interface InfoExportRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 来源 BlackGraySourceEnum
   */
  source?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 所属部门
   */
  applyDept?: string
  /**
   * 入库原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 更新时间 : yyyy-MM-dd HH:mm:ss
   */
  updateTime?: string
  /**
   * 名单类型 BLACK_LIST 黑名单 GRAY_LIST 灰名单
   */
  listType?: string
  /**
   * 入库时间开始 : yyyy-MM-dd HH:mm:ss
   */
  warehouseTimeFrom?: string
  /**
   * 入库时间结束 : yyyy-MM-dd HH:mm:ss
   */
  warehouseTimeTo?: string
  /**
   * 出库时间开始 : yyyy-MM-dd HH:mm:ss
   */
  planOutboundTimeFrom?: string
  /**
   * 出库时间结束 : yyyy-MM-dd HH:mm:ss
   */
  planOutboundTimeTo?: string
  /**
   * 1,不降级，0降级
   */
  reduceStatus?: string
  /**
   * 排序
   */
  orderByList?: string
  /**
   * ids
   */
  ids?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单综合查询导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/109) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `GET /black/gray/base/info/export`
 * @更新时间 `2024-01-20 17:32:30`
 */
export interface InfoExportResponse {}

/**
 * 接口 [\/black\/gray\/batch\/batch\/query\/↗](http://yapi.zswltec.com:3000/project/10/interface/api/16915) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/batch/batch/query/`
 * @更新时间 `2023-12-08 13:52:12`
 */
export interface BatchQueryRequest {
  /**
   * 企业名称
   */
  businessType?: string
  /**
   * 文件
   */
  file: File
}

/**
 * 接口 [\/black\/gray\/batch\/batch\/query\/↗](http://yapi.zswltec.com:3000/project/10/interface/api/16915) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/batch/batch/query/`
 * @更新时间 `2023-12-08 13:52:12`
 */
export type BatchQueryResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: string
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间
   */
  applyTime?: string
  /**
   * 入库原因
   */
  warehouseReason?: string
  /**
   * 申请机构
   */
  applyOrganization?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
}[]

/**
 * 接口 [批量查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/85) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/batch/batch/query`
 * @更新时间 `2024-01-19 16:48:57`
 */
export interface BatchQueryRequest {
  /**
   * 企业名称
   */
  businessType?: string
  /**
   * 文件
   */
  file: File
}

/**
 * 接口 [批量查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/85) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/batch/batch/query`
 * @更新时间 `2024-01-19 16:48:57`
 */
export type BatchQueryResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: string
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 集团黑灰标识
   */
  groupBlackGrayType?: string
  source?: string
  /**
   * 申请时间
   */
  applyTime?: string
  /**
   * 入库原因
   */
  applyReason?: string
  /**
   * 申请机构
   */
  applyOrganization?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
  /**
   * 共享类型 0金融企业黑名单， 1金控黑名单
   */
  shareType?: number
}[]

/**
 * 接口 [风控系统查询可突破黑灰名单类型↗](http://yapi.zswltec.com:3000/project/10/interface/api/16807) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/can/break/business`
 * @更新时间 `2023-12-11 19:29:05`
 */
export interface BreakBusinessRequest {
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 登陆人ID
   */
  loginId?: number
}

/**
 * 接口 [风控系统查询可突破黑灰名单类型↗](http://yapi.zswltec.com:3000/project/10/interface/api/16807) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/can/break/business`
 * @更新时间 `2023-12-11 19:29:05`
 */
export type BreakBusinessResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 业务类型描述
   */
  businessDesc?: string
}[]

/**
 * 接口 [风控系统查询风险规模↗](http://yapi.zswltec.com:3000/project/10/interface/api/16903) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/enterprise/riskScale`
 * @更新时间 `2023-12-08 13:52:11`
 */
export interface EnterpriseRiskScaleRequest {
  /**
   * 企业名称
   */
  enterpriseName: string
}

/**
 * 接口 [风控系统查询风险规模↗](http://yapi.zswltec.com:3000/project/10/interface/api/16903) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/enterprise/riskScale`
 * @更新时间 `2023-12-08 13:52:11`
 */
export interface EnterpriseRiskScaleResponse {
  /**
   * 风险暴露
   */
  riskExposure?: number
}

/**
 * 接口 [风控系统查询黑灰名单库列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16681) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/base/info/list`
 * @更新时间 `2024-01-31 11:41:23`
 */
export interface InfoListRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 来源 BlackGraySourceEnum
   */
  source?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 所属部门
   */
  applyDept?: string
  /**
   * 入库原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 名单类型 BLACK_LIST 黑名单 GRAY_LIST 灰名单
   */
  listType?: string
  /**
   * 入库时间开始
   */
  warehouseTimeFrom?: string
  /**
   * 入库时间结束
   */
  warehouseTimeTo?: string
  /**
   * 出库时间开始
   */
  planOutboundTimeFrom?: string
  /**
   * 出库时间结束
   */
  planOutboundTimeTo?: string
  /**
   * 1,不降级，0降级
   */
  reduceStatus?: number
  /**
   * 排序
   */
  orderByList?: {
    /**
     * 排序字段
     */
    orderByField?: string
    /**
     * 是否倒叙 1 倒叙，其他正叙
     */
    descFlag?: number
  }[]
  /**
   * ids
   */
  ids?: number[]
  /**
   * true 单例， false 多个
   */
  singleton?: boolean
  page?: number
  pageSize?: number
}

/**
 * 接口 [风控系统查询黑灰名单库列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16681) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/base/info/list`
 * @更新时间 `2024-01-31 11:41:23`
 */
export interface InfoListResponse {
  total?: number
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一社会信用代码
     */
    unifiedSocialCreditCode?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 风险规模（万元）
     */
    riskScale?: string
    /**
     * 所属集团
     */
    membershipGroup?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    /**
     * 集团黑灰标识
     */
    groupBlackGrayType?: string
    /**
     * 来源
     */
    source?: string
    /**
     * 申请时间
     */
    applyTime?: string
    /**
     * 入库原因
     */
    applyReason?: string
    /**
     * 入库原因
     */
    applyReasonType?: string
    /**
     * 申请机构
     */
    applyOrganization?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 共享类型 0金融企业黑名单， 1金控黑名单
     */
    shareType?: number
    updateTime?: string
  }[]
  excludeForwardView?: string
}

/**
 * 接口 [风控系统查询黑灰名单库按企业汇总列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/1081) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/base/info/distinct`
 * @更新时间 `2024-03-04 11:19:22`
 */
export interface InfoDistinctRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 导出时勾选项的信用代码集合
   */
  creditCodeList?: string[]
  page?: number
  pageSize?: number
}

/**
 * 接口 [风控系统查询黑灰名单库按企业汇总列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/1081) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/base/info/distinct`
 * @更新时间 `2024-03-04 11:19:22`
 */
export interface InfoDistinctResponse {
  total?: number
  list?: {
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一社会信用代码
     */
    unifiedSocialCreditCode?: string
  }[]
  excludeForwardView?: string
}

/**
 * 接口 [风控系统查询黑灰名单库按企业汇总列表导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/1075) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `GET /black/gray/base/info/distinct/export`
 * @更新时间 `2024-03-04 11:18:52`
 */
export interface DistinctExportRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 导出时勾选项的信用代码集合
   */
  creditCodeList?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [风控系统查询黑灰名单库按企业汇总列表导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/1075) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `GET /black/gray/base/info/distinct/export`
 * @更新时间 `2024-03-04 11:18:52`
 */
export interface DistinctExportResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [风控系统查询黑灰名单库机构下列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/79) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/org/list`
 * @更新时间 `2024-01-19 16:48:57`
 */
export interface OrgListRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 来源 BlackGraySourceEnum
   */
  source?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 所属部门
   */
  applyDept?: string
  /**
   * 入库原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 名单类型 BLACK_LIST 黑名单 GRAY_LIST 灰名单
   */
  listType?: string
  /**
   * 入库时间开始
   */
  warehouseTimeFrom?: string
  /**
   * 入库时间结束
   */
  warehouseTimeTo?: string
  /**
   * 出库时间开始
   */
  planOutboundTimeFrom?: string
  /**
   * 出库时间结束
   */
  planOutboundTimeTo?: string
  /**
   * 1,不降级，0降级
   */
  reduceStatus?: number
  /**
   * 排序
   */
  orderByList?: {
    /**
     * 排序字段
     */
    orderByField?: string
    /**
     * 是否倒叙 1 倒叙，其他正叙
     */
    descFlag?: number
  }[]
  page?: number
  pageSize?: number
}

/**
 * 接口 [风控系统查询黑灰名单库机构下列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/79) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/org/list`
 * @更新时间 `2024-01-19 16:48:57`
 */
export interface OrgListResponse {
  total?: number
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一社会信用代码
     */
    unifiedSocialCreditCode?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 风险规模（万元）
     */
    riskScale?: string
    /**
     * 所属集团
     */
    membershipGroup?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    /**
     * 集团黑灰标识
     */
    groupBlackGrayType?: string
    source?: string
    /**
     * 申请时间
     */
    applyTime?: string
    /**
     * 入库原因
     */
    applyReason?: string
    /**
     * 申请机构
     */
    applyOrganization?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 共享类型 0金融企业黑名单， 1金控黑名单
     */
    shareType?: number
  }[]
}

/**
 * 接口 [风控系统查询黑灰名单库详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/16813) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/base/info/detail`
 * @更新时间 `2024-01-19 16:48:57`
 */
export interface InfoDetailRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [风控系统查询黑灰名单库详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/16813) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3732)
 * @请求头 `POST /black/gray/base/info/detail`
 * @更新时间 `2024-01-19 16:48:57`
 */
export interface InfoDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: string
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间
   */
  applyTime?: string
  /**
   * 入库原因
   */
  warehouseReason?: string
  /**
   * 申请机构
   */
  applyOrganization?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
  /**
   * 申请文件keys
   */
  rectifyFileKeys?: string[]
  /**
   * create_by
   */
  createBy?: number
  /**
   * create_by
   */
  createByCode?: string
  createName?: string
  /**
   * create_time
   */
  createTime?: string
  /**
   * 来源
   */
  source?: string
}

/* prettier-ignore-end */
