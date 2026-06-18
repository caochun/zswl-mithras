/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [FTP计息-基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12584) 的 **请求类型**
 *
 * @分类 [FTP计息相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2383)
 * @请求头 `POST /ftp/interest/baseinfo/get`
 * @更新时间 `2023-05-17 19:12:55`
 */
export interface BaseinfoGetRequest {
  /**
   * FTP计息记录id
   */
  ftpInterestId: number
}

/**
 * 接口 [FTP计息-基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12584) 的 **返回类型**
 *
 * @分类 [FTP计息相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2383)
 * @请求头 `POST /ftp/interest/baseinfo/get`
 * @更新时间 `2023-05-17 19:12:55`
 */
export interface BaseinfoGetResponse {
  /**
   * 借据编号
   */
  receiptCode?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 项目主办名称
   */
  sponsorUserName?: string
}

/**
 * 接口 [FTP计息-每日计息分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12589) 的 **请求类型**
 *
 * @分类 [FTP计息相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2383)
 * @请求头 `POST /ftp/interest/detail/pagelist`
 * @更新时间 `2023-05-20 10:40:41`
 */
export interface DetailPagelistRequest {
  /**
   * FTP计息记录id
   */
  ftpInterestId: number
  /**
   * FTP计息日期-起 yyyy-MM-dd
   */
  interestDateFrom?: string
  /**
   * FTP计息日期-止 yyyy-MM-dd
   */
  interestDateTo?: string
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
 * 接口 [FTP计息-每日计息分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12589) 的 **返回类型**
 *
 * @分类 [FTP计息相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2383)
 * @请求头 `POST /ftp/interest/detail/pagelist`
 * @更新时间 `2023-05-20 10:40:41`
 */
export interface DetailPagelistResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 日期（展示文本）
     */
    interestDateText?: string
    /**
     * 现金支出
     */
    cashOut?: number
    /**
     * 现金收入
     */
    cashIn?: number
    /**
     * 现金占用
     */
    cashOccupy?: number
    /**
     * 现金FTP
     */
    cashFtp?: string
    /**
     * 现金FTP日利率
     */
    cashFtpDay?: string
    /**
     * 现金计息
     */
    cashInterest?: number
    /**
     * 票据支出
     */
    billOut?: number
    /**
     * 票据收入
     */
    billIn?: number
    /**
     * 票据占用
     */
    billOccupy?: number
    /**
     * 票据FTP
     */
    billFtp?: string
    /**
     * 票据FTP日利率
     */
    billFtpDay?: string
    /**
     * 票据计息
     */
    billInterest?: number
    /**
     * 是否逾期
     */
    isOverdue?: number
    /**
     * 当年累计计息
     */
    totalInterestThisYear?: number
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
 * 接口 [FTP计息分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12594) 的 **请求类型**
 *
 * @分类 [FTP计息相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2383)
 * @请求头 `POST /ftp/interest/pagelist`
 * @更新时间 `2023-05-17 19:12:55`
 */
export interface InterestPagelistRequest {
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 借据编号
   */
  receiptCode?: string
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 项目主办id
   */
  sponsorUserId?: number
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
 * 接口 [FTP计息分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12594) 的 **返回类型**
 *
 * @分类 [FTP计息相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2383)
 * @请求头 `POST /ftp/interest/pagelist`
 * @更新时间 `2023-05-17 19:12:55`
 */
export interface InterestPagelistResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 累计计息
     */
    totalInterestAmount?: number
    /**
     * 更新日期
     */
    lastUpdateDate?: string
    /**
     * 业务部门名称
     */
    bizDeptName?: string
    /**
     * 项目主办名称
     */
    sponsorUserName?: string
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
