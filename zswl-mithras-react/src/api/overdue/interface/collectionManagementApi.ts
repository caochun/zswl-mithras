/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [\/test\/overdueClientInfoUpdateTask↗](http://yapi.zswltec.com:3000/project/11/interface/api/25651) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `GET /test/overdueClientInfoUpdateTask`
 * @更新时间 `2024-11-06 11:08:54`
 */
export interface TestOverdueClientInfoUpdateTaskRequest {}

/**
 * 接口 [\/test\/overdueClientInfoUpdateTask↗](http://yapi.zswltec.com:3000/project/11/interface/api/25651) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `GET /test/overdueClientInfoUpdateTask`
 * @更新时间 `2024-11-06 11:08:54`
 */
export interface TestOverdueClientInfoUpdateTaskResponse {}

/**
 * 接口 [催收动作列表导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/25645) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/download`
 * @更新时间 `2024-11-06 11:08:28`
 */
export interface ActionDownloadRequest {
  id?: number
}

/**
 * 接口 [催收动作列表导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/25645) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/download`
 * @更新时间 `2024-11-06 11:08:28`
 */
export interface ActionDownloadResponse {}

/**
 * 接口 [合同列表导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/25711) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/contract/export`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ContractExportRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 业务部门
   */
  bizDept?: number
  /**
   * 项目主办
   */
  projectSponsor?: number
  /**
   * 逾期
   */
  overdue?: boolean
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
 * 接口 [合同列表导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/25711) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/contract/export`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ContractExportResponse {}

/**
 * 接口 [修改催收信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/25471) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/update`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionUpdateRequest {
  /**
   * 催收id
   */
  ocId?: number
  /**
   * 编码
   */
  code?: string
  /**
   * 日期
   */
  date?: string
  /**
   * 类型
   */
  type?: string
  /**
   * 进展/发函原因
   */
  describe?: string
  /**
   * 人员
   */
  processPerson?: string
  /**
   * 发函类型
   */
  letterType?: string
  /**
   * 合同id
   */
  contractIds?: number[]
  /**
   * 合同codes
   */
  contractCodes?: string[]
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [修改催收信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/25471) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/update`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionUpdateResponse {
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
 * 接口 [催收列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25441) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/list`
 * @更新时间 `2024-11-06 11:08:54`
 */
export interface OverduecollectionListRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 业务部门
   */
  bizDept?: number
  /**
   * 项目主办
   */
  projectSponsor?: number
  /**
   * 逾期
   */
  overdue?: boolean
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
 * 接口 [催收列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25441) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/list`
 * @更新时间 `2024-11-06 11:08:54`
 */
export interface OverduecollectionListResponse {
  /**
   * 查询集合
   */
  list?: {
    id?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 风险敞口
     */
    riskExposure?: number
    /**
     * 逾期租金
     */
    overdueRent?: number
    /**
     * 最大逾期天数
     */
    curMaxOverdueDays?: number
    /**
     * 逾期罚息
     */
    lateCharge?: number
    /**
     * 主办
     */
    projectSponsorName?: string
    /**
     * 业务部门
     */
    bizDeptName?: string
    /**
     * 最新进展
     */
    latestProgress?: string
    /**
     * 最近跟进人
     */
    processPerson?: string
    /**
     * 最近跟进时间
     */
    processTime?: string
    /**
     * 逾期
     */
    overdue?: boolean
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
 * 接口 [催收动作详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25759) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/detail`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionDetailRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [催收动作详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25759) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/detail`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionDetailResponse {
  /**
   * 催收id
   */
  ocId?: number
  /**
   * 编码
   */
  code?: string
  /**
   * 日期
   */
  date?: string
  /**
   * 类型
   */
  type?: string
  /**
   * 进展/发函原因
   */
  describe?: string
  /**
   * 人员
   */
  processPerson?: string
  /**
   * 发函类型
   */
  letterType?: string
  /**
   * 合同id
   */
  contractIds?: number[]
  /**
   * 合同codes
   */
  contractCodes?: string[]
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [催收提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/25483) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/submit`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionSubmitRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * ocId
   */
  ocId?: number
}

/**
 * 接口 [催收提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/25483) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/submit`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionSubmitResponse {
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
 * 接口 [催收详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25447) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/detail`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface OverduecollectionDetailRequest {
  id?: number
}

/**
 * 接口 [催收详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25447) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/detail`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface OverduecollectionDetailResponse {
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
   * 风险敞口
   */
  riskExposure?: number
  /**
   * 逾期租金
   */
  overdueRent?: number
  /**
   * 最大逾期天数
   */
  curMaxOverdueDays?: number
  /**
   * 逾期罚息
   */
  lateCharge?: number
  /**
   * 催收动作列表
   */
  collectionActionList?: {
    /**
     * 催收id
     */
    ocId?: number
    /**
     * 编码
     */
    code?: string
    /**
     * 日期
     */
    date?: string
    /**
     * 类型
     */
    type?: string
    /**
     * 进展/发函原因
     */
    describe?: string
    /**
     * 人员
     */
    processPerson?: string
    /**
     * 发函类型
     */
    letterType?: string
    /**
     * 合同id
     */
    contractIds?: number[]
    /**
     * 合同codes
     */
    contractCodes?: string[]
    /**
     * id
     */
    id?: number
  }[]
}

