/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [关联评估机构信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/10819) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/relation`
 * @更新时间 `2024-08-13 18:22:03`
 */
export interface AppraisalRelationRequest {
  /**
   * 租赁物id
   */
  leaseItemId: number
  /**
   * 列表信息
   */
  relationList?: {
    /**
     * 评估机构id
     */
    companyId: number
    /**
     * 用途
     */
    purpose?: string
    /**
     * 是否被选中
     */
    selectType?: string
  }[]
}

/**
 * 接口 [关联评估机构信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/10819) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/relation`
 * @更新时间 `2024-08-13 18:22:03`
 */
export interface AppraisalRelationResponse {
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
 * 接口 [新增评估机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/10801) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/add`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface AppraisalAddRequest {
  /**
   * 统一社会信用代码
   */
  creditCode: string
}

/**
 * 接口 [新增评估机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/10801) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/add`
 * @更新时间 `2024-08-12 18:00:36`
 */
export type AppraisalAddResponse = number

/**
 * 接口 [更新评估机构最新信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/10807) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/lasted`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface AppraisalLastedRequest {
  /**
   * 统一社会信用代码
   */
  creditCode: string
}

/**
 * 接口 [更新评估机构最新信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/10807) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/lasted`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface AppraisalLastedResponse {
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
 * 接口 [模糊搜索评估机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/10795) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/queryCompany`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface AppraisalQueryCompanyRequest {
  /**
   * 评估机构名称
   */
  companyName?: string
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
 * 接口 [模糊搜索评估机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/10795) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/queryCompany`
 * @更新时间 `2024-08-12 18:00:36`
 */
export type AppraisalQueryCompanyResponse = {
  /**
   * 公司名称
   */
  companyName?: string
  /**
   * 统一社会信用代码
   */
  creditCode?: string
}[]

/**
 * 接口 [租赁物内评估机构列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/10783) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/leaseItem/list`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface LeaseItemListRequest {
  /**
   * 租赁物id
   */
  leaseItemId: number
}

/**
 * 接口 [租赁物内评估机构列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/10783) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/leaseItem/list`
 * @更新时间 `2024-08-12 18:00:36`
 */
export type LeaseItemListResponse = {
  /**
   * 评估机构id
   */
  companyId?: number
  /**
   * 评估机构名称
   */
  companyName?: string
  /**
   * 用途
   */
  purpose?: string
  /**
   * 是否被选中
   */
  selectType?: string
}[]

/**
 * 接口 [获取系统中所有评估机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/10789) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/company/list`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface CompanyListRequest {
  /**
   * 评估机构名称
   */
  companyName?: string
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
 * 接口 [获取系统中所有评估机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/10789) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/company/list`
 * @更新时间 `2024-08-12 18:00:36`
 */
export interface CompanyListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 评估机构id
     */
    companyId?: number
    /**
     * 评估机构名称
     */
    companyName?: string
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
 * 接口 [评估机构信息详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/10813) 的 **请求类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/detail`
 * @更新时间 `2024-08-13 15:38:34`
 */
export interface AppraisalDetailRequest {
  /**
   * 评估机构id
   */
  companyId?: number
}

/**
 * 接口 [评估机构信息详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/10813) 的 **返回类型**
 *
 * @分类 [租赁物-评估机构接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2474)
 * @请求头 `POST /ledger/appraisal/detail`
 * @更新时间 `2024-08-13 15:38:34`
 */
export interface AppraisalDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 评估机构id
   */
  appraisalCompanyId?: number
  /**
   * 评估机构
   */
  appraisalCompanyName?: string
  /**
   * 统一社会信用代码
   */
  creditCode?: string
  /**
   * 营业许可证到期日
   */
  bizLicenseEndDate?: string
  /**
   * 营业许可证是否为长期
   */
  bizLicenceLongTerm?: boolean
  /**
   * 业务范围
   */
  bizScope?: string
  /**
   * 成立日期
   */
  establishDate?: string
  /**
   * 用途
   */
  purpose?: string
  /**
   * 是否被选中
   */
  selected?: string
}

/* prettier-ignore-end */
