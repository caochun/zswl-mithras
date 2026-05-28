/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [\/liquidity\/test↗](http://yapi.zswltec.com:3000/project/11/interface/api/26587) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/test`
 * @更新时间 `2024-12-21 20:46:07`
 */
export interface LiquidityTestRequest {}

/**
 * 接口 [\/liquidity\/test↗](http://yapi.zswltec.com:3000/project/11/interface/api/26587) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/test`
 * @更新时间 `2024-12-21 20:46:07`
 */
export interface LiquidityTestResponse {}

/**
 * 接口 [\/liquidity\/testSetting↗](http://yapi.zswltec.com:3000/project/11/interface/api/26593) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/testSetting`
 * @更新时间 `2024-12-21 20:46:07`
 */
export interface LiquidityTestSettingRequest {}

/**
 * 接口 [\/liquidity\/testSetting↗](http://yapi.zswltec.com:3000/project/11/interface/api/26593) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/testSetting`
 * @更新时间 `2024-12-21 20:46:07`
 */
export interface LiquidityTestSettingResponse {}

/**
 * 接口 [回款账户配置列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26371) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountSetting/list`
 * @更新时间 `2024-12-22 19:02:02`
 */
export interface AccountSettingListRequest {
  /**
   * 融资编号
   */
  financingCode?: string
  /**
   * 融资机构
   */
  organizationName?: string
  /**
   * 是否模拟结清
   */
  simulateSettle?: boolean
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 银行账号
   */
  accountNumber?: string
}

/**
 * 接口 [回款账户配置列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26371) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountSetting/list`
 * @更新时间 `2024-12-22 19:02:02`
 */
export type AccountSettingListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 融资id
   */
  financingId?: number
  /**
   * 融资机构id
   */
  organizationId?: number[]
  /**
   * 融资机构名称
   */
  organizationName?: string[]
  /**
   * 融资金额
   */
  financingAmount?: number
  /**
   * 资金经理
   */
  fundManager?: number
  /**
   * 资金经理姓名
   */
  fundManagerName?: string
  /**
   * 账户基本表id
   */
  accountId?: number
  /**
   * 账户类别
   */
  accountCategory?: string
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 银行账号
   */
  accountNumber?: string
  /**
   * 账户性质
   */
  accountType?: string
  /**
   * 是否模拟结清
   */
  simulateSettle?: boolean
  /**
   * 模拟结清日期
   */
  settleTime?: string
  /**
   * 模拟结清金额
   */
  settleAmount?: number
  /**
   * 是否被编辑
   */
  isEdit?: boolean
}[]

/**
 * 接口 [回款账户配置编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26809) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountSetting/modify`
 * @更新时间 `2024-12-24 09:35:11`
 */
export type AccountSettingModifyRequest = {
  /**
   * id
   */
  id: number
  /**
   * 账户基本表id
   */
  accountId: number
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 银行账号
   */
  accountNumber?: string
  /**
   * 是否模拟结清
   */
  simulateSettle?: boolean
  /**
   * 模拟结清日期
   */
  settleTime?: string
  /**
   * 模拟结清金额
   */
  settleAmount?: number
}[]

/**
 * 接口 [回款账户配置编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26809) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountSetting/modify`
 * @更新时间 `2024-12-24 09:35:11`
 */
export interface AccountSettingModifyResponse {}

/**
 * 接口 [回款账户配置账户还原↗](http://yapi.zswltec.com:3000/project/11/interface/api/26437) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountSetting/restore`
 * @更新时间 `2024-12-22 19:02:02`
 */
export interface AccountSettingRestoreRequest {
  /**
   * id
   */
  idList?: number[]
}

/**
 * 接口 [回款账户配置账户还原↗](http://yapi.zswltec.com:3000/project/11/interface/api/26437) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountSetting/restore`
 * @更新时间 `2024-12-22 19:02:02`
 */
export interface AccountSettingRestoreResponse {}

/**
 * 接口 [基础参数配置↗](http://yapi.zswltec.com:3000/project/11/interface/api/26359) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterBase`
 * @更新时间 `2024-12-16 17:16:25`
 */
export interface SettingParameterBaseRequest {
  /**
   * 安全库存
   */
  saveStock?: string
  /**
   * 灵活授信
   */
  flexibleCredit?: string
}

/**
 * 接口 [基础参数配置↗](http://yapi.zswltec.com:3000/project/11/interface/api/26359) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterBase`
 * @更新时间 `2024-12-16 17:16:25`
 */
