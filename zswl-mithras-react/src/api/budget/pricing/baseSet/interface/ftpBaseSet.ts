/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [ftp参数设定表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12774) 的 **请求类型**
 *
 * @分类 [ftp参数设定表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2443)
 * @请求头 `POST /new/ftp/parameter/setting/list`
 * @更新时间 `2023-05-24 09:44:11`
 */
export interface SettingListRequest {}

/**
 * 接口 [ftp参数设定表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12774) 的 **返回类型**
 *
 * @分类 [ftp参数设定表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2443)
 * @请求头 `POST /new/ftp/parameter/setting/list`
 * @更新时间 `2023-05-24 09:44:11`
 */
export interface SettingListResponse {
  key?: {
    /**
     * id
     */
    id?: number
    /**
     * 参数类别
     */
    category?: string
    /**
     * 分类中文名
     */
    categoryDisplay?: string
    /**
     * 参数名称
     */
    paramName?: string
    /**
     * 参数值
     */
    value?: number
  }[]
}

/**
 * 接口 [修改ftp参数设定表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12769) 的 **请求类型**
 *
 * @分类 [ftp参数设定表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2443)
 * @请求头 `POST /new/ftp/parameter/setting/modify`
 * @更新时间 `2023-05-24 09:44:11`
 */
export interface SettingModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 参数类别
   */
  category?: string
  /**
   * 分类中文名
   */
  categoryDisplay?: string
  /**
   * 参数名称
   */
  paramName?: string
  /**
   * 参数值
   */
  value?: number
}

/**
 * 接口 [修改ftp参数设定表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12769) 的 **返回类型**
 *
 * @分类 [ftp参数设定表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2443)
 * @请求头 `POST /new/ftp/parameter/setting/modify`
 * @更新时间 `2023-05-24 09:44:11`
 */
export type SettingModifyResponse = null

/* prettier-ignore-end */
