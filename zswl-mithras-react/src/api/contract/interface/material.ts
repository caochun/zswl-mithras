/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [合同文本管理-修改单个文件的签约方式↗](http://yapi.zswltec.com:3000/project/11/interface/api/26215) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/update/signing/way/single`
 * @更新时间 `2024-12-09 21:53:37`
 */
export interface SigningwaySingleRequest {
  /**
   * id
   */
  id: number
  /**
   * 文本签约方式
   */
  textSigningWay: string
}

/**
 * 接口 [合同文本管理-修改单个文件的签约方式↗](http://yapi.zswltec.com:3000/project/11/interface/api/26215) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/update/signing/way/single`
 * @更新时间 `2024-12-09 21:53:37`
 */
export interface SigningwaySingleResponse {
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
 * 接口 [合同文本管理-修改默认的签约方式↗](http://yapi.zswltec.com:3000/project/11/interface/api/26209) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/update/signing/way/default`
 * @更新时间 `2024-12-09 21:53:38`
 */
export interface SigningwayDefaultRequest {
  /**
   * 主列表id
   */
  mainId: number
  /**
   * 文本签约方式
   */
  textSigningWay: string
}

/**
 * 接口 [合同文本管理-修改默认的签约方式↗](http://yapi.zswltec.com:3000/project/11/interface/api/26209) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/update/signing/way/default`
 * @更新时间 `2024-12-09 21:53:38`
 */
export interface SigningwayDefaultResponse {
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
 * 接口 [合同文本管理-单个客户用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/26203) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/single/sign`
 * @更新时间 `2024-12-09 21:53:40`
 */
export interface ManagesingleSignRequest {
  /**
   * id
   */
  id: number
  /**
   * 签约方ID
   */
  signerId: number
}

/**
 * 接口 [合同文本管理-单个客户用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/26203) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/single/sign`
 * @更新时间 `2024-12-09 21:53:40`
 */
export interface ManagesingleSignResponse {
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
 * 接口 [合同文本管理-台账列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26191) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/list`
 * @更新时间 `2024-12-09 21:53:42`
 */
export interface TextmanageListRequest {
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目主办
   */
  projSponsorUserId?: number
  /**
   * 签约方式 SigningWayEnum#name
   */
  signingWay?: string
  /**
   * 推送时间开始 format: yyyy-MM-dd
   */
  pushTimeFrom?: string
  /**
   * 推送时间结束 format: yyyy-MM-dd
   */
  pushTimeTo?: string
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
 * 接口 [合同文本管理-台账列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26191) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/list`
 * @更新时间 `2024-12-09 21:53:42`
 */
export interface TextmanageListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目名称
     */
    projectName?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名字
     */
    clientName?: string
    /**
     * 项目主办id
     */
    projSponsorUserId?: number
    /**
     * 项目主办名字
     */
    projSponsorUserName?: string
    /**
     * 签约方式 SigningWayEnum#name
     */
    signingWay?: string
    /**
     * 推送时间开始 format: yyyy-MM-dd HH:mm:ss
     */
    pushTime?: string
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
  others?: {}
}

/**
 * 接口 [合同文本管理-合同文本下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/26185) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/download/all`
 * @更新时间 `2024-12-09 21:53:45`
 */
export interface ManagedownloadAllRequest {
  /**
   * 合同id
   */
  contractId: number
}

/**
 * 接口 [合同文本管理-合同文本下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/26185) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/download/all`
 * @更新时间 `2024-12-09 21:53:45`
 */
export interface ManagedownloadAllResponse {
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
 * 接口 [合同文本管理-合同签署照片和视频↗](http://yapi.zswltec.com:3000/project/11/interface/api/26269) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/sign/photos/and/videos`
 * @更新时间 `2024-12-09 21:53:34`
 */
export interface PhotosandVideosRequest {
  /**
   * 合同id
   */
  contractId: number
}

/**
 * 接口 [合同文本管理-合同签署照片和视频↗](http://yapi.zswltec.com:3000/project/11/interface/api/26269) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/sign/photos/and/videos`
 * @更新时间 `2024-12-09 21:53:34`
 */
export type PhotosandVideosResponse = {
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
   * 来源业务key
   */
  sourceBusinessKey?: string
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
  /**
   * 上传人岗位
   */
  uploadByPostList?: string[]
}[]

/**
 * 接口 [合同文本管理-已签约详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26173) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/signed/detail`
 * @更新时间 `2024-12-09 21:53:48`
 */
export interface ManagesignedDetailRequest {
  /**
   * 列表id
   */
  mainId: number
}

/**
 * 接口 [合同文本管理-已签约详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26173) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/signed/detail`
 * @更新时间 `2024-12-09 21:53:48`
 */
export type ManagesignedDetailResponse = {
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
   * 来源业务key
   */
  sourceBusinessKey?: string
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
  /**
   * 上传人岗位
   */
  uploadByPostList?: string[]
}[]

/**
 * 接口 [合同文本管理-待签约批量下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/26263) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/download/wait/sign`
 * @更新时间 `2024-12-09 21:53:36`
 */
export interface DownloadwaitSignRequest {
  /**
   * 合同id
   */
  contractId: number
  /**
   * 资料记录ID列表
   */
  fileRecordIds: number[]
}

/**
 * 接口 [合同文本管理-待签约批量下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/26263) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/download/wait/sign`
 * @更新时间 `2024-12-09 21:53:36`
 */
export interface DownloadwaitSignResponse {
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
 * 接口 [合同文本管理-批量用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/26197) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/batch/sign`
 * @更新时间 `2024-12-09 21:53:41`
 */
export interface ManagebatchSignRequest {
  /**
   * 相关材料ID集合
   */
  idList: number[]
}

/**
 * 接口 [合同文本管理-批量用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/26197) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/batch/sign`
 * @更新时间 `2024-12-09 21:53:41`
 */
export interface ManagebatchSignResponse {
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
 * 接口 [合同文本管理-未签约详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26179) 的 **请求类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/unSigned/detail`
 * @更新时间 `2024-12-09 21:53:46`
 */
export interface ManageunSignedDetailRequest {
  /**
   * 列表id
   */
  mainId: number
}

/**
 * 接口 [合同文本管理-未签约详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26179) 的 **返回类型**
 *
 * @分类 [合同文本管理API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3620)
 * @请求头 `POST /contract/text/manage/unSigned/detail`
 * @更新时间 `2024-12-09 21:53:46`
 */
export type ManageunSignedDetailResponse = {
  /**
   * 主键id
   */
  id?: number
  /**
   * 资料名称
   */
  materialName?: string
  /**
   * 文本签约方式
   */
  textSigningWay?: string
  /**
   * 文件id
   */
  fileId?: number
  /**
   * 文件类型
   */
  modelType?: string
  /**
   * 文本签约状态 ContractSignStatusEnum#name
   */
  textSigningStatus?: string
  /**
   * 推送时间
   */
  pushTime?: string
  /**
   * 签约方列表
   */
  signerList?: {
    /**
     * id
     */
    id?: number
    /**
     * 签约方id
     */
    signerId?: number
    /**
     * 签约方名称
     */
    signerName?: string
    /**
     * 签约方式
     */
    signingWay?: string
    /**
     * 签约状态
     */
    signingStatus?: string
    /**
     * 签约完成时间
     */
    signingCompleteTime?: string
    /**
     * 实名认证状态
     */
    realNameAuthStatus?: string
  }[]
  /**
   * 是否可批量用印
   */
  isBatchPrint?: boolean
}[]

/* prettier-ignore-end */
