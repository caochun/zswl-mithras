/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [提交应收逾期集成单↗](http://yapi.zswltec.com:3000/project/11/interface/api/37441) 的 **请求类型**
 *
 * @分类 [应收逾期版本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5324)
 * @请求头 `POST /finance/overdue/submit`
 * @更新时间 `2025-09-19 14:25:27`
 */
export interface OverdueSubmitRequest {
  /**
   * 集成记录id
   */
  integrationRecordIds?: number[]
  /**
   * 结算记录id
   */
  settlementRecordIds?: number[]
  /**
   * 收款编号
   */
  reportId: string
}

/**
 * 接口 [提交应收逾期集成单↗](http://yapi.zswltec.com:3000/project/11/interface/api/37441) 的 **返回类型**
 *
 * @分类 [应收逾期版本表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5324)
 * @请求头 `POST /finance/overdue/submit`
 * @更新时间 `2025-09-19 14:25:27`
 */
export interface OverdueSubmitResponse {
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

/* prettier-ignore-end */
