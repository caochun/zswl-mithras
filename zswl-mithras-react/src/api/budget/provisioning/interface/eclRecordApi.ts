/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改资产减值记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37585) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/modify`
 * @更新时间 `2025-09-30 15:36:31`
 */
export interface RecordModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 调用记录编号
   */
  modelRecordKey?: string
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
 * 接口 [修改资产减值记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37585) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/modify`
 * @更新时间 `2025-09-30 15:36:31`
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
 * 接口 [删除资产减值全量记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37675) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/all/remove`
 * @更新时间 `2025-10-11 16:40:12`
 */
export interface AllRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资产减值全量记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37675) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/all/remove`
 * @更新时间 `2025-10-11 16:40:12`
 */
export interface AllRemoveResponse {
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
 * 接口 [删除资产减值记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37603) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/remove`
 * @更新时间 `2025-09-30 15:36:31`
 */
export interface RecordRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资产减值记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37603) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/remove`
 * @更新时间 `2025-09-30 15:36:31`
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
 * 接口 [新增资产减值记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37579) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/add`
 * @更新时间 `2025-10-13 17:37:47`
 */
export interface RecordAddRequest {
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
  receiptId?: number
  /**
   * 借据编号
   */
  receiptCode?: string
  /**
   * 业务类型。租赁、保理、转租赁
   */
  contractBizType?: string
  /**
   * 租赁类型。直租、回租、经营性租赁
   */
  contractLeaseType?: string
  /**
   * 项目类别
   */
  projClassify?: string
  /**
   * 利润所属部门id
   */
  profitBelongDeptId?: number
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
   * 风险等级
   */
  riskLevel?: string
  /**
   * 本月风险余额
   */
  profitCurrent?: string
  /**
   * 上月风险余额
   */
  profitTotal?: string
  /**
   * 本月风险金计提/转回
   */
  bonusCurrent?: string
  /**
   * 合同到期日
   */
  contractExpirationDate?: string
}

/**
 * 接口 [新增资产减值记录表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37579) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/add`
 * @更新时间 `2025-10-13 17:37:47`
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
 * 接口 [新增资产减值记录表导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/37669) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/import`
 * @更新时间 `2025-10-10 15:14:06`
 */
export interface RecordImportRequest {
  /**
   * 上传文件
   */
  file: {
    [k: string]: unknown
  }
}

/**
 * 接口 [新增资产减值记录表导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/37669) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/import`
 * @更新时间 `2025-10-10 15:14:06`
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
 * 接口 [新增资产减值记录表导入检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37681) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/import/check`
 * @更新时间 `2025-10-13 14:46:56`
 */
export interface ImportCheckRequest {
  /**
   * 上传文件
   */
  file: {
    [k: string]: unknown
  }
}

/**
 * 接口 [新增资产减值记录表导入检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37681) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/import/check`
 * @更新时间 `2025-10-13 14:46:56`
 */
export interface ImportCheckResponse {
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
 * 接口 [新增资产减值记录表检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37663) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/addCheck`
 * @更新时间 `2025-10-10 15:14:06`
 */
export interface RecordAddCheckRequest {
  /**
   * 调用记录编号
   */
  modelRecordKey?: string
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
 * 接口 [新增资产减值记录表检查↗](http://yapi.zswltec.com:3000/project/11/interface/api/37663) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/addCheck`
 * @更新时间 `2025-10-10 15:14:06`
 */
export interface RecordAddCheckResponse {
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
 * 接口 [资产减值记录表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37597) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/list`
 * @更新时间 `2025-10-10 15:14:06`
 */
export interface RecordListRequest {
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
 * 接口 [资产减值记录表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37597) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/list`
 * @更新时间 `2025-10-10 15:14:06`
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
     * 调用记录编号
     */
    modelRecordKey?: string
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
     * 来源类型 0自动，1手工添加
     */
    sourceType?: number
    createTime?: string
    /**
     * create_by
     */
    createBy?: number
    /**
     * update_time
     */
    updateTime?: string
    /**
     * update_by
     */
    updateBy?: number
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
 * 接口 [资产减值记录表比对列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37591) 的 **请求类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/list/compare`
 * @更新时间 `2025-10-10 15:14:06`
 */
export interface ListCompareRequest {
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
 * 接口 [资产减值记录表比对列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37591) 的 **返回类型**
 *
 * @分类 [资产减值记录表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5348)
 * @请求头 `POST /ecl/execute/record/list/compare`
 * @更新时间 `2025-10-10 15:14:06`
 */
export type ListCompareResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 调用记录编号
   */
  modelRecordKey?: string
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
   * 来源类型 0自动，1手工添加
   */
  sourceType?: number
  createTime?: string
  /**
   * create_by
   */
  createBy?: number
  /**
   * update_time
   */
  updateTime?: string
  /**
   * update_by
   */
  updateBy?: number
}[]

/* prettier-ignore-end */
