/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改季度指导基础定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12709) 的 **请求类型**
 *
 * @分类 [new-ftp-quarterly-base-pricing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2413)
 * @请求头 `POST /new/ftp/quarterly/base/pricing/modify`
 * @更新时间 `2023-05-23 09:47:37`
 */
export interface PricingModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 利率值
   */
  value?: number
}

/**
 * 接口 [修改季度指导基础定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12709) 的 **返回类型**
 *
 * @分类 [new-ftp-quarterly-base-pricing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2413)
 * @请求头 `POST /new/ftp/quarterly/base/pricing/modify`
 * @更新时间 `2023-05-23 09:47:37`
 */
export interface PricingModifyResponse {}

/**
 * 接口 [季度指导基础定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12714) 的 **请求类型**
 *
 * @分类 [new-ftp-quarterly-base-pricing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2413)
 * @请求头 `POST /new/ftp/quarterly/base/pricing/detail`
 * @更新时间 `2023-05-23 09:47:37`
 */
export interface PricingDetailRequest {
  /**
   * 主表id
   */
  mainId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [季度指导基础定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12714) 的 **返回类型**
 *
 * @分类 [new-ftp-quarterly-base-pricing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2413)
 * @请求头 `POST /new/ftp/quarterly/base/pricing/detail`
 * @更新时间 `2023-05-23 09:47:37`
 */
export interface PricingDetailResponse {
  /**
   * 第一行 ,FtpValue
   */
  row1?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第二行 ,FtpValue
   */
  row2?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第三行 ,FtpValue
   */
  row3?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第四行 ,FtpValue
   */
  row4?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第五行 ,FtpValue
   */
  row5?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第六行 ,FtpValue
   */
  row6?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第七行 ,FtpValue
   */
  row7?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第八行 ,FtpValue
   */
  row8?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
  /**
   * 第九行 ,FtpValue
   */
  row9?: {
    /**
     * id
     */
    id?: number
    /**
     * ftp值
     */
    value?: number
  }[]
}

/* prettier-ignore-end */
