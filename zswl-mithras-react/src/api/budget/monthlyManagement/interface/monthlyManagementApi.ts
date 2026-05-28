/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [Excel导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/3307) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/download`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface MonthlyDownloadRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
  /**
   * 模块类型 MonthlyModuleTypeEnum
   */
  moduleType: string
}

/**
 * 接口 [Excel导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/3307) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/download`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface MonthlyDownloadResponse {}

/**
 * 接口 [关账校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/8797) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/close/validate`
 * @更新时间 `2024-08-04 14:17:14`
 */
export interface CloseValidateRequest {
  mainId: number
}

/**
 * 接口 [关账校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/8797) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/close/validate`
 * @更新时间 `2024-08-04 14:17:14`
 */
export interface CloseValidateResponse {
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
 * 接口 [列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3325) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/list/page`
 * @更新时间 `2024-08-04 14:17:13`
 */
export interface ListPageRequest {
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
 * 接口 [列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3325) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/list/page`
 * @更新时间 `2024-08-04 14:17:13`
 */
export interface ListPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主ID
     */
    id?: number
    /**
     * 月份
     */
    yearAndMonth?: string
    /**
     * 实际利率法(含税)
     */
    airCount?: number
    /**
     * 实际利率法(不含税)
     */
    airCountExcludeTax?: number
    /**
     * 剩余本金法(含税)
     */
    rpCount?: number
    /**
     * 剩余本金法(不含税)
     */
    rpCountExcludeTax?: number
    /**
     * 当期计提成本(含税)
     */
    costCount?: number
    /**
     * 当期计提成本(不含税)
     */
    costCountExcludeTax?: number
    /**
     * 印花税
     */
    stampDutyCount?: number
    /**
     * 确认日期
     */
    confirmDate?: string
    /**
     * 状态 MonthlyManagementStatusEnum
     */
    status?: string
    /**
     * 关账日期
     */
    closeDate?: string
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
  others?: {}
}

/**
 * 接口 [刷新数据↗](http://yapi.zswltec.com:3000/project/11/interface/api/6547) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/fresh`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface MonthlyFreshRequest {
  /**
   * 主数据ID
   */
  mainId: number
}

/**
 * 接口 [刷新数据↗](http://yapi.zswltec.com:3000/project/11/interface/api/6547) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/fresh`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface MonthlyFreshResponse {
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
 * 接口 [印花税计提-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/3289) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/stampDuty/fin/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface FinPageRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
  isConfirmed?: number
  batchNumber?: string
  /**
   * 融资渠道
   */
  organizationName?: string
  /**
   * 融资编号
   */
  financingCode?: string
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
 * 接口 [印花税计提-资金端↗](http://yapi.zswltec.com:3000/project/11/interface/api/3289) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/stampDuty/fin/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface FinPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 融资id
     */
    financingId?: number
    /**
     * 融资类型
     */
    type?: string
    /**
     * 月份
     */
    yearAndMonth?: string
    /**
     * 融资渠道
     */
    organizationName?: string
    /**
     * 融资编号
     */
    financingCode?: string
    /**
     * 本月计提印花税/元
     */
    stampDuty?: number
    /**
     * 源数据ID
     */
    sourceId?: number
    /**
     * 关联主表main_id
     */
    mainId?: number
    /**
     * 是否推送，默认0未推送
     */
    isSendCq?: number
    /**
     * 收入是否已确认
     */
    isConfirmed?: number
    /**
     * 是否激活，默认1激活
     */
    isEffect?: number
    /**
     * 批次号
     */
    batchNumber?: string
    /**
     * tab类型 MonthlyModuleTypeEnum
     */
    tabType?: string
    /**
     * 是否已经更新 标志已确认之后存在刷新按钮
     */
    newUpdated?: number
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
  others?: {}
}

/**
 * 接口 [印花税计提-项目端↗](http://yapi.zswltec.com:3000/project/11/interface/api/3301) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/stampDuty/proj/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface ProjPageRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
  isConfirmed?: number
  batchNumber?: string
  /**
   * 客户ID
   */
  clientId?: number
  /**
   * 合同编号
   */
  contractCode?: string
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
 * 接口 [印花税计提-项目端↗](http://yapi.zswltec.com:3000/project/11/interface/api/3301) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/stampDuty/proj/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface ProjPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 月份
     */
    yearAndMonth?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 本月计提印花税
     */
    stampDuty?: number
    /**
     * 源数据ID
     */
    sourceId?: number
    /**
     * 关联主表main_id
     */
    mainId?: number
    /**
     * 是否推送，默认0未推送
     */
    isSendCq?: number
    /**
     * 收入是否已确认
     */
    isConfirmed?: number
    /**
     * 是否激活，默认1激活
     */
    isEffect?: number
    /**
     * 批次号
     */
    batchNumber?: string
    /**
     * tab类型 MonthlyModuleTypeEnum
     */
    tabType?: string
    /**
     * 是否已经更新 标志已确认之后存在刷新按钮
     */
    newUpdated?: number
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
  others?: {}
}

