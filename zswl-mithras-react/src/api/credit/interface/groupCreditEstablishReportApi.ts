/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [集团授信立项报告文件-下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10784) 的 **请求类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/download`
 * @更新时间 `2022-11-21 11:43:34`
 */
export interface ReportDownloadRequest {
  /**
   * 记录id
   */
  recordId: number
}

/**
 * 接口 [集团授信立项报告文件-下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10784) 的 **返回类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/download`
 * @更新时间 `2022-11-21 11:43:34`
 */
export interface ReportDownloadResponse {}

/**
 * 接口 [集团授信立项报告文件-上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10772) 的 **请求类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/upload`
 * @更新时间 `2022-11-21 11:43:23`
 */
export interface ReportUploadRequest {
  file: File
  /**
   * 集团授信立项id
   */
  groupCreditEstablishId: string
  /**
   * 文件类型：立项审批单(REPORT)，业务申请书(PROJ_INFORMATION)
   */
  materialsType: string
}

/**
 * 接口 [集团授信立项报告文件-上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10772) 的 **返回类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/upload`
 * @更新时间 `2022-11-21 11:43:23`
 */
export interface ReportUploadResponse {
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

/**
 * 接口 [集团授信立项报告文件-删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10780) 的 **请求类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/remove`
 * @更新时间 `2022-11-21 11:43:30`
 */
export interface ReportRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [集团授信立项报告文件-删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10780) 的 **返回类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/remove`
 * @更新时间 `2022-11-21 11:43:30`
 */
export interface ReportRemoveResponse {
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

/**
 * 接口 [集团授信立项报告文件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10776) 的 **请求类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/list`
 * @更新时间 `2022-11-21 11:43:27`
 */
export interface ReportListRequest {
  /**
   * groupCreditEstablishId
   */
  groupCreditEstablishId: number
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
}

/**
 * 接口 [集团授信立项报告文件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10776) 的 **返回类型**
 *
 * @分类 [集团授信立项报告文件-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1890)
 * @请求头 `POST /group/credit/establish/report/list`
 * @更新时间 `2022-11-21 11:43:27`
 */
export type ReportListResponse = {
  key?: string
  value?: {
    /**
     * 报告文件id
     */
    id?: number
    /**
     * 报告名称
     */
    typeName?: string
    /**
     * 材料类型
     */
    materialsType?: string
    /**
     * 文档名称
     */
    fileName?: string
    /**
     * 上传人
     */
    creator?: string
    /**
     * 上传时间
     */
    createTime?: string
    /**
     * 排序优先级
     */
    sort?: number
  }[]
}[]

/* prettier-ignore-end */
