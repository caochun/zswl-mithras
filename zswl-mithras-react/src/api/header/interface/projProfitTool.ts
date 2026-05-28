/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13789) 的 **请求类型**
 *
 * @分类 [会计利润测算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2683)
 * @请求头 `POST /profitcalculate/export`
 * @更新时间 `2023-06-25 16:45:05`
 */
export interface ProfitcalculateExportRequest {
  /**
   * 合同编号
   */
  contractCode?: string
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
 * 接口 [导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13789) 的 **返回类型**
 *
 * @分类 [会计利润测算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2683)
 * @请求头 `POST /profitcalculate/export`
 * @更新时间 `2023-06-25 16:45:05`
 */
export interface ProfitcalculateExportResponse {}

/**
 * 接口 [分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13794) 的 **请求类型**
 *
 * @分类 [会计利润测算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2683)
 * @请求头 `POST /profitcalculate/pagelist`
 * @更新时间 `2023-06-25 16:45:05`
 */
export interface ProfitcalculatePagelistRequest {
  /**
   * 合同编号
   */
  contractCode?: string
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
 * 接口 [分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13794) 的 **返回类型**
 *
 * @分类 [会计利润测算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2683)
 * @请求头 `POST /profitcalculate/pagelist`
 * @更新时间 `2023-06-25 16:45:05`
 */
export interface ProfitcalculatePagelistResponse {
  /**
   * 测算日期
   */
  calculateDate?: string
  /**
   * 分页结果对象
   */
  pageResult?: {
    /**
     * 查询集合
     */
    list?: {
      /**
       * 主键id
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
       * 项目类型
       */
      projectClassify?: string
      /**
       * 合同id
       */
      contractId?: number
      /**
       * 合同编号
       */
      contractCode?: string
      /**
       * 业务部门id
       */
      bizDeptId?: number
      /**
       * 业务部门名称
       */
      bizDeptName?: string
      /**
       * 业务类型
       */
      bizType?: string
      /**
       * 投放时间
       */
      contractStartDate?: string
      /**
       * 当年已确认收入（税后）
       */
      confirmIncomeThisYear?: number
      /**
       * 当年测算利息收入
       */
      calculateInterestThisYear?: number
      /**
       * 营业收入
       */
      operatingIncome?: number
      /**
       * FTP成本
       */
      ftpInterest?: number
      /**
       * 上期末风险金余额
       */
      riskBalanceEndOfLastYear?: number
      /**
       * 本期末风险金余额
       */
      riskBalanceEndOfThisYear?: number
      /**
       * 本年风险金计提/转回
       */
      riskUsedThisYear?: number
      /**
       * 附加税
       */
      additionalTax?: number
      /**
       * 利润总额
       */
      profit?: number
      /**
       * 利润总额（扣除费用）
       */
      profitExcludeFee?: number
      /**
       * 本年末剩余本金
       */
      remainingPrincipleEndOfThisYear?: number
      /**
       * 本年末保证金余额
       */
      remainingEarnestEndOfThisYear?: number
      /**
       * 年末敞口
       */
      riskExposureEndOfThisYear?: number
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
}

/* prettier-ignore-end */
