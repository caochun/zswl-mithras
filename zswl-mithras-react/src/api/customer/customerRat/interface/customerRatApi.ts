/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [客户信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3763) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/info`
 * @更新时间 `2024-06-14 14:07:07`
 */
export interface ClientInfoRequest {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [客户信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3763) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/info`
 * @更新时间 `2024-06-14 14:07:07`
 */
export interface ClientInfoResponse {
  /**
   * id
   */
  id?: number
  /**
   * 客户编号
   */
  clientCode?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 省份
   */
  province?: string
  /**
   * 城市
   */
  city?: string
  /**
   * 区、县
   */
  district?: string
  /**
   * 成立日期
   */
  establishDate?: string
  /**
   * 注册资本
   */
  registerCapital?: number
  /**
   * 行业分类
   */
  industryType?: string
  /**
   * 行业分类名称
   */
  industryTypeName?: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify?: string
  /**
   * 业务范围
   */
  bizScope?: string
}

/**
 * 接口 [客户评级修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/4687) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/update`
 * @更新时间 `2024-06-18 10:59:12`
 */
export interface ClientUpdateRequest {
  /**
   * 评级id
   */
  id?: number
  /**
   * 客户id
   */
  clientId: number
  /**
   * 模型code
   */
  code: string
  /**
   * 模型名称
   */
  name: string
}

/**
 * 接口 [客户评级修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/4687) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/update`
 * @更新时间 `2024-06-18 10:59:12`
 */
export interface ClientUpdateResponse {
  /**
   * 评级id
   */
  id?: number
}

/**
 * 接口 [客户评级准入校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4723) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/accessCheck`
 * @更新时间 `2024-06-19 17:31:05`
 */
export interface ClientAccessCheckRequest {
  /**
   * 客户id
   */
  clientId: number
}

/**
 * 接口 [客户评级准入校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4723) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/accessCheck`
 * @更新时间 `2024-06-19 17:31:05`
 */
export interface ClientAccessCheckResponse {
  /**
   * 是否存在评级数据
   */
  exist?: boolean
}

/**
 * 接口 [客户评级列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3799) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/page`
 * @更新时间 `2024-06-13 15:44:17`
 */
