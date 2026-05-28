/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改收付款↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11733) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/modify`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface InfoModifyRequest {
  /**
   * 主键
   */
  id?: number
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改收付款↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11733) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/modify`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface InfoModifyResponse {}

/**
 * 接口 [收付款列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11734) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/list`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface InfoListRequest {
  repayMonth?:any
  /**
   * 融资机构
   */
  financingOrgId?: number
  /**
   * 融资金额from
   */
  financingAmountFrom?: number
  /**
   * 融资金额to
   */
  financingAmountTo?: number
  /**
   * 付款状态
   */
  receiptRepayState?: string
  /**
   * Date From
   */
  dateFrom?: string
  /**
   * Date To
   */
  dateTo?: string
  /**
   * 资金经理
   */
  fundManager?: number
  /**
   * 创建时间from
   */
  createTimeFrom?: string
  /**
   * 创建时间to
   */
  createTimeTo?: string
  /**
   * 更新时间from
   */
  updateTimeFrom?: string
  /**
   * 更新时间to
   */
  updateTimeTo?: string
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
 * 接口 [收付款列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11734) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/list`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface InfoListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 收付款编号
     */
    receiptRepayCode?: string
    /**
     * 融资机构
     */
    financingOrgName?: string
    /**
     * 融资金额
     */
    financingAmount?: number
    /**
     * 已还本金
     */
    repayPrincipal?: number
    /**
     * 已还利息
     */
    repayInterest?: number
    /**
     * 一年内到期本金
     */
    oneYearPrincipal?: number
    /**
     * 本月待还金额
     */
    monthRepayAmount?: number
    /**
     * 收付款状态
     */
    receiptRepayState?: string
    /**
     * 审批状态
     */
    processState?: string
    /**
     * 创建人
     */
    createByName?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
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

/**
 * 接口 [收付款详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11735) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/detail`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface InfoDetailRequest {
  /**
   * 主键
   */
  id?: number
}

/**
 * 接口 [收付款详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11735) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/detail`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface InfoDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 收付款编号
   */
  receiptRepayCode?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 融资机构名称
   */
  financingOrgName?: string
  /**
   * 总授信额度
   */
  totalCreditLimit?: number
  /**
   * 剩余授信额度
   */
  remainingCreditLimit?: number
  /**
   * 融资金额
   */
  financingAmount?: number
  /**
   * 利息总额
   */
  totalInterest?: number
  /**
   * 担保详情 ,GuaranteeInfoRSP
   */
  guaranteeDetail?: {
    /**
     * 担保机构id
     */
    guaranteeAgencyId?: number
    /**
     * 担保机构名称
     */
    guaranteeAgencyName?: string
    /**
     * 担保金额
     */
    guaranteeAmount?: number
    /**
     * 剩余可担保金额
     */
    remainingAmount?: number
  }[]
  /**
   * 保理手续费
   */
  factoringFee?: number
  /**
   * 开证许可证费
   */
  licenseFee?: number
  /**
   * 保证金金额
   */
  cashDeposit?: number
  /**
   * 其他费用
   */
  otherFee?: number
  /**
   * 资金经理
   */
  fundManager?: string
  /**
   * 所属部门
   */
  department?: string
  /**
   * 部门负责人
   */
  departmentLeader?: string
  /**
   * 分管领导
   */
  chargeLeader?: string
}

/**
 * 接口 [质押明细接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11736) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/pledge/detail`
 * @更新时间 `2023-02-22 10:20:50`
 */
export interface PledgeDetailRequest {
  /**
   * 主键
   */
  id?: number
}

/**
 * 接口 [质押明细接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11736) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2134)
 * @请求头 `POST /fund/receipt/repay/base/info/pledge/detail`
 * @更新时间 `2023-02-22 10:20:50`
 */
export type PledgeDetailResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 质押编号
   */
  pledgeCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 合同金额
   */
  contractAmount?: number
  /**
   * 合同开始日期
   */
  contractStartDate?: string
  /**
   * 合同结束日期
   */
  contractEndDate?: string
  /**
   * 剩余未还本金
   */
  remainingUnpaidPrincipal?: number
}[]

/* prettier-ignore-end */
