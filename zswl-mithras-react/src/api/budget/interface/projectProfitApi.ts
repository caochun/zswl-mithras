/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [利润测算↗](http://yapi.zswltec.com:3000/project/11/interface/api/4753) 的 **请求类型**
 *
 * @分类 [财务管理-项目利润↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2688)
 * @请求头 `POST /finance/projectprofit/profit/calculation`
 * @更新时间 `2024-06-20 16:52:38`
 */
export interface ProfitCalculationRequest {
  /**
   * 测算利润月份入参
   */
  yearAndMonth?: string
}

/**
 * 接口 [利润测算↗](http://yapi.zswltec.com:3000/project/11/interface/api/4753) 的 **返回类型**
 *
 * @分类 [财务管理-项目利润↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2688)
 * @请求头 `POST /finance/projectprofit/profit/calculation`
 * @更新时间 `2024-06-20 16:52:38`
 */
export interface ProfitCalculationResponse {
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
 * 接口 [财务管理-项目利润-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13804) 的 **请求类型**
 *
 * @分类 [财务管理-项目利润↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2688)
 * @请求头 `POST /finance/projectprofit/pagelist`
 * @更新时间 `2024-06-20 16:53:58`
 */
export interface ProjectprofitPagelistRequest {
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
 * 接口 [财务管理-项目利润-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13804) 的 **返回类型**
 *
 * @分类 [财务管理-项目利润↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2688)
 * @请求头 `POST /finance/projectprofit/pagelist`
 * @更新时间 `2024-06-20 16:53:58`
 */
export interface ProjectprofitPagelistResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 年份
     */
    year?: number
    /**
     * 月份
     */
    month?: number
    /**
     * 当年累计收入
     */
    totalIncomeThisYear?: number
    /**
     * 当年累计资金成本
     */
    totalCostThisYear?: number
    /**
     * 当年累计风险金
     */
    totalRiskThisYear?: number
    /**
     * 当年累计附加税
     */
    totalAdditionalTaxThisYear?: number
    /**
     * 当年累计印花税
     */
    totalStampTaxThisYear?: number
    /**
     * 当年累计利润总额
     */
    totalProfitThisYear?: number
    /**
     * 当月收入
     */
    incomeThisMonth?: number
    /**
     * 当月资金成本
     */
    costThisMonth?: number
    /**
     * 当月风险金
     */
    riskThisMonth?: number
    /**
     * 当月利润
     */
    profitThisMonth?: number
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
 * 接口 [财务管理-项目利润-详情-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13799) 的 **请求类型**
 *
 * @分类 [财务管理-项目利润↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2688)
 * @请求头 `POST /finance/projectprofit/detail/pagelist`
 * @更新时间 `2024-06-20 16:53:56`
 */
export interface DetailPagelistRequest {
  /**
   * 项目利润记录id
   */
  projectProfitId: number
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 主办id
   */
  sponsorUserId?: number
  /**
   * 测算利润月份入参
   */
  yearAndMonth?: string
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
 * 接口 [财务管理-项目利润-详情-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13799) 的 **返回类型**
 *
 * @分类 [财务管理-项目利润↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2688)
 * @请求头 `POST /finance/projectprofit/detail/pagelist`
 * @更新时间 `2024-06-20 16:53:56`
 */
export interface DetailPagelistResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 项目利润记录id
     */
    projectProfitId?: number
    /**
     * 业务部门id
     */
    bizDeptId?: number
    /**
     * 业务部门名称
     */
    bizDeptName?: string
    /**
     * 主办id
     */
    sponsorUserId?: number
    /**
     * 主办名称
     */
    sponsorUserName?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 业务大类
     */
    bizType?: string
    /**
     * 业务小类
     */
    bizSubType?: string
    /**
     * 风控行业分类
     */
    riskControlIndustryClassify?: string
    /**
     * 年份
     */
    year?: number
    /**
     * 月份
     */
    month?: number
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 投放日
     */
    contractStartDate?: string
    /**
     * 当年累计收入
     */
    totalIncomeThisYear?: number
    /**
     * 当年累计资金成本
     */
    totalCostThisYear?: number
    /**
     * 当年累计风险金
     */
    totalRiskThisYear?: number
    /**
     * 当年累计附加税
     */
    totalAdditionalTaxThisYear?: number
    /**
     * 当年累计印花税
     */
    totalStampTaxThisYear?: number
    /**
     * 当年累计利润总额
     */
    totalProfitThisYear?: number
    /**
     * 当月收入
     */
    incomeThisMonth?: number
    /**
     * 当月资金成本
     */
    costThisMonth?: number
    /**
     * 当月风险金
     */
    riskThisMonth?: number
    /**
     * 当月利润
     */
    profitThisMonth?: number
    /**
     * 当年累计毛利
     */
    totalGrossProfitThisYear?: number
    /**
     * 当月毛利
     */
    grossProfitThisMonth?: number
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

/* prettier-ignore-end */
