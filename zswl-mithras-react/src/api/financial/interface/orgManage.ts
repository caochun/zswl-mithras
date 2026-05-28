/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改担保机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/11224) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/modify`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 是否集团内关联方 1是/0否
   */
  relatedParty?: number
  /**
   * 成立日期
   */
  establishDate?: string
  /**
   * 核准日期
   */
  approvalDate?: string
  /**
   * 营运许可证是否为长期
   */
  longTimeLicense?: boolean
  /**
   * 营业许可证到期日(如果许可证是非长期类型)
   */
  bizLicenseEndDate?: string
  /**
   * 经济类型
   */
  economyType?: string
  /**
   * 组织机构类型
   */
  orgType?: string
  /**
   * 注册币种
   */
  registerCurrencyType?: string
  /**
   * 注册资本
   */
  registerCapital?: number
  /**
   * 实收币种
   */
  realCurrencyType?: string
  /**
   * 实收资本
   */
  realCapital?: number
  /**
   * 法人代表
   */
  corpRepresent?: string
  /**
   * 业务范围
   */
  bizScope?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改担保机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/11224) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/modify`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyModifyResponse {
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
 * 接口 [删除担保机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/11225) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/remove`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyRemoveRequest {
  /**
   * ids
   */
  ids: number[]
}

/**
 * 接口 [删除担保机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/11225) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/remove`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyRemoveResponse {
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
 * 接口 [同步天眼查数据↗](http://yapi.zswltec.com:3000/project/11/interface/api/11223) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/sync`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencySyncRequest {
  /**
   * 担保机构id
   */
  id?: number
}

/**
 * 接口 [同步天眼查数据↗](http://yapi.zswltec.com:3000/project/11/interface/api/11223) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/sync`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencySyncResponse {
  /**
   * id
   */
  id?: number
  /**
   * 担保机构名称
   */
  guaranteeAgencyName?: string
  /**
   * 担保机构编号
   */
  guaranteeAgencyCode?: string
  /**
   * 统一社会信用代码
   */
  uscCode?: string
  /**
   * 是否集团内关联方 是/否
   */
  relatedParty?: number
  /**
   * 成立日期
   */
  establishDate?: string
  /**
   * 核准日期
   */
  approvalDate?: string
  /**
   * 营运许可证是否为长期
   */
  longTimeLicense?: boolean
  /**
   * 营业许可证到期日(如果许可证是非长期类型)
   */
  bizLicenseEndDate?: string
  /**
   * 经济类型
   */
  economyType?: string
  /**
   * 组织机构类型
   */
  orgType?: string
  /**
   * 注册币种
   */
  registerCurrencyType?: string
  /**
   * 注册资本
   */
  registerCapital?: number
  /**
   * 实收币种
   */
  realCurrencyType?: string
  /**
   * 实收资本
   */
  realCapital?: number
  /**
   * 法人代表
   */
  corpRepresent?: string
  /**
   * 业务范围
   */
  bizScope?: string
  /**
   * 资金经理
   */
  fundManagerName?: string
  /**
   * 业务部门
   */
  deptName?: string
  /**
   * 业务分管领导
   */
  divisionLeaderName?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [担保机构下拉列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/11310) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/pulldown`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyPulldownRequest {
  /**
   * 担保机构名称
   */
  guaranteeAgencyName?: string
  /**
   * 融资机构id
   */
  organizationId?: number
}

/**
 * 接口 [担保机构下拉列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/11310) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/pulldown`
 * @更新时间 `2024-10-10 23:36:15`
 */
export type AgencyPulldownResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 担保机构名称
   */
  guaranteeAgencyName?: string
  /**
   * 担保机构编号
   */
  guaranteeAgencyCode?: string
  /**
   * 总担保额度
   */
  totalGuaranteeLimit?: number
  /**
   * 已使用担保额度
   */
  usedGuaranteeLimit?: number
  /**
   * 剩余担保额度
   */
  remainingGuaranteeLimit?: number
  /**
   * 担保期限
   */
  guaranteePeriod?: string
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
   * 创建人name
   */
  createByName?: string
}[]

/**
 * 接口 [担保机构列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/11222) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/list`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyListRequest {
  /**
   * 担保机构名称
   */
  guaranteeAgencyName?: string
  /**
   * 担保额度from
   */
  guaranteeLimitFrom?: string
  /**
   * 担保额度to
   */
  guaranteeLimitTo?: string
  /**
   * 担保日期from
   */
  guaranteeDateFrom?: string
  /**
   * 担保日期to
   */
  guaranteeDateTo?: string
  /**
   * 创建人id
   */
  createBy?: number
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
 * 接口 [担保机构列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/11222) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/list`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 担保机构名称
     */
    guaranteeAgencyName?: string
    /**
     * 担保机构编号
     */
    guaranteeAgencyCode?: string
    /**
     * 总担保额度
     */
    totalGuaranteeLimit?: number
    /**
     * 已使用担保额度
     */
    usedGuaranteeLimit?: number
    /**
     * 剩余担保额度
     */
    remainingGuaranteeLimit?: number
    /**
     * 担保期限
     */
    guaranteePeriod?: string
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
     * 创建人name
     */
    createByName?: string
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
    KEY?: {}
  }
}

/**
 * 接口 [担保机构详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/11226) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/detail`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyDetailRequest {
  id?: number
}

/**
 * 接口 [担保机构详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/11226) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/detail`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 担保机构名称
   */
  guaranteeAgencyName?: string
  /**
   * 担保机构编号
   */
  guaranteeAgencyCode?: string
  /**
   * 统一社会信用代码
   */
  uscCode?: string
  /**
   * 是否集团内关联方 是/否
   */
  relatedParty?: number
  /**
   * 成立日期
   */
  establishDate?: string
  /**
   * 核准日期
   */
  approvalDate?: string
  /**
   * 营运许可证是否为长期
   */
  longTimeLicense?: boolean
  /**
   * 营业许可证到期日(如果许可证是非长期类型)
   */
  bizLicenseEndDate?: string
  /**
   * 经济类型
   */
  economyType?: string
  /**
   * 组织机构类型
   */
  orgType?: string
  /**
   * 注册币种
   */
  registerCurrencyType?: string
  /**
   * 注册资本
   */
  registerCapital?: number
  /**
   * 实收币种
   */
  realCurrencyType?: string
  /**
   * 实收资本
   */
  realCapital?: number
  /**
   * 法人代表
   */
  corpRepresent?: string
  /**
   * 业务范围
   */
  bizScope?: string
  /**
   * 资金经理
   */
  fundManagerName?: string
  /**
   * 业务部门
   */
  deptName?: string
  /**
   * 业务分管领导
   */
  divisionLeaderName?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [新增担保机构并同步天眼查信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/11221) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/add`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyAddRequest {
  /**
   * 担保机构名称
   */
  guaranteeAgencyName: string
  /**
   * 统一社会信用代码
   */
  uscCode: string
}

/**
 * 接口 [新增担保机构并同步天眼查信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/11221) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/add`
 * @更新时间 `2024-10-10 23:36:15`
 */
export type AgencyAddResponse = number

/**
 * 接口 [新增用于手动录入的担保机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/11280) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/addhalf`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface AgencyAddhalfRequest {
  /**
   * 担保机构名称
   */
  guaranteeAgencyName: string
  /**
   * 统一社会信用代码
   */
  uscCode: string
}

