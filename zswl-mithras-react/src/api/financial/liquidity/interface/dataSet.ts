/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [现金流数据设置↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12484) 的 **请求类型**
 *
 * @分类 [流动性风险-数据设置-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2363)
 * @请求头 `POST /capital/flow/setting/edit`
 * @更新时间 `2023-05-15 18:52:19`
 */
export interface SettingEditRequest {
  /**
   * 期初现金流余额
   */
  beginCashflowAmount?: number
  /**
   * 其他收入
   */
  otherIncome?: number
  /**
   * 其他支出
   */
  otherExpenses?: number
  /**
   * 融资明细
   */
  inDetail?: {
    /**
     * 金额
     */
    amount?: number
    /**
     * 日期
     */
    date?: string
    /**
     * 备注
     */
    remark?: string
  }[]
  /**
   * 项目投放明细
   */
  outDetail?: {
    /**
     * 金额
     */
    amount?: number
    /**
     * 日期
     */
    date?: string
    /**
     * 备注
     */
    remark?: string
  }[]
}

/**
 * 接口 [现金流数据设置↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12484) 的 **返回类型**
 *
 * @分类 [流动性风险-数据设置-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2363)
 * @请求头 `POST /capital/flow/setting/edit`
 * @更新时间 `2023-05-15 18:52:19`
 */
export interface SettingEditResponse {
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
 * 接口 [现金流数据设置详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12489) 的 **请求类型**
 *
 * @分类 [流动性风险-数据设置-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2363)
 * @请求头 `POST /capital/flow/setting/detail`
 * @更新时间 `2023-05-16 15:27:43`
 */
export interface SettingDetailRequest {
  /**
   * 时间范围-从
   */
  timeFrom?: string
  /**
   * 时间范围-到
   */
  timeTo?: string
  /**
   * 预估逾期率
   */
  overdueRate?: number
}

/**
 * 接口 [现金流数据设置详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12489) 的 **返回类型**
 *
 * @分类 [流动性风险-数据设置-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2363)
 * @请求头 `POST /capital/flow/setting/detail`
 * @更新时间 `2023-05-16 15:27:43`
 */
export interface SettingDetailResponse {
  /**
   * 期初现金流余额
   */
  beginCashflowAmount?: number
  /**
   * 其他收入
   */
  otherIncome?: number
  /**
   * 其他支出
   */
  otherExpenses?: number
  /**
   * 近【】天租金/利息回笼
   */
  rentInterestReturn?: number
  /**
   * 保证金/手续费等收入
   */
  earnestMoneyRevenue?: number
  /**
   * 近【】天归还融资本金
   */
  returnFinancingPrincipal?: number
  /**
   * 归还融资利息
   */
  returnFinancingInterest?: number
  /**
   * 项目保证金
   */
  projEarnestMoney?: number
  /**
   * 时间区间-从
   */
  timeFrom?: string
  /**
   * 时间区间-到
   */
  timeTo?: string
  /**
   * 总计资金流入量
   */
  sumAmountIn?: number
  /**
   * 总计资金流出量
   */
  sumAmountOut?: number
  /**
   * 总资金盈缺
   */
  totalFundingSurplus?: number
  /**
   * 期间现金余额
   */
  periodCashBalance?: number
  /**
   * 融资明细
   */
  inDetail?: {
    /**
     * 金额
     */
    amount?: number
    /**
     * 日期
     */
    date?: string
    /**
     * 备注
     */
    remark?: string
  }[]
  /**
   * 项目投放明细
   */
  outDetail?: {
    /**
     * 金额
     */
    amount?: number
    /**
     * 日期
     */
    date?: string
    /**
     * 备注
     */
    remark?: string
  }[]
}

/* prettier-ignore-end */
