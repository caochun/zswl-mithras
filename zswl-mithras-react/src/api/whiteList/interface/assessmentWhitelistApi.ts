/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [评估机构白名单-保存出库原因↗](http://yapi.zswltec.com:3000/project/11/interface/api/37111) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/out/reason/save`
 * @更新时间 `2025-09-05 16:45:47`
 */
export interface ReasonSaveRequest {
  /**
   * id
   */
  id: number
  /**
   * 出库原因
   */
  outReason?: string
}

/**
 * 接口 [评估机构白名单-保存出库原因↗](http://yapi.zswltec.com:3000/project/11/interface/api/37111) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/out/reason/save`
 * @更新时间 `2025-09-05 16:45:47`
 */
export interface ReasonSaveResponse {
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
 * 接口 [评估机构白名单-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37051) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/pagelist`
 * @更新时间 `2025-09-05 09:42:09`
 */
export interface WhitelistPagelistRequest {
  /**
   * 评估机构名称
   */
  companyName?: string
  /**
   * 状态
   */
  recordStatus?: string
  /**
   * 到期日-开始
   */
  recordExpireDateFrom?: string
  /**
   * 到期日-结束
   */
  recordExpireDateTo?: string
  /**
   * 创建人id
   */
  createBy?: number
  /**
   * 流程状态
   */
  processStatus?: string
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
 * 接口 [评估机构白名单-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37051) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/pagelist`
 * @更新时间 `2025-09-05 09:42:09`
 */
export interface WhitelistPagelistResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 评估机构名称
     */
    companyName?: string
    /**
     * 统一社会信用代码
     */
    uscCode?: string
    /**
     * 状态
     */
    recordStatus?: string
    /**
     * 流程状态
     */
    processStatus?: string
    /**
     * 生效日期
     */
    recordEffectDate?: string
    /**
     * 到期日
     */
    recordExpireDate?: string
    /**
     * 创建人id
     */
    createBy?: number
    /**
     * 创建人名称
     */
    createByName?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 评估机构池中的id
     */
    companyId?: number
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
 * 接口 [评估机构白名单-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37099) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/delete`
 * @更新时间 `2025-09-05 09:42:09`
 */
export interface WhitelistDeleteRequest {
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
 * 接口 [评估机构白名单-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37099) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/delete`
 * @更新时间 `2025-09-05 09:42:09`
 */
export interface WhitelistDeleteResponse {
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
 * 接口 [评估机构白名单-取消操作↗](http://yapi.zswltec.com:3000/project/11/interface/api/37105) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/cancel`
 * @更新时间 `2025-09-05 11:13:40`
 */
export interface WhitelistCancelRequest {
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
 * 接口 [评估机构白名单-取消操作↗](http://yapi.zswltec.com:3000/project/11/interface/api/37105) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/cancel`
 * @更新时间 `2025-09-05 11:13:40`
 */
export interface WhitelistCancelResponse {
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
 * 接口 [评估机构白名单-提交出库申请↗](http://yapi.zswltec.com:3000/project/11/interface/api/37075) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/out/submit`
 * @更新时间 `2025-09-05 16:45:47`
 */
export interface OutSubmitRequest {
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
 * 接口 [评估机构白名单-提交出库申请↗](http://yapi.zswltec.com:3000/project/11/interface/api/37075) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/out/submit`
 * @更新时间 `2025-09-05 16:45:47`
 */
export type OutSubmitResponse = string

/**
 * 接口 [评估机构白名单-提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/37057) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/submit`
 * @更新时间 `2025-09-03 10:09:50`
 */
export interface WhitelistSubmitRequest {
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
 * 接口 [评估机构白名单-提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/37057) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/submit`
 * @更新时间 `2025-09-03 10:09:50`
 */
export type WhitelistSubmitResponse = string

/**
 * 接口 [评估机构白名单-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/37081) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/add`
 * @更新时间 `2025-09-03 18:43:53`
 */
export interface WhitelistAddRequest {
  /**
   * 统一社会信用代码
   */
  uscCode: string
}

/**
 * 接口 [评估机构白名单-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/37081) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/add`
 * @更新时间 `2025-09-03 18:43:53`
 */
export type WhitelistAddResponse = number

/**
 * 接口 [评估机构白名单-更新工商信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37045) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/commerce/refresh`
 * @更新时间 `2025-09-03 10:09:50`
 */
export interface CommerceRefreshRequest {
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
 * 接口 [评估机构白名单-更新工商信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37045) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/commerce/refresh`
 * @更新时间 `2025-09-03 10:09:50`
 */
export interface CommerceRefreshResponse {
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
 * 接口 [评估机构白名单-评估机构详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37063) 的 **请求类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/detail`
 * @更新时间 `2025-09-05 16:36:19`
 */
export interface WhitelistDetailRequest {
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
 * 接口 [评估机构白名单-评估机构详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37063) 的 **返回类型**
 *
 * @分类 [评估机构白名单↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5270)
 * @请求头 `POST /appraisalcompany/whitelist/detail`
 * @更新时间 `2025-09-05 16:36:19`
 */
export interface WhitelistDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 评估机构名称
   */
  companyName?: string
  /**
   * 统一社会信用代码
   */
  uscCode?: string
  /**
   * 成立日期
   */
  establishDate?: string
  /**
   * 营业许可证到期日期
   */
  licenseExpireDate?: string
  /**
   * 营业许可证是否长期
   */
  licenseIsLongTerm?: number
  /**
   * 业务范围
   */
  businessScope?: string
  /**
   * 状态
   */
  recordStatus?: string
  /**
   * 流程状态
   */
  processStatus?: string
  /**
   * 评估机构白名单生效日期
   */
  recordEffectDate?: string
  /**
   * 评估机构白名单到期日期
   */
  recordExpireDate?: string
  /**
   * 评估机构池对应id
   */
  companyId?: number
  /**
   * 关联项目信息
   */
  relatedProjectList?: {
    key?: number
    value?: string
  }[]
}

/* prettier-ignore-end */
