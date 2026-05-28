/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [文件下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11593) 的 **请求类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/download`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileDownloadRequest {
  /**
   * id ,Long
   */
  ids: number[]
}

/**
 * 接口 [文件下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11593) 的 **返回类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/download`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileDownloadResponse {}

/**
 * 接口 [文件上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11591) 的 **请求类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/upload`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileUploadRequest {
  /**
   * (MultipartFile)
   */
  file: string
  /**
   * FTP_QUARTERLY_GUIDANCE(季度)、FTP_MONTHLY_GUIDANCE(月度)
   */
  bizType: string
  /**
   * 所属的指导id
   */
  belongId: string
}

/**
 * 接口 [文件上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11591) 的 **返回类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/upload`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileUploadResponse {}

/**
 * 接口 [文件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11590) 的 **请求类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/list`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileListRequest {
  /**
   * FTP_QUARTERLY_GUIDANCE(季度)、FTP_MONTHLY_GUIDANCE(月度)
   */
  bizType: string
  /**
   * 指导id
   */
  id: number
}

/**
 * 接口 [文件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11590) 的 **返回类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/list`
 * @更新时间 `2023-01-10 19:54:25`
 */
export type FileListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 文件名
   */
  fileName?: string
}[]

/**
 * 接口 [文件删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11592) 的 **请求类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/remove`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileRemoveRequest {
  /**
   * id ,Long
   */
  ids: number[]
}

/**
 * 接口 [文件删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11592) 的 **返回类型**
 *
 * @分类 [ftp-materials-file-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2064)
 * @请求头 `POST /ftp/materials/file/remove`
 * @更新时间 `2023-01-10 19:54:25`
 */
export interface FileRemoveResponse {}

/* prettier-ignore-end */
