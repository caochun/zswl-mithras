/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [资金管理-融资管理-ftp收益表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/35971) 的 **请求类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/base/info/list`
 * @更新时间 `2025-07-17 17:06:25`
 */
export interface InfoListRequest {
  /**
   * 融资编号
   */
  financingCode?: string
  /**
   * 融资机构id
   */
  organizationId?: number
  /**
   * 资金主办
   */
  fundManagerId?: number
  /**
   * 融资id
   */
  financingIds?: number[]
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
 * 接口 [资金管理-融资管理-ftp收益表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/35971) 的 **返回类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/base/info/list`
 * @更新时间 `2025-07-17 17:06:25`
 */
export interface InfoListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 融资id
     */
    fundFinancingId?: number
    /**
     * 融资类型 直融 or 间融 financingtypeenum
     */
    financingType?: string
    /**
     * 融资编号
     */
    financingCode?: string
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
     * ftp收益率
     */
    ftpYieldRate?: number
    /**
     * 当年ftp收益率
     */
    ftpIncomeCurrentYear?: number
    /**
     * 当月ftp收益率
     */
    ftpIncomeCurrentMonth?: number
    /**
     * 资金经理id
     */
    fundManagerId?: number
    /**
     * 资金经理Name
     */
    fundManagerName?: string
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
 * 接口 [资金管理-融资管理-ftp收益表统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/35761) 的 **请求类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/base/info/count`
 * @更新时间 `2025-07-17 17:06:21`
 */
export interface InfoCountRequest {}

/**
 * 接口 [资金管理-融资管理-ftp收益表统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/35761) 的 **返回类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/base/info/count`
 * @更新时间 `2025-07-17 17:06:21`
 */
export interface InfoCountResponse {
  /**
   * 主键id
   */
  id?: number
  /**
   * 融资id
   */
  fundFinancingId?: number
  /**
   * 融资类型 直融 or 间融 financingtypeenum
   */
  financingType?: string
  /**
   * 融资编号
   */
  financingCode?: string
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
   * ftp收益率
   */
  ftpYieldRate?: number
  /**
   * 当年ftp收益率
   */
  ftpIncomeCurrentYear?: number
  /**
   * 当月ftp收益率
   */
  ftpIncomeCurrentMonth?: number
  /**
   * 资金经理id
   */
  fundManagerId?: number
  /**
   * 资金经理Name
   */
  fundManagerName?: string
}

/**
 * 接口 [资金管理-融资管理-ftp收益表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/35755) 的 **请求类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/base/info/detail`
 * @更新时间 `2025-07-17 14:57:02`
 */
export interface InfoDetailRequest {
  /**
   * 融资id
   */
  fundFinancingId: number
  /**
   * 融资类型
   */
  financingType?: string
}

/**
 * 接口 [资金管理-融资管理-ftp收益表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/35755) 的 **返回类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/base/info/detail`
 * @更新时间 `2025-07-17 14:57:02`
 */
export interface InfoDetailResponse {
  /**
   * 主键id
   */
  id?: number
  /**
   * 融资id
   */
  fundFinancingId?: number
  /**
   * 融资类型 直融 or 间融 financingtypeenum
   */
  financingType?: string
  /**
   * 融资编号
   */
  financingCode?: string
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
   * ftp收益率
   */
  ftpYieldRate?: number
  /**
   * 资金经理id
   */
  fundManagerId?: number
  /**
   * 资金经理Name
   */
  fundManagerName?: string
}

/**
 * 接口 [资金管理-融资管理-ftp收益记录获取融资机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/35965) 的 **请求类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/organization/list`
 * @更新时间 `2025-07-17 14:28:20`
 */
export interface OrganizationListRequest {
  /**
   * 融资类型
   */
  organizationName?: string
}

/**
 * 接口 [资金管理-融资管理-ftp收益记录获取融资机构↗](http://yapi.zswltec.com:3000/project/11/interface/api/35965) 的 **返回类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/organization/list`
 * @更新时间 `2025-07-17 14:28:20`
 */
export type OrganizationListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 机构名称
   */
  organizationName?: string
  /**
   * 机构编号
   */
  organizationCode?: string
}[]

/**
 * 接口 [资金管理-融资管理-ftp收益记录表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/35767) 的 **请求类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/detail/record/list`
 * @更新时间 `2025-07-17 14:14:18`
 */
export interface RecordListRequest {
  /**
   * 融资id
   */
  fundFinancingId: number
  /**
   * 融资类型
   */
  financingType?: string
}

/**
 * 接口 [资金管理-融资管理-ftp收益记录表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/35767) 的 **返回类型**
 *
 * @分类 [资金管理-融资管理-ftp收益表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5120)
 * @请求头 `POST /ftp/income/detail/record/list`
 * @更新时间 `2025-07-17 14:14:18`
 */
export type RecordListResponse = {
  /**
   * ftp基本信息 id
   */
  ftpIncomeId?: number
  /**
   * 产品名称
   */
  abbreviation?: string
  bodys?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 计息日期
     */
    interestDate?: string
    /**
     * 剩余本金
     */
    remainingPrincipal?: number
    /**
     * ftp收益率
     */
    ftpYieldRate?: number
    /**
     * ftp日收益率
     */
    ftpYieldRateDay?: number
    /**
     * ftp收益
     */
    ftpIncome?: number
    /**
     * ftp当年累计收益
     */
    ftpIncomeCurrentYear?: number
  }[]
}[]

/* prettier-ignore-end */
