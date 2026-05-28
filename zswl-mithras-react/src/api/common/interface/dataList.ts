/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [立项资料清单-批量下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/9780) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/proj/download`
 * @更新时间 `2022-10-12 14:50:53`
 */
export interface ProjDownloadRequest {
  /**
   * 记录id
   */
  ids: number[]
}

/**
 * 接口 [立项资料清单-批量下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/9780) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/proj/download`
 * @更新时间 `2022-10-12 14:50:53`
 */
export interface ProjDownloadResponse {}

/**
 * 接口 [资料清单-下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1360) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/download`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsDownloadRequest {
  /**
   * 记录id
   */
  ids: number[]
}

/**
 * 接口 [资料清单-下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1360) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/download`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsDownloadResponse {}

/**
 * 接口 [付款申请-资料清单列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/8324) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/payment/listOther`
 * @更新时间 `2022-09-23 17:39:13`
 */
export interface PaymentListOtherRequest {
  /**
   * 付款id
   */
  paymentId: number
}

/**
 * 接口 [付款申请-资料清单列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/8324) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/payment/listOther`
 * @更新时间 `2022-09-23 17:39:13`
 */
export type PaymentListOtherResponse = {
  /**
   * 分组类型
   */
  groupType?: string
  /**
   * 分组类型名称
   */
  groupTypeName?: string
  /**
   * 文件列表
   */
  fileDataList?: {
    /**
     * 文件id
     */
    fileId?: number
    /**
     * 资料类型
     */
    materialType?: string
    /**
     * 资料类型名称
     */
    materialTypeName?: string
    /**
     * 文件名称
     */
    fileName?: string
    /**
     * 排序字段
     */
    sort?: number
  }[]
  /**
   * 排序
   */
  sort?: number
}[]

/**
 * 接口 [合同管理-资料清单列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/5920) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/contract/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface ContractListRequest {
  /**
   * 主合同id
   */
  contractId: number
}

/**
 * 接口 [合同管理-资料清单列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/5920) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/contract/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export type ContractListResponse = {
  /**
   * 分组类型
   */
  groupType?: string
  /**
   * 分组类型名称
   */
  groupTypeName?: string
  /**
   * 文件列表
   */
  fileDataList?: {
    /**
     * 文件id
     */
    fileId?: number
    /**
     * 资料类型
     */
    materialType?: string
    /**
     * 资料类型名称
     */
    materialTypeName?: string
    /**
     * 文件名称
     */
    fileName?: string
    /**
     * 排序字段
     */
    sort?: number
  }[]
  /**
   * 排序
   */
  sort?: number
}[]

/**
 * 接口 [资料清单-上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1356) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/upload`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsUploadRequest {
  file: File
  belongId: string
  materialsType: string
  businessType: string
}

/**
 * 接口 [资料清单-上传↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1356) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/upload`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsUploadResponse {
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
 * 接口 [资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1364) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export type MaterialsListRequest = {
  /**
   * belongIds
   */
  belongIds: number[]
  /**
   * businessType
   */
  businessType: string
}[]

/**
 * 接口 [资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1364) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export type MaterialsListResponse = {
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 归属id
   */
  id?: number
  /**
   * 业务分类资料列表
   */
  businessMaterialList?: {
    /**
     * 资料类型
     */
    materialName?: string
    /**
     * 资料列表
     */
    materialList?: {
      /**
       * 附件名
       */
      filename?: string
      /**
       * 记录id
       */
      recordId?: number
    }[]
  }[]
}[]

/**
 * 接口 [资料清单-删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1368) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/remove`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsRemoveRequest {
  /**
   * id
   */
  ids: number[]
  /**
   * businessType
   */
  businessType: string
}

/**
 * 接口 [资料清单-删除↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1368) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/remove`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsRemoveResponse {
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
 * 接口 [资料清单预览判断接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6300) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/preview`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsPreviewRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [资料清单预览判断接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6300) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/preview`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface MaterialsPreviewResponse {
  /**
   * 文件预览类型
   */
  previewType?: string
  /**
   * 图片url
   */
  url?: string
}

/**
 * 接口 [集团授信立项资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10852) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/group/credit/establish/list`
 * @更新时间 `2022-11-21 11:48:42`
 */
export interface EstablishListRequest {
  /**
   * groupCreditEstablishId
   */
  groupCreditEstablishId: number
}

/**
 * 接口 [集团授信立项资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10852) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/group/credit/establish/list`
 * @更新时间 `2022-11-21 11:48:42`
 */
export type EstablishListResponse = {
  /**
   * 客户名
   */
  name?: string
  /**
   * 客户类型
   */
  clientType?: string
  /**
   * 展示类型名
   */
  clientTypeName?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 归属id
   */
  id?: number
  /**
   * 业务分类资料列表
   */
  businessMaterialList?: {
    /**
     * 资料类型
     */
    materialName?: string
    /**
     * 资料列表
     */
    materialList?: {
      /**
       * 附件名
       */
      filename?: string
      /**
       * 记录id
       */
      recordId?: number
    }[]
  }[]
}[]

/**
 * 接口 [集团授信评审资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10856) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/group/credit/review/list`
 * @更新时间 `2022-11-21 11:48:45`
 */
export interface ReviewListRequest {
  /**
   * groupCreditReviewId
   */
  groupCreditReviewId: number
}

/**
 * 接口 [项目评审资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2020) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/proj/review/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface ReviewListRequest {
  /**
   * projReviewId
   */
  projReviewId: number
}

/**
 * 接口 [项目评审资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2020) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/proj/review/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export type ReviewListResponse = {
  /**
   * 客户名
   */
  name?: string
  /**
   * 客户类型
   */
  clientType?: string
  /**
   * 展示类型名
   */
  clientTypeName?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 归属id
   */
  id?: number
  /**
   * 业务分类资料列表
   */
  businessMaterialList?: {
    /**
     * 资料类型
     */
    materialName?: string
    /**
     * 资料列表
     */
    materialList?: {
      /**
       * 附件名
       */
      filename?: string
      /**
       * 记录id
       */
      recordId?: number
    }[]
  }[]
}[]

/**
 * 接口 [项目资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1376) 的 **请求类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/proj/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export interface ProjListRequest {
  /**
   * projId
   */
  projId: number
}

/**
 * 接口 [项目资料清单-列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1376) 的 **返回类型**
 *
 * @分类 [资料清单-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_290)
 * @请求头 `POST /materials/proj/list`
 * @更新时间 `2022-09-08 19:18:24`
 */
export type ProjListResponse = {
  /**
   * 客户名
   */
  name?: string
  /**
   * 客户类型
   */
  clientType?: string
  /**
   * 展示类型名
   */
  clientTypeName?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 归属id
   */
  id?: number
  /**
   * 业务分类资料列表
   */
  businessMaterialList?: {
    /**
     * 资料类型
     */
    materialName?: string
    /**
     * 资料列表
     */
    materialList?: {
      /**
       * 附件名
       */
      filename?: string
      /**
       * 记录id
       */
      recordId?: number
    }[]
  }[]
}[]

/* prettier-ignore-end */
