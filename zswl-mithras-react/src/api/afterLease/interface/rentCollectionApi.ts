/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [租金催收罚息减免-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/18985) 的 **请求类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/penalty/reduction/list`
 * @更新时间 `2024-08-26 15:57:00`
 */
export interface ReductionListRequest {
  /**
   * 收款id
   */
  collectionId?: number[]
  /**
   * 罚息减免ID
   */
  reduceBaseId?: number
}

/**
 * 接口 [租金催收罚息减免-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/18985) 的 **返回类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/penalty/reduction/list`
 * @更新时间 `2024-08-26 15:57:00`
 */
export interface ReductionListResponse {
  id?: number
  /**
   * 备注
   */
  notes?: string
  items?: {
    /**
     * id
     */
    id?: number
    /**
     * 减免信息id
     */
    reduceBaseId?: number
    /**
     * 收款id
     */
    collectionId?: number
    /**
     * 客户id
     */
    clientId?: number
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
     * 借据id
     */
    receiptId?: number
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * 期项
     */
    phase?: number
    /**
     * 合同金额
     */
    applyCreditAmount?: number
    /**
     * 计划收款金额
     */
    planCollectionAmount?: number
    /**
     * 计划收款日期
     */
    planCollectionDate?: string
    /**
     * 实收金额
     */
    collectionAmount?: number
    /**
     * 罚息截止日
     */
    penaltyCloseDate?: string
    /**
     * 应收罚息
     */
    penaltyInterest?: number
    /**
     * 申请减免罚息
     */
    reducePenaltyInterest?: number
  }[]
}

/**
 * 接口 [租金催收罚息减免-变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/18979) 的 **请求类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/penalty/modify`
 * @更新时间 `2024-08-26 15:57:33`
 */
export interface PenaltyModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 备注
   */
  notes?: string
  items?: {
    /**
     * id
     */
    id?: number
    /**
     * 减免信息id
     */
    reduceBaseId?: number
    /**
     * 收款id
     */
    collectionId?: number
    /**
     * 客户id
     */
    clientId?: number
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
     * 借据id
     */
    receiptId?: number
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * 期项
     */
    phase?: number
    /**
     * 合同金额
     */
    applyCreditAmount?: number
    /**
     * 计划收款金额
     */
    planCollectionAmount?: number
    /**
     * 计划收款日期
     */
    planCollectionDate?: string
    /**
     * 实收金额
     */
    collectionAmount?: number
    /**
     * 罚息截止日
     */
    penaltyCloseDate?: string
    /**
     * 应收罚息
     */
    penaltyInterest?: number
    /**
     * 申请减免罚息
     */
    reducePenaltyInterest?: number
  }[]
}

/**
 * 接口 [租金催收罚息减免-变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/18979) 的 **返回类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/penalty/modify`
 * @更新时间 `2024-08-26 15:57:33`
 */
export interface PenaltyModifyResponse {
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
 * 接口 [租金催收罚息减免-提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/18991) 的 **请求类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/penalty/effect`
 * @更新时间 `2024-08-26 15:57:44`
 */
export interface PenaltyEffectRequest {
  /**
   * 备注
   */
  notes: string
  items?: {
    /**
     * id
     */
    id?: number
    /**
     * 减免信息id
     */
    reduceBaseId?: number
    /**
     * 收款id
     */
    collectionId?: number
    /**
     * 客户id
     */
    clientId?: number
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
     * 借据id
     */
    receiptId?: number
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * 期项
     */
    phase?: number
    /**
     * 合同金额
     */
    applyCreditAmount?: number
    /**
     * 计划收款金额
     */
    planCollectionAmount?: number
    /**
     * 计划收款日期
     */
    planCollectionDate?: string
    /**
     * 实收金额
     */
    collectionAmount?: number
    /**
     * 罚息截止日
     */
    penaltyCloseDate?: string
    /**
     * 应收罚息
     */
    penaltyInterest?: number
    /**
     * 申请减免罚息
     */
    reducePenaltyInterest?: number
  }[]
}

/**
 * 接口 [租金催收罚息减免-提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/18991) 的 **返回类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/penalty/effect`
 * @更新时间 `2024-08-26 15:57:44`
 */
export type PenaltyEffectResponse = number

/**
 * 接口 [租金催收首页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/10648) 的 **请求类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/index/list`
 * @更新时间 `2022-11-21 13:32:01`
 */
export interface IndexListRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目主办
   */
  projSponsorUserId?: number
  /**
   * 业务部门
   */
  bizDeptId?: number
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 收款日期从
   */
  planCollectionDateFrom?: string
  /**
   * 收款日期到
   */
  planCollectionDateTo?: string
  /**
   * 过滤条件枚举：HIDE_FINISH：隐藏收款完成项；NOT_NOTICE_YET：只看未通知；OVERDUE：只看逾期；ALL：显示全部
   */
  filterConditionType?: string
  /**
   * 合同状态
   */
  contractStatus?: string
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
}

/**
 * 接口 [租金催收首页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/10648) 的 **返回类型**
 *
 * @分类 [租金催收首页-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1866)
 * @请求头 `POST /rent/collection/index/list`
 * @更新时间 `2022-11-21 13:32:01`
 */
export interface IndexListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 付款id
     */
    paymentId?: number
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 业务类型（租赁、保理、转租赁、债权转让）
     */
    bizType?: string
    /**
     * 主客户id
     */
    clientId?: number
    /**
     * 主客户名称
     */
    clientName?: string
    /**
     * 付款申请编号
     */
    paymentCode?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 合同状态
     */
    contractStatus?: string
    /**
     * 借据状态(START_RENT：起租、SETTLE：结清、OVERDUE：逾期)
     */
    paymentState?: string
    /**
     * 业务部门id
     */
    bizDeptId?: number
    /**
     * 业务部门名称
     */
    bizDeptName?: string
    /**
     * 项目主办用户id
     */
    projSponsorUserId?: number
    /**
     * 项目主办用户名称
     */
    projSponsorUserName?: string
    /**
     * 收款卡片列表
     */
    collectionCardList?: {
      /**
       * 收款id
       */
      collectionId?: number
      /**
       * 计划收款日期
       */
      planCollectionDate?: string
      /**
       * 期项
       */
      phase?: number
      /**
       * 计划收款金额
       */
      planCollectionAmount?: number
      /**
       * 是否通知过苍穹系统收款（通知过则显示小铃铛）
       */
      noticeFinancialFlag?: boolean
      /**
       * 卡片状态（已收款(绿色)：PAID、待收款（黄色，计划收款日期7天内）：PENDING、已逾期（红色）：OVERDUE、未到期（灰色）：NOT_YET_EXPIRED
       */
      state?: string
      /**
       * 标签列表（减免：DEDUCTION、逾期：OVERDUE、已通知：NOTIFIED）
       */
      tagList?: string[]
    }[]
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
}

/* prettier-ignore-end */
