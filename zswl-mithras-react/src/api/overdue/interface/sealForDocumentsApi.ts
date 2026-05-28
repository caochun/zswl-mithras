/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [文书用印列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25429) 的 **请求类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/pageList`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingPageListRequest {
  /**
   * 用印类型
   */
  type?: string
  /**
   * 申请人
   */
  applicant?: number
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
 * 接口 [文书用印列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/25429) 的 **返回类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/pageList`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingPageListResponse {
  /**
   * 查询集合
   */
  list?: {
    id?: number
    code?: string
    type?: string
    reason?: string
    processStatus?: string
    createTime?: string
    createBy?: number
    createByName?: string
    processId?: string
    applyName?: string
    applyDeptName?: string
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
 * 接口 [文书用印删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/27187) 的 **请求类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/remove`
 * @更新时间 `2025-02-20 15:45:36`
 */
export interface PrintingRemoveRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [文书用印删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/27187) 的 **返回类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/remove`
 * @更新时间 `2025-02-20 15:45:36`
 */
export interface PrintingRemoveResponse {
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
 * 接口 [文书用印提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/25417) 的 **请求类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/submit`
 * @更新时间 `2024-11-04 10:50:33`
 */
export interface PrintingSubmitRequest {
  /**
   * 主键id
   */
  id?: number
}

/**
 * 接口 [文书用印提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/25417) 的 **返回类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/submit`
 * @更新时间 `2024-11-04 10:50:33`
 */
export interface PrintingSubmitResponse {
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
 * 接口 [文书用印新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/25423) 的 **请求类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/add`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingAddRequest {
  /**
   * 文书用印类型
   */
  type: string
  /**
   * 文书用印原因
   */
  reason?: string
}

/**
 * 接口 [文书用印新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/25423) 的 **返回类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/add`
 * @更新时间 `2025-02-20 15:43:52`
 */
export type PrintingAddResponse = number

/**
 * 接口 [文书用印编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/25705) 的 **请求类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/save`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingSaveRequest {
  /**
   * 文书用印类型
   */
  type?: string
  /**
   * 文书用印原因
   */
  reason?: string
  /**
   * 申请人
   */
  applyName?: string
  /**
   * 申请部门
   */
  applyDeptName?: string
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [文书用印编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/25705) 的 **返回类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/save`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingSaveResponse {
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
 * 接口 [文书用印详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25411) 的 **请求类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/detail`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingDetailRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [文书用印详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/25411) 的 **返回类型**
 *
 * @分类 [文书用印↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3530)
 * @请求头 `POST /printing/detail`
 * @更新时间 `2025-02-20 15:43:52`
 */
export interface PrintingDetailResponse {
  /**
   * 文书用印类型
   */
  type?: string
  /**
   * 文书用印原因
   */
  reason?: string
  /**
   * 申请人
   */
  applyName?: string
  /**
   * 申请部门
   */
  applyDeptName?: string
  /**
   * id
   */
  id?: number
}

/* prettier-ignore-end */
