/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [票据逾期名单导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/26815) 的 **请求类型**
 *
 * @分类 [OverdueListController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3722)
 * @请求头 `GET /overdueList/import`
 * @更新时间 `2024-12-24 15:59:21`
 */
export interface OverdueListImportRequest {
  /**
   * 业务日期 yyyy-MM-dd
   */
  busiDate: string
}

/**
 * 接口 [票据逾期名单导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/26815) 的 **返回类型**
 *
 * @分类 [OverdueListController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3722)
 * @请求头 `GET /overdueList/import`
 * @更新时间 `2024-12-24 15:59:21`
 */
export interface OverdueListImportResponse {}

/**
 * 接口 [票据逾期名单查询↗](http://yapi.zswltec.com:3000/project/11/interface/api/26821) 的 **请求类型**
 *
 * @分类 [OverdueListController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3722)
 * @请求头 `POST /overdueList/list`
 * @更新时间 `2024-12-24 15:59:22`
 */
export interface OverdueListListRequest {
  sourceScene?: string
  version?: string
  page?: number
  pageSize?: number
  /**
   * 名单截止日期
   */
  busiDate?: string
}

/**
 * 接口 [票据逾期名单查询↗](http://yapi.zswltec.com:3000/project/11/interface/api/26821) 的 **返回类型**
 *
 * @分类 [OverdueListController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3722)
 * @请求头 `POST /overdueList/list`
 * @更新时间 `2024-12-24 15:59:22`
 */
export interface OverdueListListResponse {
  /**
   * 查询集合
   */
  list?: {
    id?: number
    /**
     * 机构名称
     */
    orgName?: string
    /**
     * 机构编号
     */
    orgCode?: string
    /**
     * 持续逾期开始时间
     */
    overdueStartDate?: string
    /**
     * 截止时间
     */
    busiDate?: string
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
    key?: {}
  }
}

/* prettier-ignore-end */
