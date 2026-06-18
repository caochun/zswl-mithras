/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [黑灰名单出库导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/103) 的 **请求类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `GET /black/gray/manual/outbound/record/export`
 * @更新时间 `2024-01-20 17:01:14`
 */
export interface RecordExportRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 申请时间开始 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeTo?: string
  /**
   * 出库时间开始 : yyyy-MM-dd HH:mm:ss
   */
  planOutboundTimeFrom?: string
  /**
   * 出库时间结束 : yyyy-MM-dd HH:mm:ss
   */
  planOutboundTimeTo?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 审批状态
   */
  auditStatus?: string
  /**
   * id
   */
  ids?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单出库导出↗](http://yapi.zswltec.com:3000/project/10/interface/api/103) 的 **返回类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `GET /black/gray/manual/outbound/record/export`
 * @更新时间 `2024-01-20 17:01:14`
 */
export interface RecordExportResponse {}

/**
 * 接口 [修改黑灰名单人工出库表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16735) 的 **请求类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/modify`
 * @更新时间 `2024-01-24 15:39:11`
 */
export interface OutboundModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 黑灰名单ID
   */
  blackGrayId: number
  /**
   * 记录状态
   */
  manualOutboundStatus?: string
  /**
   * 申请原因
   */
  applyReason?: {
    id?: number
    /**
     * 申请原因
     */
    applyReasonType?: string
    /**
     * 申请原因名称
     */
    applyReasonName?: string
    /**
     * 0 不选中，1选中
     */
    status?: number
    /**
     * 风险规模
     */
    riskScale?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
  }[]
  /**
   * 申请文件keys
   */
  applyFileKeys?: string[]
  /**
   * 出库说明
   */
  message?: string
}

/**
 * 接口 [修改黑灰名单人工出库表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16735) 的 **返回类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/modify`
 * @更新时间 `2024-01-24 15:39:11`
 */
export interface OutboundModifyResponse {
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
 * 接口 [新增黑灰名单人工出库表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16747) 的 **请求类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/add`
 * @更新时间 `2024-01-24 15:39:11`
 */
export interface OutboundAddRequest {
  /**
   * 黑灰名单ID
   */
  blackGrayId: number
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
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
  /**
   * 申请原因
   */
  applyReason?: {
    id?: number
    /**
     * 申请原因
     */
    applyReasonType?: string
    /**
     * 申请原因名称
     */
    applyReasonName?: string
    /**
     * 0 不选中，1选中
     */
    status?: number
    /**
     * 风险规模
     */
    riskScale?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
  }[]
  /**
   * 申请文件keys
   */
  applyFileKeys?: string[]
  /**
   * 出库说明
   */
  message?: string
}

/**
 * 接口 [新增黑灰名单人工出库表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16747) 的 **返回类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/add`
 * @更新时间 `2024-01-24 15:39:11`
 */
export type OutboundAddResponse = number

/**
 * 接口 [黑灰名单人工出库表列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16741) 的 **请求类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/list`
 * @更新时间 `2024-01-24 15:39:11`
 */
export interface OutboundListRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 申请时间开始
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束
   */
  applyTimeTo?: string
  /**
   * 出库时间开始
   */
  planOutboundTimeFrom?: string
  /**
   * 出库时间结束
   */
  planOutboundTimeTo?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 审批状态
   */
  auditStatus?: number
  /**
   * id
   */
  ids?: number[]
  page?: number
  pageSize?: number
}

/**
 * 接口 [黑灰名单人工出库表列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/16741) 的 **返回类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/list`
 * @更新时间 `2024-01-24 15:39:11`
 */
export interface OutboundListResponse {
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
     * 申请机构
     */
    applyOrganization?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    /**
     * 申请时间
     */
    createTime?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 实际出库时间
     */
    actualOutboundTime?: string
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
    /**
     * 黑灰名单入库机构
     */
    warehouseOrganization?: string
    createBy?: number
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 审批任务id
     */
    auditTaskId?: number
    /**
     * 上一处理人
     */
    preOperator?: string
  }[]
}

/**
 * 接口 [黑灰名单人工出库表删除↗](http://yapi.zswltec.com:3000/project/10/interface/api/16753) 的 **请求类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/remove`
 * @更新时间 `2024-01-24 15:39:27`
 */
export interface OutboundRemoveRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [黑灰名单人工出库表删除↗](http://yapi.zswltec.com:3000/project/10/interface/api/16753) 的 **返回类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/remove`
 * @更新时间 `2024-01-24 15:39:27`
 */
export interface OutboundRemoveResponse {
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
 * 接口 [黑灰名单人工出库表详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/217) 的 **请求类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/detail`
 * @更新时间 `2024-01-24 19:27:58`
 */
export interface OutboundDetailRequest {
  /**
   * id
   */
  id: number
  auditTaskId?: number
}

/**
 * 接口 [黑灰名单人工出库表详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/217) 的 **返回类型**
 *
 * @分类 [黑灰名单人工出库表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3768)
 * @请求头 `POST /black/gray/manual/outbound/detail`
 * @更新时间 `2024-01-24 19:27:58`
 */
export interface OutboundDetailResponse {
  /**
   * id
   */
  id: number
  blackGrayId?: number
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
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 入库时间
   */
  warehouseTime?: string
  /**
   * 出库时间
   */
  planOutboundTime?: string
  /**
   * 申请原因
   */
  applyReason?: {
    id?: number
    /**
     * 申请原因
     */
    applyReasonType?: string
    /**
     * 申请原因名称
     */
    applyReasonName?: string
    /**
     * 0 不选中，1选中
     */
    status?: number
    /**
     * 风险规模
     */
    riskScale?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
  }[]
  /**
   * 申请文件keys
   */
  applyFileKeys?: string[]
  /**
   * 记录状态
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
   * 黑灰名单入库机构
   */
  warehouseOrganization?: string
  /**
   * 创建人、发起人
   */
  createBy?: number
  /**
   * 创建人code
   */
  createByCode?: string
  createTime?: string
  /**
   * 审批任务id
   */
  auditTaskId?: number
  /**
   * 来源
   */
  source?: string
  /**
   * 出库说明
   */
  message?: string
}

/* prettier-ignore-end */
