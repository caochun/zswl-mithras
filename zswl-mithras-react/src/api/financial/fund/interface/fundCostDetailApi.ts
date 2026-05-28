/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改费用↗](http://yapi.zswltec.com:3000/project/11/interface/api/22345) 的 **请求类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/modify`
 * @更新时间 `2024-10-11 14:43:58`
 */
export interface FeeModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 机构id
   */
  organizationId: number
  /**
   * 机构名称
   */
  organizationName: string
  /**
   * 费用类型
   */
  expenseType?: string
  /**
   * 金额（万元）
   */
  amount?: number
  /**
   * 支付方式
   */
  paymentMethod?: string
  /**
   * 支付时间
   */
  payDate?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改费用↗](http://yapi.zswltec.com:3000/project/11/interface/api/22345) 的 **返回类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/modify`
 * @更新时间 `2024-10-11 14:43:58`
 */
export interface FeeModifyResponse {
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
 * 接口 [删除费用项↗](http://yapi.zswltec.com:3000/project/11/interface/api/22333) 的 **请求类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/remove`
 * @更新时间 `2024-09-27 15:09:45`
 */
export interface FeeRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除费用项↗](http://yapi.zswltec.com:3000/project/11/interface/api/22333) 的 **返回类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/remove`
 * @更新时间 `2024-09-27 15:09:45`
 */
export interface FeeRemoveResponse {
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
 * 接口 [新增费用项↗](http://yapi.zswltec.com:3000/project/11/interface/api/22327) 的 **请求类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/add`
 * @更新时间 `2024-10-11 14:43:58`
 */
export interface FeeAddRequest {
  /**
   * 融资id
   */
  financingId: number
  /**
   * 机构id
   */
  organizationId: number
  /**
   * 机构名称
   */
  organizationName: string
  /**
   * 费用类型
   */
  expenseType?: string
  /**
   * 金额（万元）
   */
  amount?: number
  /**
   * 支付方式
   */
  paymentMethod?: string
  /**
   * 支付时间
   */
  payDate?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [新增费用项↗](http://yapi.zswltec.com:3000/project/11/interface/api/22327) 的 **返回类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/add`
 * @更新时间 `2024-10-11 14:43:58`
 */
export interface FeeAddResponse {
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
 * 接口 [费用项列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22339) 的 **请求类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/list`
 * @更新时间 `2024-10-11 14:43:58`
 */
export interface FeeListRequest {
  /**
   * 融资id
   */
  financingId: number
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
 * 接口 [费用项列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22339) 的 **返回类型**
 *
 * @分类 [间融-费用明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3254)
 * @请求头 `POST /fund/financing/fee/list`
 * @更新时间 `2024-10-11 14:43:58`
 */
export interface FeeListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 机构id
     */
    organizationId?: number
    /**
     * 机构名称
     */
    organizationName?: string
    /**
     * 费用类型
     */
    expenseType?: string
    /**
     * 金额（万元）
     */
    amount?: number
    /**
     * 支付方式
     */
    paymentMethod?: string
    /**
     * 备注
     */
    remark?: string
    /**
     * 支付时间
     */
    payDate?: string
    /**
     * 核销状态
     */
    writeOffStatus?: string
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
