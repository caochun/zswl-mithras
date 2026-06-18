/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [变更流程-附加标记信息-修改↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12914) 的 **请求类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/modify`
 * @更新时间 `2023-06-02 16:14:37`
 */
export interface RemarkModifyRequest {
  id: number
  /**
   * 模块类型
   */
  moduleType: string
  /**
   * 变更流程标记信息类型
   */
  remarkType: string
  /**
   * 标记信息
   */
  remarkJson: {
    reason: string
    originalContent: string
    toBeContent: string
    createTime?: string
  }
  /**
   * 主表id
   */
  mainId: number
}

/**
 * 接口 [变更流程-附加标记信息-修改↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12914) 的 **返回类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/modify`
 * @更新时间 `2023-06-02 16:14:37`
 */
export interface RemarkModifyResponse {
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
 * 接口 [变更流程-附加标记信息-所有（包括历史）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12924) 的 **请求类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/all`
 * @更新时间 `2023-06-02 16:14:44`
 */
export interface RemarkAllRequest {
  /**
   * 模块类型
   */
  moduleType: string
  /**
   * 主表id
   */
  mainId: number
  /**
   * 标记类型
   */
  remarkType: string
}

/**
 * 接口 [变更流程-附加标记信息-所有（包括历史）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12924) 的 **返回类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/all`
 * @更新时间 `2023-06-02 16:14:44`
 */
export interface RemarkAllResponse {
  /**
   * 模块类型
   */
  moduleType?: string
  /**
   * 变更流程标记信息类型
   */
  remarkType: string
  /**
   * 标记信息列表
   */
  remarkJsonList: {
    reason: string
    originalContent: string
    toBeContent: string
    createTime?: string
  }[]
  /**
   * 主表id
   */
  mainId: number
}

/**
 * 接口 [变更流程-附加标记信息-新增↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12909) 的 **请求类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/add`
 * @更新时间 `2023-06-02 16:14:33`
 */
export interface RemarkAddRequest {
  /**
   * 模块类型
   */
  moduleType: string
  /**
   * 变更流程标记信息类型
   */
  remarkType: string
  /**
   * 标记信息
   */
  remarkJson: {
    reason: string
    originalContent: string
    toBeContent: string
    createTime?: string
  }
  /**
   * 主表id
   */
  mainId: number
}

/**
 * 接口 [变更流程-附加标记信息-新增↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12909) 的 **返回类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/add`
 * @更新时间 `2023-06-02 16:14:33`
 */
export interface RemarkAddResponse {
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
 * 接口 [变更流程-附加标记信息-详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12919) 的 **请求类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/detail`
 * @更新时间 `2023-06-02 16:14:40`
 */
export interface RemarkDetailRequest {
  /**
   * 模块类型
   */
  moduleType: string
  /**
   * 主表id
   */
  mainId: number
  /**
   * 标记类型
   */
  remarkType: string
}

/**
 * 接口 [变更流程-附加标记信息-详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12919) 的 **返回类型**
 *
 * @分类 [process-modify-remark-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2483)
 * @请求头 `POST /process/modify/remark/detail`
 * @更新时间 `2023-06-02 16:14:40`
 */
export interface RemarkDetailResponse {
  id?: number
  /**
   * 模块类型
   */
  moduleType?: string
  /**
   * 变更流程标记信息类型
   */
  remarkType: string
  /**
   * 标记信息
   */
  remarkJson: {
    reason: string
    originalContent: string
    toBeContent: string
    createTime?: string
  }
  /**
   * 主表id
   */
  mainId: number
}

/* prettier-ignore-end */
