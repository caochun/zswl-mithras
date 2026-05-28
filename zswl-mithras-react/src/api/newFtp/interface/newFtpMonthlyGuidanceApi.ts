/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [ftp报价表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12734) 的 **请求类型**
 *
 * @分类 [new-ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2423)
 * @请求头 `POST /new/ftp/monthly/guidance/detail`
 * @更新时间 `2023-05-23 09:47:49`
 */
export interface GuidanceDetailRequest {
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
 * 接口 [ftp报价表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12734) 的 **返回类型**
 *
 * @分类 [new-ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2423)
 * @请求头 `POST /new/ftp/monthly/guidance/detail`
 * @更新时间 `2023-05-23 09:47:49`
 */
export interface GuidanceDetailResponse {
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
  /**
   * 第十行 ,FtpValue
   */
  row10?: {
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
   * 第十一行 ,FtpValue
   */
  row11?: {
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
   * 第十二行 ,FtpValue
   */
  row12?: {
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
   * 第十三行 ,FtpValue
   */
  row13?: {
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
   * 第十四行 ,FtpValue
   */
  row14?: {
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
   * 第十五行 ,FtpValue
   */
  row15?: {
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

/**
 * 接口 [修改ftp报价表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12729) 的 **请求类型**
 *
 * @分类 [new-ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2423)
 * @请求头 `POST /new/ftp/monthly/guidance/modify`
 * @更新时间 `2023-05-23 09:47:48`
 */
export interface GuidanceModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * ftp值
   */
  value?: number
}

/**
 * 接口 [修改ftp报价表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12729) 的 **返回类型**
 *
 * @分类 [new-ftp-monthly-guidance-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2423)
 * @请求头 `POST /new/ftp/monthly/guidance/modify`
 * @更新时间 `2023-05-23 09:47:48`
 */
export interface GuidanceModifyResponse {}

/* prettier-ignore-end */
