/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [机构\/产品列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3877) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/org/list`
 * @更新时间 `2024-06-12 19:59:35`
 */
export interface OrgListRequest {
  name?: string
}

/**
 * 接口 [机构\/产品列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3877) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/org/list`
 * @更新时间 `2024-06-12 19:59:35`
 */
export type OrgListResponse = {
  id?: number
  name?: string
  isDirect?: number
}[]

/**
 * 接口 [根据机构ID查询融资编号↗](http://yapi.zswltec.com:3000/project/11/interface/api/3241) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/code/list`
 * @更新时间 `2024-05-19 18:09:24`
 */
export interface CodeListRequest {
  /**
   * 融资ID
   */
  financingId?: number
  /**
   * 现金流项目
   */
  cashFlowItem?: string
}

/**
 * 接口 [根据机构ID查询融资编号↗](http://yapi.zswltec.com:3000/project/11/interface/api/3241) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/code/list`
 * @更新时间 `2024-05-19 18:09:24`
 */
export type CodeListResponse = {
  /**
   * 融资ID
   */
  financingId?: number
  /**
   * 融资编号
   */
  financingCode?: string
}[]

/**
 * 接口 [根据融资ID和期项和现金流项目查询金额↗](http://yapi.zswltec.com:3000/project/11/interface/api/3253) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/amount/detail`
 * @更新时间 `2024-05-19 18:09:24`
 */
export interface AmountDetailRequest {
  /**
   * 核销类型
   */
  writeOffType?: string
  /**
   * 现金流ID
   */
  cashFlowId: number
  /**
   * 期项
   */
  phase: number
  /**
   * 现金流类型
   */
  cashFlowType: string
}

/**
 * 接口 [根据融资ID和期项和现金流项目查询金额↗](http://yapi.zswltec.com:3000/project/11/interface/api/3253) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/amount/detail`
 * @更新时间 `2024-05-19 18:09:24`
 */
export interface AmountDetailResponse {
  /**
   * 应付金额
   */
  shouldPayAmount?: number
  /**
   * 应付时间
   */
  shouldPayTime?: string
  /**
   * 未付金额
   */
  noPayAmount?: number
  /**
   * 已核销金额
   */
  writeOffedAmount?: string[]
}

/**
 * 接口 [根据融资id和现金流项目查期项↗](http://yapi.zswltec.com:3000/project/11/interface/api/3247) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/phase/list`
 * @更新时间 `2024-05-19 18:09:24`
 */
export interface PhaseListRequest {
  /**
   * 借据ID
   */
  receiptId: number
  /**
   * 现金流项目
   */
  cashFlowItem?: string
}

/**
 * 接口 [根据融资id和现金流项目查期项↗](http://yapi.zswltec.com:3000/project/11/interface/api/3247) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/phase/list`
 * @更新时间 `2024-05-19 18:09:24`
 */
export type PhaseListResponse = {
  /**
   * 现金流ID
   */
  cashFlowId?: number
  /**
   * 期项
   */
  phase?: string
}[]

/**
 * 接口 [现金流项目信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3865) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/cashflow/list`
 * @更新时间 `2024-06-13 15:09:01`
 */
export interface CashflowListRequest {
  /**
   * 收付款id
   */
  receiptRepayBaseId: number
  /**
   * 现金流类型
   */
  cashFlowItem: string
}

/**
 * 接口 [现金流项目信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3865) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/cashflow/list`
 * @更新时间 `2024-06-13 15:09:01`
 */
export type CashflowListResponse = {
  /**
   * 收付款id
   */
  receiptRepayBaseId?: number
  /**
   * 现金流编号
   */
  cashFlowCode?: string
  /**
   * 现金流类型
   */
  cashFlowItem?: string
  /**
   * 计划付款/收款金额
   */
  shouldPayAmount?: number
  /**
   * 实际付款/收款金额
   */
  noPayAmount?: number
  /**
   * 计划付款/收款日期
   */
  shouldPayTime?: string
}[]

/**
 * 接口 [获取子列表信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3895) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/sub/list`
 * @更新时间 `2024-06-13 15:09:01`
 */
export interface SubListRequest {
  /**
   * 银行流水id
   */
  bankFlowIds: number[]
}

/**
 * 接口 [获取子列表信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3895) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/sub/list`
 * @更新时间 `2024-06-13 15:09:01`
 */
export type SubListResponse = {
  /**
   * 资金收付款id
   */
  receiptRepayBaseId?: number
  /**
   * 资金收付款code
   */
  receiptRepayBaseCode?: string
  /**
   * 现金流编号
   */
  cashFlowCode?: string
  /**
   * 机构ID
   */
  orgId?: number
  /**
   * 机构名称
   */
  orgName?: string
  /**
   * 现金流项目
   */
  cashFlowItem?: string
  /**
   * 现金流项目名称
   */
  cashFlowItemName?: string
  /**
   * 应收时间/应付时间
   */
  shouldPayTime?: string
  /**
   * 应收金额/应付金额
   */
  shouldPayAmount?: number
  /**
   * 未收金额/未付金额
   */
  noPayAmount?: number
  /**
   * 本次核销金额
   */
  thisWriteOffAmount?: number
  /**
   * 状态
   */
  status?: string
  /**
   * 是否直融，1-是，0-否
   */
  isDirect?: number
}[]

/**
 * 接口 [融资列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3871) 的 **请求类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/info/list`
 * @更新时间 `2024-06-12 19:59:35`
 */
export interface InfoListRequest {
  /**
   * 机构/产品的id
   */
  id: number
  /**
   * 是否直融，0-否，1-是
   */
  isDirect: number
}

/**
 * 接口 [融资列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3871) 的 **返回类型**
 *
 * @分类 [银行流水中心-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_800)
 * @请求头 `POST /bank/center/finance/info/list`
 * @更新时间 `2024-06-12 19:59:35`
 */
export type InfoListResponse = {
  /**
   * 收付款id
   */
  receiptRepayBaseId?: number
  /**
   * 融资编号
   */
  financingCode?: string
  /**
   * 融资金额
   */
  financingAmount?: number
  /**
   * 实际贷款日期
   */
  actualLoanDate?: string
}[]

/* prettier-ignore-end */
