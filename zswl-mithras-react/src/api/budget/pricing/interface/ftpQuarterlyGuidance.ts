/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [季度指导模板下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11583) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/template/download`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface TemplateDownloadRequest {}

/**
 * 接口 [季度指导模板下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11583) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/template/download`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface TemplateDownloadResponse {}

/**
 * 接口 [excel导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11612) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/excel/export`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface ExcelExportRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [excel导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11612) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/excel/export`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface ExcelExportResponse {}

/**
 * 接口 [excel导入↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11584) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/excel/import`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface ExcelImportRequest {
  /**
   * (MultipartFile)
   */
  file: string
  /**
   * (Long)
   */
  guidanceId: string
}

/**
 * 接口 [excel导入↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11584) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/excel/import`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface ExcelImportResponse {}

/**
 * 接口 [季度指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11581) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/list`
 * @更新时间 `2023-01-11 17:35:05`
 */
export interface GuidanceListRequest {
  /**
   * 创建人id
   */
  createBy?: number
  /**
   * 年份
   */
  year?: number
  /**
   * 季度
   */
  quarter?: number
  /**
   * 流程状态
   */
  processStatus?: string
  /**
   * 创建日期from
   */
  createDateFrom?: string
  /**
   * 创建日期to
   */
  createDateTo?: string
  /**
   * 更新日期from
   */
  updateDateFrom?: string
  /**
   * 更新日期to
   */
  updateDateTo?: string
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
 * 接口 [季度指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11581) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/list`
 * @更新时间 `2023-01-11 17:35:05`
 */
export interface GuidanceListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 列表'时间'字段
     */
    timeDisplay?: string
    /**
     * 创建人Name
     */
    createByName?: string
    /**
     * 审批状态
     */
    processStatus?: string
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
 * 接口 [季度指导详情-sheet1列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11603) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/base`
 * @更新时间 `2023-01-11 17:35:05`
 */
export interface PricingBaseRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [季度指导详情-sheet1列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11603) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/base`
 * @更新时间 `2023-01-11 17:35:05`
 */
export type PricingBaseResponse = {
  /**
   * 所属指引id
   */
  guidanceId?: number
  /**
   * 孟月(FIRST_MONTH)、仲月(SECOND_MONTH)、季月(THIRD_MONTH)、均值(MEAN_VALUE)，见枚举monthType
   */
  monthType?: string
  /**
   * 国有(STATE_OWNED)、民营上市企业(PRIVATE_LISTED)、民营非上市企业(PRIVATE_NON_LISTED)、其他(OTHER)，见枚举enterpriseType
   */
  enterpriseType?: string
  /**
   * 1年期(ONE_YEAR)、1-3年(ONE_TO_THREE_YEARS)、3年以上(MORE_THAN_THREE_YEARS)，见枚举creditTerm
   */
  creditTerm?: string
  /**
   * 项目分类,枚举projectClassify，ENCOURAGEMENT(鼓励类),MODERATE_SUPPORT(适度支持类),CAUTIOUS(谨慎类),CONSTRUCTION_MACHINERY(工程机械类（厂商担保模式）),INTRA_GROUP_COLLABORATION(集团内协同业务)
   */
  projectClassify?: string
  /**
   * 利率值，展示需除以10000
   */
  percentValue?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [季度指导详情-sheet2-表1-右↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11605) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/cuntomer`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface PricingCuntomerRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [季度指导详情-sheet2-表1-右↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11605) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/cuntomer`
 * @更新时间 `2023-01-11 17:35:06`
 */
export type PricingCuntomerResponse = {
  /**
   * 所属指引id
   */
  guidanceId?: number
  /**
   * 国有、其他
   */
  enterpriseType?: string
  /**
   * 1年期、1-3年、3年以上
   */
  creditTerm?: string
  /**
   * 利率值
   */
  percentValue?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [季度指导详情-sheet2-表1-左↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11604) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/month`
 * @更新时间 `2023-01-11 17:35:05`
 */
export interface PricingMonthRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [季度指导详情-sheet2-表1-左↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11604) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/month`
 * @更新时间 `2023-01-11 17:35:05`
 */
export type PricingMonthResponse = {
  /**
   * 所属指引id
   */
  guidanceId?: number
  /**
   * 项目分类
   */
  projectClassify?: string
  /**
   * 孟月、仲月、季月、均值
   */
  monthType?: string
  /**
   * credit_term
   */
  creditTerm?: string
  /**
   * 利率值
   */
  percentValue?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [季度指导详情-sheet2-表2↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11606) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/enterprise`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface PricingEnterpriseRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [季度指导详情-sheet2-表2↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11606) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/detail/pricing/enterprise`
 * @更新时间 `2023-01-11 17:35:06`
 */
export type PricingEnterpriseResponse = {
  /**
   * 所属指引id
   */
  guidanceId?: number
  /**
   * 国有、其他
   */
  enterpriseType?: string
  /**
   * 项目分类
   */
  projectClassify?: string
  /**
   * credit_term
   */
  creditTerm?: string
  /**
   * 利率值
   */
  percentValue?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11585) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/submit`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface GuidanceSubmitRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11585) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/submit`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface GuidanceSubmitResponse {}

/**
 * 接口 [新增季度指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11580) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/add`
 * @更新时间 `2023-01-11 17:35:05`
 */
export interface GuidanceAddRequest {
  /**
   * 年度
   */
  year: number
  /**
   * 季度
   */
  quarter: number
}

/**
 * 接口 [新增季度指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11580) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/add`
 * @更新时间 `2023-01-11 17:35:05`
 */
export type GuidanceAddResponse = number

/**
 * 接口 [版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11614) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/compare/preVersion`
 * @更新时间 `2023-01-11 17:35:07`
 */
export interface ComparePreVersionRequest {
  /**
   * 版本id
   */
  id: number
}

/**
 * 接口 [版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11614) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/compare/preVersion`
 * @更新时间 `2023-01-11 17:35:07`
 */
export interface ComparePreVersionResponse {
  /**
   * 旧版本数据(该参数为map)
   */
  oldData?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * List
     */
    mapValue?: {}
  }
  /**
   * 新版本数据(该参数为map)
   */
  newData?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * List<Map<String, DiffValue>>
     */
    mapValue?: {}
  }
  /**
   * 模块change标志(该参数为map)
   */
  moduleChanged?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * Boolean
     */
    mapValue?: {}
  }
}

/**
 * 接口 [版本表列↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11613) 的 **请求类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/version/list`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface VersionListRequest {
  /**
   * 主数据ID
   */
  mainId?: number
  /**
   * 模块类型
   */
  module?: string
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
 * 接口 [版本表列↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11613) 的 **返回类型**
 *
 * @分类 [ftp-quarterly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2068)
 * @请求头 `POST /ftp/quarterly/guidance/version/list`
 * @更新时间 `2023-01-11 17:35:06`
 */
export interface VersionListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 主数据id
     */
    mainId?: number
    /**
     * 版本号
     */
    version?: string
    /**
     * 版本类型
     */
    type?: number
    /**
     * 是否可和上版本比较
     */
    canCompare?: number
    /**
     * 业务模块枚举
     */
    module?: string
    createTime?: string
    createBy?: number
    updateTime?: string
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
}

/* prettier-ignore-end */
