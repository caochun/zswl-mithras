/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [征信查询列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37897) 的 **请求类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `POST /creditreport/project/list`
 * @更新时间 `2025-10-27 14:39:00`
 */
export interface ProjectListRequest {
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
   * 项目id
   */
  projectId?: number
}

/**
 * 接口 [征信查询列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37897) 的 **返回类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `POST /creditreport/project/list`
 * @更新时间 `2025-10-27 14:39:00`
 */
export interface ProjectListResponse {
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
 * 接口 [征信查询删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37903) 的 **请求类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `GET /creditreport/project/delete`
 * @更新时间 `2025-10-27 14:39:00`
 */
export interface ProjectDeleteRequest {
  id?: string
}

/**
 * 接口 [征信查询删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37903) 的 **返回类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `GET /creditreport/project/delete`
 * @更新时间 `2025-10-27 14:39:00`
 */
export type ProjectDeleteResponse = null

/**
 * 接口 [根据项目id反显项目信息和关联客户信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37891) 的 **请求类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `GET /creditreport/project/showCreditReportByProjId`
 * @更新时间 `2025-10-27 08:50:30`
 */
export interface ProjectShowCreditReportByProjIdRequest {
  projId?: string
}

/**
 * 接口 [根据项目id反显项目信息和关联客户信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37891) 的 **返回类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `GET /creditreport/project/showCreditReportByProjId`
 * @更新时间 `2025-10-27 08:50:30`
 */
export interface ProjectShowCreditReportByProjIdResponse {
  /**
   * 关联项目id
   */
  projId?: number
  /**
   * 关联项目编号
   */
  projCode?: string
  /**
   * 关联项目名称
   */
  projectName?: string
  /**
   * 查询版本
   */
  selectVersion?: string
  /**
   * 信用报告封装格式
   */
  reportFormat?: string
  /**
   * 客户列表
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
 * 接口 [根据项目id反显项目信息和关联客户信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37921) 的 **请求类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `POST /creditreport/project/showCreditReportByProjId`
 * @更新时间 `2025-10-27 14:38:59`
 */
export interface ProjectShowCreditReportByProjIdRequest {
  /**
   * 项目id
   */
  projectId?: number
  /**
   * 业务类型 PROJ_ESTABLISH:项目立项 PROJ_REVIEW:项目评审 GROUP_CREDIT_ESTABLISH:授信立项 GROUP_CREDIT_REVIEW：授信评审 PAYMENT：付款申请
   */
  bizType?: string
}

/**
 * 接口 [根据项目id反显项目信息和关联客户信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37921) 的 **返回类型**
 *
 * @分类 [CreditSearchProjectController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5390)
 * @请求头 `POST /creditreport/project/showCreditReportByProjId`
 * @更新时间 `2025-10-27 14:38:59`
 */
export interface ProjectShowCreditReportByProjIdResponse {
  /**
   * 关联项目id
   */
  projId?: number
  /**
   * 关联项目编号
   */
  projCode?: string
  /**
   * 关联项目名称
   */
  projectName?: string
  /**
   * 查询版本
   */
  selectVersion?: string
  /**
   * 信用报告封装格式
   */
  reportFormat?: string
  /**
   * 客户列表
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

/* prettier-ignore-end */