/**
 * 接口 [新增用于手动录入的担保机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/11280) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/agency/addhalf`
 * @更新时间 `2024-10-10 23:36:15`
 */
export type AgencyAddhalfResponse = number

/**
 * 接口 [额度使用详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22369) 的 **请求类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/info/limitDetail`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface InfoLimitDetailRequest {
  /**
   * id
   */
  id: number
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
 * 接口 [额度使用详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22369) 的 **返回类型**
 *
 * @分类 [担保机构-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1962)
 * @请求头 `POST /fund/guarantee/info/limitDetail`
 * @更新时间 `2024-10-10 23:36:15`
 */
export interface InfoLimitDetailResponse {
  /**
   * 详情
   */
  limitDetailList?: {
    /**
     * 融资id
     */
    financingId?: number
    /**
     * 融资编号
     */
    financingCode?: string
    /**
     * 融资机构id
     */
    organizationId?: number
    /**
     * 融资机构名称
     */
    organizationName?: string
    /**
     * 剩余本金
     */
    remainingAmount?: number
    /**
     * 融资金额
     */
    financingAmount?: number
    /**
     * 担保融资金额
     */
    guaranteeFinancingAmount?: number
    /**
     * 信用融资金额
     */
    creditFinancingAmount?: number
    /**
     * 占用信用额度（元）
     */
    usedCreditAmount?: number
    /**
     * 占用担保额度（元）
     */
    usedGuaranteeAmount?: number
    /**
     * 剩余信用额度（元）
     */
    remainingCreditAmount?: number
    /**
     * 剩余担保额度（元）
     */
    remainingGuaranteeAmount?: number
    /**
     * 借款日期
     */
    borrowDate?: string
    /**
     * 到期日期
     */
    expireDate?: string
    /**
     * 融资状态
     */
    financingStatus?: string
    /**
     * 创建人id
     */
    createBy?: number
    /**
     * 创建人名称
     */
    createUserName?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 修改时间
     */
    updateTime?: string
  }[]
  /**
   * 总计
   */
  limitDetailSum?: {
    /**
     * 占用总额度（元）
     */
    usedTotalCreditAmountSum?: number
    /**
     * 剩余总授信额度（元）
     */
    remainingTotalCreditAmountSum?: number
  }
}

/* prettier-ignore-end */
