/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10712) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/download`
 * @更新时间 `2022-11-20 10:53:44`
 */
export interface FileDownloadRequest {
  /**
   * 文件
   */
  fileId: number
  /**
   * 模块主id
   */
  mainId: number
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsType: string
}

/**
 * 接口 [下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10712) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/download`
 * @更新时间 `2022-11-20 10:53:44`
 */
export interface FileDownloadResponse {}

/**
 * 接口 [批量下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11277) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/batch/download`
 * @更新时间 `2022-12-20 16:47:45`
 */
export interface BatchDownloadRequest {
  /**
   * 文件
   */
  fileId: number[]
  /**
   * 模块主id
   */
  mainId: number
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsType: string
}

/**
 * 接口 [批量下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11277) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/batch/download`
 * @更新时间 `2022-12-20 16:47:45`
 */
export interface BatchDownloadResponse {}

/**
 * 接口 [文件下载方式修改-批量下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11639) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `GET /file/batch/download`
 * @更新时间 `2023-02-07 15:06:02`
 */
export interface BatchDownloadRequest {
  /**
   * 文件
   */
  fileId: string
  /**
   * 模块主id
   */
  mainId: string
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsType?: string
  /**
   * 是否平铺压缩
   */
  colZipFlag?: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [文件下载方式修改-批量下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11639) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `GET /file/batch/download`
 * @更新时间 `2023-02-07 15:06:02`
 */
export interface BatchDownloadResponse {}

/**
 * 接口 [上传文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10708) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/upload`
 * @更新时间 `2023-01-10 10:29:14`
 */
export interface FileUploadRequest {
  /**
   * 文件
   */
  file: File
  /**
   * 模块主id
   */
  mainId: string
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsType: string
  /**
   * 文件子类型
   */
  materialsSubType?: string
}

/**
 * 接口 [上传文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10708) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/upload`
 * @更新时间 `2023-01-10 10:29:14`
 */
export interface FileUploadResponse {
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
 * 接口 [下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11637) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `GET /file/download`
 * @更新时间 `2023-02-07 14:37:55`
 */
export interface FileDownloadRequest {
  /**
   * 文件
   */
  fileId: string
  mainId: string
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsType?: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [下载报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11637) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `GET /file/download`
 * @更新时间 `2023-02-07 14:37:55`
 */
export interface FileDownloadResponse {
  /**
   * 文件地址
   */
  fileUrl?: string
  /**
   * 文件
   */
  id?: number
  /**
   * 归属id
   */
  belongId?: number
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 资料类型
   */
  materialsType?: string
  /**
   * 资料类型名称
   */
  materialsTypeName?: string
  /**
   * 资料子类型
   */
  materialSubType?: string
  /**
   * 资料子类型名称
   */
  materialSubTypeName?: string
  /**
   * oss上传文件名
   */
  ossFilename?: string
  /**
   * 附件名
   */
  filename?: string
  /**
   * 文件名后缀
   */
  suffix?: string
  /**
   * 文件路径
   */
  filePath?: string
  systemGenerate?: number
  /**
   * 上传时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 上传人id
   */
  createBy?: number
  /**
   * 上传人姓名
   */
  createByName?: string
  /**
   * 修改人id
   */
  updateBy?: number
  /**
   * 修改人姓名
   */
  updateByName?: string
}

/**
 * 接口 [删除文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10716) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/remove`
 * @更新时间 `2022-11-19 16:27:46`
 */
export interface FileRemoveRequest {
  /**
   * 文件
   */
  fileId: number
  /**
   * 模块主id
   */
  mainId: number
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
}

/**
 * 接口 [删除文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10716) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/remove`
 * @更新时间 `2022-11-19 16:27:46`
 */
export interface FileRemoveResponse {
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
 * 接口 [批量删除文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11274) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/batch/remove`
 * @更新时间 `2022-12-20 17:18:51`
 */
export interface BatchRemoveRequest {
  /**
   * 文件
   */
  fileIds: number[]
  /**
   * 模块主id
   */
  mainId: number
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
}

/**
 * 接口 [批量删除文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11274) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/batch/remove`
 * @更新时间 `2022-12-20 17:18:51`
 */
export interface BatchRemoveResponse {
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
 * 接口 [文件下载方式修改-批量下载报告-本地打包后下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11638) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `GET /file/downloads`
 * @更新时间 `2023-01-16 16:53:39`
 */
export interface FileDownloadsRequest {
  /**
   * 文件
   */
  fileId: string
  /**
   * 模块主id
   */
  mainId: string
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  materialsType: string
}

/**
 * 接口 [文件下载方式修改-批量下载报告-本地打包后下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11638) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `GET /file/downloads`
 * @更新时间 `2023-01-16 16:53:39`
 */
export interface FileDownloadsResponse {
  /**
   * 文件
   */
  id?: number
  /**
   * 归属id
   */
  belongId?: number
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 资料类型
   */
  materialsType?: string
  /**
   * 资料子类型
   */
  materialSubType?: string
  /**
   * oss上传文件名
   */
  ossFilename?: string
  /**
   * 附件名
   */
  filename?: string
  /**
   * 文件名后缀
   */
  suffix?: string
  /**
   * 文件路径
   */
  filePath?: string
  /**
   * 文件地址
   */
  fileUrl?: string
  systemGenerate?: number
  /**
   * 上传时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 上传人id
   */
  createBy?: number
  /**
   * 上传人姓名
   */
  createByName?: string
  /**
   * 修改人id
   */
  updateBy?: number
  /**
   * 修改人姓名
   */
  updateByName?: string
}

/**
 * 接口 [获取文件信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10720) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/list`
 * @更新时间 `2023-02-07 09:40:53`
 */
export interface FileListRequest {
  /**
   * 模块主id
   */
  mainId: number
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsTypes?: string[]
  /**
   * 各模块扩展参数
   */
  ext?: {
    KEY?: {}
  }
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
 * 接口 [获取文件信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10720) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/list`
 * @更新时间 `2023-02-07 09:40:53`
 */
export interface FileListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 文件
     */
    id?: number
    /**
     * 归属id
     */
    belongId?: number
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 资料类型
     */
    materialsType?: string
    /**
     * 资料类型名称
     */
    materialsTypeName?: string
    /**
     * 资料子类型
     */
    materialSubType?: string
    /**
     * 资料子类型名称
     */
    materialSubTypeName?: string
    /**
     * oss上传文件名
     */
    ossFilename?: string
    /**
     * 附件名
     */
    filename?: string
    /**
     * 文件名后缀
     */
    suffix?: string
    /**
     * 文件路径
     */
    filePath?: string
    systemGenerate?: number
    /**
     * 上传时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 上传人id
     */
    createBy?: number
    /**
     * 上传人姓名
     */
    createByName?: string
    /**
     * 修改人id
     */
    updateBy?: number
    /**
     * 修改人姓名
     */
    updateByName?: string
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
 * 接口 [获取文件分组信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11594) 的 **请求类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/list/group`
 * @更新时间 `2023-02-07 09:40:56`
 */
export interface ListGroupRequest {
  /**
   * 模块主id
   */
  mainId: number
  /**
   * 模块类型 BusinessModuleEnum
   */
  moduleType: string
  /**
   * 文件类型 MaterialsEnum
   */
  materialsTypes?: string[]
  /**
   * 各模块扩展参数
   */
  ext?: {
    KEY?: {}
  }
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
 * 接口 [获取文件分组信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11594) 的 **返回类型**
 *
 * @分类 [文本相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1882)
 * @请求头 `POST /file/list/group`
 * @更新时间 `2023-02-07 09:40:56`
 */
export type ListGroupResponse = {
  key?: string
  value?: {
    /**
     * 文件
     */
    id?: number
    /**
     * 归属id
     */
    belongId?: number
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 资料类型
     */
    materialsType?: string
    /**
     * 资料类型名称
     */
    materialsTypeName?: string
    /**
     * 资料子类型
     */
    materialSubType?: string
    /**
     * 资料子类型名称
     */
    materialSubTypeName?: string
    /**
     * oss上传文件名
     */
    ossFilename?: string
    /**
     * 附件名
     */
    filename?: string
    /**
     * 文件名后缀
     */
    suffix?: string
    /**
     * 文件路径
     */
    filePath?: string
    systemGenerate?: number
    /**
     * 上传时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 上传人id
     */
    createBy?: number
    /**
     * 上传人姓名
     */
    createByName?: string
    /**
     * 修改人id
     */
    updateBy?: number
    /**
     * 修改人姓名
     */
    updateByName?: string
  }[]
}[]

/* prettier-ignore-end */
