/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [季度指导模板下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11600) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/template/download`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface TemplateDownloadRequest {}

/**
 * 接口 [季度指导模板下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11600) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/template/download`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface TemplateDownloadResponse {}

/**
 * 接口 [excel导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11609) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/excel/export`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface ExcelExportRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [excel导出↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11609) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/excel/export`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface ExcelExportResponse {}

/**
 * 接口 [excel导入↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11601) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/excel/import`
 * @更新时间 `2023-01-11 17:35:01`
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
 * 接口 [excel导入↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11601) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/excel/import`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface ExcelImportResponse {}

/**
 * 接口 [提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11602) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/submit`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface GuidanceSubmitRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11602) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/submit`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface GuidanceSubmitResponse {}

/**
 * 接口 [新增月度指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11595) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/add`
 * @更新时间 `2023-01-11 17:35:00`
 */
export interface GuidanceAddRequest {
  /**
   * 年度
   */
  year: number
  /**
   * 月度
   */
  month: number
}

/**
 * 接口 [新增月度指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11595) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/add`
 * @更新时间 `2023-01-11 17:35:00`
 */
export type GuidanceAddResponse = number

/**
 * 接口 [月度指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11596) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/list`
 * @更新时间 `2023-01-11 17:35:00`
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
   * 月份
   */
  month?: number
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
 * 接口 [月度指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11596) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/list`
 * @更新时间 `2023-01-11 17:35:00`
 */
export interface GuidanceListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
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
 * 接口 [月度指导详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11597) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/detail`
 * @更新时间 `2023-01-11 17:35:00`
 */
export interface GuidanceDetailRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [月度指导详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11597) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/detail`
 * @更新时间 `2023-01-11 17:35:00`
 */
export interface GuidanceDetailResponse {
  /**
   * 年度
   */
  year?: number
  /**
   * 月度
   */
  month?: number
  /**
   * 审批状态
   */
  processStatus?: string
  /**
   * 一年期ftp收益指导报价
   */
  oneYearEarningsGuidance?: number
  /**
   * 1-3年期ftp收益指导报价
   */
  oneToThreeEarningsGuidance?: number
  /**
   * 3年以上ftp收益指导报价
   */
  moreThanThreeEarningsGuidance?: number
  /**
   * 卖出价
   */
  sellingPrice?: number
  /**
   * 买入价
   */
  buyingPrice?: number
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [月度指导详情-定价指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11599) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/detail/pricing`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface DetailPricingRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [月度指导详情-定价指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11599) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/detail/pricing`
 * @更新时间 `2023-01-11 17:35:01`
 */
export type DetailPricingResponse = {
  /**
   * 所属指引id
   */
  guidanceId?: number
  /**
   * 企业类型
   */
  enterpriseType?: string
  /**
   * 期限
   */
  creditTerm?: string
  /**
   * 项目分类
   */
  projectClassify?: string
  /**
   * 值
   */
  value?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [月度指导详情-计价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11598) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/detail/valuation`
 * @更新时间 `2023-01-11 17:35:01`
 */
export interface DetailValuationRequest {
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [月度指导详情-计价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11598) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/detail/valuation`
 * @更新时间 `2023-01-11 17:35:01`
 */
export type DetailValuationResponse = {
  /**
   * 所属指引id
   */
  guidanceId?: number
  /**
   * 期限
   */
  crditTerm?: string
  /**
   * 融资成本
   */
  financingCost?: number
  /**
   * 担保成本
   */
  guaranteeCost?: number
  /**
   * 成本小计
   */
  subtotalCost?: number
  /**
   * 国股银票转贴现利率
   */
  discountRate?: number
  /**
   * 权重1
   */
  discountRateWeight?: number
  /**
   * 1年期shibor利率
   */
  shiborRate?: number
  /**
   * 权重2
   */
  shiborRateWeight?: number
  /**
   * 同期lpr利率
   */
  lprRate?: number
  /**
   * 权重3
   */
  lprRateWeight?: number
  /**
   * 融资成本趋势
   */
  financeCostTrends?: number
  /**
   * 权重4
   */
  financeCostTrendsWeight?: number
  /**
   * 小计
   */
  subtotalRate?: number
  /**
   * 调整后计价小计
   */
  subtotalAdjustmentValuation?: number
  /**
   * 鼓励介入类
   */
  encourageValuation?: number
  /**
   * 适度类
   */
  moderateSupportValuation?: number
  /**
   * 谨慎支持类
   */
  cautiousValuation?: number
  /**
   * 国有/上市公司
   */
  stateOwnListedValuation?: number
  /**
   * 其他类
   */
  otherValuation?: number
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11611) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/compare/preVersion`
 * @更新时间 `2023-01-11 17:35:02`
 */
export interface ComparePreVersionRequest {
  /**
   * 版本id
   */
  id: number
}

/**
 * 接口 [版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11611) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/compare/preVersion`
 * @更新时间 `2023-01-11 17:35:02`
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
 * 接口 [版本表列↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11610) 的 **请求类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/version/list`
 * @更新时间 `2023-01-11 17:35:01`
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
 * 接口 [版本表列↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11610) 的 **返回类型**
 *
 * @分类 [ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2066)
 * @请求头 `POST /ftp/monthly/guidance/version/list`
 * @更新时间 `2023-01-11 17:35:01`
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