/**
 * 接口 [推送单条数据到苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/8875) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/push/single`
 * @更新时间 `2024-08-06 14:14:55`
 */
export interface PushSingleRequest {
  /**
   * 记录ID
   */
  id: number
  /**
   * 主数据ID
   */
  mainId: number
  /**
   * tabType
   */
  tabType: string
}

/**
 * 接口 [推送单条数据到苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/8875) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/push/single`
 * @更新时间 `2024-08-06 14:14:55`
 */
export interface PushSingleResponse {
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
 * 接口 [提交↗](http://yapi.zswltec.com:3000/project/11/interface/api/3313) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/submit`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface MonthlySubmitRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
}

/**
 * 接口 [提交↗](http://yapi.zswltec.com:3000/project/11/interface/api/3313) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/submit`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface MonthlySubmitResponse {
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
 * 接口 [收入确认-实际利率法列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3295) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/air/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface AirPageRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
  /**
   * 客户ID
   */
  clientId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 是否逾期 OverdueTypeEnum
   */
  overdueType?: string
  isConfirmed?: number
  batchNumber?: string
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
 * 接口 [收入确认-实际利率法列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3295) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/air/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface AirPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 借据id
     */
    receiptId?: number
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 客户ID
     */
    clientId?: number
    /**
     * 租赁类型
     */
    leaseType?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 税率
     */
    taxRate?: number
    /**
     * 本月收入金额（含税）
     */
    incomeSum?: number
    /**
     * 本月收入金额（不含税）
     */
    incomeWithoutTaxSum?: number
    /**
     * 当前是否逾期
     */
    overdueType?: string
    /**
     * 月份
     */
    yearAndMonth?: string
    /**
     * 实际起租日
     */
    actualLeaseDate?: string
    /**
     * 源数据ID
     */
    sourceId?: number
    /**
     * 关联主表main_id
     */
    mainId?: number
    /**
     * 是否推送，默认0未推送
     */
    isSendCq?: number
    /**
     * 收入是否已确认
     */
    isConfirmed?: number
    /**
     * 是否激活，默认1激活
     */
    isEffect?: number
    /**
     * 批次号
     */
    batchNumber?: string
    /**
     * tab类型 MonthlyModuleTypeEnum
     */
    tabType?: string
    /**
     * 是否已经更新 标志已确认之后存在刷新按钮
     */
    newUpdated?: number
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
  others?: {}
}

/**
 * 接口 [收入计提-剩余本金法列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3283) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/rp/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface RpPageRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
  isConfirmed?: number
  batchNumber?: string
  /**
   * 客户ID
   */
  clientId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 是否逾期 OverdueTypeEnum
   */
  overdueType?: string
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
 * 接口 [收入计提-剩余本金法列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3283) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/rp/page`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface RpPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 借据id
     */
    receiptId?: number
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 租赁类型
     */
    leaseType?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 税率
     */
    taxRate?: number
    /**
     * 本月收入金额（含税）
     */
    incomeSum?: number
    /**
     * 本月收入金额（不含税）
     */
    incomeWithoutTaxSum?: number
    /**
     * 当前是否逾期
     */
    overdueType?: string
    /**
     * 月份
     */
    yearAndMonth?: string
    /**
     * 实际起租日
     */
    actualLeaseDate?: string
    /**
     * 最近一期全额偿还租金期次
     */
    theLatestFullRefundRentPeriod?: number
    /**
     * 最近一期全额偿还租金应收日期
     */
    theLatestFullRefundRentDate?: string
    /**
     * 最近一期全额偿还租金应收剩余本金
     */
    theLatestFullRefundRentCapital?: number
    /**
     * 合同名义利率
     */
    contractNominalInterestRate?: number
    /**
     * 源数据ID
     */
    sourceId?: number
    /**
     * 关联主表main_id
     */
    mainId?: number
    /**
     * 是否推送，默认0未推送
     */
    isSendCq?: number
    /**
     * 收入是否已确认
     */
    isConfirmed?: number
    /**
     * 是否激活，默认1激活
     */
    isEffect?: number
    /**
     * 批次号
     */
    batchNumber?: string
    /**
     * tab类型 MonthlyModuleTypeEnum
     */
    tabType?: string
    /**
     * 是否已经更新 标志已确认之后存在刷新按钮
     */
    newUpdated?: number
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
  others?: {}
}

/**
 * 接口 [新增月结↗](http://yapi.zswltec.com:3000/project/11/interface/api/6541) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/base/add`
 * @更新时间 `2024-08-04 14:17:13`
 */