export interface SettingParameterBaseResponse {
  /**
   * 安全库存
   */
  saveStock?: number
  /**
   * 灵活授信
   */
  flexibleCredit?: number
  /**
   * 账户余额更新时间
   */
  accountBalanceUpdateTime?: string
}

/**
 * 接口 [基础参数配置编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26449) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterBase/modify`
 * @更新时间 `2024-12-19 10:48:15`
 */
export interface ParameterBaseModifyRequest {
  /**
   * 安全库存
   */
  saveStock?: number
  /**
   * 灵活授信
   */
  flexibleCredit?: number
}

/**
 * 接口 [基础参数配置编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26449) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterBase/modify`
 * @更新时间 `2024-12-19 10:48:15`
 */
export interface ParameterBaseModifyResponse {}

/**
 * 接口 [基础参数配置详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26443) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterBase/detail`
 * @更新时间 `2024-12-19 10:48:15`
 */
export interface ParameterBaseDetailRequest {}

/**
 * 接口 [基础参数配置详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26443) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterBase/detail`
 * @更新时间 `2024-12-19 10:48:15`
 */
export interface ParameterBaseDetailResponse {
  /**
   * 安全库存
   */
  saveStock?: number
  /**
   * 灵活授信
   */
  flexibleCredit?: number
  /**
   * 账户余额更新时间
   */
  accountBalanceUpdateTime?: string
}

/**
 * 接口 [流动性指标配置↗](http://yapi.zswltec.com:3000/project/11/interface/api/26353) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterIndex`
 * @更新时间 `2024-12-16 17:16:25`
 */
export interface SettingParameterIndexRequest {
  /**
   * 指标名称
   */
  indexName?: string
  /**
   * 指标内容
   */
  saveStock?: string
}

/**
 * 接口 [流动性指标配置↗](http://yapi.zswltec.com:3000/project/11/interface/api/26353) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterIndex`
 * @更新时间 `2024-12-16 17:16:25`
 */
export type SettingParameterIndexResponse = {
  /**
   * 指标名称
   */
  indexName?: string
  /**
   * 指标内容
   */
  indexValue?: {
    /**
     * 预警等级
     */
    level?: string
    /**
     * 单位
     */
    unit?: string
    /**
     * 值
     */
    value?: number
  }[]
}[]

/**
 * 接口 [流动性指标配置编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26461) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterIndex/modify`
 * @更新时间 `2024-12-25 09:58:03`
 */
export type ParameterIndexModifyRequest = {
  /**
   * 指标名称
   */
  indexName?: string
  /**
   * 指标注释
   */
  indexDisplay?: string
  /**
   * 红色-预警等级
   */
  redLevel?: string
  /**
   * 红色-符号
   */
  redSign?: string
  /**
   * 红色-值
   */
  redValue?: number
  /**
   * 黄色-预警等级
   */
  yellowLevel?: string
  /**
   * 黄色-符号
   */
  yellowSign?: string
  /**
   * 黄色-值
   */
  yellowValue?: number
}[]

/**
 * 接口 [流动性指标配置编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26461) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterIndex/modify`
 * @更新时间 `2024-12-25 09:58:03`
 */
export interface ParameterIndexModifyResponse {}

/**
 * 接口 [流动性指标配置详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26455) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterIndex/detail`
 * @更新时间 `2024-12-25 09:58:05`
 */
export interface ParameterIndexDetailRequest {}

/**
 * 接口 [流动性指标配置详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26455) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/setting/parameterIndex/detail`
 * @更新时间 `2024-12-25 09:58:05`
 */
export type ParameterIndexDetailResponse = {
  /**
   * 指标名称
   */
  indexName?: string
  /**
   * 指标注释
   */
  indexDisplay?: string
  /**
   * 红色-预警等级
   */
  redLevel?: string
  /**
   * 红色-符号
   */
  redSign?: string
  /**
   * 红色-值
   */
  redValue?: number
  /**
   * 黄色-预警等级
   */
  yellowLevel?: string
  /**
   * 黄色-符号
   */
  yellowSign?: string
  /**
   * 黄色-值
   */
  yellowValue?: number
}[]

/**
 * 接口 [账户余额明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26365) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountBalance/list`
 * @更新时间 `2024-12-22 19:19:22`
 */
