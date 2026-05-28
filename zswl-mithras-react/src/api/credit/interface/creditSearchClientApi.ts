/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [征信报告查询列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37909) 的 **请求类型**
 *
 * @分类 [CreditSearchClientController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5396)
 * @请求头 `POST /creditreport/client/list`
 * @更新时间 `2025-10-27 08:51:25`
 */
export interface ClientListRequest {
  sourceScene?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
  /**
   * 客户id
   */
  clientId?: number
}

/**
 * 接口 [征信报告查询列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37909) 的 **返回类型**
 *
 * @分类 [CreditSearchClientController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5396)
 * @请求头 `POST /creditreport/client/list`
 * @更新时间 `2025-10-27 08:51:25`
 */
export interface ClientListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 查询编号
     */
    creditCode?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 统一社会信用代码
     */
    cscCode?: string
    /**
     * 中征码
     */
    zhongZhengCode?: string
    /**
     * 申请人id
     */
    applyUser?: number
    /**
     * 申请部门id
     */
    applyOrg?: number
    /**
     * 申请人
     */
    applyUserName?: string
    /**
     * 申请部门
     */
    applyOrgName?: string
    /**
     * 申请状态
     */
    applyStatus?: string
    /**
     * 申请通过时间
     */
    applyTime?: string
    /**
     * 查询状态
     */
    selectStatus?: string
    /**
     * 查询完成时间
     */
    selectTime?: string
    /**
     * 项目编号
     */
    projCode?: number
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 查询版本
     */
    selectVersion?: string
    /**
     * 查询目的
     */
    selectGoal?: string
    /**
     * 信用报告封装格式
     */
    reportFormat?: string
    /**
     * 统一社会信用代码-集
     */
    cscCodeList?: string[]
    /**
     * 中征码-集
     */
    zhongZhengCodeList?: string[]
    /**
     * 查询目的-集
     */
    selectGoalList?: string[]
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

/**
 * 接口 [征信查询删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37915) 的 **请求类型**
 *
 * @分类 [CreditSearchClientController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5396)
 * @请求头 `GET /creditreport/client/delete`
 * @更新时间 `2025-10-27 08:51:26`
 */
export interface ClientDeleteRequest {
  id?: string
}

/**
 * 接口 [征信查询删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37915) 的 **返回类型**
 *
 * @分类 [CreditSearchClientController↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5396)
 * @请求头 `GET /creditreport/client/delete`
 * @更新时间 `2025-10-27 08:51:26`
 */
export type ClientDeleteResponse = null

/* prettier-ignore-end */
