/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [付款申请拦截↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11709) 的 **请求类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/paymentapply/intercept`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface PaymentapplyInterceptRequest {
  /**
   * 付款申请id
   */
  paymentId: number
  /**
   * 合同id
   */
  contractId: number
}

/**
 * 接口 [付款申请拦截↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11709) 的 **返回类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/paymentapply/intercept`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface PaymentapplyInterceptResponse {
  /**
   * 是否拦截,false:拦截,true:不拦截
   */
  intercept?: boolean
  /**
   * 超额指标名称 ,String
   */
  metricNames?: string[]
}

/**
 * 接口 [修改预警监控指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11690) 的 **请求类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/modify`
 * @更新时间 `2023-06-08 10:53:02`
 */
export interface StrategyModifyRequest {
  /**
   * 主键
   */
  id?: number
  /**
   * 预警值1
   */
  earlyWarningValueOne?: string
  /**
   * 预警值2
   */
  earlyWarningValueTwo?: string
  /**
   * 限定值1
   */
  limitValueOne?: number
  /**
   * 限定值2
   */
  limitValueTwo?: number
  /**
   * 预警状态（1启用，0禁用）
   */
  earlyWarningState?: number
}

/**
 * 接口 [修改预警监控指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11690) 的 **返回类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/modify`
 * @更新时间 `2023-06-08 10:53:02`
 */
export interface StrategyModifyResponse {}

/**
 * 接口 [快照重计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12949) 的 **请求类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/snapshot/recalculate`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface SnapshotRecalculateRequest {
  /**
   * 数据时点
   */
  date?: string
}

/**
 * 接口 [快照重计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12949) 的 **返回类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/snapshot/recalculate`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface SnapshotRecalculateResponse {}

/**
 * 接口 [立项拦截↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11708) 的 **请求类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/proreview/intercept`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface ProreviewInterceptRequest {
  /**
   * 立项id
   */
  projEstablishId: number
}

/**
 * 接口 [立项拦截↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11708) 的 **返回类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/proreview/intercept`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface ProreviewInterceptResponse {
  /**
   * 是否拦截,false:拦截,true:不拦截
   */
  intercept?: boolean
  /**
   * 超额指标名称 ,String
   */
  metricNames?: string[]
}

/**
 * 接口 [预警监控指标列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11691) 的 **请求类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/list`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface StrategyListRequest {
  /**
   * 指标类型(LIQUIDITY_RISK-流动性风险指标限额,CREDIT_RISK-信用风险指标限额,MARKET_RISK-市场风险指标限额,BUSINESS_RISK-业务风险指标限额,REGIONAL_RISK-区域风险限额指标)
   */
  metricType?: string
  /**
   * 指标类别(CONTROL-控制类,GUIDANCE-指导类)
   */
  metricCategory?: string
  /**
   * 指标名称
   */
  metricName?: string
  /**
   * 预警状态（1启用，0禁用）
   */
  earlyWarningState?: number
  /**
   * 指标更新时间from
   */
  updateTimeFrom?: string
  /**
   * 指标更新时间to
   */
  updateTimeTo?: string
  /**
   * 指标快照日期
   */
  date?: string
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
 * 接口 [预警监控指标列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11691) 的 **返回类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/list`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface StrategyListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 指标编号
     */
    metricCode?: string
    /**
     * 指标类型
     */
    metricType?: string
    /**
     * 指标类别
     */
    metricCategory?: string
    /**
     * 指标名称
     */
    metricName?: string
    /**
     * 计算逻辑
     */
    computationalLogic?: string
    /**
     * 预警值1
     */
    earlyWarningValueOne?: number
    /**
     * 限定值1
     */
    limitValueOne?: number
    /**
     * 当前值1
     */
    currentValueOne?: number
    /**
     * 单位1
     */
    valueUnitOne?: string
    /**
     * 预警值2
     */
    earlyWarningValueTwo?: number
    /**
     * 当前值2
     */
    currentValueTwo?: number
    /**
     * 限定值2
     */
    limitValueTwo?: number
    /**
     * 单位2
     */
    valueUnitTwo?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 空值原因
     */
    nullReason?: string
    /**
     * 预警状态（1启用，0禁用）
     */
    earlyWarningState?: number
    /**
     * 比较方式1
     */
    comparisonMethodOne?: string
    /**
     * 比较方式2
     */
    comparisonMethodTwo?: string
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
   * 其他携带参数(该参数为map)
   */
  others?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * Object
     */
    mapValue?: {}
  }
}

/**
 * 接口 [预警监控指标详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11692) 的 **请求类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/detail`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface StrategyDetailRequest {
  /**
   * 主键
   */
  id: number
}

/**
 * 接口 [预警监控指标详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11692) 的 **返回类型**
 *
 * @分类 [risk-control-strategy-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2122)
 * @请求头 `POST /risk/control/strategy/detail`
 * @更新时间 `2023-06-08 10:53:03`
 */
export interface StrategyDetailResponse {
  /**
   * 主键
   */
  id?: number
  /**
   * 指标编号
   */
  metricCode?: string
  /**
   * 指标类型
   */
  metricType?: string
  /**
   * 指标类别
   */
  metricCategory?: string
  /**
   * 指标名称
   */
  metricName?: string
  /**
   * 计算逻辑
   */
  computationalLogic?: string
  /**
   * 预警值1
   */
  earlyWarningValueOne?: number
  /**
   * 比较方法1
   */
  comparisonMethodOne?: string
  /**
   * 预警值2
   */
  earlyWarningValueTwo?: number
  /**
   * 比较方法2
   */
  comparisonMethodTwo?: string
  /**
   * 当前值1
   */
  currentValueOne?: number
  /**
   * 当前值2
   */
  currentValueTwo?: number
  /**
   * 限定值1
   */
  limitValueOne?: number
  /**
   * 限定值2
   */
  limitValueTwo?: number
  /**
   * 单位1
   */
  valueUnitOne?: string
  /**
   * 单位2
   */
  valueUnitTwo?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 空值原因
   */
  nullReason?: string
  /**
   * 预警状态（1启用，0禁用）
   */
  earlyWarningState?: number
}

/* prettier-ignore-end */
