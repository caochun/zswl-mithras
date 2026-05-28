/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [ecl_业务配置表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37555) 的 **请求类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/list`
 * @更新时间 `2025-09-30 10:04:40`
 */
export interface ConfigListRequest {
  /**
   * 配置code EclConfigEnum
   */
  configCode?: string
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
 * 接口 [ecl_业务配置表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37555) 的 **返回类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/list`
 * @更新时间 `2025-09-30 10:04:40`
 */
export interface ConfigListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 配置模块
     */
    configModule?: string
    /**
     * 配置code EclConfigEnum
     */
    configCode?: string
    /**
     * 配置名称 EclConfigEnum
     */
    configName?: string
    /**
     * 配置详情
     */
    configValue?: string
    /**
     * 版本时间
     */
    versionTime?: string
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
 * 接口 [ecl_业务配置表版本列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37561) 的 **请求类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/version/list`
 * @更新时间 `2025-09-30 15:55:15`
 */
export interface VersionListRequest {
  /**
   * 配置code EclConfigEnum
   */
  configCode?: string
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
 * 接口 [ecl_业务配置表版本列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37561) 的 **返回类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/version/list`
 * @更新时间 `2025-09-30 15:55:15`
 */
export interface VersionListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 配置模块
     */
    configModule?: string
    /**
     * 配置code EclConfigEnum
     */
    configCode?: string
    /**
     * 配置名称 EclConfigEnum
     */
    configName?: string
    /**
     * 配置详情
     */
    configValue?: string
    /**
     * 版本时间
     */
    versionTime?: string
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
 * 接口 [ecl_业务配置表版本详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37609) 的 **请求类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/version/detail`
 * @更新时间 `2025-09-30 15:55:15`
 */
export interface VersionDetailRequest {
  id: number
}

/**
 * 接口 [ecl_业务配置表版本详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37609) 的 **返回类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/version/detail`
 * @更新时间 `2025-09-30 15:55:15`
 */
export interface VersionDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 配置模块
   */
  configModule?: string
  /**
   * 配置code EclConfigEnum
   */
  configCode?: string
  /**
   * 配置名称 EclConfigEnum
   */
  configName?: string
  /**
   * 配置详情
   */
  configValue?: string
  /**
   * 版本时间
   */
  versionTime?: string
}

/**
 * 接口 [ecl_业务配置表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37573) 的 **请求类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/detail`
 * @更新时间 `2025-10-13 10:47:00`
 */
export interface ConfigDetailRequest {
  id?: number
  /**
   * 配置code EclConfigEnum
   */
  configCode?: string
}

/**
 * 接口 [ecl_业务配置表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/37573) 的 **返回类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/detail`
 * @更新时间 `2025-10-13 10:47:00`
 */
export interface ConfigDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 配置模块
   */
  configModule?: string
  /**
   * 配置code EclConfigEnum
   */
  configCode?: string
  /**
   * 配置名称 EclConfigEnum
   */
  configName?: string
  /**
   * 配置详情
   */
  configValue?: string
  /**
   * 版本时间
   */
  versionTime?: string
}

/**
 * 接口 [修改ecl_业务配置表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37567) 的 **请求类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/modify`
 * @更新时间 `2025-09-30 10:04:40`
 */
export interface ConfigModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 配置模块
   */
  configModule?: string
  /**
   * 配置code
   */
  configCode?: string
  /**
   * 配置名称
   */
  configName?: string
  /**
   * 配置详情
   */
  configValue?: string
  /**
   * 版本时间
   */
  versionTime?: string
  /**
   * 配置版本号
   */
  configVersion?: string
}

/**
 * 接口 [修改ecl_业务配置表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37567) 的 **返回类型**
 *
 * @分类 [ecl_业务配置表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5342)
 * @请求头 `POST /ecl/business/config/modify`
 * @更新时间 `2025-09-30 10:04:40`
 */
export interface ConfigModifyResponse {
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

/* prettier-ignore-end */
