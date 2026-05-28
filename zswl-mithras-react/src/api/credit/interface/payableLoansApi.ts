/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [征信报告-信贷记录明细表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38323) 的 **请求类型**
 *
 * @分类 [征信报告-信贷记录明细表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5468)
 * @请求头 `POST /credit/report/record/details/list`
 * @更新时间 `2025-11-28 15:50:08`
 */
export interface DetailsListRequest {
  /**
   * 征信报告基本表id
   */
  creditReportId: number
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
 * 接口 [征信报告-信贷记录明细表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38323) 的 **返回类型**
 *
 * @分类 [征信报告-信贷记录明细表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5468)
 * @请求头 `POST /credit/report/record/details/list`
 * @更新时间 `2025-11-28 15:50:08`
 */
export interface DetailsListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 查询编号
     */
    creditCode?: number
    /**
     * 征信报告基本表id
     */
    creditReportId?: number
    /**
     * 账户编号
     */
    accountNumber?: string
    /**
     * 债权机构
     */
    creditorInstitution?: string
    /**
     * 业务种类
     */
    businessType?: string
    /**
     * 接收日期
     */
    dateReceived?: string
    /**
     * 币种
     */
    currency?: string
    /**
     * 借款金额
     */
    loanAmount?: string
    /**
     * 余额
     */
    balance?: string
    /**
     * 关闭日期
     */
    closeDate?: string
    /**
     * 五级分类
     */
    fiveClassification?: string
    /**
     * 最近一次还款日期
     */
    lastRepaymentDate?: string
    /**
     * 最近一次还款总额
     */
    lastRepaymentAmount?: string
    /**
     * 最近一次还款形式
     */
    lastRepaymentType?: string
    /**
     * 逻辑删除，0-未删除，1-已删除
     */
    deleted?: number
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

/* prettier-ignore-end */
