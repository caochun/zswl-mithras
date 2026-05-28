/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [新增项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13214) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `GET /policy/add/proj/list`
 * @更新时间 `2023-10-19 15:38:26`
 */
export interface ProjListRequest {}

/**
 * 接口 [新增项目列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13214) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `GET /policy/add/proj/list`
 * @更新时间 `2023-10-19 15:38:26`
 */
export type ProjListResponse = {
  /**
   * 下拉展示文本
   */
  label?: string
  /**
   * 下拉选项值
   */
  value?: string
}[]

/**
 * 接口 [保单信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13109) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/list`
 * @更新时间 `2023-10-19 15:38:02`
 */
export interface InfoListRequest {
  /**
   * 客户id
   */
  policyId?: number
  paymentId?: number
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
 * 接口 [保单信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13109) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/list`
 * @更新时间 `2023-10-19 15:38:02`
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
     * 付款id
     */
    paymentId?: string
    /**
     * 付款保单
     */
    paymentPolicyId?: number
    /**
     * 保单id
     */
    policyId?: number
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
     * 保单金额
     */
    policyAmount?: number
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
     * remark
     */
    remark?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 创建人
     */
    createBy?: number
    /**
     * 创建人名称
     */
    createName?: string
    /**
     * 标识信息
     */
    identificationInformation?: string
    /**
     * 附件列表
     */
    files?: {
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
 * 接口 [保单详情-已废弃↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13124) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/detail`
 * @更新时间 `2023-07-27 19:18:59`
 */
export interface InfoDetailRequest {
  /**
   * id
   */
  id?: number
  /**
   * 项目id
   */
  projId?: number
}

/**
 * 接口 [保单详情-已废弃↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13124) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/detail`
 * @更新时间 `2023-07-27 19:18:59`
 */
export interface InfoDetailResponse {
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
  createByName?: string
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [保单资料清单↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13134) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/materials/list`
 * @更新时间 `2023-06-15 17:01:58`
 */
export interface MaterialsListRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [保单资料清单↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13134) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/materials/list`
 * @更新时间 `2023-06-15 17:01:58`
 */
export type MaterialsListResponse = {
  /**
   * 文件id
   */
  id?: number
  /**
   * 文件名
   */
  materialsName?: string
}[]

/**
 * 接口 [修改保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13119) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/modify`
 * @更新时间 `2023-10-19 15:38:02`
 */
export interface InfoModifyRequest {
  /**
   * 主键id
   */
  id?: string
  /**
   * 付款id
   */
  paymentId?: string
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
   * 保单金额
   */
  policyAmount?: string
  /**
   * 保险起始日 : yyyy-MM-dd
   */
  insuranceStartDate?: string
  /**
   * 保险到期日 : yyyy-MM-dd
   */
  insuranceEndDate?: string
  /**
   * 是否续保 PolicyRenewInsuranceEnum
   */
  renewInsuranceFlag?: string
  /**
   * remark
   */
  remark?: string
  /**
   * 标识信息
   */
  identificationInformation?: string
  /**
   * files
   */
  files?: string
  /**
   * 删除id
   */
  removeFileIds?: string
}

/**
 * 接口 [修改保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13119) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/modify`
 * @更新时间 `2023-10-19 15:38:02`
 */
export interface InfoModifyResponse {
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
 * 接口 [删除保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13129) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/remove`
 * @更新时间 `2023-10-19 15:38:02`
 */
export interface InfoRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13129) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/remove`
 * @更新时间 `2023-10-19 15:38:02`
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
 * 接口 [合同列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13902) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `GET /policy/add/contract/list`
 * @更新时间 `2023-10-19 15:38:26`
 */
export interface ContractListRequest {
  /**
   * projId
   */
  projId: string
}

/**
 * 接口 [合同列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13902) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `GET /policy/add/contract/list`
 * @更新时间 `2023-10-19 15:38:26`
 */
export type ContractListResponse = {
  /**
   * 下拉展示文本
   */
  label?: string
  /**
   * 下拉选项值
   */
  value?: string
}[]

/**
 * 接口 [导入保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14503) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/import`
 * @更新时间 `2023-10-19 15:38:02`
 */
export interface PolicyImportRequest {
  /**
   * 保单文件
   */
  file: File
  /**
   * 付款id
   */
  paymentId: string
  /**
   * 保单id
   */
  policyId?: string
}

/**
 * 接口 [导入保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14503) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/import`
 * @更新时间 `2023-10-19 15:38:02`
 */
export type PolicyImportResponse = string

/**
 * 接口 [新增保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13114) 的 **请求类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/add`
 * @更新时间 `2023-10-19 15:38:10`
 */
export interface InfoAddRequest {
  /**
   * 付款id
   */
  paymentId: string
  /**
   * 保单id
   */
  policyId?: string
  /**
   * 付款保单id
   */
  paymentPolicyId?: string
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
   * 保单金额
   */
  policyAmount?: string
  /**
   * 保险起始日 : yyyy-MM-dd
   */
  insuranceStartDate?: string
  /**
   * 保险到期日 : yyyy-MM-dd
   */
  insuranceEndDate?: string
  /**
   * 是否续保 PolicyRenewInsuranceEnum
   */
  renewInsuranceFlag?: string
  /**
   * 标识信息
   */
  identificationInformation?: string
  /**
   * remark
   */
  remark?: string
  /**
   * files
   */
  files?: string
}

/**
 * 接口 [新增保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13114) 的 **返回类型**
 *
 * @分类 [保单管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2558)
 * @请求头 `POST /policy/info/add`
 * @更新时间 `2023-10-19 15:38:10`
 */
export type InfoAddResponse = number

/* prettier-ignore-end */
