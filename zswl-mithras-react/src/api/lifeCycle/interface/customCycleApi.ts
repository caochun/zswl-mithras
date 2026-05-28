/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [客户全周期卡片↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13764) 的 **请求类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/card`
 * @更新时间 `2023-06-26 11:31:49`
 */
export interface LifecycleCardRequest {}

/**
 * 接口 [客户全周期卡片↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13764) 的 **返回类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/card`
 * @更新时间 `2023-06-26 11:31:49`
 */
export interface LifecycleCardResponse {
  /**
   * 总客户数
   */
  totalClientNum?: number
  /**
   * 总数本月新增
   */
  totalClientNumMonthIncrease?: number
  /**
   * 存续客户数
   */
  existingClientNum?: number
  /**
   * 存续客户数本月新增
   */
  existingClientNumMonthIncrease?: number
  /**
   * 结清客户数
   */
  settledClientNum?: number
  /**
   * 结清客户数本月新增
   */
  settledClientNumMonthIncrease?: number
  /**
   * 逾期客户数
   */
  overdueClientNum?: number
  /**
   * 逾期客户数本月新增
   */
  overdueClientNumMonthIncrease?: number
}

/**
 * 接口 [借据详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13784) 的 **请求类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/receipt`
 * @更新时间 `2023-06-26 11:31:51`
 */
export interface LifecycleReceiptRequest {
  /**
   * 客户id
   */
  clientId?: number
}

/**
 * 接口 [借据详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13784) 的 **返回类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/receipt`
 * @更新时间 `2023-06-26 11:31:51`
 */
export interface LifecycleReceiptResponse {
  /**
   * 逾期金额
   */
  overdueAmount?: number
  /**
   * 借据列表 ,PageR
   */
  receiptPage?: {
    /**
     * 查询集合 ,T
     */
    list?: {}[]
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
     * 其他携带参数(该参数为map)
     */
    others?: {
      /**
       * String
       */
      mapKey?: {}
      /**
       * Object
       */
      mapValue?: {}
    }
  }
}

/**
 * 接口 [客户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13769) 的 **请求类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/clientlist`
 * @更新时间 `2023-06-26 11:31:50`
 */
export interface LifecycleClientlistRequest {
  /**
   * 客户id
   */
  clientName?: string
  /**
   * 主办id
   */
  sponsorId?: number
  /**
   * 创建日期From
   */
  createDateFrom?: string
  /**
   * 创建日期To
   */
  createDateTo?: string
  /**
   * 所属部门id
   */
  deptId?: number
  /**
   * 创建人id
   */
  createBy?: number
  /**
   * 所选卡片, 可选值 TOTAL, EXISTING, SETTLED, OVERDUE
   */
  cardName?: string
  /**
   * 页码
   */
  page?: number
  /**
   * 每页条数
   */
  pageSize?: number
}

/**
 * 接口 [客户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13769) 的 **返回类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/clientlist`
 * @更新时间 `2023-06-26 11:31:50`
 */
export interface LifecycleClientlistResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * 客户id
     */
    id?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 客户编号
     */
    clientCode?: string
    /**
     * 客户类型
     */
    clientType?: string
    /**
     * 境内or境外
     */
    domesticOrAbroad?: string
    /**
     * 行业分类
     */
    industryType?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 创建人id
     */
    createBy?: number
    /**
     * 创建人名称
     */
    creatorName?: string
    /**
     * 创建人部门id
     */
    createByDept?: number
    /**
     * 创建人部门
     */
    creatorDeptName?: string
    /**
     * 客户状态
     */
    clientStatus?: string
    /**
     * 流程状态
     */
    processStatus?: string
    /**
     * 流程状态（中文）
     */
    processStatusName?: string
    /**
     * 行业分类名称
     */
    industryTypeName?: string
    /**
     * 是否需要审批（列表请求体showApprovalFlag为true时有该字段）（流程状态为变更中有该字段）
     */
    needApprovalFlag?: boolean
    /**
     * 所属部门id
     */
    belongDeptId?: number
    /**
     * 所属部门名称
     */
    belongDeptName?: string
    /**
     * 所属负责项目经理的id
     */
    belongSponsorId?: number
    /**
     * 所属负责项目经理的名称
     */
    belongSponsorName?: string
    /**
     * 授信金额（万元）
     */
    applyCreditAmount?: number
    /**
     * 剩余本金（万元）
     */
    lastPrincipal?: number
    /**
     * 存量风险敞口（万元）
     */
    stockRiskExposure?: number
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
   * 其他携带参数(该参数为map)
   */
  others?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * Object
     */
    mapValue?: {}
  }
}

