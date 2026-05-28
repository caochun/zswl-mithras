/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [保单台账-保单信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13159) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/list`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface LedgerListRequest {
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 保险起始日
   */
  insuranceStartDateFrom?: string
  /**
   * 保险起始日
   */
  insuranceStartDateTo?: string
  /**
   * 保险到期日
   */
  insuranceEndDateFrom?: string
  /**
   * 保险到期日
   */
  insuranceEndDateTo?: string
  /**
   * 是否续保 PolicyRenewInsuranceEnum
   */
  renewInsuranceFlag?: string
  /**
   * 项目主办
   */
  projSponsorUserId?: number
  /**
   * 项目协办
   */
  projCosponsorUserId?: number
  /**
   * 审批状态
   */
  approvalStatus?: string
  /**
   * 保单状态
   */
  policyStatus?: string
  /**
   * 15日内到期保单
   */
  expires?: boolean
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
 * 接口 [保单台账-保单信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13159) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/list`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface LedgerListResponse {
  /**
   * 查询集合
   */
  list?: {
    key?: string
    /**
     * 保单编号
     */
    policyCode?: string
    /**
     * 数据来源
     */
    dataSource?: string
    /**
     * projId
     */
    projId?: number
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名
     */
    clientName?: string
    /**
     * 保险起始日
     */
    insuranceStartDate?: string
    /**
     * 保险到期日
     */
    insuranceEndDate?: string
    /**
     * 是否续保 PolicyRenewInsuranceEnum
     */
    renewInsuranceFlag?: string
    /**
     * 保险公司名称
     */
    insuranceCompany?: string
    /**
     * 项目主办用户id
     */
    projSponsorUserId?: number
    /**
     * 项目主办用户名称
     */
    projSponsorUserName?: string
    /**
     * 项目协办方用户id列表
     */
    projCosponsorUserIds?: number[]
    /**
     * 项目协办方用户名列表
     */
    projCosponsorUserNames?: string[]
    /**
     * 审批状态
     */
    approvalStatus?: string
    /**
     * 标识信息
     */
    identificationInformation?: string
    /**
     * 逾期天数
     */
    overdueDays?: number
    /**
     * 续保保单反馈日
     */
    renewalPolicyFeedbackDate?: string
    /**
     * 保单状态
     */
    policyStatus?: string
    createTime?: string
    /**
     * id
     */
    id?: number
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
 * 接口 [保单台账-保单详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13154) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/detail`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface LedgerDetailRequest {
  /**
   * id
   */
  id: number
  /**
   * 数据来源
   */
  dataSource: string
}

/**
 * 接口 [保单台账-保单详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13154) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/detail`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface LedgerDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * projId
   */
  projId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名
   */
  clientName?: string
  /**
   * 保险起始日
   */
  insuranceStartDate?: string
  /**
   * 保险到期日
   */
  insuranceEndDate?: string
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 保单金额
   */
  policyAmount?: number
  /**
   * 保险种类
   */
  policyType?: string
  /**
   * remark
   */
  remark?: string
  /**
   * 是否续保 PolicyRenewInsuranceEnum
   */
  renewInsuranceFlag?: string
  /**
   * 项目主办用户id
   */
  projSponsorUserId?: number
  /**
   * 项目主办用户名称
   */
  projSponsorUserName?: string
  /**
   * 标识信息
   */
  identificationInformation?: string
  /**
   * 逾期天数
   */
  overdueDays?: number
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 项目协办方用户名列表
   */
  projCosponsorUserNames?: string[]
  /**
   * 合同到期日
   */
  actualFinishDate?: string
  /**
   * 资料清单
   */
  materials?: {
    /**
     * 文件id
     */
    id?: number
    /**
     * 文件名
     */
    name?: string
    createBy?: number
    createName?: string
    createTime?: string
  }[]
}

/**
 * 接口 [保单台账-列表（excel导出）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13169) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/list/export`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface ListExportRequest {
  /**
   * 需要导出的数据id列表-dataSource=policy
   */
  policyExportIds?: number[]
  /**
   * 需要导出的数据id列表-dataSource=payment
   */
  paymentExportIds?: number[]
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 保险起始日
   */
  insuranceStartDateFrom?: string
  /**
   * 保险起始日
   */
  insuranceStartDateTo?: string
  /**
   * 保险到期日
   */
  insuranceEndDateFrom?: string
  /**
   * 保险到期日
   */
  insuranceEndDateTo?: string
  /**
   * 是否续保 PolicyRenewInsuranceEnum
   */
  renewInsuranceFlag?: string
  /**
   * 项目主办
   */
  projSponsorUserId?: number
  /**
   * 项目协办
   */
  projCosponsorUserId?: number
  /**
   * 审批状态
   */
  approvalStatus?: string
  /**
   * 保单状态
   */
  policyStatus?: string
  /**
   * 15日内到期保单
   */
  expires?: boolean
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
 * 接口 [保单台账-列表（excel导出）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13169) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/list/export`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface ListExportResponse {
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
 * 接口 [保单台账-合同保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13900) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/contract/policy`
 * @更新时间 `2023-10-24 10:15:21`
 */
export interface ContractPolicyRequest {
  /**
   * contractId
   */
  contractId: number
  /**
   * policyId
   */
  policyIds?: number[]
}

/**
 * 接口 [保单台账-合同保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13900) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/contract/policy`
 * @更新时间 `2023-10-24 10:15:21`
 */
export type ContractPolicyResponse = {
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * projId
   */
  projId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * contract_id
   */
  contractId?: number
  /**
   * contract_code
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名
   */
  clientName?: string
  /**
   * 保险起始日
   */
  insuranceStartDate?: string
  /**
   * 保险到期日
   */
  insuranceEndDate?: string
  /**
   * 保单金额
   */
  policyAmount?: number
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 项目主办用户id
   */
  projSponsorUserId?: number
  /**
   * 项目主办用户名称
   */
  projSponsorUserName?: string
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 项目协办方用户名列表
   */
  projCosponsorUserNames?: string[]
  creater?: boolean
  approvalStatus?: string
  /**
   * 保险种类
   */
  policyType?: string
  /**
   * remark
   */
  remark?: string
  /**
   * 是否续保 PolicyRenewInsuranceEnum
   */
  renewInsuranceFlag?: string
  /**
   * 创建人ID
   */
  createBy?: number
  /**
   * 创建人姓名
   */
  createName?: string
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 标识信息
   */
  identificationInformation?: string
  /**
   * 保单等级
   */
  level?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [保单台账-合同保单信息导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14485) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/contract/policy/export`
 * @更新时间 `2023-10-19 15:37:22`
 */
export interface PolicyExportRequest {
  /**
   * contractId
   */
  contractId: number
  /**
   * policyId
   */
  policyIds?: number[]
}

/**
 * 接口 [保单台账-合同保单信息导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14485) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/contract/policy/export`
 * @更新时间 `2023-10-19 15:37:22`
 */
export interface PolicyExportResponse {
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
 * 接口 [保单台账-合同信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13898) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/contract/detail`
 * @更新时间 `2023-10-24 10:19:04`
 */
export interface ContractDetailRequest {
  /**
   * id
   */
  id?: number
  contractId?: number
  /**
   * 数据来源
   */
  dataSource: string
}

/**
 * 接口 [保单台账-合同信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13898) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/contract/detail`
 * @更新时间 `2023-10-24 10:19:04`
 */
export interface ContractDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 合同金额
   */
  applyCreditAmount?: number
  /**
   * 实际起租日
   */
  actualLeaseDate?: string
  /**
   * 实际结束日
   */
  actualFinishDate?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户name
   */
  clientName?: string
  /**
   * 项目主办用户id
   */
  projSponsorUserId?: number
  /**
   * 项目主办用户名称
   */
  projSponsorUserName?: string
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 项目协办方用户名列表
   */
  projCosponsorUserNames?: string[]
}

/**
 * 接口 [保单台账-确认保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14497) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/tmp/sync`
 * @更新时间 `2023-10-23 10:49:21`
 */
export interface TmpSyncRequest {
  /**
   * 合同ID
   */
  contractId: string
}

/**
 * 接口 [保单台账-确认保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14497) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /policy/ledger/tmp/sync`
 * @更新时间 `2023-10-23 10:49:21`
 */
export interface TmpSyncResponse {
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
 * 接口 [待维护保单项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13734) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /maintenance/policy/proj/list`
 * @更新时间 `2023-10-19 15:37:21`
 */
export interface ProjListRequest {
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 保险起始日
   */
  insuranceStartDateFrom?: string
  /**
   * 保险起始日
   */
  insuranceStartDateTo?: string
  /**
   * 保险到期日
   */
  insuranceEndDateFrom?: string
  /**
   * 保险到期日
   */
  insuranceEndDateTo?: string
}

/**
 * 接口 [待维护保单项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13734) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `POST /maintenance/policy/proj/list`
 * @更新时间 `2023-10-19 15:37:21`
 */
export type ProjListResponse = {
  /**
   * 保单id
   */
  id?: number
  contractId?: number
  /**
   * 数据来源
   */
  dataSource?: number
  /**
   * 合同编号
   */
  contractCode?: string
  actualFinishDate?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名
   */
  clientName?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * projId
   */
  projId?: number
  /**
   * 推送保单id
   */
  policyId?: number
  /**
   * 项目主办用户id
   */
  projSponsorUserId?: number
  /**
   * 项目主办用户名称
   */
  projSponsorUserName?: string
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 项目协办方用户名列表
   */
  projCosponsorUserNames?: string[]
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 保险种类
   */
  policyType?: string
  /**
   * 保险起始日
   */
  insuranceStartDate?: string
  /**
   * 保险到期日
   */
  insuranceEndDate?: string
  /**
   * 付款编号
   */
  paymentCode?: string
  /**
   * 付款id
   */
  paymentId?: string
  /**
   * 是否自动推送，0 手动 1 自动
   */
  automatic?: number
  noticeFlag?: number
  /**
   * 逾期天数
   */
  overdueDays?: number
  /**
   * 保单金额
   */
  policyAmount?: number
  /**
   * 标识信息
   */
  identificationInformation?: string
}[]

/**
 * 接口 [待维护保单项目导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14491) 的 **请求类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `GET /maintenance/policy/proj/export`
 * @更新时间 `2023-10-19 15:37:22`
 */
export interface ProjExportRequest {
  /**
   * dataSource为1时ID
   */
  policyIds?: string
  /**
   * dataSource为0时ID
   */
  paymentPolicyIds?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 保单编号
   */
  policyCode?: string
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * 保险起始日 : yyyy-MM-dd
   */
  insuranceStartDateFrom?: string
  /**
   * 保险起始日 : yyyy-MM-dd
   */
  insuranceStartDateTo?: string
  /**
   * 保险到期日 : yyyy-MM-dd
   */
  insuranceEndDateFrom?: string
  /**
   * 保险到期日 : yyyy-MM-dd
   */
  insuranceEndDateTo?: string
}

/**
 * 接口 [待维护保单项目导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14491) 的 **返回类型**
 *
 * @分类 [保单台账接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2568)
 * @请求头 `GET /maintenance/policy/proj/export`
 * @更新时间 `2023-10-19 15:37:22`
 */
export interface ProjExportResponse {
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
