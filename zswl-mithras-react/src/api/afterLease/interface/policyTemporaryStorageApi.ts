/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [保单暂存表c列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14641) 的 **请求类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/list`
 * @更新时间 `2023-10-24 11:05:26`
 */
export interface TmpListRequest {
  contractId: number
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
 * 接口 [保单暂存表c列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14641) 的 **返回类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/list`
 * @更新时间 `2023-10-24 11:05:26`
 */
export interface TmpListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
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
     * 通知标识 0未通知， 1，已通知
     */
    noticeFlag?: number
    /**
     * 续保结果 0需要续保，1 已续保或不需续保
     */
    renewInsuranceResult?: number
    /**
     * 保险公司名称
     */
    insuranceCompany?: string
    /**
     * contract_code
     */
    contractCode?: string
    /**
     * 保单状态
     */
    policyStatus?: string
    /**
     * 标识信息
     */
    identificationInformation?: string
    /**
     * 是否自动推送，0 手动 1 自动
     */
    automatic?: number
    /**
     * 保单种类
     */
    policyType?: string
    /**
     * 是否续保
     */
    renewInsuranceFlag?: string
    /**
     * 备注
     */
    remark?: string
    createTime?: string
    createBy?: number
    createByName?: string
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
 * 接口 [保单暂存表c导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14749) 的 **请求类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/export`
 * @更新时间 `2023-10-24 11:05:26`
 */
export interface TmpExportRequest {
  /**
   * ids
   */
  ids?: number[]
}

/**
 * 接口 [保单暂存表c导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14749) 的 **返回类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/export`
 * @更新时间 `2023-10-24 11:05:26`
 */
export interface TmpExportResponse {
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
 * 接口 [修改保单暂存表c↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14647) 的 **请求类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/modify`
 * @更新时间 `2023-10-24 10:39:16`
 */
export interface TmpModifyRequest {
  /**
   * 主键id
   */
  id: number
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 保单编号
   */
  policyCode?: string
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
   * 通知标识 0未通知， 1，已通知
   */
  noticeFlag?: number
  /**
   * 续保结果 0需要续保，1 已续保或不需续保
   */
  renewInsuranceResult?: number
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * contract_code
   */
  contractCode?: string
  /**
   * 保单状态
   */
  policyStatus?: string
  /**
   * 标识信息
   */
  identificationInformation?: string
  /**
   * 是否自动推送，0 手动 1 自动
   */
  automatic?: number
  /**
   * 保单种类
   */
  policyType?: string
  /**
   * 是否续保
   */
  renewInsuranceFlag?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [修改保单暂存表c↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14647) 的 **返回类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/modify`
 * @更新时间 `2023-10-24 10:39:16`
 */
export interface TmpModifyResponse {
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
 * 接口 [删除保单暂存表c↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14635) 的 **请求类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/remove`
 * @更新时间 `2023-10-23 09:05:18`
 */
export interface TmpRemoveRequest {
  /**
   * ids
   */
  ids: number[]
}

/**
 * 接口 [删除保单暂存表c↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14635) 的 **返回类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/remove`
 * @更新时间 `2023-10-23 09:05:18`
 */
export interface TmpRemoveResponse {
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
 * 接口 [导入保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14653) 的 **请求类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/import`
 * @更新时间 `2023-10-23 13:55:40`
 */
export interface TmpImportRequest {
  /**
   * 保单文件
   */
  file: File
  /**
   * 合同id
   */
  contractId: string
}

/**
 * 接口 [导入保单信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14653) 的 **返回类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/import`
 * @更新时间 `2023-10-23 13:55:40`
 */
export type TmpImportResponse = string

/**
 * 接口 [新增保单暂存表c↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14629) 的 **请求类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/add`
 * @更新时间 `2023-10-24 11:05:26`
 */
export interface TmpAddRequest {
  /**
   * 合同id
   */
  contractId: number
  /**
   * 保单编号
   */
  policyCode: string
  /**
   * 保单金额
   */
  policyAmount: number
  /**
   * 保险起始日
   */
  insuranceStartDate: string
  /**
   * 保险到期日
   */
  insuranceEndDate: string
  /**
   * 通知标识 0未通知， 1，已通知
   */
  noticeFlag?: number
  /**
   * 续保结果 0需要续保，1 已续保或不需续保
   */
  renewInsuranceResult?: number
  /**
   * 保险公司名称
   */
  insuranceCompany?: string
  /**
   * contract_code
   */
  contractCode?: string
  /**
   * 保单状态
   */
  policyStatus?: string
  /**
   * 标识信息
   */
  identificationInformation?: string
  /**
   * 是否自动推送，0 手动 1 自动
   */
  automatic?: number
  /**
   * 保单种类
   */
  policyType?: string
  /**
   * 是否续保
   */
  renewInsuranceFlag?: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [新增保单暂存表c↗](http://api-dev.zswl.cn:3011/project/11/interface/api/14629) 的 **返回类型**
 *
 * @分类 [保单暂存表c-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_3012)
 * @请求头 `POST /policy/info/tmp/add`
 * @更新时间 `2023-10-24 11:05:26`
 */
export type TmpAddResponse = number

/* prettier-ignore-end */
