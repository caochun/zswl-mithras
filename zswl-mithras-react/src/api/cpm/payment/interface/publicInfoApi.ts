/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [公开信息-修改表格内容↗](http://yapi.zswltec.com:3000/project/11/interface/api/21541) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/modify/table/content`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface TableContentRequest {
  /**
   * 付款申请ID
   */
  paymentId: number
  /**
   * 客户ID
   */
  clientId: number
  /**
   * 查询开始时间 format:yyyy-MM-dd
   */
  queryFrom: string
  /**
   * 查询结束时间 format:yyyy-MM-dd
   */
  queryTo: string
  /**
   * 表格内容
   */
  rowList?: {
    /**
     * 行ID，文件上传的MainId
     */
    id?: number
    /**
     * 标题
     */
    title?: string
    /**
     * 行Key PublicInfoRowKeyEnum#name
     */
    rowKey?: string
    /**
     * 文本描述
     */
    description?: string
    /**
     * 调查类型 InvestigationResultEnum#name
     */
    investigationType?: string
    /**
     * 调查说明
     */
    investigationExplain?: string
    /**
     * 结果-文件列表
     */
    resultFileList?: {
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
      /**
       * 上传人岗位
       */
      uploadByPostList?: string[]
    }[]
    /**
     * 项目经理-说明
     */
    projectManagerExplain?: string
    /**
     * 项目经理-文件列表
     */
    projectManagerFileList?: {
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
      /**
       * 上传人岗位
       */
      uploadByPostList?: string[]
    }[]
  }[]
}

/**
 * 接口 [公开信息-修改表格内容↗](http://yapi.zswltec.com:3000/project/11/interface/api/21541) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/modify/table/content`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface TableContentResponse {
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
 * 接口 [公开信息-删除指定区间表格↗](http://yapi.zswltec.com:3000/project/11/interface/api/21559) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/delete/interval/table`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface IntervalTableRequest {
  /**
   * 付款申请ID
   */
  paymentId: number
  /**
   * 客户ID
   */
  clientId: number
  /**
   * 查询开始时间 format:yyyy-MM-dd
   */
  queryFrom: string
  /**
   * 查询结束时间 format:yyyy-MM-dd
   */
  queryTo: string
}

/**
 * 接口 [公开信息-删除指定区间表格↗](http://yapi.zswltec.com:3000/project/11/interface/api/21559) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/delete/interval/table`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface IntervalTableResponse {
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
 * 接口 [公开信息-客户列表查询↗](http://yapi.zswltec.com:3000/project/11/interface/api/21553) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/client/list`
 * @更新时间 `2024-09-10 20:14:44`
 */
export interface ClientListRequest {
  /**
   * 付款申请ID
   */
  paymentId: number
}

/**
 * 接口 [公开信息-客户列表查询↗](http://yapi.zswltec.com:3000/project/11/interface/api/21553) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/client/list`
 * @更新时间 `2024-09-10 20:14:44`
 */
export type ClientListResponse = {
  /**
   * 客户ID
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 客户类型
   */
  clientType?: string
  /**
   * 是否存在必填未填
   */
  isExistRequiredNotFill?: boolean
  /**
   * 查询区间列表
   */
  queryIntervalList?: {
    /**
     * 是否存在必填未填
     */
    isExistRequiredNotFill?: boolean
    /**
     * 查询开始时间
     */
    queryFrom?: string
    /**
     * 查询结束时间
     */
    queryTo?: string
  }[]
}[]

/**
 * 接口 [公开信息-导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/21571) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/export`
 * @更新时间 `2024-09-10 19:36:40`
 */
export interface InfoExportRequest {
  /**
   * 付款申请ID
   */
  paymentId: number
}

/**
 * 接口 [公开信息-导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/21571) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/export`
 * @更新时间 `2024-09-10 19:36:40`
 */
export interface InfoExportResponse {
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
 * 接口 [公开信息-新建↗](http://yapi.zswltec.com:3000/project/11/interface/api/21565) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/create/interval/table`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface IntervalTableRequest {
  /**
   * 付款申请ID
   */
  paymentId: number
  /**
   * 客户ID
   */
  clientId: number
  /**
   * 查询开始时间 format:yyyy-MM-dd
   */
  queryFrom: string
  /**
   * 查询结束时间 format:yyyy-MM-dd
   */
  queryTo: string
}

/**
 * 接口 [公开信息-新建↗](http://yapi.zswltec.com:3000/project/11/interface/api/21565) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/create/interval/table`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface IntervalTableResponse {
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
 * 接口 [公开信息-查询指定区间表格↗](http://yapi.zswltec.com:3000/project/11/interface/api/21547) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/query/interval/table`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface IntervalTableRequest {
  /**
   * 付款申请ID
   */
  paymentId: number
  /**
   * 客户ID
   */
  clientId: number
  /**
   * 查询开始时间 format:yyyy-MM-dd
   */
  queryFrom: string
  /**
   * 查询结束时间 format:yyyy-MM-dd
   */
  queryTo: string
}

/**
 * 接口 [公开信息-查询指定区间表格↗](http://yapi.zswltec.com:3000/project/11/interface/api/21547) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/query/interval/table`
 * @更新时间 `2024-09-10 23:58:48`
 */
export interface IntervalTableResponse {
  /**
   * 表格详情-确认人ID
   */
  confirmBy?: number
  /**
   * 表格详情-确认人名称
   */
  confirmName?: string
  /**
   * 查询开始时间 format:yyyy-MM-dd
   */
  queryFrom?: string
  /**
   * 查询结束时间 format:yyyy-MM-dd
   */
  queryTo?: string
  /**
   * 担保人/承租人ID
   */
  clientId?: number
  /**
   * 担保人/承租人名称
   */
  clientName?: string
  /**
   * 确认时间 format:yyyy-MM-dd HH:mm:ss
   */
  confirmTime?: string
  /**
   * 表格内容
   */
  rowList?: {
    /**
     * 行ID，文件上传的MainId
     */
    id?: number
    /**
     * 标题
     */
    title?: string
    /**
     * 行Key PublicInfoRowKeyEnum#name
     */
    rowKey?: string
    /**
     * 文本描述
     */
    description?: string
    /**
     * 调查类型 InvestigationResultEnum#name
     */
    investigationType?: string
    /**
     * 调查说明
     */
    investigationExplain?: string
    /**
     * 结果-文件列表
     */
    resultFileList?: {
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
      /**
       * 上传人岗位
       */
      uploadByPostList?: string[]
    }[]
    /**
     * 项目经理-说明
     */
    projectManagerExplain?: string
    /**
     * 项目经理-文件列表
     */
    projectManagerFileList?: {
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
      /**
       * 上传人岗位
       */
      uploadByPostList?: string[]
    }[]
  }[]
}

/**
 * 接口 [公开信息-项目经理提交前校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/21535) 的 **请求类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/submit/check`
 * @更新时间 `2024-09-10 19:36:40`
 */
export interface SubmitCheckRequest {
  /**
   * 流程ID
   */
  processInstanceId: string
}

/**
 * 接口 [公开信息-项目经理提交前校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/21535) 的 **返回类型**
 *
 * @分类 [公开信息-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3122)
 * @请求头 `POST /public/info/submit/check`
 * @更新时间 `2024-09-10 19:36:40`
 */
export interface SubmitCheckResponse {
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
