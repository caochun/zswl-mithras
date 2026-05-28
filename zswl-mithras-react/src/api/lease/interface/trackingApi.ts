/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [\/trackEvent\/testEffect↗](http://yapi.zswltec.com:3000/project/11/interface/api/2083) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/testEffect`
 * @更新时间 `2024-04-23 11:31:03`
 */
export interface TrackEventTestEffectRequest {}

/**
 * 接口 [\/trackEvent\/testEffect↗](http://yapi.zswltec.com:3000/project/11/interface/api/2083) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/testEffect`
 * @更新时间 `2024-04-23 11:31:03`
 */
export interface TrackEventTestEffectResponse {
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
 * 接口 [合同编号下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/2077) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/contractCodeList`
 * @更新时间 `2024-04-23 11:31:03`
 */
export interface TrackEventContractCodeListRequest {}

/**
 * 接口 [合同编号下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/2077) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/contractCodeList`
 * @更新时间 `2024-04-23 11:31:03`
 */
export type TrackEventContractCodeListResponse = string[]

/**
 * 接口 [处理人下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/1993) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/queryProcessor`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventQueryProcessorRequest {}

/**
 * 接口 [处理人下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/1993) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/queryProcessor`
 * @更新时间 `2024-04-26 13:33:33`
 */
export type TrackEventQueryProcessorResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 用户名称
   */
  name?: string
  /**
   * 所属机构
   */
  orgs?: string
}[]

/**
 * 接口 [批量导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/2119) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/download`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventDownloadRequest {
  /**
   * 记录ID集合
   */
  ids?: number[]
  /**
   * 任务名称
   */
  taskName?: string
  /**
   * 任务类型 枚举-TrackTaskTypeEnum
   */
  taskType?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 任务状态
   */
  taskStatus?: boolean
  /**
   * 提出人
   */
  createBy?: number
  /**
   * 处理人
   */
  processorId?: number
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
 * 接口 [批量导出↗](http://yapi.zswltec.com:3000/project/11/interface/api/2119) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/download`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventDownloadResponse {}

/**
 * 接口 [项目名称下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/2113) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/projNameList`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventProjNameListRequest {}

/**
 * 接口 [项目名称下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/2113) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/projNameList`
 * @更新时间 `2024-04-26 13:33:33`
 */
export type TrackEventProjNameListResponse = string[]

/**
 * 接口 [信息回显↗](http://yapi.zswltec.com:3000/project/11/interface/api/1909) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/contractInfo`
 * @更新时间 `2024-04-22 09:35:41`
 */
export interface TrackEventContractInfoRequest {
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 业务来源 -TrackTaskBizSourceEnum
   */
  bizSource?: string
}

/**
 * 接口 [信息回显↗](http://yapi.zswltec.com:3000/project/11/interface/api/1909) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/contractInfo`
 * @更新时间 `2024-04-22 09:35:41`
 */
export interface TrackEventContractInfoResponse {
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务来源 -TrackTaskBizSourceEnum
   */
  bizSource?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 业务类型。租赁、保理、转租赁
   */
  bizType?: string
  /**
   * 租赁类型。直租、回租、经营性租赁
   */
  leaseType?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户Name
   */
  clientName?: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify?: string
  /**
   * 项目主办用户id
   */
  projSponsorUserId?: number
  /**
   * 项目主办用户名称
   */
  projSponsorUserName?: string
  /**
   * 项目协办方用户id列表
   */
  projCosponsorUserIds?: number[]
  /**
   * 项目协办方用户名称列表
   */
  projCosponsorUserNames?: string[]
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 业务部门负责人id
   */
  bizDeptLeaderId?: number
  /**
   * 业务部门负责人名称
   */
  bizDeptLeaderName?: string
}

/**
 * 接口 [关闭任务↗](http://yapi.zswltec.com:3000/project/11/interface/api/1987) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/close`
 * @更新时间 `2024-04-22 09:35:41`
 */
export interface TrackEventCloseRequest {
  id: string
}

/**
 * 接口 [关闭任务↗](http://yapi.zswltec.com:3000/project/11/interface/api/1987) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/close`
 * @更新时间 `2024-04-22 09:35:41`
 */
export type TrackEventCloseResponse = boolean

/**
 * 接口 [跟踪事项列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/1915) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/list`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventListRequest {
  /**
   * 任务名称
   */
  taskName?: string
  /**
   * 任务类型 枚举-TrackTaskTypeEnum
   */
  taskType?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 任务状态
   */
  taskStatus?: boolean
  /**
   * 提出人
   */
  createBy?: number
  /**
   * 处理人
   */
  processorId?: number
  /**
   * 业务来源
   */
  bizSource?: string
  /**
   * 业务id
   */
  bizId?: number
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
 * 接口 [跟踪事项列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/1915) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/list`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 跟踪任务id
     */
    id?: number
    /**
     * 任务名称
     */
    taskName?: string
    /**
     * 任务类型 枚举-TrackTaskTypeEnum
     */
    taskType?: string
    /**
     * 提出人
     */
    createBy?: number
    /**
     * 提出人名称
     */
    createByName?: string
    /**
     * 计划日期
     */
    planTime?: string
    /**
     * 起租后X自然日
     */
    startRentAfterDay?: number
    /**
     * 处理人id
     */
    processorId?: number
    /**
     * 处理人名称
     */
    processor?: string
    /**
     * 提醒频率 枚举-TrackFrequencyEnum
     */
    remindFrequency?: string
    /**
     * 任务内容
     */
    taskContent?: string
    /**
     * 任务状态
     */
    taskStatus?: boolean
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 客户id
     */
    clientName?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 项目编号
     */
    projCode?: string
    /**
     * 创建时间
     */
    createTime?: string
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
  others?: {
    KEY?: {}
  }
}

/**
 * 接口 [跟踪事项新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/1921) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/add`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventAddRequest {
  /**
   * 任务名称
   */
  taskName: string
  /**
   * 任务类型 枚举-TrackTaskTypeEnum
   */
  taskType: string
  /**
   * 提出人
   */
  createBy?: number
  /**
   * 计划日期
   */
  planTime?: string
  /**
   * 起租后X自然日
   */
  startRentAfterDay?: number
  /**
   * 处理人id
   */
  processorId: number
  /**
   * 处理人岗位
   */
  processorDept?: string
  /**
   * 提醒频率 枚举-TrackFrequencyEnum
   */
  remindFrequency: string
  /**
   * 任务内容
   */
  taskContent: string
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务来源
   */
  bizSource?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 是否来自台账
   */
  isLedger?: boolean
}

/**
 * 接口 [跟踪事项新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/1921) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/add`
 * @更新时间 `2024-04-26 13:33:33`
 */
export type TrackEventAddResponse = boolean

/**
 * 接口 [跟踪事项编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/1927) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/update`
 * @更新时间 `2024-04-22 09:35:41`
 */
export interface TrackEventUpdateRequest {
  /**
   * 跟踪任务id
   */
  id?: number
  /**
   * 任务名称
   */
  taskName: string
  /**
   * 任务类型 枚举-TrackTaskTypeEnum
   */
  taskType: string
  /**
   * 提出人
   */
  createBy?: number
  /**
   * 计划日期
   */
  planTime?: string
  /**
   * 起租后X自然日
   */
  startRentAfterDay?: number
  /**
   * 处理人id
   */
  processorId: number
  /**
   * 处理人岗位
   */
  processorRoleList?: string[]
  /**
   * 提醒频率 枚举-TrackFrequencyEnum
   */
  remindFrequency: string
  /**
   * 任务内容
   */
  taskContent: string
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务来源
   */
  bizSource?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
}

/**
 * 接口 [跟踪事项编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/1927) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `POST /trackEvent/update`
 * @更新时间 `2024-04-22 09:35:41`
 */
export type TrackEventUpdateResponse = boolean

/**
 * 接口 [跟踪事项详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/1903) 的 **请求类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/detail`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventDetailRequest {
  id: string
}

/**
 * 接口 [跟踪事项详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/1903) 的 **返回类型**
 *
 * @分类 [跟踪事项-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_494)
 * @请求头 `GET /trackEvent/detail`
 * @更新时间 `2024-04-26 13:33:33`
 */
export interface TrackEventDetailResponse {
  /**
   * 合同
   */
  trackEventContractInfo?: {
    /**
     * 业务id
     */
    bizId?: number
    /**
     * 业务来源 -TrackTaskBizSourceEnum
     */
    bizSource?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 项目编号
     */
    projCode?: string
    /**
     * 业务类型。租赁、保理、转租赁
     */
    bizType?: string
    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    leaseType?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户Name
     */
    clientName?: string
    /**
     * 风控行业分类
     */
    riskControlIndustryClassify?: string
    /**
     * 项目主办用户id
     */
    projSponsorUserId?: number
    /**
     * 项目主办用户名称
     */
    projSponsorUserName?: string
    /**
     * 项目协办方用户id列表
     */
    projCosponsorUserIds?: number[]
    /**
     * 项目协办方用户名称列表
     */
    projCosponsorUserNames?: string[]
    /**
     * 业务部门id
     */
    bizDeptId?: number
    /**
     * 业务部门名称
     */
    bizDeptName?: string
    /**
     * 业务部门负责人id
     */
    bizDeptLeaderId?: number
    /**
     * 业务部门负责人名称
     */
    bizDeptLeaderName?: string
  }
  /**
   * 跟踪任务id
   */
  id?: number
  /**
   * 任务名称
   */
  taskName?: string
  /**
   * 任务类型 枚举-TrackTaskTypeEnum
   */
  taskType?: string
  /**
   * 提出人
   */
  createBy?: number
  /**
   * 提出人名称
   */
  createByName?: string
  /**
   * 计划日期
   */
  planTime?: string
  /**
   * 起租后X自然日
   */
  startRentAfterDay?: number
  /**
   * 处理人id
   */
  processorId?: number
  /**
   * 处理人名称
   */
  processor?: string
  /**
   * 处理人岗位
   */
  processorDept?: string
  /**
   * 提醒频率 枚举-TrackFrequencyEnum
   */
  remindFrequency?: string
  /**
   * 任务内容
   */
  taskContent?: string
  /**
   * 任务状态
   */
  taskStatus?: boolean
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 客户id
   */
  clientName?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
}

/* prettier-ignore-end */
