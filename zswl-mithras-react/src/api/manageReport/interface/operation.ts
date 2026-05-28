/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [业务运行分析-明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26275) 的 **请求类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yewuyunyingfenxi/detail`
 * @更新时间 `2024-12-11 19:14:41`
 */
export interface ManagereportyewuyunyingfenxiDetailRequest {
  /**
   * 流程类型
   */
  processModelTypeList?: string[]
  /**
   * 流程开始日期-起，格式：yyyy-MM-dd
   */
  processStartDateFrom: string
  /**
   * 流程开始日期-止，格式：yyyy-MM-dd
   */
  processStartDateTo: string
  /**
   * 业务部门ID
   */
  bizDeptId?: number
  /**
   * 项目主办ID
   */
  projSponsorUserId?: number
  /**
   * 业务类型，PUBLIC-公用，INDUSTRY-产业
   */
  businessCategory?: string
  /**
   * 审批状态，RUNNING-审批中，FINISH-审批完成
   */
  processStatus?: string
  /**
   * 租赁类型，hui_zu-回租，zhi_zu-直租
   */
  leaseType?: string
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
 * 接口 [业务运行分析-明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26275) 的 **返回类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yewuyunyingfenxi/detail`
 * @更新时间 `2024-12-11 19:14:41`
 */
export interface ManagereportyewuyunyingfenxiDetailResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 流程实例ID
     */
    processInstanceId?: string
    /**
     * 流程模型类型
     */
    processModelType?: string
    /**
     * 项目阶段
     */
    projectStage?: string
    /**
     * 租赁类型
     */
    leaseTypes?: string
    /**
     * 租赁类型-展示
     */
    leaseTypesDisplay?: string
    /**
     * 客户风控行业分类
     */
    clientRiskControlIndustryClassify?: string
    /**
     * 业务类型
     */
    businessCategory?: string
    /**
     * 项目编号
     */
    projCode?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目金额（毫厘）
     */
    projAmount?: number
    /**
     * 业务部门ID
     */
    bizDeptId?: number
    /**
     * 业务部门名称
     */
    bizDeptName?: string
    /**
     * 项目主办ID
     */
    projSponsorUserId?: number
    /**
     * 项目主办名称
     */
    projSponsorUserName?: string
    /**
     * 流程状态
     */
    processStatus?: number
    /**
     * 流程状态-展示
     */
    processStatusDisplay?: string
    /**
     * 流程开始时间
     */
    processStartTimeStr?: string
    /**
     * 流程结束时间
     */
    processEndTimeStr?: string
    /**
     * 流程结束时间所属月份
     */
    processEndTimeMonth?: string
    /**
     * 流程结束时间所属年份
     */
    processEndTimeYear?: string
    /**
     * 投放日期
     */
    minPayDateStr?: string
    /**
     * 投放日期所属月份
     */
    minPayDateMonth?: string
    /**
     * 投放金额（毫厘）
     */
    actualPayAmount?: number
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
 * 接口 [业务运行分析-统计列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26293) 的 **请求类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yewuyunyingfenxi/statistic`
 * @更新时间 `2024-12-13 09:50:53`
 */
export interface ManagereportyewuyunyingfenxiStatisticRequest {
  /**
   * 统计纬度，按部门-DEPT，按月份-MONTH
   */
  groupType: string
  /**
   * 流程类型
   */
  processModelTypeList?: string[]
  /**
   * 流程开始日期-起，格式：yyyy-MM-dd
   */
  processStartDateFrom: string
  /**
   * 流程开始日期-止，格式：yyyy-MM-dd
   */
  processStartDateTo: string
  /**
   * 业务部门ID
   */
  bizDeptId?: number
  /**
   * 项目主办ID
   */
  projSponsorUserId?: number
  /**
   * 业务类型，PUBLIC-公用，INDUSTRY-产业
   */
  businessCategory?: string
  /**
   * 审批状态，RUNNING-审批中，FINISH-审批完成
   */
  processStatus?: string
  /**
   * 租赁类型，hui_zu-回租，zhi_zu-直租
   */
  leaseType?: string
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
 * 接口 [业务运行分析-统计列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26293) 的 **返回类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yewuyunyingfenxi/statistic`
 * @更新时间 `2024-12-13 09:50:53`
 */
