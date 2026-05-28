/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改票据管理表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12934) 的 **请求类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/modify`
 * @更新时间 `2023-06-05 10:04:38`
 */
export interface ManagementModifyRequest {
  /**
   * 票据id
   */
  id?: number
  /**
   * 票据code
   */
  billCode?: string
  /**
   * 票据金额
   */
  billAmount?: number
  /**
   * 票据到期日期
   */
  billExpireDate?: string
}

/**
 * 接口 [修改票据管理表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12934) 的 **返回类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/modify`
 * @更新时间 `2023-06-05 10:04:38`
 */
export type ManagementModifyResponse = null

/**
 * 接口 [删除票据管理表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12944) 的 **请求类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/remove`
 * @更新时间 `2023-06-05 10:04:39`
 */
export interface ManagementRemoveRequest {
  id: number
}

/**
 * 接口 [删除票据管理表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12944) 的 **返回类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/remove`
 * @更新时间 `2023-06-05 10:04:39`
 */
export type ManagementRemoveResponse = null

/**
 * 接口 [新增票据管理表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12929) 的 **请求类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/add`
 * @更新时间 `2023-06-05 10:07:03`
 */
export interface ManagementAddRequest {
  /**
   * 管理收付款主表id
   */
  mainId: number
  /**
   * 票据类型 收款/付款
   */
  billType: string
  /**
   * 票据code
   */
  billCode: string
  /**
   * 票据金额
   */
  billAmount: number
  /**
   * 票据到期日期
   */
  billExpireDate: string
}

/**
 * 接口 [新增票据管理表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12929) 的 **返回类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/add`
 * @更新时间 `2023-06-05 10:07:03`
 */
export type ManagementAddResponse = null

/**
 * 接口 [票据管理表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12939) 的 **请求类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/list`
 * @更新时间 `2023-06-05 10:04:39`
 */
export interface ManagementListRequest {
  /**
   * 票据类型 收款/付款
   */
  billType: string
  mainId: number
  page?: number
  pageSize?: number
  version?: string
}

/**
 * 接口 [票据管理表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12939) 的 **返回类型**
 *
 * @分类 [billManagement↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2498)
 * @请求头 `POST /bill/management/list`
 * @更新时间 `2023-06-05 10:04:39`
 */
export interface ManagementListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 票据id
     */
    id?: number
    /**
     * 管理收付款主表id
     */
    mainId?: number
    /**
     * 票据类型 收款/付款
     */
    billType?: string
    /**
     * 票据code
     */
    billCode?: string
    /**
     * 票据金额
     */
    billAmount?: number
    /**
     * 票据到期日期
     */
    billExpireDate?: string
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
