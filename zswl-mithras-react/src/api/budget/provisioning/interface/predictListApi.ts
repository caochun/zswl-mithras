/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [删除资产减值预测表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37765) 的 **请求类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/base/info/remove`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface InfoRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资产减值预测表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37765) 的 **返回类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/base/info/remove`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface InfoRemoveResponse {
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
 * 接口 [删除资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37747) 的 **请求类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/calculation`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface PredictCalculationRequest {
  /**
   * id
   */
  id?: number
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
 * 接口 [删除资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37747) 的 **返回类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/calculation`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface PredictCalculationResponse {
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
 * 接口 [新增资产减值预测表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37759) 的 **请求类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/base/info/add`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface InfoAddRequest {
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 拨备预测计划名称
   */
  budgetPlanName?: string
  /**
   * 拨备预测日期
   */
  predictDataFrom?: string
  /**
   * 拨备预测日期
   */
  predictDataTo?: string
  /**
   * 拨备预测来源 0自动创建 1 手工添加
   */
  source?: number
}

/**
 * 接口 [新增资产减值预测表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37759) 的 **返回类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/base/info/add`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface InfoAddResponse {
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
 * 接口 [资产减值预测表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37753) 的 **请求类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/base/info/list`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface InfoListRequest {
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
 * 接口 [资产减值预测表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37753) 的 **返回类型**
 *
 * @分类 [资产减值预测表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5366)
 * @请求头 `POST /ecl/execute/predict/base/info/list`
 * @更新时间 `2025-10-21 16:08:06`
 */
export interface InfoListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 预算计划id
     */
    budgetPlanId?: number
    /**
     * 拨备预测计划名称
     */
    budgetPlanName?: string
    /**
     * 拨备预测日期
     */
    predictData?: number
    /**
     * 拨备预测来源 0自动创建 1 手工添加
     */
    source?: number
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

/* prettier-ignore-end */