export interface ClientPageRequest {
  /**
   * 客户id，展示tab页数据时需要传递
   */
  clientId?: number
  /**
   * 客户编号
   */
  clientCode?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 模型名称
   */
  modelName?: string
  /**
   * 发起时间-开始
   */
  createTimeStart?: string
  /**
   * 发起时间-结束
   */
  createTimeEnd?: string
  /**
   * 评级状态
   */
  ratingStatus?: boolean
  /**
   * 流程状态
   */
  processStatus?: string
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
 * 接口 [客户评级列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3799) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/page`
 * @更新时间 `2024-06-13 15:44:17`
 */
export interface ClientPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 客户编号
     */
    clientCode?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 模型名称
     */
    modelName?: string
    /**
     * 评级结果
     */
    score?: string
    /**
     * 评级认定结果
     */
    finalScore?: string
    /**
     * 发起时间
     */
    createTime?: string
    /**
     * 生效日期
     */
    effectTime?: string
    /**
     * 失效日期
     */
    abandonTime?: string
    /**
     * 发起机构
     */
    startOrg?: string
    /**
     * 发起人
     */
    createBy?: number
    /**
     * 发起人名称
     */
    createByName?: string
    /**
     * 评级状态
     */
    ratingStatus?: string
    /**
     * 流程状态
     */
    processStatus?: string
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
 * 接口 [客户评级删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/4003) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/delete`
 * @更新时间 `2024-06-14 14:07:07`
 */
export interface ClientDeleteRequest {
  /**
   * 评级id
   */
  id?: number
}

/**
 * 接口 [客户评级删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/4003) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/delete`
 * @更新时间 `2024-06-14 14:07:07`
 */
export interface ClientDeleteResponse {
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
 * 接口 [客户评级新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/3781) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/add`
 * @更新时间 `2024-06-19 15:19:28`
 */
export interface ClientAddRequest {
  /**
   * 客户id
   */
  clientId: number
  /**
   * 模型code
   */
  code: string
  /**
   * 模型名称
   */
  name: string
}

/**
 * 接口 [客户评级新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/3781) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/add`
 * @更新时间 `2024-06-19 15:19:28`
 */
export interface ClientAddResponse {
  /**
   * 评级id
   */
  id?: number
}

/**
 * 接口 [客户评级新增校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4741) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/addCheck`
 * @更新时间 `2024-06-19 15:19:28`
 */
export interface ClientAddCheckRequest {
  /**
   * 客户id
   */
  clientId: number
  /**
   * 模型code
   */
  code: string
  /**
   * 模型名称
   */
  name: string
}

/**
 * 接口 [客户评级新增校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4741) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/addCheck`
 * @更新时间 `2024-06-19 15:19:28`
 */
export interface ClientAddCheckResponse {
  /**
   * 是否存在评级数据
   */
  exist?: boolean
}

/**
 * 接口 [客户评级详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/3805) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/detail`
 * @更新时间 `2024-06-18 10:59:13`
 */
export interface ClientDetailRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [客户评级详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/3805) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/detail`
 * @更新时间 `2024-06-18 10:59:13`
 */
export interface ClientDetailResponse {
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户编号
   */
  clientCode?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 模型名称
   */
  modelName?: string
  /**
   * 模型编号
   */
  modelCode?: string
  /**
   * 评级结果
   */
  score?: string
  /**
   * 评级认定结果
   */
  adjustScore?: string
  /**
   * 发起时间
   */
  createTime?: string
  /**
   * 发起机构
   */
  startOrg?: string
  /**
   * 发起人
   */
  createBy?: number
  /**
   * 发起人名称
   */
  createByName?: string
  /**
   * 流程状态
   */
  processStatus?: string
  /**
   * 评级状态
   */
  ratingStatus?: string
}

/**
 * 接口 [指标审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/4729) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/indexApproval`
 * @更新时间 `2024-06-19 17:31:05`
 */
export interface ClientIndexApprovalRequest {
  /**
   * id
   */
  id?: number
  /**
   * 审批详情
   */
  ratingApprovalRSP?: {
    /**
     * 指标名称
     */
    filedName?: string
    /**
     * 审批状态
     */
    approvalStatus?: boolean
    /**
     * 审批意见
     */
    approvalOpinion?: string
  }
}

/**
 * 接口 [指标审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/4729) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/indexApproval`
 * @更新时间 `2024-06-19 17:31:05`
 */
export interface ClientIndexApprovalResponse {
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
 * 接口 [推翻记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/4747) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/overturnRecord`
 * @更新时间 `2024-06-19 16:54:44`
 */
export interface ClientOverturnRecordRequest {
  /**
   * 评级id
   */
  id?: number
}

/**
 * 接口 [推翻记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/4747) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/overturnRecord`
 * @更新时间 `2024-06-19 16:54:44`
 */
export interface ClientOverturnRecordResponse {
  /**
   * 推翻时间
   */
  overturnTime?: string
  /**
   * 评级结果
   */
  score?: string
  /**
   * 评级认定结果
   */
  finalScore?: string
  /**
   * 推翻理由
   */
  overturnOpinion?: string
  /**
   * 推翻人
   */
  overturnUserName?: string
}

/**
 * 接口 [提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/3829) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/effect`
 * @更新时间 `2024-06-18 19:09:00`
 */
export interface ClientEffectRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/3829) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/effect`
 * @更新时间 `2024-06-18 19:09:00`
 */
export interface ClientEffectResponse {
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
 * 接口 [摘要信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/4693) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/abstract`
 * @更新时间 `2024-06-18 18:54:50`
 */
export interface ClientAbstractRequest {
  /**
   * 评级id
   */
  id: number
}

/**
 * 接口 [摘要信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/4693) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/abstract`
 * @更新时间 `2024-06-18 18:54:50`
 */
export interface ClientAbstractResponse {
  /**
   * 定性得分
   */
  qualitativeScore?: string
  /**
   * 定量得分
   */
  quantitativeScore?: string
  /**
   * 模型得分
   */
  modelScore?: string
  /**
   * 违约率
   */
  defaultRate?: string
  /**
   * 初评等级
   */
  score?: string
  /**
   * 评级结果
   */
  adjustScore?: string
}

/**
 * 接口 [模型获取↗](http://yapi.zswltec.com:3000/project/11/interface/api/3769) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/modelQuery`
 * @更新时间 `2024-06-12 09:55:30`
 */
export interface RatingModelQueryRequest {
  /**
   * 业务类型，客户评级还是债项评级 RatingBizTypeEnum
   */
  bizType?: string
}

/**
 * 接口 [模型获取↗](http://yapi.zswltec.com:3000/project/11/interface/api/3769) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/modelQuery`
 * @更新时间 `2024-06-12 09:55:30`
 */
export type RatingModelQueryResponse = {
  /**
   * 模型名称
   */
  name?: string
  /**
   * 模型编码
   */
  code?: string
}[]

/**
 * 接口 [确认完成评级\/保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/3793) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/finish`
 * @更新时间 `2024-06-18 10:59:12`
 */
export interface ClientFinishRequest {
  /**
   * 客户评级id
   */
  id: number
  /**
   * 操作类型，确认完成评级：true，保存：false
   */
  operationType: boolean
  /**
   * 评分参数
   */
  param?: {
    /**
     * 字段标识
     */
    fieldName: string
    /**
     * 所选选项
     */
    fieldValue: {}
    /**
     * 数据时点
     */
    date?: string
    /**
     * 分组名称
     */
    groupName?: string
  }[]
}

/**
 * 接口 [确认完成评级\/保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/3793) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/finish`
 * @更新时间 `2024-06-18 10:59:12`
 */
export interface ClientFinishResponse {
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
 * 接口 [评级报告↗](http://yapi.zswltec.com:3000/project/11/interface/api/3823) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/report`
 * @更新时间 `2024-06-19 14:05:43`
 */
export interface ClientReportRequest {
  /**
   * 评级id
   */
  id: number
}

/**
 * 接口 [评级报告↗](http://yapi.zswltec.com:3000/project/11/interface/api/3823) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/report`
 * @更新时间 `2024-06-19 14:05:43`
 */
export interface ClientReportResponse {
  /**
   * 历史评级信息
   */
  historyInfo?: {
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户编号
     */
    clientCode?: string
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 模型名称
     */
    modelName?: string
    /**
     * 模型编号
     */
    modelCode?: string
    /**
     * 评级结果
     */
    score?: string
    /**
     * 评级认定结果
     */
    adjustScore?: string
    /**
     * 发起时间
     */
    createTime?: string
    /**
     * 发起机构
     */
    startOrg?: string
    /**
     * 发起人
     */
    createBy?: number
    /**
     * 发起人名称
     */
    createByName?: string
    /**
     * 流程状态
     */
    processStatus?: string
    /**
     * 评级状态
     */
    ratingStatus?: string
  }
  /**
   * 定性指标
   */
  qualitativeList?: {
    /**
     * 指标编号
     */
    fieldCode?: string
    /**
     * 指标名称
     */
    fieldName?: string
    /**
     * 指标档位
     */
    value?: {}
    /**
     * 分组名称
     */
    dataType?: string
    /**
     * 列表数据
     */
    enumList?: {
      value?: {}
      label?: string
      enLable?: string
      extra?: {}
    }[]
    /**
     * 描述
     */
    fieldComment?: string
    /**
     * 审批状态
     */
    approvalStatus?: boolean
    /**
     * 数据是否变化-流程中被退回，再发起时返回
     */
    isChange?: boolean
    /**
     * 审批意见
     */
    approvalOpinion?: string
  }[]
  /**
   * 定量指标
   */
  quantitativeList?: {
    /**
     * 序号
     */
    index?: number
    /**
     * 指标名称
     */
    fieldName?: string
    /**
     * 描述
     */
    fieldComment?: string
    /**
     * 数据类型
     */
    dataType?: string
    /**
     * 计算表达式
     */
    executeExpress?: string
    /**
     * 取数方式 RatingFetchMethodEnum
     */
    fetchMethod?: string
    /**
     * 指标字段值
     */
    value?: {}
    /**
     * 数据时点
     */
    date?: string
    /**
     * 数据是否变化-流程中被退回，再发起时返回
     */
    isChange?: boolean
    /**
     * 审批状态
     */
    approvalStatus?: boolean
    /**
     * 审批意见
     */
    approvalOpinion?: string
  }[]
  /**
   * 评级调整事项
   */
  adjustEventList?: {
    /**
     * 指标编号
     */
    fieldCode?: string
    /**
     * 指标名称
     */
    fieldName?: string
    /**
     * 指标档位
     */
    value?: {}
    /**
     * 分组名称
     */
    dataType?: string
    /**
     * 列表数据
     */
    enumList?: {
      value?: {}
      label?: string
      enLable?: string
      extra?: {}
    }[]
    /**
     * 描述
     */
    fieldComment?: string
    /**
     * 审批状态
     */
    approvalStatus?: boolean
    /**
     * 数据是否变化-流程中被退回，再发起时返回
     */
    isChange?: boolean
    /**
     * 审批意见
     */
    approvalOpinion?: string
  }[]
  /**
   * 评级结果
   */
  ratingScoreRSP?: {
    /**
     * 定性得分
     */
    qualitativeScore?: string
    /**
     * 定量得分
     */
    quantitativeScore?: string
    /**
     * 模型得分
     */
    modelScore?: string
    /**
     * 违约率
     */
    defaultRate?: string
    /**
     * 初评等级
     */
    score?: string
    /**
     * 评级结果
     */
    adjustScore?: string
  }
}

/**
 * 接口 [评级推翻↗](http://yapi.zswltec.com:3000/project/11/interface/api/3817) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/overturn`
 * @更新时间 `2024-06-19 17:31:05`
 */
export interface ClientOverturnRequest {
  /**
   * 评级id
   */
  id: number
  /**
   * 评级认定结果
   */
  finalScore?: string
  /**
   * 意见
   */
  overturnOpinion?: string
}

/**
 * 接口 [评级推翻↗](http://yapi.zswltec.com:3000/project/11/interface/api/3817) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/overturn`
 * @更新时间 `2024-06-19 17:31:05`
 */
export interface ClientOverturnResponse {
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
 * 接口 [评级推翻审核↗](http://yapi.zswltec.com:3000/project/11/interface/api/3811) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/overturn/approval`
 * @更新时间 `2024-06-12 09:55:30`
 */
export interface OverturnApprovalRequest {
  /**
   * 评级id
   */
  id: number
  /**
   * 是否同意推翻
   */
  agreeOverturn: boolean
}

/**
 * 接口 [评级推翻审核↗](http://yapi.zswltec.com:3000/project/11/interface/api/3811) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/overturn/approval`
 * @更新时间 `2024-06-12 09:55:30`
 */
export interface OverturnApprovalResponse {
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
 * 接口 [评级调整↗](http://yapi.zswltec.com:3000/project/11/interface/api/8887) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/adjust`
 * @更新时间 `2024-08-06 17:27:32`
 */
export interface ClientAdjustRequest {
  /**
   * 评级id
   */
  id: number
  /**
   * 评级认定结果
   */
  finalScore?: string
  /**
   * 意见
   */
  overturnOpinion: string
}

/**
 * 接口 [评级调整↗](http://yapi.zswltec.com:3000/project/11/interface/api/8887) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/adjust`
 * @更新时间 `2024-08-06 17:27:32`
 */
export interface ClientAdjustResponse {
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
 * 接口 [试算↗](http://yapi.zswltec.com:3000/project/11/interface/api/3787) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/execute`
 * @更新时间 `2024-06-19 14:05:43`
 */
export interface ClientExecuteRequest {
  /**
   * 客户评级id
   */
  id: number
  /**
   * 评分参数
   */
  param: {
    /**
     * 字段标识
     */
    fieldName: string
    /**
     * 所选选项
     */
    fieldValue: {}
    /**
     * 数据时点
     */
    date?: string
  }[]
}

/**
 * 接口 [试算↗](http://yapi.zswltec.com:3000/project/11/interface/api/3787) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/execute`
 * @更新时间 `2024-06-19 14:05:43`
 */
export interface ClientExecuteResponse {
  /**
   * 得分
   */
  score?: string
  /**
   * 调整后得分
   */
  adjustScore?: string
  /**
   * 项目限额
   */
  projQuota?: string
  /**
   * 试算次数
   */
  executeCount?: number
  /**
   * 最大试算次数
   */
  executeCountLimit?: number
  /**
   * 评分卡版本
   */
  scoreCardVersion?: string
  /**
   * 评分卡名称
   */
  scoreCardName?: string
  /**
   * 评分卡编号
   */
  scoreCardCode?: string
}

/**
 * 接口 [问卷获取↗](http://yapi.zswltec.com:3000/project/11/interface/api/3775) 的 **请求类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/paramInfo`
 * @更新时间 `2024-06-19 14:05:43`
 */
export interface ClientParamInfoRequest {
  /**
   * 债项评级id
   */
  id: number
}

/**
 * 接口 [问卷获取↗](http://yapi.zswltec.com:3000/project/11/interface/api/3775) 的 **返回类型**
 *
 * @分类 [客户评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_932)
 * @请求头 `POST /rating/client/paramInfo`
 * @更新时间 `2024-06-19 14:05:43`
 */
export interface ClientParamInfoResponse {
  /**
   * 问卷信息
   */
  info?: {
    KEY?: {
      KEY?: {
        /**
         * 字段标识
         */
        fieldName?: string
        /**
         * 字段显示名
         */
        fieldComment?: string
        /**
         * 数据类型
         */
        dataType?: string
        /**
         * 计算表达式
         */
        executeExpress?: string
        /**
         * 取数方式 RatingFetchMethodEnum
         */
        fetchMethod?: string
        /**
         * 分组名称
         */
        groupName?: string
        /**
         * 字段分组编码
         */
        groupCode?: string
        /**
         * 单位
         */
        util?: string
        /**
         * 计算公式
         */
        formal?: string
        /**
         * 枚举项[如果dataType是枚举/枚举集合时]
         */
        enumList?: {
          value?: {}
          label?: string
          enLable?: string
          extra?: {}
        }[]
      }[]
    }
  }
  /**
   * 试算次数
   */
  executeCount?: number
  /**
   * 试算最大次数
   */
  executeCountLimit?: number
  /**
   * 答题结果,有草稿时返回
   */
  ratingParam?: {
    /**
     * 字段标识
     */
    fieldName: string
    /**
     * 所选选项
     */
    fieldValue: {}
    /**
     * 数据时点
     */
    date?: string
    /**
     * 分组名称
     */
    groupName?: string
  }[]
}

/* prettier-ignore-end */
