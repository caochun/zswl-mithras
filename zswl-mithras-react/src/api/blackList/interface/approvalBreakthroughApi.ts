/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改审批突破↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16705) 的 **请求类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/modify`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 黑灰名单id
   */
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
   * 拟开展业务类型
   */
  proposedBusinessType?: string
  /**
   * 原计划出库日期
   */
  planOutboundTime?: string
  /**
   * 拟开展业务规模（万元）
   */
  proposeBusinessScale?: number
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 申请文件keys
   */
  applyFileKeys?: string
}

/**
 * 接口 [修改审批突破↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16705) 的 **返回类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/modify`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessModifyResponse {
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
 * 接口 [审批突破列表↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16699) 的 **请求类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/list`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessListRequest {
  /**
   * 申请机构
   */
  applyOrganization?: string
  /**
   * 申请部门
   */
  applyDept?: string
  /**
   * 申请时间开始
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束
   */
  applyTimeTo?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 审批状态
   */
  auditStatus?: number
  page?: number
  pageSize?: number
}

/**
 * 接口 [审批突破列表↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16699) 的 **返回类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/list`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessListResponse {
  total?: number
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 黑灰名单id
     */
    blackGrayId?: number
    /**
     * 拟开展业务类型
     */
    proposedBusinessType?: string
    /**
     * 原计划出库日期
     */
    planOutboundTime?: string
    /**
     * 拟开展业务规模（万元）
     */
    proposeBusinessScale?: number
    /**
     * 申请原因
     */
    applyReason?: string
    /**
     * 突破流程状态
     */
    auditStatus?: number
    /**
     * 当前处理人
     */
    currentOperator?: string
  }[]
}

/**
 * 接口 [审批突破详情↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16687) 的 **请求类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/detail`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessDetailRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [审批突破详情↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16687) 的 **返回类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/detail`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 黑灰名单id
   */
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
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 拟开展业务类型
   */
  proposedBusinessType?: string
  /**
   * 原计划出库日期
   */
  planOutboundTime?: string
  /**
   * 拟开展业务规模（万元）
   */
  proposeBusinessScale?: number
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 申请原因附件
   */
  applyFileKeys?: string[]
  /**
   * 审批任务id
   */
  auditTaskId?: number
}

/**
 * 接口 [新增审批突破↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16693) 的 **请求类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/add`
 * @更新时间 `2023-12-15 16:42:06`
 */
export interface BusinessAddRequest {
  /**
   * 黑灰名单id
   */
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
   * 拟开展业务类型
   */
  proposedBusinessType?: string
  /**
   * 原计划出库日期
   */
  planOutboundTime?: string
  /**
   * 拟开展业务规模（万元）
   */
  proposeBusinessScale?: number
  /**
   * 申请原因
   */
  applyReason?: string
  /**
   * 申请文件keys
   */
  applyFileKeys?: string
}

/**
 * 接口 [新增审批突破↗](http://api-dev.zswl.cn:3011/project/10/interface/api/16693) 的 **返回类型**
 *
 * @分类 [黑灰名单审批突破-接口↗](http://api-dev.zswl.cn:3011/project/10/interface/api/cat_3759)
 * @请求头 `POST /black/gray/break/business/add`
 * @更新时间 `2023-12-15 16:42:06`
 */
export type BusinessAddResponse = number

/* prettier-ignore-end */