export type ManagereportyewuyunyingfenxiStatisticResponse = {
  /**
   * 业务部门ID
   */
  bizDeptId?: number
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 所属月份
   */
  yearAndMonth?: string
  /**
   * 立项创建-数量
   */
  projEstablishCreateQuantity?: number
  /**
   * 立项创建-金额
   */
  projEstablishCreateAmount?: number
  /**
   * 评审创建-数量
   */
  projReviewCreateQuantity?: number
  /**
   * 评审创建-金额
   */
  projReviewCreateAmount?: number
  /**
   * 租赁物创建-数量
   */
  leaseItemCreateQuantity?: number
  /**
   * 租赁物创建-金额
   */
  leaseItemCreateAmount?: number
  /**
   * 合同创建-数量
   */
  contractCreateQuantity?: number
  /**
   * 合同创建-金额
   */
  contractCreateAmount?: number
  /**
   * 合同付款-数量
   */
  paymentCreateQuantity?: number
  /**
   * 合同付款-金额
   */
  paymentCreateAmount?: number
  /**
   * 合同投放-数量
   */
  paymentActualPayQuantity?: number
  /**
   * 合同投放-金额
   */
  paymentActualPayAmount?: number
}[]

/**
 * 接口 [运营待办-明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26305) 的 **请求类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yunyingdaiban/detail`
 * @更新时间 `2024-12-13 16:45:29`
 */
export interface ManagereportyunyingdaibanDetailRequest {
  /**
   * 流程实例ID
   */
  processInstanceIdList?: string[]
  /**
   * 岗位，运营经办-YYJB，运营复核-YYFH，运营负责人-YYFZR
   */
  job?: string
}

/**
 * 接口 [运营待办-明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26305) 的 **返回类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yunyingdaiban/detail`
 * @更新时间 `2024-12-13 16:45:29`
 */
export type ManagereportyunyingdaibanDetailResponse = {
  /**
   * 流程实例ID
   */
  processInstanceId?: string
  /**
   * 流程模型
   */
  processModelType?: string
  /**
   * 业务类型
   */
  leaseTypesDisplay?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 业务部门ID
   */
  bizDeptId?: number
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 项目主办ID
   */
  projSponsorUserId?: number
  /**
   * 项目主办名称
   */
  projSponsorUserName?: string
  /**
   * 流程状态
   */
  processStatus?: number
  /**
   * 流程状态-展示
   */
  processStatusDisplay?: string
  /**
   * 当前审批人ID
   */
  currentAssignerId?: number
  /**
   * 当前审批人名称
   */
  currentAssignerName?: string
}[]

/**
 * 接口 [运营待办-统计列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26311) 的 **请求类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yunyingdaiban/statistic`
 * @更新时间 `2024-12-16 14:11:36`
 */
export interface ManagereportyunyingdaibanStatisticRequest {
  /**
   * 流程实例ID
   */
  processInstanceIdList?: string[]
  /**
   * 岗位，运营经办-YYJB，运营复核-YYFH，运营负责人-YYFZR
   */
  job?: string
}

/**
 * 接口 [运营待办-统计列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26311) 的 **返回类型**
 *
 * @分类 [管理报表相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3626)
 * @请求头 `POST /managereport/yunyingdaiban/statistic`
 * @更新时间 `2024-12-16 14:11:36`
 */
export type ManagereportyunyingdaibanStatisticResponse = {
  /**
   * 流程模型集合
   */
  processModelTypeList?: string
  /**
   * 流程类型展示
   */
  processDisplay?: string
  /**
   * 已到达-运营经办-流程ID集合
   */
  arriveYYJBProcessInstanceIds?: string[]
  /**
   * 已到达-运营复核-流程ID集合
   */
  arriveYYFHProcessInstanceIds?: string[]
  /**
   * 已到达-运营负责人-流程ID集合
   */
  arriveYYFZRProcessInstanceIds?: string[]
  /**
   * 已到达-合计-流程ID集合
   */
  arriveHJProcessInstanceIds?: string[]
  /**
   * 将到达-运营经办-流程ID集合
   */
  willArriveYYJBProcessInstanceIds?: string[]
  /**
   * 将到达-运营复核-流程ID集合
   */
  willArriveYYFHProcessInstanceIds?: string[]
  /**
   * 将到达-运营负责人-流程ID集合
   */
  willArriveYYFZRProcessInstanceIds?: string[]
  /**
   * 将达到-合计-流程ID集合
   */
  willArriveHJProcessInstanceIds?: string[]
}[]

/* prettier-ignore-end */
