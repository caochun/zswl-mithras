/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [替换车证↗](http://yapi.zswltec.com:3000/project/11/interface/api/2851) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/reUpload`
 * @更新时间 `2024-05-13 15:27:04`
 */
export interface VehicleReUploadRequest {
  /**
   * 租赁物id
   */
  leaseholdId: string
  /**
   * 操作类型
   */
  operateType?: string
  /**
   * 车证Id
   */
  vehicleId?: string
  /**
   * 文件列表
   */
  files: string
}

/**
 * 接口 [替换车证↗](http://yapi.zswltec.com:3000/project/11/interface/api/2851) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/reUpload`
 * @更新时间 `2024-05-13 15:27:04`
 */
export type VehicleReUploadResponse = {
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
  /**
   * 文件子类型
   */
  materialsSubType?: string
}[]

/**
 * 接口 [统计各状态车证数量↗](http://yapi.zswltec.com:3000/project/11/interface/api/2863) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/count`
 * @更新时间 `2024-06-28 10:16:09`
 */
export interface VehicleCountRequest {
  /**
   * 租赁物id
   */
  leaseholdId: number
  /**
   * 流程id
   */
  processInstanceId?: string
}

/**
 * 接口 [统计各状态车证数量↗](http://yapi.zswltec.com:3000/project/11/interface/api/2863) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/count`
 * @更新时间 `2024-06-28 10:16:09`
 */
export interface VehicleCountResponse {
  /**
   * 本次识别合计
   */
  total?: number
  /**
   * 识别成功张数
   */
  succeed?: number
  /**
   * 识别失败张数
   */
  fail?: number
  /**
   * 支持对内容进行锁定
   */
  canLock?: boolean
}

/**
 * 接口 [车证上传\/追加车证\/重新上传↗](http://yapi.zswltec.com:3000/project/11/interface/api/2839) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/upload`
 * @更新时间 `2024-06-27 20:06:09`
 */
export interface VehicleUploadRequest {
  /**
   * 租赁物id
   */
  leaseholdId: string
  /**
   * 操作类型
   */
  operateType?: string
  /**
   * 车证Id
   */
  vehicleId?: string
  /**
   * 文件列表
   */
  files: string
}

/**
 * 接口 [车证上传\/追加车证\/重新上传↗](http://yapi.zswltec.com:3000/project/11/interface/api/2839) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/upload`
 * @更新时间 `2024-06-27 20:06:09`
 */
export interface VehicleUploadResponse {
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
 * 接口 [车证下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/2869) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/exportExcel`
 * @更新时间 `2024-05-12 17:38:23`
 */
export interface VehicleExportExcelRequest {
  /**
   * id
   */
  vehicleIds?: number[]
  /**
   * 租赁物id
   */
  leaseItemInfoId?: number
  /**
   * 机动车所有人
   */
  vehicleRegistrationOwner?: string
  /**
   * 机动车登记编号
   */
  vehicleRegistrationNumber?: string
  /**
   * 制造厂名称
   */
  vehicleManufacturer?: string
  /**
   * 车辆识别代号/车架号
   */
  vehicleVin?: string
  /**
   * 车辆识别状态
   */
  status?: string
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
 * 接口 [车证下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/2869) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/exportExcel`
 * @更新时间 `2024-05-12 17:38:23`
 */
export interface VehicleExportExcelResponse {
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
 * 接口 [车证分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/2833) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/list`
 * @更新时间 `2024-07-01 18:43:42`
 */
export interface VehicleListRequest {
  /**
   * id
   */
  vehicleIds?: number[]
  /**
   * 租赁物id
   */
  leaseItemInfoId?: number
  /**
   * 机动车所有人
   */
  vehicleRegistrationOwner?: string
  /**
   * 机动车登记编号
   */
  vehicleRegistrationNumber?: string
  /**
   * 制造厂名称
   */
  vehicleManufacturer?: string
  /**
   * 车辆识别代号/车架号
   */
  vehicleVin?: string
  /**
   * 车辆识别状态
   */
  status?: string
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
 * 接口 [车证分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/2833) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/list`
 * @更新时间 `2024-07-01 18:43:42`
 */
export type VehicleListResponse = {
  /**
   * 车证id
   */
  id?: number
  /**
   * 机动车登记证书编号
   */
  registrationPageNo?: string
  /**
   * 机动车所有人
   */
  vehicleRegistrationOwner?: string
  /**
   * 机动车登记编号
   */
  vehicleRegistrationNumber?: string
  /**
   * 制造厂名称
   */
  vehicleManufacturer?: string
  /**
   * 车辆识别代号/车架号
   */
  vehicleVin?: string
  /**
   * 锁定内容不支持修改
   */
  locked?: boolean
  /**
   * 文件id
   */
  fileId?: number
  /**
   * 文件名
   */
  fileName?: string
  /**
   * 车证识别状态
   */
  status?: string
  /**
   * 变更记录
   */
  changeRecordRspList?: {
    /**
     * 文件id
     */
    fileId?: number
    /**
     * 文件名称
     */
    fileName?: string
    /**
     * 变更记录列表
     */
    changeRecordList?: string[]
  }[]
}[]

/**
 * 接口 [车证批量修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/2857) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/update`
 * @更新时间 `2024-07-01 18:43:42`
 */
export interface VehicleUpdateRequest {
  /**
   * 车证首页ids
   */
  ids: number[]
  /**
   * 机动车登记证书编号
   */
  registrationPageNo?: string
  /**
   * 机动车所有人
   */
  vehicleRegistrationOwner?: string
  /**
   * 机动车登记编号
   */
  vehicleRegistrationNumber?: string
  /**
   * 制造厂名称
   */
  vehicleManufacturer?: string
  /**
   * 车辆识别代号/车架号
   */
  vehicleVin?: string
  /**
   * 变更记录
   */
  changeRecordData?: {
    /**
     * 文件id
     */
    fileId?: number
    /**
     * 文件名称
     */
    fileName?: string
    /**
     * 变更记录列表
     */
    changeRecordList?: string[]
  }
}

/**
 * 接口 [车证批量修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/2857) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/update`
 * @更新时间 `2024-07-01 18:43:42`
 */
export interface VehicleUpdateResponse {
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
 * 接口 [车证批量删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/2845) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/delete`
 * @更新时间 `2024-07-01 18:43:42`
 */
export interface VehicleDeleteRequest {
  /**
   * 车证首页id
   */
  vehicleIds?: number[]
  /**
   * 车证变更记录文件id
   */
  changeRecordFileIds?: number[]
  /**
   * 操作类型
   */
  operateType?: string
  /**
   * 租赁物id
   */
  leaseItemInfoId: number
}

/**
 * 接口 [车证批量删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/2845) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/delete`
 * @更新时间 `2024-07-01 18:43:42`
 */
export interface VehicleDeleteResponse {
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
 * 接口 [车证解锁↗](http://yapi.zswltec.com:3000/project/11/interface/api/2881) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/unlock`
 * @更新时间 `2024-05-12 17:38:23`
 */
export interface VehicleUnlockRequest {
  /**
   * id
   */
  vehicleIds: number[]
  /**
   * 租赁物id
   */
  leaseItemInfoId?: number
}

/**
 * 接口 [车证解锁↗](http://yapi.zswltec.com:3000/project/11/interface/api/2881) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/unlock`
 * @更新时间 `2024-05-12 17:38:23`
 */
export interface VehicleUnlockResponse {
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
 * 接口 [车证锁定↗](http://yapi.zswltec.com:3000/project/11/interface/api/2875) 的 **请求类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/lock`
 * @更新时间 `2024-05-12 17:38:23`
 */
export interface VehicleLockRequest {
  /**
   * id
   */
  vehicleIds: number[]
  /**
   * 租赁物id
   */
  leaseItemInfoId?: number
}

/**
 * 接口 [车证锁定↗](http://yapi.zswltec.com:3000/project/11/interface/api/2875) 的 **返回类型**
 *
 * @分类 [租赁物车证清单-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_686)
 * @请求头 `POST /lease/vehicle/lock`
 * @更新时间 `2024-05-12 17:38:23`
 */
export interface VehicleLockResponse {
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
