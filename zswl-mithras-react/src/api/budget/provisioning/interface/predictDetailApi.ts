/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37801) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/modify`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 内评评级
   */
  innerMdLevel?: string
  /**
   * ecl违约概率
   */
  eclPd?: string
  /**
   * 外评级别
   */
  outerLevel?: string
  /**
   * ecl外评违约概率
   */
  eclOuterPd?: string
  /**
   * 所属分组
   */
  group?: string
  /**
   * 五级分类
   */
  classify?: string
  /**
   * 逾期天数
   */
  lateDay?: number
  /**
   * 租赁物类型
   */
  leaseType?: string
  /**
   * 剩余本金
   */
  remainPrincipal?: string
  /**
   * 应计利息
   */
  accruedInterest?: string
  /**
   * 保证金
   */
  deposit?: string
  /**
   * 下一期租金
   */
  nextRent?: string
  /**
   * 风险敞口
   */
  riskExposure?: string
  /**
   * ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）
   */
  ead?: string
  /**
   * 合同到期日
   */
  contractExpirationDate?: string
  /**
   * 债项阶段
   */
  eclStep?: string
  /**
   * 期限调整系数t
   */
  eclFactorT?: string
  /**
   * ecl基准/乐观/悲观调整因子z
   */
  eclParamZ?: string
  /**
   * ecl基准/乐观/悲观情景权重
   */
  eclParamWeight?: string
  /**
   * 违约损失率(lgd)
   */
  lgd?: string
  /**
   * 下迁等级
   */
  rzyEclDownLevel?: number
  /**
   * 基准pdforward
   */
  basePdForward?: string
  /**
   * 乐观pdforward
   */
  optPdForward?: string
  /**
   * 悲观pdforward
   */
  gloPdForward?: string
  /**
   * 基准pdifrs9
   */
  eclBaseIfrs9?: string
  /**
   * 乐观pdifrs9
   */
  eclOptIfrs9?: string
  /**
   * 悲观pdifrs9
   */
  eclGloIfrs9?: string
  /**
   * 基准ecl
   */
  baseEcl?: string
  /**
   * 乐观ecl
   */
  optEcl?: string
  /**
   * 悲观ecl
   */
  gloEcl?: string
  /**
   * ecl
   */
  ecl?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37801) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/modify`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordModifyResponse {
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
 * 接口 [删除资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37795) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/remove`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37795) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/remove`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordRemoveResponse {
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
 * 接口 [新增资产减值预测导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/37771) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/import`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordImportRequest {
  /**
   * 预测计划id
   */
  executePredictId: string
  /**
   * 上传文件
   */
  file: File
}

/**
 * 接口 [新增资产减值预测导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/37771) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/import`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordImportResponse {
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
 * 接口 [新增资产减值预测导入检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37789) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/check`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordCheckRequest {
  /**
   * 预测计划id
   */
  executePredictId: string
  /**
   * 上传文件
   */
  file: File
}

/**
 * 接口 [新增资产减值预测导入检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37789) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/check`
 * @更新时间 `2025-10-21 16:08:29`
 */
export type RecordCheckResponse = string[]

/**
 * 接口 [新增资产减值预测检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37783) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/addCheck`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordAddCheckRequest {
  /**
   * 预测计划id
   */
  executePredictId: number
  /**
   * 借据id
   */
  receiptId?: number
  /**
   * 借据编号
   */
  receiptCode: string
  /**
   * 计算月份
   */
  calculationDate: string
  /**
   * 拨备预测记录表id
   */
  budgetPlanProfitDetail?: string
  /**
   * 拨备计提详情id
   */
  kpiProvisionDetailId?: number
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 内评评级
   */
  innerMdLevel?: string
  /**
   * ecl违约概率
   */
  eclPd?: string
  /**
   * 外评级别
   */
  outerLevel?: string
  /**
   * ecl外评违约概率
   */
  eclOuterPd?: string
  /**
   * 所属分组
   */
  group?: string
  /**
   * 五级分类
   */
  classify?: string
  /**
   * 逾期天数
   */
  lateDay?: number
  /**
   * 租赁物类型
   */
  leaseType?: string
  /**
   * 剩余本金
   */
  remainPrincipal?: string
  /**
   * 应计利息
   */
  accruedInterest?: string
  /**
   * 保证金
   */
  deposit?: string
  /**
   * 下一期租金
   */
  nextRent?: string
  /**
   * 风险敞口
   */
  riskExposure?: string
  /**
   * ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）
   */
  ead?: string
  /**
   * 合同到期日
   */
  contractExpirationDate?: string
  /**
   * 债项阶段
   */
  eclStep?: string
  /**
   * 期限调整系数t
   */
  eclFactorT?: string
  /**
   * ecl基准/乐观/悲观调整因子z
   */
  eclParamZ?: string
  /**
   * ecl基准/乐观/悲观情景权重
   */
  eclParamWeight?: string
  /**
   * 违约损失率(lgd)
   */
  lgd?: string
  /**
   * 下迁等级
   */
  rzyEclDownLevel?: number
  /**
   * 基准pdforward
   */
  basePdForward?: string
  /**
   * 乐观pdforward
   */
  optPdForward?: string
  /**
   * 悲观pdforward
   */
  gloPdForward?: string
  /**
   * 基准pdifrs9
   */
  eclBaseIfrs9?: string
  /**
   * 乐观pdifrs9
   */
  eclOptIfrs9?: string
  /**
   * 悲观pdifrs9
   */
  eclGloIfrs9?: string
  /**
   * 基准ecl
   */
  baseEcl?: string
  /**
   * 乐观ecl
   */
  optEcl?: string
  /**
   * 悲观ecl
   */
  gloEcl?: string
  /**
   * ecl
   */
  ecl?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [新增资产减值预测检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37783) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/addCheck`
 * @更新时间 `2025-10-21 16:08:29`
 */
export type RecordAddCheckResponse = string

/**
 * 接口 [新增资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37777) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/add`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordAddRequest {
  /**
   * 预测计划id
   */
  executePredictId: number
  /**
   * 借据id
   */
  receiptId?: number
  /**
   * 借据编号
   */
  receiptCode: string
  /**
   * 计算月份
   */
  calculationDate: string
  /**
   * 拨备预测记录表id
   */
  budgetPlanProfitDetail?: string
  /**
   * 拨备计提详情id
   */
  kpiProvisionDetailId?: number
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 内评评级
   */
  innerMdLevel?: string
  /**
   * ecl违约概率
   */
  eclPd?: string
  /**
   * 外评级别
   */
  outerLevel?: string
  /**
   * ecl外评违约概率
   */
  eclOuterPd?: string
  /**
   * 所属分组
   */
  group?: string
  /**
   * 五级分类
   */
  classify?: string
  /**
   * 逾期天数
   */
  lateDay?: number
  /**
   * 租赁物类型
   */
  leaseType?: string
  /**
   * 剩余本金
   */
  remainPrincipal?: string
  /**
   * 应计利息
   */
  accruedInterest?: string
  /**
   * 保证金
   */
  deposit?: string
  /**
   * 下一期租金
   */
  nextRent?: string
  /**
   * 风险敞口
   */
  riskExposure?: string
  /**
   * ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）
   */
  ead?: string
  /**
   * 合同到期日
   */
  contractExpirationDate?: string
  /**
   * 债项阶段
   */
  eclStep?: string
  /**
   * 期限调整系数t
   */
  eclFactorT?: string
  /**
   * ecl基准/乐观/悲观调整因子z
   */
  eclParamZ?: string
  /**
   * ecl基准/乐观/悲观情景权重
   */
  eclParamWeight?: string
  /**
   * 违约损失率(lgd)
   */
  lgd?: string
  /**
   * 下迁等级
   */
  rzyEclDownLevel?: number
  /**
   * 基准pdforward
   */
  basePdForward?: string
  /**
   * 乐观pdforward
   */
  optPdForward?: string
  /**
   * 悲观pdforward
   */
  gloPdForward?: string
  /**
   * 基准pdifrs9
   */
  eclBaseIfrs9?: string
  /**
   * 乐观pdifrs9
   */
  eclOptIfrs9?: string
  /**
   * 悲观pdifrs9
   */
  eclGloIfrs9?: string
  /**
   * 基准ecl
   */
  baseEcl?: string
  /**
   * 乐观ecl
   */
  optEcl?: string
  /**
   * 悲观ecl
   */
  gloEcl?: string
  /**
   * ecl
   */
  ecl?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [新增资产减值预测详情记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37777) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/add`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordAddResponse {
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
 * 接口 [资产减值预测详情记录表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37807) 的 **请求类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/list`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordListRequest {
  /**
   * execute_predict_id
   */
  executePredictId: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 测算日期
   */
  createTimeFrom?: string
  /**
   * 测算日期
   */
  createTimeTo?: string
  /**
   * 是否最新数据
   */
  fastFlag?: boolean
  /**
   * 内评级别
   */
  innerMdLevel?: string
  /**
   * 所属分组
   */
  group?: string
  /**
   * 五级分类
   */
  classify?: string
  /**
   * 债项阶段
   */
  eclStep?: string
  /**
   * 租赁物类型
   */
  leaseType?: string
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
 * 接口 [资产减值预测详情记录表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37807) 的 **返回类型**
 *
 * @分类 [资产减值预测详情记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5372)
 * @请求头 `POST /ecl/execute/predict/record/list`
 * @更新时间 `2025-10-21 16:08:29`
 */
export interface RecordListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 预测计划id
     */
    executePredictId?: number
    /**
     * 拨备预测记录表id
     */
    budgetPlanProfitDetail?: string
    /**
     * 拨备计提详情id
     */
    kpiProvisionDetailId?: number
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 内评评级
     */
    innerMdLevel?: string
    /**
     * ecl违约概率
     */
    eclPd?: string
    /**
     * 外评级别
     */
    outerLevel?: string
    /**
     * ecl外评违约概率
     */
    eclOuterPd?: string
    /**
     * 所属分组
     */
    group?: string
    /**
     * 五级分类
     */
    classify?: string
    /**
     * 逾期天数
     */
    lateDay?: number
    /**
     * 租赁物类型
     */
    leaseType?: string
    /**
     * 剩余本金
     */
    remainPrincipal?: string
    /**
     * 应计利息
     */
    accruedInterest?: string
    /**
     * 保证金
     */
    deposit?: string
    /**
     * 下一期租金
     */
    nextRent?: string
    /**
     * 风险敞口
     */
    riskExposure?: string
    /**
     * ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）
     */
    ead?: string
    /**
     * 合同到期日
     */
    contractExpirationDate?: string
    /**
     * 债项阶段
     */
    eclStep?: string
    /**
     * 期限调整系数t
     */
    eclFactorT?: string
    /**
     * ecl基准/乐观/悲观调整因子z
     */
    eclParamZ?: string
    /**
     * ecl基准/乐观/悲观情景权重
     */
    eclParamWeight?: string
    /**
     * 违约损失率(lgd)
     */
    lgd?: string
    /**
     * 下迁等级
     */
    rzyEclDownLevel?: number
    /**
     * 基准pdforward
     */
    basePdForward?: string
    /**
     * 乐观pdforward
     */
    optPdForward?: string
    /**
     * 悲观pdforward
     */
    gloPdForward?: string
    /**
     * 基准pdifrs9
     */
    eclBaseIfrs9?: string
    /**
     * 乐观pdifrs9
     */
    eclOptIfrs9?: string
    /**
     * 悲观pdifrs9
     */
    eclGloIfrs9?: string
    /**
     * 基准ecl
     */
    baseEcl?: string
    /**
     * 乐观ecl
     */
    optEcl?: string
    /**
     * 悲观ecl
     */
    gloEcl?: string
    /**
     * ecl
     */
    ecl?: string
    /**
     * 备注
     */
    remark?: string
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
