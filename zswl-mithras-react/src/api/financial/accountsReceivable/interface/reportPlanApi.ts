/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [关闭逾期报送计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37399) 的 **请求类型**
 *
 * @分类 [逾期报送计划表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5312)
 * @请求头 `POST /finance/overdue/report/base/close`
 * @更新时间 `2025-09-19 09:56:09`
 */
export interface BaseCloseRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [关闭逾期报送计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37399) 的 **返回类型**
 *
 * @分类 [逾期报送计划表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5312)
 * @请求头 `POST /finance/overdue/report/base/close`
 * @更新时间 `2025-09-19 09:56:09`
 */
export interface BaseCloseResponse {
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
 * 接口 [新增逾期报送计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37405) 的 **请求类型**
 *
 * @分类 [逾期报送计划表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5312)
 * @请求头 `POST /finance/overdue/report/base/add`
 * @更新时间 `2025-09-19 09:56:09`
 */
export interface BaseAddRequest {
  /**
   * 计划月份
   */
  planDate: string
}

/**
 * 接口 [新增逾期报送计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37405) 的 **返回类型**
 *
 * @分类 [逾期报送计划表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5312)
 * @请求头 `POST /finance/overdue/report/base/add`
 * @更新时间 `2025-09-19 09:56:09`
 */
export type BaseAddResponse = number

/**
 * 接口 [逾期报送计划表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37411) 的 **请求类型**
 *
 * @分类 [逾期报送计划表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5312)
 * @请求头 `POST /finance/overdue/report/base/list`
 * @更新时间 `2025-09-19 16:20:27`
 */
export interface BaseListRequest {
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
 * 接口 [逾期报送计划表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37411) 的 **返回类型**
 *
 * @分类 [逾期报送计划表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5312)
 * @请求头 `POST /finance/overdue/report/base/list`
 * @更新时间 `2025-09-19 16:20:27`
 */
export interface BaseListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 计划月份
     */
    planDate?: string
    /**
     * 报送状态 OverduePlanStatueEnum
     */
    reportStatus?: string
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
