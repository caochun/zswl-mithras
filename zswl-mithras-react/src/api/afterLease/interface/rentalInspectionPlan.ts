/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [下载指定的项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10664) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/report/specific/download`
 * @更新时间 `2022-11-19 11:44:48`
 */
export interface SpecificDownloadRequest {
  /**
   * 业务数据id列表
   */
  ids: number[]
}

/**
 * 接口 [下载指定的项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10664) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/report/specific/download`
 * @更新时间 `2022-11-19 11:44:48`
 */
export interface SpecificDownloadResponse {}

/**
 * 接口 [下载指定部门报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10448) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/report/dept/download`
 * @更新时间 `2022-11-18 18:17:21`
 */
export interface DeptDownloadRequest {
  /**
   * 租后检查计划id
   */
  planId: number
  /**
   * 业务部门id
   */
  deptId: number
}

/**
 * 接口 [下载指定部门报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10448) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/report/dept/download`
 * @更新时间 `2022-11-18 18:17:21`
 */
export interface DeptDownloadResponse {}

/**
 * 接口 [保存需检查项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10520) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/save`
 * @更新时间 `2022-11-19 14:38:12`
 */
export interface ProjectSaveRequest {
  /**
   * 检查计划id
   */
  planId: number
  /**
   * id
   */
  id?: number
  /**
   * 项目id
   */
  projectId: number
  /**
   * 检查形式 参考枚举 AfterLeaseCheckWayEnum
   */
  checkWay?: string
  /**
   * 本次是否需要检查，true-需要，false-不需要
   */
  check: boolean
  /**
   * 协查风控经理id
   */
  riskManagerId?: number
  /**
   * 协查风控经理名称
   */
  riskManagerName?: string
}

/**
 * 接口 [保存需检查项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10520) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/save`
 * @更新时间 `2022-11-19 14:38:12`
 */
export interface ProjectSaveResponse {
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
 * 接口 [变更项目报告类型（变更模板）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10564) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/reporttype/change`
 * @更新时间 `2022-11-17 10:48:27`
 */
export interface ReporttypeChangeRequest {
  /**
   * 主键id
   */
  id: number
  /**
   * 项目报告模板类型
   */
  reportType: string
}

/**
 * 接口 [变更项目报告类型（变更模板）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10564) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/reporttype/change`
 * @更新时间 `2022-11-17 10:48:27`
 */
export interface ReporttypeChangeResponse {
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
 * 接口 [新增需检查项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10352) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/add`
 * @更新时间 `2022-11-14 10:43:14`
 */
export interface ProjectAddRequest {
  /**
   * 检查计划id
   */
  planId: number
  /**
   * 项目列表
   */
  projectList: {
    /**
     * 项目id
     */
    projectId: number
    /**
     * 检查形式 参考枚举 AfterLeaseCheckWayEnum
     */
    checkWay?: string
    /**
     * 本次是否需要检查，true-需要，false-不需要
     */
    check: boolean
    /**
     * 协查风控经理id
     */
    riskManagerId?: number
    /**
     * 协查风控经理名称
     */
    riskManagerName?: string
  }[]
}

/**
 * 接口 [新增需检查项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10352) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/add`
 * @更新时间 `2022-11-14 10:43:14`
 */
export interface ProjectAddResponse {
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
 * 接口 [移除被选择的检查项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10644) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/remove`
 * @更新时间 `2022-11-18 18:17:21`
 */
export interface ProjectRemoveRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [移除被选择的检查项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10644) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/remove`
 * @更新时间 `2022-11-18 18:17:21`
 */
export interface ProjectRemoveResponse {
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
 * 接口 [获取按照业务部门分组的项目信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10372) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/listGroupByDept`
 * @更新时间 `2022-11-16 10:04:51`
 */
export interface ProjectListGroupByDeptRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取按照业务部门分组的项目信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10372) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/listGroupByDept`
 * @更新时间 `2022-11-16 10:04:51`
 */
export type ProjectListGroupByDeptResponse = {
  /**
   * 业务部门id
   */
  deptId?: number
  /**
   * 业务部门名称
   */
  deptName?: string
  /**
   * 项目id列表
   */
  projectIdList?: number[]
}[]

/**
 * 接口 [获取检查计划项目信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10740) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/info/get`
 * @更新时间 `2022-11-20 11:25:57`
 */
export interface InfoGetRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取检查计划项目信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10740) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/info/get`
 * @更新时间 `2022-11-20 11:25:57`
 */
export interface InfoGetResponse {
  /**
   * 检查计划的项目id
   */
  id?: number
  /**
   * 项目评审的项目id
   */
  projectId?: number
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 项目主办id
   */
  projectSponsorId?: number
  /**
   * 报告类型
   */
  reportType?: string
  /**
   * 审批状态
   */
  approvalStatus?: string
  /**
   * 承租人/债权人列表
   */
  lesseeList?: {
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户类型
     */
    clientType?: string
    /**
     * 存量风险敝口
     */
    stockRiskExposure?: number
    /**
     * 客户名称
     */
    clientName?: string
  }[]
  /**
   * 担保人列表
   */
  guarantorList?: {
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户类型
     */
    clientType?: string
    /**
     * 存量风险敝口
     */
    stockRiskExposure?: number
    /**
     * 客户名称
     */
    clientName?: string
  }[]
}

/**
 * 接口 [获取检查项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10368) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/list`
 * @更新时间 `2022-11-19 14:55:49`
 */
export interface ProjectListRequest {
  /**
   * 租后检查计划id
   */
  planId: number
  /**
   * 租后检查项目id列表
   */
  projectIdList?: number[]
}

/**
 * 接口 [获取检查项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10368) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/list`
 * @更新时间 `2022-11-19 14:55:49`
 */
export type ProjectListResponse = {
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 项目id
   */
  projectId?: number
  /**
   * 项目编号
   */
  projectCode?: string
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 项目类型
   */
  projectType?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 项目主办id
   */
  projectSponsorId?: number
  /**
   * 项目主办名称
   */
  projectSponsorName?: string
  /**
   * 是否检查
   */
  check?: boolean
  /**
   * 检查方式
   */
  checkWay?: string
  /**
   * 协查风控经理id
   */
  riskManagerId?: number
  /**
   * 协查风控经理名称
   */
  riskManagerName?: string
  /**
   * 检查报告类型
   */
  reportType?: string
  /**
   * 审批状态
   */
  approvalStatus?: string
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [非季度检查计划查询项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10612) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/query`
 * @更新时间 `2022-11-18 10:30:04`
 */
export interface ProjectQueryRequest {
  /**
   * 检查计划id
   */
  planId: number
  /**
   * 项目名称
   */
  projectName: string
}

/**
 * 接口 [非季度检查计划查询项目↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10612) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/query`
 * @更新时间 `2022-11-18 10:30:04`
 */
export type ProjectQueryResponse = {
  /**
   * 项目id
   */
  projectId?: number
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 项目编号
   */
  projectCode?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 主办id
   */
  projectSponsorId?: number
  /**
   * 主办名称
   */
  projectSponsorName?: string
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 是否已被选择
   */
  isSelected?: boolean
}[]

/**
 * 接口 [项目检查提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10600) 的 **请求类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/process/submit`
 * @更新时间 `2022-11-17 20:11:49`
 */
export interface ProcessSubmitRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [项目检查提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10600) 的 **返回类型**
 *
 * @分类 [租后管理-检查计划关联项目相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1838)
 * @请求头 `POST /afterlease/checkplan/project/process/submit`
 * @更新时间 `2022-11-17 20:11:49`
 */
export interface ProcessSubmitResponse {
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

/* prettier-ignore-end */