/**
 * 接口 [客户详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13809) 的 **请求类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/clientdetail`
 * @更新时间 `2023-06-26 11:31:50`
 */
export interface LifecycleClientdetailRequest {
  /**
   * 客户id
   */
  clientId?: number
}

/**
 * 接口 [客户详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13809) 的 **返回类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/clientdetail`
 * @更新时间 `2023-06-26 11:31:50`
 */
export interface LifecycleClientdetailResponse {
  /**
   * 客户id
   */
  id?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 客户编号
   */
  clientCode?: string
  /**
   * 客户类型
   */
  clientType?: string
  /**
   * 境内or境外
   */
  domesticOrAbroad?: string
  /**
   * 行业分类
   */
  industryType?: string
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 创建人id
   */
  createBy?: number
  /**
   * 创建人名称
   */
  creatorName?: string
  /**
   * 创建人部门id
   */
  createByDept?: number
  /**
   * 创建人部门
   */
  creatorDeptName?: string
  /**
   * 客户状态
   */
  clientStatus?: string
  /**
   * 流程状态
   */
  processStatus?: string
  /**
   * 流程状态（中文）
   */
  processStatusName?: string
  /**
   * 行业分类名称
   */
  industryTypeName?: string
  /**
   * 是否需要审批（列表请求体showApprovalFlag为true时有该字段）（流程状态为变更中有该字段）
   */
  needApprovalFlag?: boolean
  /**
   * 所属部门id
   */
  belongDeptId?: number
  /**
   * 所属部门名称
   */
  belongDeptName?: string
  /**
   * 所属负责项目经理的id
   */
  belongSponsorId?: number
  /**
   * 所属负责项目经理的名称
   */
  belongSponsorName?: string
  /**
   * 授信金额（万元）
   */
  applyCreditAmount?: number
  /**
   * 剩余本金（万元）
   */
  lastPrincipal?: number
  /**
   * 存量风险敞口（万元）
   */
  stockRiskExposure?: number
}

/**
 * 接口 [客户项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13774) 的 **请求类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/projectlist`
 * @更新时间 `2023-06-26 11:31:50`
 */
export interface LifecycleProjectlistRequest {
  /**
   * 客户id
   */
  clientId?: number
}

/**
 * 接口 [客户项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13774) 的 **返回类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/projectlist`
 * @更新时间 `2023-06-26 11:31:50`
 */
export interface LifecycleProjectlistResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 申报授信金额
     */
    applyCreditAmount?: number
    /**
     * 合同金额
     */
    contractAmount?: number
    /**
     * 业务类型, 取枚举projectBizType
     */
    bizType?: string
    /**
     * 项目阶段，取枚举projStageEnum
     */
    projStage?: string
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
   * 其他携带参数(该参数为map)
   */
  others?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * Object
     */
    mapValue?: {}
  }
}

/**
 * 接口 [查询五级分类↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13779) 的 **请求类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/fivelevel`
 * @更新时间 `2023-06-26 11:31:51`
 */
export interface LifecycleFivelevelRequest {
  /**
   * 客户id
   */
  clientId?: number
}

/**
 * 接口 [查询五级分类↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13779) 的 **返回类型**
 *
 * @分类 [client-life-cycle-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2678)
 * @请求头 `POST /client/lifecycle/fivelevel`
 * @更新时间 `2023-06-26 11:31:51`
 */
export type LifecycleFivelevelResponse = string

/* prettier-ignore-end */