export interface AccountBalanceListRequest {
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 银行账号
   */
  accountNumber?: string
  /**
   * 账户性质
   */
  accountType?: string
  /**
   * 起止日期-开始
   */
  queryDateStart: string
  /**
   * 起止日期-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [账户余额明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26365) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountBalance/list`
 * @更新时间 `2024-12-22 19:19:22`
 */
export interface AccountBalanceListResponse {
  /**
   * 列表-以账户和日期为维度
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 数据时点
     */
    date?: string
    /**
     * 账户基本表id
     */
    accountId?: number
    /**
     * 开户银行
     */
    accountBank?: string
    /**
     * 银行账号
     */
    accountNumber?: string
    /**
     * 账户性质
     */
    accountType?: string
    /**
     * 提款
     */
    drawingsAmount?: number
    /**
     * 租金回流
     */
    rentReflowAmount?: number
    /**
     * 其他流入
     */
    otherFlowAmount?: number
    /**
     * 投放
     */
    paymentAmount?: number
    /**
     * 还本付息
     */
    repayAmount?: number
    /**
     * 还本付息-调整(编辑字段)
     */
    repayEditAmount?: number
    /**
     * 还本付息-abs
     */
    repayAbsAmount?: number
    /**
     * 还本付息-非abs
     */
    repayNoAbsAmount?: number
    /**
     * 刚性支出
     */
    mustExpenseAmount?: number
    /**
     * 其他支出
     */
    otherExpenseAmount?: number
    /**
     * 结余-预估
     */
    estimateBalanceAmount?: number
    /**
     * 结余受限-预估
     */
    estimateBalanceLimitAmount?: number
    /**
     * 结余-实际
     */
    actualBalanceAmount?: number
    /**
     * 差额
     */
    diffAmount?: number
    /**
     * 合计值-仅合计列表回返回
     */
    sum?: number
    /**
     * 监管户合计-仅合计列表回返回
     */
    supervisionSum?: number
    /**
     * 非监管户合计-仅合计列表回返回
     */
    noSupervisionSum?: number
    /**
     * 颜色
     */
    color?: string
  }[]
  /**
   * 合计-以日期为维度
   */
  sum?: {
    /**
     * 数据时点
     */
    date?: string
    /**
     * 总合计
     */
    allSum?: {
      /**
       * id
       */
      id?: number
      /**
       * 数据时点
       */
      date?: string
      /**
       * 账户基本表id
       */
      accountId?: number
      /**
       * 开户银行
       */
      accountBank?: string
      /**
       * 银行账号
       */
      accountNumber?: string
      /**
       * 账户性质
       */
      accountType?: string
      /**
       * 提款
       */
      drawingsAmount?: number
      /**
       * 租金回流
       */
      rentReflowAmount?: number
      /**
       * 其他流入
       */
      otherFlowAmount?: number
      /**
       * 投放
       */
      paymentAmount?: number
      /**
       * 还本付息
       */
      repayAmount?: number
      /**
       * 还本付息-调整(编辑字段)
       */
      repayEditAmount?: number
      /**
       * 还本付息-abs
       */
      repayAbsAmount?: number
      /**
       * 还本付息-非abs
       */
      repayNoAbsAmount?: number
      /**
       * 刚性支出
       */
      mustExpenseAmount?: number
      /**
       * 其他支出
       */
      otherExpenseAmount?: number
      /**
       * 结余-预估
       */
      estimateBalanceAmount?: number
      /**
       * 结余受限-预估
       */
      estimateBalanceLimitAmount?: number
      /**
       * 结余-实际
       */
      actualBalanceAmount?: number
      /**
       * 差额
       */
      diffAmount?: number
      /**
       * 合计值-仅合计列表回返回
       */
      sum?: number
      /**
       * 监管户合计-仅合计列表回返回
       */
      supervisionSum?: number
      /**
       * 非监管户合计-仅合计列表回返回
       */
      noSupervisionSum?: number
      /**
       * 颜色
       */
      color?: string
    }
    /**
     * 监管户合计
     */
    supervisionSum?: {
      /**
       * id
       */
      id?: number
      /**
       * 数据时点
       */
      date?: string
      /**
       * 账户基本表id
       */
      accountId?: number
      /**
       * 开户银行
       */
      accountBank?: string
      /**
       * 银行账号
       */
      accountNumber?: string
      /**
       * 账户性质
       */
      accountType?: string
      /**
       * 提款
       */
      drawingsAmount?: number
      /**
       * 租金回流
       */
      rentReflowAmount?: number
      /**
       * 其他流入
       */
      otherFlowAmount?: number
      /**
       * 投放
       */
      paymentAmount?: number
      /**
       * 还本付息
       */
      repayAmount?: number
      /**
       * 还本付息-调整(编辑字段)
       */
      repayEditAmount?: number
      /**
       * 还本付息-abs
       */
      repayAbsAmount?: number
      /**
       * 还本付息-非abs
       */
      repayNoAbsAmount?: number
      /**
       * 刚性支出
       */
      mustExpenseAmount?: number
      /**
       * 其他支出
       */
      otherExpenseAmount?: number
      /**
       * 结余-预估
       */
      estimateBalanceAmount?: number
      /**
       * 结余受限-预估
       */
      estimateBalanceLimitAmount?: number
      /**
       * 结余-实际
       */
      actualBalanceAmount?: number
      /**
       * 差额
       */
      diffAmount?: number
      /**
       * 合计值-仅合计列表回返回
       */
      sum?: number
      /**
       * 监管户合计-仅合计列表回返回
       */
      supervisionSum?: number
      /**
       * 非监管户合计-仅合计列表回返回
       */
      noSupervisionSum?: number
      /**
       * 颜色
       */
      color?: string
    }
    /**
     * 非监管户合计
     */
    noSupervisionSum?: {
      /**
       * id
       */
      id?: number
      /**
       * 数据时点
       */
      date?: string
      /**
       * 账户基本表id
       */
      accountId?: number
      /**
       * 开户银行
       */
      accountBank?: string
      /**
       * 银行账号
       */
      accountNumber?: string
      /**
       * 账户性质
       */
      accountType?: string
      /**
       * 提款
       */
      drawingsAmount?: number
      /**
       * 租金回流
       */
      rentReflowAmount?: number
      /**
       * 其他流入
       */
      otherFlowAmount?: number
      /**
       * 投放
       */
      paymentAmount?: number
      /**
       * 还本付息
       */
      repayAmount?: number
      /**
       * 还本付息-调整(编辑字段)
       */
      repayEditAmount?: number
      /**
       * 还本付息-abs
       */
      repayAbsAmount?: number
      /**
       * 还本付息-非abs
       */
      repayNoAbsAmount?: number
      /**
       * 刚性支出
       */
      mustExpenseAmount?: number
      /**
       * 其他支出
       */
      otherExpenseAmount?: number
      /**
       * 结余-预估
       */
      estimateBalanceAmount?: number
      /**
       * 结余受限-预估
       */
      estimateBalanceLimitAmount?: number
      /**
       * 结余-实际
       */
      actualBalanceAmount?: number
      /**
       * 差额
       */
      diffAmount?: number
      /**
       * 合计值-仅合计列表回返回
       */
      sum?: number
      /**
       * 监管户合计-仅合计列表回返回
       */
      supervisionSum?: number
      /**
       * 非监管户合计-仅合计列表回返回
       */
      noSupervisionSum?: number
      /**
       * 颜色
       */
      color?: string
    }
  }[]
}

