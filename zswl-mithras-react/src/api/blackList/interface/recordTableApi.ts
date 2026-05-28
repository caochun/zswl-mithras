/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [黑灰名单上传模版下载↗](http://yapi.zswltec.com:3000/project/10/interface/api/16927) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `GET /black/gray/warehouse/record/upload/template/download`
 * @更新时间 `2023-12-09 15:45:33`
 */
export interface TemplateDownloadRequest {}

/**
 * 接口 [黑灰名单上传模版下载↗](http://yapi.zswltec.com:3000/project/10/interface/api/16927) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `GET /black/gray/warehouse/record/upload/template/download`
 * @更新时间 `2023-12-09 15:45:33`
 */
export interface TemplateDownloadResponse {}

/**
 * 接口 [黑灰名单导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/16933) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `GET /black/gray/warehouse/record/export`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface RecordExportRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 任务编号
   */
  taskNum?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 来源
   */
  source?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间开始 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeTo?: string
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
   * 审批状态
   */
  auditStatus?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 所属部门
   */
  applyDept?: string
  /**
   * 是否查询历史 0 历史，1其他
   */
  isHistory?: string
  /**
   * 仅查看待处理 0 待处理，1其他
   */
  isPendingProcess?: string
  /**
   * 是否在库 0 在库，1其他
   */
  isStock?: string
  ids?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/16933) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `GET /black/gray/warehouse/record/export`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface RecordExportResponse {}

/**
 * 接口 [上传黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16711) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/upload`
 * @更新时间 `2023-12-12 19:17:59`
 */
export interface RecordUploadRequest {
  /**
   * 黑灰名单记录表
   */
  file?: File
  /**
   * 来源
   */
  source: string
  blackGrayType: string
}

/**
 * 接口 [上传黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16711) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/upload`
 * @更新时间 `2023-12-12 19:17:59`
 */
