/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [ftp主表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12669) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/list`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoListRequest {
  /**
   * 所属月份
   */
  month?: string
  /**
   * 创建人
   */
  createBy?: number
  /**
   * 流程状态
   */
  ftpProcessStatus?: string
  /**
   * 创建开始时间
   */
  createStartTime?: string
  /**
   * 创建结束时间
   */
  createEndTime?: string
  /**
   * 生效开始时间
   */
  effectStartTime?: string
  /**
   * 生效结束时间
   */
  effectEndTime?: string
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
 * 接口 [ftp主表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12669) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/list`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 所属月份
     */
    month?: string
    /**
     * 创建人
     */
    createByName?: string
    ftpProcessStatus?: string
    createTime?: string
    effectTime?: string
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
   * 其他携带参数(该参数为map)
   */
  others?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * Object
     */
    mapValue?: {}
  }
}

/**
 * 接口 [ftp文字描述信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12854) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/desclist`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoDesclistRequest {
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
 * 接口 [ftp文字描述信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12854) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/desclist`
 * @更新时间 `2023-05-29 15:19:40`
 */
export type InfoDesclistResponse = {
  /**
   * 描述类型
   */
  descType?: string
  /**
   * 描述文本内容
   */
  descContent?: string
  /**
   * id
   */
  id?: number
}[]

/**
 * 接口 [ftp文字描述信息更新↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12859) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/descmodify`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoDescmodifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 描述类型
   */
  descContent?: string
}

/**
 * 接口 [ftp文字描述信息更新↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12859) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/descmodify`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoDescmodifyResponse {}

/**
 * 接口 [ftp计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12749) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/calculate`
 * @更新时间 `2023-05-29 15:19:39`
 */
export interface InfoCalculateRequest {
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
 * 接口 [ftp计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12749) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/calculate`
 * @更新时间 `2023-05-29 15:19:39`
 */
export interface InfoCalculateResponse {}

/**
 * 接口 [提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12799) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/submit`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoSubmitRequest {
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
 * 接口 [提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12799) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/submit`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoSubmitResponse {}

/**
 * 接口 [新增ftp主表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12664) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/add`
 * @更新时间 `2023-05-29 15:19:39`
 */
export interface InfoAddRequest {
  /**
   * 所属月份
   */
  month: string
}

/**
 * 接口 [新增ftp主表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12664) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/add`
 * @更新时间 `2023-05-29 15:19:39`
 */
export type InfoAddResponse = number

/**
 * 接口 [流程详情页面内比对接口-季度指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12884) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/flow/detail/compare/quarterly`
 * @更新时间 `2023-05-29 15:19:41`
 */
export interface CompareQuarterlyRequest {
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
 * 接口 [流程详情页面内比对接口-季度指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12884) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/flow/detail/compare/quarterly`
 * @更新时间 `2023-05-29 15:19:41`
 */
export interface CompareQuarterlyResponse {}

/**
 * 接口 [流程详情页面内比对接口-月度指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12879) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/flow/detail/compare/monthly`
 * @更新时间 `2023-05-29 15:19:41`
 */
export interface CompareMonthlyRequest {
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
 * 接口 [流程详情页面内比对接口-月度指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12879) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/flow/detail/compare/monthly`
 * @更新时间 `2023-05-29 15:19:41`
 */
export interface CompareMonthlyResponse {}

/**
 * 接口 [测试版本比对↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12869) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/testCompare`
 * @更新时间 `2023-05-29 10:40:23`
 */
export interface InfoTestCompareRequest {
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
 * 接口 [测试版本比对↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12869) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/testCompare`
 * @更新时间 `2023-05-29 10:40:23`
 */
export interface InfoTestCompareResponse {
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
 * 接口 [测试生成版本↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12864) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/testProcessEnd`
 * @更新时间 `2023-05-29 10:40:23`
 */
export interface InfoTestProcessEndRequest {
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
 * 接口 [测试生成版本↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12864) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/testProcessEnd`
 * @更新时间 `2023-05-29 10:40:23`
 */
export interface InfoTestProcessEndResponse {}

/**
 * 接口 [版本日志↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12804) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/versions`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoVersionsRequest {
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
 * 接口 [版本日志↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12804) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/base/info/versions`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface InfoVersionsResponse {
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
    /**
     * 操作人id
     */
    operatorId?: number
    /**
     * 操作人名称
     */
    operatorName?: string
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
   * 其他携带参数(该参数为map)
   */
  others?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * Object
     */
    mapValue?: {}
  }
}

/**
 * 接口 [版本比对↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12874) 的 **请求类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/preVersion`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface FtpPreVersionRequest {
  /**
   * 版本id
   */
  id: number
}

/**
 * 接口 [版本比对↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12874) 的 **返回类型**
 *
 * @分类 [new-ftp-base-info-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2398)
 * @请求头 `POST /new/ftp/preVersion`
 * @更新时间 `2023-05-29 15:19:40`
 */
export interface FtpPreVersionResponse {
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

/* prettier-ignore-end */
