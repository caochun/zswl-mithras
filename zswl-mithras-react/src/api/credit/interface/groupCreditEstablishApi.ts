/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改集团授信立项基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10756) 的 **请求类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/modify`
 * @更新时间 `2022-11-21 11:43:07`
 */
export interface InfoModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 授信说明
   */
  projBackground?: string
  /**
   * 申报授信金额
   */
  applyCreditAmount: number
  /**
   * 额度有效期限月数
   */
  validMonthCount: number
  /**
   * 额度是否可循环
   */
  creditAmountLoop: number
  /**
   * 项目主办用户id
   */
  projSponsorUserId: number
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 业务部门id
   */
  bizDeptId: number
  /**
   * 业务部门负责人id
   */
  bizDeptLeaderId: number
  /**
   * 业务分管领导id
   */
  bizDivisionLeaderId: number
  /**
   * 风控经理id
   */
  riskControlManagerId?: number
}

/**
 * 接口 [修改集团授信立项基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10756) 的 **返回类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/modify`
 * @更新时间 `2022-11-21 11:43:07`
 */
export interface InfoModifyResponse {
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
 * 接口 [新增集团授信立项基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10752) 的 **请求类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/add`
 * @更新时间 `2022-11-21 11:43:04`
 */
export interface InfoAddRequest {
  /**
   * 授信客户id
   */
  clientId: number
  /**
   * 授信名称
   */
  projName: string
  /**
   * 审批类型
   */
  approvalType: string
}

/**
 * 接口 [新增集团授信立项基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10752) 的 **返回类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/add`
 * @更新时间 `2022-11-21 11:43:04`
 */
export interface InfoAddResponse {
  /**
   * id
   */
  id?: number
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 审批类型
   */
  approvalType?: string
}

/**
 * 接口 [获取集团授信存量风险敞口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10768) 的 **请求类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/exposure`
 * @更新时间 `2022-11-21 11:43:17`
 */
export interface CreditExposureRequest {
  /**
   * 客户id
   */
  clientId: number
}

/**
 * 接口 [获取集团授信存量风险敞口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10768) 的 **返回类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/exposure`
 * @更新时间 `2022-11-21 11:43:17`
 */
export type CreditExposureResponse = number

/**
 * 接口 [集团授信立项基本信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10760) 的 **请求类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/list`
 * @更新时间 `2022-11-21 11:43:10`
 */
export interface InfoListRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 业务部门
   */
  bizDeptId?: number
  /**
   * 项目主办
   */
  projSponsorUserId?: number
  /**
   * 立项状态
   */
  groupCreditEstablishStatus?: string
  /**
   * 审批状态
   */
  groupCreditEstablishProcessStatus?: string
  /**
   * 创建时间从
   */
  createFrom?: string
  /**
   * 创建时间到
   */
  createTo?: string
  /**
   * 更新时间从
   */
  updateFrom?: string
  /**
   * 更新时间到
   */
  updateTo?: string
  /**
   * 申报授信金额从
   */
  creditAmountFrom?: number
  /**
   * 申报授信金额到
   */
  creditAmountTo?: number
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
}

/**
 * 接口 [集团授信立项基本信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10760) 的 **返回类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/list`
 * @更新时间 `2022-11-21 11:43:10`
 */
export interface InfoListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 授信主体客户id
     */
    clientId?: number
    /**
     * 授信主体客户名称
     */
    clientName?: string
    /**
     * 授信名称
     */
    projName?: string
    /**
     * 项目编号
     */
    projCode?: string
    /**
     * 申报授信金额
     */
    applyCreditAmount?: number
    /**
     * 业务部门id
     */
    bizDeptId?: number
    /**
     * 业务部门名称
     */
    bizDeptName?: string
    /**
     * 项目主办用户id
     */
    projSponsorUserId?: number
    /**
     * 项目主办用户名称
     */
    projSponsorUserName?: string
    /**
     * 项目协办方用户id列表
     */
    projCosponsorUserIds?: number[]
    /**
     * 项目协办方用户名称列表
     */
    projCosponsorUserNames?: string[]
    /**
     * 立项状态
     */
    groupCreditEstablishStatus?: string
    /**
     * 流程状态
     */
    groupCreditEstablishProcessStatus?: string
    /**
     * 创建时间
     */
    createTime?: string
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
}

/**
 * 接口 [集团授信立项基本信息详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10764) 的 **请求类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/detail`
 * @更新时间 `2022-11-21 11:43:13`
 */
export interface InfoDetailRequest {
  /**
   * 集团授信立项id
   */
  groupCreditEstablishId: number
}

/**
 * 接口 [集团授信立项基本信息详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10764) 的 **返回类型**
 *
 * @分类 [集团授信立项基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1886)
 * @请求头 `POST /group/credit/establish/base/info/detail`
 * @更新时间 `2022-11-21 11:43:13`
 */
export interface InfoDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 授信主体客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 授信主体存量风险敞口
   */
  clientRiskExposure?: number
  /**
   * 授信名称
   */
  projName?: string
  /**
   * 审批类型
   */
  approvalType?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 授信说明
   */
  projBackground?: string
  /**
   * 申报授信金额
   */
  applyCreditAmount?: number
  /**
   * 额度有效期限月数
   */
  validMonthCount?: number
  /**
   * 额度是否可循环
   */
  creditAmountLoop?: number
  /**
   * 项目主办用户id
   */
  projSponsorUserId?: number
  /**
   * 项目主办用户名称
   */
  projSponsorUserName?: string
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 项目协办方用户名称列表
   */
  projCosponsorUserNames?: string[]
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 业务部门负责人id
   */
  bizDeptLeaderId?: number
  /**
   * 业务部门负责人名称
   */
  bizDeptLeaderName?: string
  /**
   * 业务分管领导id
   */
  bizDivisionLeaderId?: number
  /**
   * 业务分管领导名称
   */
  bizDivisionLeaderName?: string
  /**
   * 风控经理id
   */
  riskControlManagerId?: number
  /**
   * 风控经理名称
   */
  riskControlManagerName?: string
  /**
   * 立项状态
   */
  groupCreditEstablishStatus?: string
  /**
   * 流程状态
   */
  groupCreditEstablishProcessStatus?: string
}

/* prettier-ignore-end */
