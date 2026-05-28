/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改帐龄-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21619) 的 **请求类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/modify`
 * @更新时间 `2024-09-11 18:29:03`
 */
export interface ItemModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 帐龄id
   */
  accountAgeId?: number
  /**
   * 核算组织编码 默认 10000396
   */
  accountancyOrganizationNumber?: string
  /**
   * 核算组织名称 默认 浙江浙商融资租赁有限公司
   */
  accountancyOrganizationName?: string
  /**
   * 状态
   */
  status?: string
  /**
   * 期初款项原值
   */
  originalValueInitial?: number
  /**
   * 本期增加额 >0增加 <0减少
   */
  originalValueIncrease?: number
  /**
   * 期末款项原值
   */
  originalValueFinal?: number
  /**
   * 币别
   */
  currency?: string
  /**
   * 科目名称编号
   */
  accountNumber?: string
  /**
   * 款项内容
   */
  paymentContent?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户单位名称， 取合同对应承租人的“客户名称”字段
   */
  customerUnitName?: string
  /**
   * 业务日期
   */
  businessDate?: string
  /**
   * 账龄截止日
   */
  agingDeadline?: string
  /**
   * 业务账龄（月）向下取整
   */
  businessAge?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 收款id
   */
  collectionId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 租金的应收日期 合同逾期日期
   */
  planCollectionDate?: string
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [修改帐龄-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21619) 的 **返回类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/modify`
 * @更新时间 `2024-09-11 18:29:03`
 */
export interface ItemModifyResponse {
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
 * 接口 [删除帐龄-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21631) 的 **请求类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/remove`
 * @更新时间 `2024-09-11 18:29:11`
 */
export interface ItemRemoveRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [删除帐龄-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21631) 的 **返回类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/remove`
 * @更新时间 `2024-09-11 18:29:11`
 */
export interface ItemRemoveResponse {
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
 * 接口 [帐龄-详情表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21625) 的 **请求类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/list`
 * @更新时间 `2024-09-11 18:58:50`
 */
export interface ItemListRequest {
  /**
   * 帐龄id
   */
  accountAgeId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
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
 * 接口 [帐龄-详情表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21625) 的 **返回类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/list`
 * @更新时间 `2024-09-11 18:58:50`
 */
export interface ItemListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 帐龄id
     */
    accountAgeId?: number
    /**
     * 核算组织编码 默认 10000396
     */
    accountancyOrganizationNumber?: string
    /**
     * 核算组织名称 默认 浙江浙商融资租赁有限公司
     */
    accountancyOrganizationName?: string
    /**
     * 状态
     */
    status?: string
    /**
     * 期初款项原值
     */
    originalValueInitial?: number
    /**
     * 本期增加额
     */
    originalValueIncrease?: number
    /**
     * 本期减少额
     */
    originalValueReduce?: number
    /**
     * 期末款项原值
     */
    originalValueFinal?: number
    /**
     * 币别
     */
    currency?: string
    /**
     * 科目名称编号
     */
    accountNumber?: string
    /**
     * 款项内容
     */
    paymentContent?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户单位名称， 取合同对应承租人的“客户名称”字段
     */
    customerUnitName?: string
    /**
     * 业务日期
     */
    businessDate?: string
    /**
     * 账龄截止日
     */
    agingDeadline?: string
    /**
     * 业务账龄（月）向下取整
     */
    businessAge?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 收款id
     */
    collectionId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 租金的应收日期 合同逾期日期
     */
    planCollectionDate?: string
    /**
     * 逻辑删除，0-未删除，1-已删除
     */
    deleted?: number
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

/**
 * 接口 [推送至苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/21643) 的 **请求类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/send`
 * @更新时间 `2024-09-11 18:29:23`
 */
export interface ItemSendRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [推送至苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/21643) 的 **返回类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/send`
 * @更新时间 `2024-09-11 18:29:23`
 */
export interface ItemSendResponse {
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
 * 接口 [新增帐龄-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21613) 的 **请求类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/add`
 * @更新时间 `2024-09-11 18:51:30`
 */
export interface ItemAddRequest {
  /**
   * 帐龄id
   */
  accountAgeId?: number
  /**
   * 核算组织编码 默认 10000396
   */
  accountancyOrganizationNumber?: string
  /**
   * 核算组织名称 默认 浙江浙商融资租赁有限公司
   */
  accountancyOrganizationName?: string
  /**
   * 期初款项原值
   */
  originalValueInitial?: number
  /**
   * 本期增加额 >0增加 <0减少
   */
  originalValueIncrease?: number
  /**
   * 本期减少额
   */
  originalValueReduce?: number
  /**
   * 期末款项原值
   */
  originalValueFinal?: number
  /**
   * 币别
   */
  currency?: string
  /**
   * 科目名称编号
   */
  accountNumber?: string
  /**
   * 款项内容
   */
  paymentContent?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户单位名称， 取合同对应承租人的“客户名称”字段
   */
  customerUnitName?: string
  /**
   * 业务日期
   */
  businessDate?: string
  /**
   * 账龄截止日
   */
  agingDeadline?: string
  /**
   * 业务账龄（月）向下取整
   */
  businessAge?: number
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 收款id
   */
  collectionId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 租金的应收日期 合同逾期日期
   */
  planCollectionDate?: string
}

/**
 * 接口 [新增帐龄-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21613) 的 **返回类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/add`
 * @更新时间 `2024-09-11 18:51:30`
 */
export interface ItemAddResponse {
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
 * 接口 [重新生成-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21637) 的 **请求类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/regeneration`
 * @更新时间 `2024-09-11 18:29:15`
 */
export interface ItemRegenerationRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [重新生成-详情表↗](http://yapi.zswltec.com:3000/project/11/interface/api/21637) 的 **返回类型**
 *
 * @分类 [帐龄-详情表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3134)
 * @请求头 `POST /finance/account/age/item/regeneration`
 * @更新时间 `2024-09-11 18:29:15`
 */
export interface ItemRegenerationResponse {
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
