/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [irr计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13009) 的 **请求类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/irr`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface GenerationIrrRequest {
  /**
   * 还款频率
   */
  repayRate: string
  /**
   * 现金流计划
   */
  itemList: {
    /**
     * 日期
     */
    cashFlowDate: string
    /**
     * 期项
     */
    cashFlowPhase: number
    /**
     * 租金
     */
    rent?: number
    /**
     * 本金
     */
    principal?: number
    /**
     * 利息
     */
    interest?: number
    /**
     * 剩余本金
     */
    remainingPrincipal: number
    /**
     * 现金流金额
     */
    cashFlowAmount: number
  }[]
}

/**
 * 接口 [irr计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13009) 的 **返回类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/irr`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface GenerationIrrResponse {
  /**
   * irr值；直接返回字符串显示
   */
  irr?: string
  /**
   * 预览文件名称
   */
  ossFilename?: string
}

/**
 * 接口 [irr详情预览文件地址获取↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13014) 的 **请求类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/irr/file/url`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface FileUrlRequest {
  /**
   * 文件路径
   */
  ossFilename: string
}

/**
 * 接口 [irr详情预览文件地址获取↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13014) 的 **返回类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/irr/file/url`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface FileUrlResponse {
  /**
   * 预览地址
   */
  url?: string
}

/**
 * 接口 [导入现金流计划表\/概算租金表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13004) 的 **请求类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/import`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface GenerationImportRequest {
  file: File
}

/**
 * 接口 [导入现金流计划表\/概算租金表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13004) 的 **返回类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/import`
 * @更新时间 `2023-06-10 15:49:30`
 */
export type GenerationImportResponse = {
  /**
   * 日期
   */
  cashFlowDate?: string
  /**
   * 期项
   */
  cashFlowPhase?: number
  /**
   * 租金
   */
  rent?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 剩余本金
   */
  remainingPrincipal?: number
  /**
   * 现金流金额
   */
  cashFlowAmount?: number
}[]

/**
 * 接口 [导出现金流计划表\/概算租金表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12994) 的 **请求类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/export`
 * @更新时间 `2023-06-10 15:49:30`
 */
export type GenerationExportRequest = {
  /**
   * 日期
   */
  cashFlowDate: string
  /**
   * 期项
   */
  cashFlowPhase: number
  /**
   * 租金
   */
  rent?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 剩余本金
   */
  remainingPrincipal: number
  /**
   * 现金流金额
   */
  cashFlowAmount: number
}[]

/**
 * 接口 [导出现金流计划表\/概算租金表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12994) 的 **返回类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/export`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface GenerationExportResponse {
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
 * 接口 [生成现金流计划表\/租金概算表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12999) 的 **请求类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/execute`
 * @更新时间 `2023-06-10 15:49:30`
 */
export interface GenerationExecuteRequest {
  /**
   * 授信金额
   */
  creditAmount: number
  /**
   * 保证金金额
   */
  earnestMoney: number
  /**
   * 首期租金
   */
  downPayment: number
  /**
   * 服务费/咨询费/手续费
   */
  consultingFee: number
  /**
   * 名义货价
   */
  nominalPrice: number
  /**
   * 起租日期
   */
  startDate: string
  /**
   * 利率
   */
  interestRate: number
  /**
   * 还款频率枚举
   */
  repayRate: string
  /**
   * 还款期数
   */
  repayTimes: number
  /**
   * 租金计算方式
   */
  rentalCalcType: string
  /**
   * 支付方式
   */
  payType: string
}

/**
 * 接口 [生成现金流计划表\/租金概算表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12999) 的 **返回类型**
 *
 * @分类 [现金流计划生成-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2518)
 * @请求头 `POST /utils/cashFlow/generation/execute`
 * @更新时间 `2023-06-10 15:49:30`
 */
export type GenerationExecuteResponse = {
  /**
   * 日期
   */
  cashFlowDate?: string
  /**
   * 期项
   */
  cashFlowPhase?: number
  /**
   * 租金
   */
  rent?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 剩余本金
   */
  remainingPrincipal?: number
  /**
   * 现金流金额
   */
  cashFlowAmount?: number
}[]

/* prettier-ignore-end */