/**
 * 接口 [合同下拉列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25465) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `GET /overduecollection/contract/pulldown`
 * @更新时间 `2024-11-04 10:51:20`
 */
export interface ContractPulldownRequest {
  clientId: string
}

/**
 * 接口 [合同下拉列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25465) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `GET /overduecollection/contract/pulldown`
 * @更新时间 `2024-11-04 10:51:20`
 */
export interface ContractPulldownResponse {}

/**
 * 接口 [合同列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25453) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `GET /overduecollection/contract/list`
 * @更新时间 `2024-11-04 10:51:20`
 */
export interface ContractListRequest {
  clientId: string
}

/**
 * 接口 [合同列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25453) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `GET /overduecollection/contract/list`
 * @更新时间 `2024-11-04 10:51:20`
 */
export type ContractListResponse = {
  /**
   * 合同id
   */
  id?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 合同状态
   */
  contractStatus?: string
  /**
   * 合同金额
   */
  contractAmount?: number
  /**
   * 当前逾期天数
   */
  overdueDays?: number
  /**
   * 逾期金额
   */
  overdueRent?: number
  /**
   * 逾期罚息
   */
  lateCharge?: number
  /**
   * 剩余本金
   */
  remainPrincipal?: number
  /**
   * 剩余保证金
   */
  remainDeposit?: number
  /**
   * 风险敞口
   */
  riskExposure?: number
  /**
   * 项目主办
   */
  projSponsorName?: string
  /**
   * 业务部门
   */
  bizDeptName?: string
}[]

/**
 * 接口 [新增催收动作↗](http://yapi.zswltec.com:3000/project/11/interface/api/25459) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/add`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface ActionAddRequest {
  /**
   * 催收id
   */
  ocId?: number
  /**
   * 编码
   */
  code?: string
  /**
   * 日期
   */
  date?: string
  /**
   * 类型
   */
  type?: string
  /**
   * 进展/发函原因
   */
  describe?: string
  /**
   * 人员
   */
  processPerson?: string
  /**
   * 发函类型
   */
  letterType?: string
  /**
   * 合同id
   */
  contractIds?: number[]
  /**
   * 合同codes
   */
  contractCodes?: string[]
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [新增催收动作↗](http://yapi.zswltec.com:3000/project/11/interface/api/25459) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/action/add`
 * @更新时间 `2024-11-11 09:32:59`
 */
export type ActionAddResponse = number

/**
 * 接口 [生成函件↗](http://yapi.zswltec.com:3000/project/11/interface/api/25477) 的 **请求类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/letter/generate`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface LetterGenerateRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * ocId
   */
  ocId?: number
}

/**
 * 接口 [生成函件↗](http://yapi.zswltec.com:3000/project/11/interface/api/25477) 的 **返回类型**
 *
 * @分类 [催收管理↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3536)
 * @请求头 `POST /overduecollection/letter/generate`
 * @更新时间 `2024-11-11 09:32:59`
 */
export interface LetterGenerateResponse {
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
