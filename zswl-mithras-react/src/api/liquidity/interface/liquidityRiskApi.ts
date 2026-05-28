/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [流动性指标↗](http://yapi.zswltec.com:3000/project/11/interface/api/26383) 的 **请求类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/index`
 * @更新时间 `2025-02-21 14:54:37`
 */
export interface ManageIndexRequest {
  /**
   * 预测区间-开始
   */
  queryDateStart: string
  /**
   * 预测区间-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [流动性指标↗](http://yapi.zswltec.com:3000/project/11/interface/api/26383) 的 **返回类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/index`
 * @更新时间 `2025-02-21 14:54:37`
 */
export interface ManageIndexResponse {
  /**
   * 负债久期
   */
  durationLiability?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 资产久期
   */
  durationAssets?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 资产久期（质押/监管）
   */
  durationAssetsPledgedSupervised?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 资产负债久期比
   */
  assetLiabilityDurationRatio?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 高流动性资产
   */
  highLiquidityAssets?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 高流动性负债
   */
  highLiquidityLiability?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 流动性覆盖率
   */
  liquidityCoverageRatio?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 流动性缺口
   */
  liquidityGap?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 流动性缺口率
   */
  liquidityGapRate?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
  /**
   * 可用授信比
   */
  availableCreditRatio?: {
    /**
     * 值
     */
    value?: number
    /**
     * 颜色
     */
    color?: string
    /**
     * 层级
     */
    level?: number
  }
}

/**
 * 接口 [流动性看板↗](http://yapi.zswltec.com:3000/project/11/interface/api/26377) 的 **请求类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/board`
 * @更新时间 `2025-02-21 14:54:37`
 */
export interface ManageBoardRequest {
  /**
   * 预测区间-开始
   */
  queryDateStart: string
  /**
   * 预测区间-结束
   */
  queryDateEnd: string
  /**
   * 可用余额预测天数
   */
  predictDay: number
}

/**
 * 接口 [流动性看板↗](http://yapi.zswltec.com:3000/project/11/interface/api/26377) 的 **返回类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/board`
 * @更新时间 `2025-02-21 14:54:37`
 */
export interface ManageBoardResponse {
  /**
   * 列表
   */
  list?: {
    /**
     * 数据时点
     */
    date?: string
    /**
     * 期初余额
     */
    initialBalance?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 预计回收租金
     */
    expectedRentRecovery?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 现金流支出
     */
    cashFlowExpenditure?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 债务偿还
     */
    debtRepayment?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 非ABS还款
     */
    nonAbsRepayment?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * ABS还款
     */
    absRepayment?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 刚性支出
     */
    rigidExpenditure?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 期末余额
     */
    endingBalance?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 监管户净流入（累计）
     */
    supervisedAccountFunds?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 监管户资金的负值
     */
    negativeSupervisedAccountFunds?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 非监管户净流入（累计）
     */
    nonSupervisedAccountFunds?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 监管户净流入（当日）
     */
    dailySupervisedAccountIncome?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 非监管户净流入（当日）
     */
    dailyNonSupervisedAccountIncome?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 期间实际或计划投放等支出金额
     */
    periodActualOrPlannedExpenditure?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 期间实际或计划融资等收款金额
     */
    periodActualOrPlannedFinancingReceipts?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 当日最大可用余额
     */
    dailyMaxAvailableBalance?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
  }[]
  /**
   * 合计
   */
  sum?: {
    /**
     * 数据时点
     */
    date?: string
    /**
     * 期初余额
     */
    initialBalance?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 预计回收租金
     */
    expectedRentRecovery?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 现金流支出
     */
    cashFlowExpenditure?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 债务偿还
     */
    debtRepayment?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 非ABS还款
     */
    nonAbsRepayment?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * ABS还款
     */
    absRepayment?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 刚性支出
     */
    rigidExpenditure?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 期末余额
     */
    endingBalance?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 监管户净流入（累计）
     */
    supervisedAccountFunds?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 监管户资金的负值
     */
    negativeSupervisedAccountFunds?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 非监管户净流入（累计）
     */
    nonSupervisedAccountFunds?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 监管户净流入（当日）
     */
    dailySupervisedAccountIncome?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 非监管户净流入（当日）
     */
    dailyNonSupervisedAccountIncome?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 期间实际或计划投放等支出金额
     */
    periodActualOrPlannedExpenditure?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 期间实际或计划融资等收款金额
     */
    periodActualOrPlannedFinancingReceipts?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
    /**
     * 当日最大可用余额
     */
    dailyMaxAvailableBalance?: {
      /**
       * 值
       */
      value?: number
      /**
       * 颜色
       */
      color?: string
      /**
       * 层级
       */
      level?: number
    }
  }
}

/**
 * 接口 [租金流入↗](http://yapi.zswltec.com:3000/project/11/interface/api/27193) 的 **请求类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/rent/income`
 * @更新时间 `2025-02-23 21:40:17`
 */
export interface RentIncomeRequest {
  /**
   * 本期到期日-开始
   */
  expireDateFrom?: string
  /**
   * 本期到期日-结束
   */
  expireDateTo?: string
  /**
   * 预测区间-开始
   */
  queryDateStart: string
  /**
   * 预测区间-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [租金流入↗](http://yapi.zswltec.com:3000/project/11/interface/api/27193) 的 **返回类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/rent/income`
 * @更新时间 `2025-02-23 21:40:17`
 */
export type RentIncomeResponse = {
  /**
   * 承租人Id
   */
  tenantId?: number
  /**
   * 承租人名称
   */
  tenantName?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 合同Id
   */
  contractId?: number
  /**
   * 本期到期日 format: yyyy-MM-dd
   */
  expireDate?: string
  /**
   * 本期应还金额
   */
  shouldPayAmount?: string
  /**
   * 流入账户
   */
  incomeAccount?: string
  /**
   * 流入账户性质
   */
  incomeAccountProperty?: string
}[]

/**
 * 接口 [还本付息↗](http://yapi.zswltec.com:3000/project/11/interface/api/27199) 的 **请求类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/repay`
 * @更新时间 `2025-02-23 21:44:03`
 */
export interface ManageRepayRequest {
  /**
   * 本期到期日-开始
   */
  expireDateFrom?: string
  /**
   * 本期到期日-结束
   */
  expireDateTo?: string
  /**
   * 预测区间-开始
   */
  queryDateStart: string
  /**
   * 预测区间-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [还本付息↗](http://yapi.zswltec.com:3000/project/11/interface/api/27199) 的 **返回类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/repay`
 * @更新时间 `2025-02-23 21:44:03`
 */
export type ManageRepayResponse = {
  /**
   * 融资机构
   */
  organizationName?: string[]
  /**
   * 融资编号
   */
  financingCode?: string
  /**
   * 融资Id
   */
  financingId?: number
  /**
   * 本期到期日 format: yyyy-MM-dd
   */
  expireDate?: string
  /**
   * 本期应还金额
   */
  shouldPayAmount?: string
  /**
   * 本期应还本金可能为空
   */
  shouldPayPrincipal?: string
  /**
   * 本期应还利息可能为空
   */
  shouldPayInterest?: string
  /**
   * 本金流出账户
   */
  principalOutflowAccount?: string
  /**
   * 本金流出账户性质
   */
  principalOutflowAccountProperty?: string
  /**
   * 利息流出账户
   */
  interestOutflowAccount?: string
  /**
   * 利息流出账户性质
   */
  interestOutflowAccountProperty?: string
}[]

/**
 * 接口 [错配明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/26389) 的 **请求类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/mismatch`
 * @更新时间 `2025-02-21 14:54:37`
 */
export interface ManageMismatchRequest {
  /**
   * 预测区间-开始
   */
  queryDateStart: string
  /**
   * 预测区间-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [错配明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/26389) 的 **返回类型**
 *
 * @分类 [流动性管理-流动性管理-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3650)
 * @请求头 `POST /liquidity/manage/mismatch`
 * @更新时间 `2025-02-21 14:54:37`
 */
export type ManageMismatchResponse = {
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
  organizationId?: number[]
  /**
   * 融资机构名称
   */
  organizationName?: string[]
  /**
   * 期项
   */
  phase?: number
  /**
   * 现金流出时间
   */
  cashOutflowTime?: string
  /**
   * 现金流出金额
   */
  cashOutflowAmount?: number
  /**
   * 流入明细
   */
  cashInFlowList?: {
    /**
     * 质押/监管合同编号
     */
    pledgeContractCode?: string
    /**
     * 现金流入时间
     */
    cashInflowTime?: string
    /**
     * 现金流入金额
     */
    cashInflowAmount?: number
  }[]
  /**
   * 现金流入时间
   */
  cashInflowTime?: string
  /**
   * 现金流入金额
   */
  cashInflowAmount?: number
}[]

/* prettier-ignore-end */
