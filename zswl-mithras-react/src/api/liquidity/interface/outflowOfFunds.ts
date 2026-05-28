/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [压力测试-流出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12494) 的 **请求类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /stress/testing/outflow/list`
 * @更新时间 `2023-05-17 09:35:42`
 */
export interface OutflowListRequest {
  /**
   * 时间范围-从
   */
  timeFrom?: string
  /**
   * 时间范围-到
   */
  timeTo?: string
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
 * 接口 [压力测试-流出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12494) 的 **返回类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /stress/testing/outflow/list`
 * @更新时间 `2023-05-17 09:35:42`
 */
export interface OutflowListResponse {
  dataType?: string
  chartType?: string
}

/**
 * 接口 [现金流流出明细-导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12514) 的 **请求类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /cash/outflow/export`
 * @更新时间 `2023-05-16 17:58:54`
 */
export interface OutflowExportRequest {
  /**
   * 时间范围-从
   */
  timeFrom?: string
  /**
   * 时间范围-到
   */
  timeTo?: string
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
 * 接口 [现金流流出明细-导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12514) 的 **返回类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /cash/outflow/export`
 * @更新时间 `2023-05-16 17:58:54`
 */
export interface OutflowExportResponse {
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
 * 接口 [现金流流出明细-资产端↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12499) 的 **请求类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /assets/cash/outflow/list`
 * @更新时间 `2023-05-16 16:33:43`
 */
export interface OutflowListRequest {
  /**
   * 时间范围-从
   */
  timeFrom?: string
  /**
   * 时间范围-到
   */
  timeTo?: string
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
 * 接口 [现金流流出明细-资产端↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12499) 的 **返回类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /assets/cash/outflow/list`
 * @更新时间 `2023-05-16 16:33:43`
 */
export interface OutflowListResponse {
  /**
   * 明细记录
   */
  records?: {
    /**
     * 查询集合
     */
    list?: {
      /**
       * 项目id
       */
      projectId?: number
      /**
       * 数据类型
       */
      dataType?: string
      /**
       * 项目名称
       */
      projName?: string
      /**
       * 合同Id
       */
      contractId?: number
      /**
       * 合同编号
       */
      contractCode?: string
      /**
       * 合同总金额
       */
      contractAmount?: number
      /**
       * 现金流出时间
       */
      cashOutflowTime?: string
      /**
       * 保证金
       */
      earnestMoney?: number
      /**
       * 预计流出现金流合计
       */
      estimateCashOutflowAmount?: number
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
   * 合计
   */
  sum?: {
    /**
     * 合同总金额合计
     */
    contractAmountSum?: number
    /**
     * 保证金合计
     */
    earnestMoneySum?: number
    /**
     * 预计流出现金流合计-合计
     */
    estimateCashOutflowSum?: number
  }
}

/**
 * 接口 [现金流流出明细-资金端↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12504) 的 **请求类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /funds/cash/outflow/list`
 * @更新时间 `2023-05-16 15:27:49`
 */
export interface OutflowListRequest {
  /**
   * 时间范围-从
   */
  timeFrom?: string
  /**
   * 时间范围-到
   */
  timeTo?: string
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
 * 接口 [现金流流出明细-资金端↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12504) 的 **返回类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /funds/cash/outflow/list`
 * @更新时间 `2023-05-16 15:27:49`
 */
export interface OutflowListResponse {
  /**
   * 明细记录
   */
  records?: {
    /**
     * 查询集合
     */
    list?: {
      /**
       * 融资机构Id
       */
      financingOrgId?: number
      /**
       * 融资机构
       */
      financingOrg?: string
      /**
       * 融资详情Id
       */
      financingId?: number
      /**
       * 融资编码
       */
      financingCode?: string
      /**
       * 融资总额
       */
      financingAmount?: number
      /**
       * 现金流出时间
       */
      cashOutflowTime?: string
      /**
       * 本金
       */
      principle?: number
      /**
       * 利息
       */
      interest?: number
      /**
       * 预计流出现金流合计
       */
      estimateCashOutflowAmount?: number
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
   * 合计
   */
  sum?: {
    /**
     * 融资总额合计
     */
    financingSum?: number
    /**
     * 本金合计
     */
    principleSum?: number
    /**
     * 利息合计
     */
    interestSum?: number
    /**
     * 预计流出现金流合计-合计
     */
    estimateCashOutflowSum?: number
  }
}

/**
 * 接口 [预估现金流流出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12509) 的 **请求类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /estimate/cash/outflow/list`
 * @更新时间 `2023-05-17 09:35:42`
 */
export interface OutflowListRequest {
  /**
   * 时间范围-从
   */
  timeFrom?: string
  /**
   * 时间范围-到
   */
  timeTo?: string
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
 * 接口 [预估现金流流出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12509) 的 **返回类型**
 *
 * @分类 [流动性风险-资金流出-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2368)
 * @请求头 `POST /estimate/cash/outflow/list`
 * @更新时间 `2023-05-17 09:35:42`
 */
export interface OutflowListResponse {
  dataType?: string
  chartType?: string
}

/* prettier-ignore-end */