/**
 * 接口 [账户余额明细导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/26803) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountBalance/import`
 * @更新时间 `2024-12-24 09:35:11`
 */
export interface AccountBalanceImportRequest {
  file: File
}

/**
 * 接口 [账户余额明细导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/26803) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountBalance/import`
 * @更新时间 `2024-12-24 09:35:11`
 */
export interface AccountBalanceImportResponse {
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
 * 接口 [账户余额明细编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26431) 的 **请求类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountBalance/modify`
 * @更新时间 `2024-12-22 19:02:02`
 */
export type AccountBalanceModifyRequest = {
  /**
   * id
   */
  id?: number
  /**
   * 提款
   */
  drawingsAmount?: number
  /**
   * 其他流入
   */
  otherFlowAmount?: number
  /**
   * 投放
   */
  paymentAmount?: number
  /**
   * 还本付息-调整(编辑字段)
   */
  repayEditAmount?: number
  /**
   * 刚性支出
   */
  mustExpenseAmount?: number
  /**
   * 其他支出
   */
  otherExpenseAmount?: number
  /**
   * 结余受限-预估
   */
  estimateBalanceLimitAmount?: number
  /**
   * 结余-实际
   */
  actualBalanceAmount?: number
}[]

/**
 * 接口 [账户余额明细编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/26431) 的 **返回类型**
 *
 * @分类 [流动性管理-基本信息-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3656)
 * @请求头 `POST /liquidity/accountBalance/modify`
 * @更新时间 `2024-12-22 19:02:02`
 */
export interface AccountBalanceModifyResponse {}

/* prettier-ignore-end */