export interface BaseAddRequest {
  /**
   * yyyy-MM
   */
  yearAndMonth: string
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
 * 接口 [新增月结↗](http://yapi.zswltec.com:3000/project/11/interface/api/6541) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/base/add`
 * @更新时间 `2024-08-04 14:17:13`
 */
export type BaseAddResponse = number

/**
 * 接口 [更新单条数据↗](http://yapi.zswltec.com:3000/project/11/interface/api/8881) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/update/single`
 * @更新时间 `2024-08-06 14:14:55`
 */
export interface UpdateSingleRequest {
  /**
   * 主数据ID
   */
  mainId?: number
  /**
   * 批次号
   */
  singleRecordId?: number
  /**
   * TabType (AIR, RP, STAMP_DUTY_PROJ, STAMP_DUTY_FIN, COST)
   */
  tabType?: string
}

/**
 * 接口 [更新单条数据↗](http://yapi.zswltec.com:3000/project/11/interface/api/8881) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/update/single`
 * @更新时间 `2024-08-06 14:14:55`
 */
export interface UpdateSingleResponse {
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
 * 接口 [更新记录的生效状态↗](http://yapi.zswltec.com:3000/project/11/interface/api/6553) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/update/status`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface UpdateStatusRequest {
  /**
   * 主数据ID
   */
  mainId: number
  /**
   * 被修改记录ID
   */
  recordId: number
  /**
   * 状态
   */
  status: number
  /**
   * TAB类型枚举 MonthlyModuleTypeEnum
   */
  tabType: string
}

/**
 * 接口 [更新记录的生效状态↗](http://yapi.zswltec.com:3000/project/11/interface/api/6553) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/update/status`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface UpdateStatusResponse {
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
 * 接口 [月结关账↗](http://yapi.zswltec.com:3000/project/11/interface/api/8791) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/close`
 * @更新时间 `2024-08-04 14:17:14`
 */
export interface MonthlyCloseRequest {
  mainId: number
}

/**
 * 接口 [月结关账↗](http://yapi.zswltec.com:3000/project/11/interface/api/8791) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/close`
 * @更新时间 `2024-08-04 14:17:14`
 */
export interface MonthlyCloseResponse {
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
 * 接口 [校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/3331) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/validate`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface MonthlyValidateRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
}

/**
 * 接口 [校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/3331) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/validate`
 * @更新时间 `2024-08-02 14:03:29`
 */
export interface MonthlyValidateResponse {
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
 * 接口 [每月成本计提↗](http://yapi.zswltec.com:3000/project/11/interface/api/3319) 的 **请求类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/cost/list`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface CostListRequest {
  /**
   * 处理月份 yyyy-MM
   */
  yearAndMonth: string
  /**
   * 融资编号
   */
  financingCode?: string
  /**
   * 融资渠道
   */
  organizationName?: string
  isConfirmed?: number
  batchNumber?: string
  interestPay?: boolean
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
 * 接口 [每月成本计提↗](http://yapi.zswltec.com:3000/project/11/interface/api/3319) 的 **返回类型**
 *
 * @分类 [月结管理接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_806)
 * @请求头 `POST /monthly/cost/list`
 * @更新时间 `2024-08-06 14:14:54`
 */
export interface CostListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id序列号
     */
    id?: number
    /**
     * 融资id
     */
    financingId?: number
    /**
     * 融资编号
     */
    financingCode?: string
    /**
     * 月份
     */
    yearAndMonth?: string
    /**
     * 融资渠道
     */
    organizationName?: string
    /**
     * 融资金额(元)
     */
    financingAmount?: number
    /**
     * 融资余额(元)
     */
    remainingAmount?: number
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 融资利率
     */
    financingRate?: number
    /**
     * 日利率
     */
    dailyRate?: number
    /**
     * 累计计提资金成本
     */
    totalCapitalCost?: number
    /**
     * 累计计提资金成本税后
     */
    totalCapitalCostAfterTax?: number
    /**
     * 当期应付利息
     */
    termCapitalCost?: number
    /**
     * 当期应付利息（税后）
     */
    termCapitalCostAfterTax?: number
    /**
     * 质押资产类型
     */
    propertyType?: string
    financingCost?: number
    /**
     * 起息日
     */
    valueDate?: string
    /**
     * 质押资产类型展示
     */
    propertyTypeDisplay?: string
    /**
     * 借款性质
     */
    loanProperty?: string
    /**
     * 收入是否已确认
     */
    isConfirmed?: number
    /**
     * 源数据ID
     */
    sourceId?: number
    /**
     * 关联主表main_id
     */
    mainId?: number
    /**
     * 是否推送，默认0未推送
     */
    isSendCq?: number
    /**
     * 是否激活，默认1激活
     */
    isEffect?: number
    /**
     * 批次号
     */
    batchNumber?: string
    /**
     * tab类型 MonthlyModuleTypeEnum
     */
    tabType?: string
    /**
     * 是否已经更新 标志已确认之后存在刷新按钮
     */
    newUpdated?: number
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
  others?: {}
}

/* prettier-ignore-end */
