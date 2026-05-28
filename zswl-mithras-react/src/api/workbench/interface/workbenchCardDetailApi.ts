/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [工作台-不良项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12989) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/undesirable/proj`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface UndesirableProjRequest {
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
}

/**
 * 接口 [工作台-不良项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12989) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/undesirable/proj`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface UndesirableProjResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户Id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 项目主办
     */
    projSponsorUserId?: number
    /**
     * 项目主办名
     */
    projSponsorUserName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 合同金额
     */
    contractAmount?: number
    /**
     * 剩余本金
     */
    lastPrincipal?: number
    /**
     * 存量风险敞口
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
   * 其他携带参数
   */
  others?: {
    KEY?: {}
  }
}

/**
 * 接口 [工作台-各行业存量本金柱状图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12339) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/stockprincipal`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ChartStockprincipalRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-各行业存量本金柱状图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12339) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/stockprincipal`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ChartStockprincipalResponse {
  /**
   * 表名称
   */
  title?: string
  /**
   * 表数据
   */
  data?: {
    /**
     * 数据类型(名称)
     */
    dataType?: string
    /**
     * 图表类型:bar-柱状, line-折线
     */
    chartType?: string
    /**
     * 图表数据集合
     */
    list?: {
      /**
       * 横向坐标展示名称
       */
      name?: string
      /**
       * 纵向坐标展示值
       */
      value?: string
      /**
       * 鼠标悬停展示值
       */
      hoverValue?: string
      /**
       * 单位
       */
      unitDisplay?: string
    }[]
  }[]
}

/**
 * 接口 [工作台-当前角色table列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12394) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/tabs`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface ChartTabsRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-当前角色table列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12394) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/tabs`
 * @更新时间 `2023-06-09 16:26:56`
 */
export type ChartTabsResponse = string[]

/**
 * 接口 [工作台-新增投放表格↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12459) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/launch`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ListLaunchRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-新增投放表格↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12459) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/launch`
 * @更新时间 `2023-06-09 16:26:57`
 */
export type ListLaunchResponse = {}[]

/**
 * 接口 [工作台-新增评审表格↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12454) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/review`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ListReviewRequest {
  /**
   * 业务部门id
   */
  deptId?: number
  /**
   * 开始时间
   */
  startDateTime?: string
}

/**
 * 接口 [工作台-新增评审表格↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12454) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/review`
 * @更新时间 `2023-06-09 16:26:57`
 */
export type ListReviewResponse = {
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 项目主办
   */
  projectOrganizer?: string
  /**
   * 项目协办
   */
  projectCoOrganizer?: string
  /**
   * 部门名称
   */
  deptName?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 授信金额，万元，小数点后2位
   */
  applyCreditAmount?: string
  /**
   * 审批通过时间
   */
  approveTime?: string
  /**
   * 创建时间
   */
  createTime?: string
}[]

/**
 * 接口 [工作台-本年新增投放客户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12964) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/client`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearClientRequest {
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
}

/**
 * 接口 [工作台-本年新增投放客户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12964) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/client`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearClientResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 项目id
     */
    projectId?: number
    /**
     * key
     */
    key?: string
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户Id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 项目阶段
     */
    projStage?: string
    /**
     * 项目主办
     */
    projSponsorUserId?: number
    /**
     * 项目主办名
     */
    projSponsorUserName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 数据类型
     */
    dataType?: string
    /**
     * 申请授信金额
     */
    applyCreditAmount?: number
    /**
     * 合同金额
     */
    contractAmount?: number
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
 * 接口 [工作台-本年新增立项列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12969) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/proj`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearProjRequest {
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
}

/**
 * 接口 [工作台-本年新增立项列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12969) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/proj`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearProjResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 项目id
     */
    projectId?: number
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户Id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 项目主办
     */
    projSponsorUserId?: number
    /**
     * 项目主办名
     */
    projSponsorUserName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 申请授信金额
     */
    applyCreditAmount?: number
    /**
     * 存量风险敞口
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
   * 其他携带参数
   */
  others?: {
    KEY?: {}
  }
}

/**
 * 接口 [工作台-本年新增项目评审列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12974) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/review`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearReviewRequest {
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
}

/**
 * 接口 [工作台-本年新增项目评审列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12974) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/review`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearReviewResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 项目评审id
     */
    projectId?: number
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户Id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 项目主办
     */
    projSponsorUserId?: number
    /**
     * 项目主办名
     */
    projSponsorUserName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 申请授信金额
     */
    applyCreditAmount?: number
    /**
     * 存量风险敞口
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
   * 其他携带参数
   */
  others?: {
    KEY?: {}
  }
}