export interface RecordUploadResponse {
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
 * 接口 [业务类型树↗](http://yapi.zswltec.com:3000/project/10/interface/api/16645) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/business/type/list`
 * @更新时间 `2024-01-19 10:50:02`
 */
export interface TypeListRequest {
  /**
   * 所属机构ID
   */
  orgId?: number
}

/**
 * 接口 [业务类型树↗](http://yapi.zswltec.com:3000/project/10/interface/api/16645) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/business/type/list`
 * @更新时间 `2024-01-19 10:50:02`
 */
export type TypeListResponse = {
  id?: number
  /**
   * 业务类型代码
   */
  businessName?: string
  /**
   * 业务类型描述
   */
  businessDesc?: string
  /**
   * 所属金控id
   */
  mainId?: number
  parentId?: number
  level?: number
  child?: {}[]
}[]

/**
 * 接口 [修改黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16657) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/modify`
 * @更新时间 `2024-01-19 10:43:55`
 */
export interface RecordModifyRequest {
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
   * 金融企业业务类型
   */
  customiseBusinessType?: string
  /**
   * 观察期
   */
  periodUnderObservation?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: number
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 集团是否纳入黑名单
   */
  blacklistStatus?: number
  /**
   * 申请原因下拉
   */
  applyReasonType?: string[]
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 集团黑灰标识
   */
  groupBlackGrayType?: string
  /**
   * 申请时间
   */
  applyTime?: string
  applyOrganization?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
  /**
   * 入库原因
   */
  warehouseReason?: string
  /**
   * 自动出库标识 0自动出库， 1手动出库
   */
  autoOutboundStatus?: number
  /**
   * 入库文件key
   */
  warehouseFileKeys?: string[]
  /**
   * 整改文件key
   */
  rectifyFileKeys?: string[]
  /**
   * 记录状态
   */
  recordStatus?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
}

/**
 * 接口 [修改黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16657) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/modify`
 * @更新时间 `2024-01-19 10:43:55`
 */
export interface RecordModifyResponse {
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
 * 接口 [批量新增黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/97) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/batch/add`
 * @更新时间 `2024-01-20 11:15:50`
 */
export type BatchAddRequest = {
  /**
   * 企业名称
   */
  enterpriseName: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode: string
  /**
   * 业务类型
   */
  businessType: string
  /**
   * 金融企业业务类型
   */
  customiseBusinessType?: string
  /**
   * 任务编号
   */
  taskNum?: string
  /**
   * 来源
   */
  source: string
  /**
   * 观察期
   */
  periodUnderObservation?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: number
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 集团是否纳入黑名单 0否 1纳入黑名单
   */
  blacklistStatus?: number
  /**
   * 申请原因下拉
   */
  applyReasonType?: string[]
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType: string
  /**
   * 集团黑灰标识
   */
  groupBlackGrayType?: string
  /**
   * 入库文件key
   */
  warehouseFileKeys?: string[]
  /**
   * 整改文件key
   */
  rectifyFileKeys?: string[]
  /**
   * 入库时间
   */
  warehouseTime?: string
}[]

/**
 * 接口 [批量新增黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/97) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/batch/add`
 * @更新时间 `2024-01-20 11:15:50`
 */
export interface BatchAddResponse {
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
 * 接口 [批量补全黑灰名单集团信息↗](http://yapi.zswltec.com:3000/project/10/interface/api/1087) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/batch/modify`
 * @更新时间 `2024-03-04 17:38:46`
 */
export type BatchModifyRequest = {
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
}[]

/**
 * 接口 [批量补全黑灰名单集团信息↗](http://yapi.zswltec.com:3000/project/10/interface/api/1087) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/batch/modify`
 * @更新时间 `2024-03-04 17:38:46`
 */
export interface BatchModifyResponse {
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
 * 接口 [新增黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16663) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/add`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface RecordAddRequest {
  /**
   * 企业名称
   */
  enterpriseName: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode: string
  /**
   * 业务类型
   */
  businessType: string
  /**
   * 金融企业业务类型
   */
  customiseBusinessType?: string
  /**
   * 任务编号
   */
  taskNum?: string
  /**
   * 来源
   */
  source: string
  /**
   * 观察期
   */
  periodUnderObservation?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: number
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 集团是否纳入黑名单 0否 1纳入黑名单
   */
  blacklistStatus?: number
  /**
   * 申请原因下拉
   */
  applyReasonType?: string[]
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType: string
  /**
   * 集团黑灰标识
   */
  groupBlackGrayType?: string
  /**
   * 入库文件key
   */
  warehouseFileKeys?: string[]
  /**
   * 整改文件key
   */
  rectifyFileKeys?: string[]
  /**
   * 入库时间
   */
  warehouseTime?: string
}

/**
 * 接口 [新增黑灰名单记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16663) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/add`
 * @更新时间 `2024-01-19 17:12:33`
 */
export type RecordAddResponse = number

/**
 * 接口 [解析黑灰名单上传记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/19) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/analysis/upload`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface AnalysisUploadRequest {
  /**
   * 黑灰名单记录表
   */
  file?: File
  /**
   * 来源
   */
  source: string
  blackGrayType: string
}

/**
 * 接口 [解析黑灰名单上传记录表↗](http://yapi.zswltec.com:3000/project/10/interface/api/19) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/analysis/upload`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface AnalysisUploadResponse {
  /**
   * 符合条件条数
   */
  addList?: {
    enterpriseName: string
    unifiedSocialCreditCode: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    /**
     * warehouse_time
     */
    warehouseTime?: string
    /**
     * plan_outbound_time
     */
    planOutboundTime?: string
    /**
     * 申请原因
     */
    applyReason?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 风险规模（万元）
     */
    riskScale?: number
  }[]
  /**
   * 失败条数
   */
  ErrorList?: {
    enterpriseName?: string
    unifiedSocialCreditCode?: string
    errorFields?: string[]
  }[]
}

/**
 * 接口 [黑灰名单记录表列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16669) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/list`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface RecordListRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 任务编号
   */
  taskNum?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 来源
   */
  source?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间开始
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束
   */
  applyTimeTo?: string
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
   * 审批状态
   */
  auditStatus?: number
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 所属部门
   */
  applyDept?: string
  /**
   * 是否查询历史 0 历史，1其他
   */
  isHistory?: number
  /**
   * 仅查看待处理 0 待处理，1其他
   */
  isPendingProcess?: number
  /**
   * 是否在库 0 在库，1其他
   */
  isStock?: number
  ids?: number[]
  page?: number
  pageSize?: number
}

/**
 * 接口 [黑灰名单记录表列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16669) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/list`
 * @更新时间 `2024-01-19 17:12:33`
 */
export interface RecordListResponse {
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
     * 风险规模（万元）
     */
    riskScale?: number
    /**
     * 申请原因
     */
    applyReason?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 报告机构
     */
    applyOrganization?: string
    /**
     * 申请时间
     */
    applyTime?: string
    /**
     * 记录状态
     */
    auditStatus?: number
    /**
     * 当前处理人
     */
    currentOperator?: string
    /**
     * 入库类型
     */
    source?: string
    createBy?: number
  }[]
}

/**
 * 接口 [黑灰名单记录表删除↗](http://yapi.zswltec.com:3000/project/10/interface/api/229) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/delete`
 * @更新时间 `2024-01-31 11:29:43`
 */
export interface RecordDeleteRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [黑灰名单记录表删除↗](http://yapi.zswltec.com:3000/project/10/interface/api/229) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/delete`
 * @更新时间 `2024-01-31 11:29:43`
 */
export interface RecordDeleteResponse {
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
 * 接口 [黑灰名单记录表详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/16651) 的 **请求类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/detail`
 * @更新时间 `2024-01-31 09:54:53`
 */
export interface RecordDetailRequest {
  /**
   * 黑灰名单记录表id
   */
  blackGrayRecordId?: number
}

/**
 * 接口 [黑灰名单记录表详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/16651) 的 **返回类型**
 *
 * @分类 [黑灰名单记录表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3723)
 * @请求头 `POST /black/gray/warehouse/record/detail`
 * @更新时间 `2024-01-31 09:54:53`
 */
export interface RecordDetailResponse {
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
   * 任务编号
   */
  taskNum?: string
  /**
   * 观察期
   */
  periodUnderObservation?: string
  /**
   * 风险规模（万元）
   */
  riskScale?: number
  /**
   * 所属集团
   */
  membershipGroup?: string
  /**
   * 集团是否纳入黑名单
   */
  blacklistStatus?: number
  /**
   * 申请原因类型
   */
  applyReasonType?: string[]
  applyReasonName?: string[]
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 集团黑灰标识
   */
  groupBlackGrayType?: string
  /**
   * 申请时间
   */
  applyTime?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 所属部门
   */
  applyDept?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
  /**
   * 入库文件key
   */
  warehouseFileKeys?: string[]
  /**
   * 整改文件key
   */
  rectifyFileKeys?: string[]
  /**
   * 记录状态
   */
  auditStatus?: number
  /**
   * 创建人、发起人
   */
  createBy?: number
  /**
   * 创建人code
   */
  createByCode?: string
  /**
   * 审批任务id
   */
  auditTaskId?: number
  /**
   * 来源
   */
  source?: string
}

/* prettier-ignore-end */
