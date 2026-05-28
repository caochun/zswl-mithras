/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改关联交易记录；如果是已报送记录，高度提示↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12654) 的 **请求类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/modify`
 * @更新时间 `2023-05-22 16:24:29`
 */
export interface ReportModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 关联交易金额（万）
   */
  amount: number
  /**
   * 描述
   */
  description: string
  /**
   * 重大交易原因
   */
  importantReason: string
  /**
   * 关联交易级别
   */
  level: string
  /**
   * 董事会/委员会意见
   */
  opinion?: string
  /**
   * 交易目的
   */
  purpose: string
  /**
   * 风险/影响
   */
  risk?: string
  /**
   * 一级分类
   */
  tradeCategoryParentName: string
  /**
   * 二级分类
   */
  tradeCategoryName: string
  /**
   * 交易日期
   */
  tradeDate: string
  /**
   * 交易对手上一年末审计净资产（万）
   */
  tradePartyAssets?: number
  /**
   * 关联交易对手名称；默认填上：浙江浙商融资租赁有限公司
   */
  tradePartyName: string
  /**
   * 主体机构
   */
  subjectPartyName: string
}

/**
 * 接口 [修改关联交易记录；如果是已报送记录，高度提示↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12654) 的 **返回类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/modify`
 * @更新时间 `2023-05-22 16:24:29`
 */
export interface ReportModifyResponse {
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
 * 接口 [关联交易关联方查询；返回最多50条↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12754) 的 **请求类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/related/clients`
 * @更新时间 `2023-05-23 10:20:22`
 */
export interface RelatedClientsRequest {
  /**
   * 关联方名称；支持模糊
   */
  name?: string
}

/**
 * 接口 [关联交易关联方查询；返回最多50条↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12754) 的 **返回类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/related/clients`
 * @更新时间 `2023-05-23 10:20:22`
 */
export type RelatedClientsResponse = string[]

/**
 * 接口 [关联交易批量报送↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12659) 的 **请求类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/submit`
 * @更新时间 `2023-05-22 16:24:35`
 */
export interface ReportSubmitRequest {
  /**
   * 提交报送记录id列表
   */
  idList: number[]
}

/**
 * 接口 [关联交易批量报送↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12659) 的 **返回类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/submit`
 * @更新时间 `2023-05-22 16:24:35`
 */
export interface ReportSubmitResponse {
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
 * 接口 [关联交易记录列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12639) 的 **请求类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/list`
 * @更新时间 `2023-05-22 16:24:15`
 */
export interface ReportListRequest {
  /**
   * 交易时间-从
   */
  tradeDateFrom?: string
  /**
   * 交易时间-到
   */
  tradeDateTo?: string
  /**
   * 交易金额-从
   */
  amountFrom?: number
  /**
   * 交易金额-到
   */
  amountTo?: number
  /**
   * 关联交易级别
   */
  level?: string
  /**
   * 交易对手名称
   */
  tradePartyName?: string
  /**
   * 报送状态
   */
  reportStatus?: string
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
}

/**
 * 接口 [关联交易记录列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12639) 的 **返回类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/list`
 * @更新时间 `2023-05-22 16:24:15`
 */
export interface ReportListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 关联交易金额（万）
     */
    amount?: number
    /**
     * 描述
     */
    description?: string
    /**
     * 重大交易原因
     */
    importantReason?: string
    /**
     * 关联交易级别
     */
    level?: string
    /**
     * 董事会/委员会意见
     */
    opinion?: string
    /**
     * 交易目的
     */
    purpose?: string
    /**
     * 风险/影响
     */
    risk?: string
    /**
     * 一级分类
     */
    tradeCategoryParentName?: string
    /**
     * 二级分类
     */
    tradeCategoryName?: string
    /**
     * 交易日期
     */
    tradeDate?: string
    /**
     * 交易对手上一年末审计净资产（万）
     */
    tradePartyAssets?: number
    /**
     * 关联交易对手名称
     */
    tradePartyName?: string
    /**
     * 主体机构
     */
    subjectPartyName?: string
    /**
     * 报送状态
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

/**
 * 接口 [删除关联交易记录↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12649) 的 **请求类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/remove`
 * @更新时间 `2023-05-22 16:24:27`
 */
export interface ReportRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除关联交易记录↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12649) 的 **返回类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/remove`
 * @更新时间 `2023-05-22 16:24:27`
 */
export interface ReportRemoveResponse {
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
 * 接口 [手动新增关联交易记录↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12644) 的 **请求类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/add`
 * @更新时间 `2023-05-22 16:24:23`
 */
export interface ReportAddRequest {
  /**
   * 关联交易金额（万）
   */
  amount: number
  /**
   * 描述
   */
  description: string
  /**
   * 重大交易原因
   */
  importantReason: string
  /**
   * 关联交易级别
   */
  level: string
  /**
   * 董事会/委员会意见
   */
  opinion?: string
  /**
   * 交易目的
   */
  purpose: string
  /**
   * 风险/影响
   */
  risk?: string
  /**
   * 一级分类
   */
  tradeCategoryParentName: string
  /**
   * 二级分类
   */
  tradeCategoryName: string
  /**
   * 交易日期
   */
  tradeDate: string
  /**
   * 交易对手上一年末审计净资产（万）
   */
  tradePartyAssets?: number
  /**
   * 关联交易对手名称；默认填上：浙江浙商融资租赁有限公司
   */
  tradePartyName: string
  /**
   * 主体机构
   */
  subjectPartyName: string
}

/**
 * 接口 [手动新增关联交易记录↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12644) 的 **返回类型**
 *
 * @分类 [关联交易报送-Api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2393)
 * @请求头 `POST /risk/control/gljy/report/add`
 * @更新时间 `2023-05-22 16:24:23`
 */
export interface ReportAddResponse {
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