/**
 * 接口 [工作台-本年累计投放金额列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12979) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/payment`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearPaymentRequest {
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
}

/**
 * 接口 [工作台-本年累计投放金额列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12979) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/year/payment`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface YearPaymentResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户Id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 项目主办
     */
    projSponsorUserId?: number
    /**
     * 项目主办名
     */
    projSponsorUserName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 合同金额
     */
    contractAmount?: number
    /**
     * 合同状态
     */
    contractStatus?: string
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
 * 接口 [工作台-柱状图指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12319) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/bar`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface ChartBarRequest {
  /**
   * 时间范围枚举, 本周(WEEKLY), 本月(MONTHLY), 本季度(QUARTERLY), 本年(YEARLY)
   */
  workbenchMetricTimeScope?: string
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-柱状图指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12319) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/bar`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface ChartBarResponse {
  /**
   * 表名称
   */
  title?: string
  /**
   * 表数据
   */
  data?: {
    /**
     * 数据类型(名称)
     */
    dataType?: string
    /**
     * 图表类型:bar-柱状, line-折线
     */
    chartType?: string
    /**
     * 图表数据集合
     */
    list?: {
      /**
       * 横向坐标展示名称
       */
      name?: string
      /**
       * 纵向坐标展示值
       */
      value?: string
      /**
       * 鼠标悬停展示值
       */
      hoverValue?: string
      /**
       * 单位
       */
      unitDisplay?: string
    }[]
  }[]
}

/**
 * 接口 [工作台-资金流动性分析曲线图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12324) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/fundsliquidity`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface ChartFundsliquidityRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-资金流动性分析曲线图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12324) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/fundsliquidity`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface ChartFundsliquidityResponse {
  /**
   * 表名称
   */
  title?: string
  /**
   * 表数据
   */
  data?: {
    /**
     * 数据类型(名称)
     */
    dataType?: string
    /**
     * 图表类型:bar-柱状, line-折线
     */
    chartType?: string
    /**
     * 图表数据集合
     */
    list?: {
      /**
       * 横向坐标展示名称
       */
      name?: string
      /**
       * 纵向坐标展示值
       */
      value?: string
      /**
       * 鼠标悬停展示值
       */
      hoverValue?: string
      /**
       * 单位
       */
      unitDisplay?: string
    }[]
  }[]
}

/**
 * 接口 [工作台-逾期项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12984) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/overdue/proj`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface OverdueProjRequest {
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
}

/**
 * 接口 [工作台-逾期项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12984) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/list/overdue/proj`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface OverdueProjResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户Id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 项目主办
     */
    projSponsorUserId?: number
    /**
     * 项目主办名
     */
    projSponsorUserName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 合同金额
     */
    contractAmount?: number
    /**
     * 剩余本金
     */
    lastPrincipal?: number
    /**
     * 存量风险敞口
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
   * 其他携带参数
   */
  others?: {
    KEY?: {}
  }
}

/**
 * 接口 [工作台-项目投放\/回款情况曲线图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12344) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/releasecollection`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ChartReleasecollectionRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-项目投放\/回款情况曲线图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12344) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/releasecollection`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ChartReleasecollectionResponse {
  /**
   * 表名称
   */
  title?: string
  /**
   * 表数据
   */
  data?: {
    /**
     * 数据类型(名称)
     */
    dataType?: string
    /**
     * 图表类型:bar-柱状, line-折线
     */
    chartType?: string
    /**
     * 图表数据集合
     */
    list?: {
      /**
       * 横向坐标展示名称
       */
      name?: string
      /**
       * 纵向坐标展示值
       */
      value?: string
      /**
       * 鼠标悬停展示值
       */
      hoverValue?: string
      /**
       * 单位
       */
      unitDisplay?: string
    }[]
  }[]
}

/**
 * 接口 [工作台-项目整体收益率曲线图（行业对比）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12334) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/projecttype/returnrate`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ProjecttypeReturnrateRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-项目整体收益率曲线图（行业对比）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12334) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/projecttype/returnrate`
 * @更新时间 `2023-06-09 16:26:57`
 */
export interface ProjecttypeReturnrateResponse {
  /**
   * 表名称
   */
  title?: string
  /**
   * 表数据
   */
  data?: {
    /**
     * 数据类型(名称)
     */
    dataType?: string
    /**
     * 图表类型:bar-柱状, line-折线
     */
    chartType?: string
    /**
     * 图表数据集合
     */
    list?: {
      /**
       * 横向坐标展示名称
       */
      name?: string
      /**
       * 纵向坐标展示值
       */
      value?: string
      /**
       * 鼠标悬停展示值
       */
      hoverValue?: string
      /**
       * 单位
       */
      unitDisplay?: string
    }[]
  }[]
}

/**
 * 接口 [工作台-项目整体收益率曲线图（部门对比）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12329) 的 **请求类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/dept/returnrate`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface DeptReturnrateRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-项目整体收益率曲线图（部门对比）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12329) 的 **返回类型**
 *
 * @分类 [工作台-指标图-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2513)
 * @请求头 `POST /workbench/chart/dept/returnrate`
 * @更新时间 `2023-06-09 16:26:56`
 */
export interface DeptReturnrateResponse {
  /**
   * 表名称
   */
  title?: string
  /**
   * 表数据
   */
  data?: {
    /**
     * 数据类型(名称)
     */
    dataType?: string
    /**
     * 图表类型:bar-柱状, line-折线
     */
    chartType?: string
    /**
     * 图表数据集合
     */
    list?: {
      /**
       * 横向坐标展示名称
       */
      name?: string
      /**
       * 纵向坐标展示值
       */
      value?: string
      /**
       * 鼠标悬停展示值
       */
      hoverValue?: string
      /**
       * 单位
       */
      unitDisplay?: string
    }[]
  }[]
}

/* prettier-ignore-end */
