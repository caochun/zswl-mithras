/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [业务流水-资金端-保存核销明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/3577) 的 **请求类型**
 *
 * @分类 [业务流水接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_908)
 * @请求头 `POST /business/flow/finance/detail/save`
 * @更新时间 `2024-06-03 19:20:30`
 */
export interface DetailSaveRequest {
  /**
   * 资金收付款id
   */
  receiptRepayId: number
  /**
   * 现金流类型
   */
  cashFlowItem: string
  /**
   * 现金流编号
   */
  cashFlowCode: string
  /**
   * 核销日期
   */
  cashFlowDate: string
  /**
   * 核销金额
   */
  totalAmount: number
  /**
   * 本金金额
   */
  principalAmount?: number
  /**
   * 利息金额
   */
  interestAmount?: number
  /**
   * 结算方式
   */
  settleMethod: string
}

/**
 * 接口 [业务流水-资金端-保存核销明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/3577) 的 **返回类型**
 *
 * @分类 [业务流水接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_908)
 * @请求头 `POST /business/flow/finance/detail/save`
 * @更新时间 `2024-06-03 19:20:30`
 */
export interface DetailSaveResponse {
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
 * 接口 [业务流水-资金端-核销明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3583) 的 **请求类型**
 *
 * @分类 [业务流水接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_908)
 * @请求头 `POST /business/flow/finance/detail/list`
 * @更新时间 `2024-06-03 19:20:30`
 */
export interface DetailListRequest {
  /**
   * 现金流编号
   */
  cashFlowCode: string
}

/**
 * 接口 [业务流水-资金端-核销明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3583) 的 **返回类型**
 *
 * @分类 [业务流水接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_908)
 * @请求头 `POST /business/flow/finance/detail/list`
 * @更新时间 `2024-06-03 19:20:30`
 */
export type DetailListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 总金额
   */
  totalAmount?: number
  /**
   * 本金金额
   */
  principalAmount?: number
  /**
   * 利息金额
   */
  interestAmount?: number
  /**
   * 核销日期
   */
  cashFlowDate?: string
  /**
   * 银行流水号
   */
  bankFlowNo?: string
}[]

/**
 * 接口 [业务流水-资金端列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3571) 的 **请求类型**
 *
 * @分类 [业务流水接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_908)
 * @请求头 `POST /business/flow/finance/list`
 * @更新时间 `2024-06-03 19:20:30`
 */
export interface FinanceListRequest {
  /**
   * 核销状态
   */
  writeOffStatus?: string[]
  /**
   * 日期开始
   */
  dateFrom?: string
  /**
   * 日期结束
   */
  dateTo?: string
  /**
   * 融资渠道
   */
  financingRoute?: string
  /**
   * 业务流水类型，付款-PAY，收款-COLLECT
   */
  flowType: string
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
 * 接口 [业务流水-资金端列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3571) 的 **返回类型**
 *
 * @分类 [业务流水接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_908)
 * @请求头 `POST /business/flow/finance/list`
 * @更新时间 `2024-06-03 19:20:30`
 */
export interface FinanceListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * idKey
     */
    idKey?: string
    /**
     * 收付款id
     */
    receiptRepayId?: number
    /**
     * 核销状态
     */
    writeOffStatus?: string
    /**
     * 现金流项目
     */
    cashFlowItem?: string
    /**
     * 现金流项目展示文案
     */
    cashFlowItemDisplay?: string
    /**
     * 金额
     */
    amount?: number
    /**
     * 日期
     */
    date?: string
    /**
     * 流水ID
     */
    serialNo?: string
    /**
     * 融资渠道
     */
    financingRoute?: string
    /**
     * 融资金额
     */
    financingAmount?: number
    /**
     * 业务类型
     */
    businessType?: string
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

/* prettier-ignore-end */
