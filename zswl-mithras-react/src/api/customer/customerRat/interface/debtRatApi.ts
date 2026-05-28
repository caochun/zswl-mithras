/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [债项评级修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/4699) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/update`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountUpdateRequest {
  /**
   * id
   */
  id: number
  /**
   * 评审id
   */
  projReviewId: number
  /**
   * 项目名称
   */
  projName: string
  /**
   * 项目编号
   */
  projCode: string
  /**
   * 主承租人ID
   */
  clientId: number
  /**
   * 主承租人统一社会信用代码
   */
  clientUscCode: string
  /**
   * 评估主体ID
   */
  evaluationSubjectId: number
  /**
   * 评估主体名称
   */
  evaluationSubjectName: string
  /**
   * 模型名称
   */
  name: string
  /**
   * 模型编号
   */
  code: string
  /**
   * 债项类型：是否为项目制
   */
  projSystem: boolean
  /**
   * 债项类型：是否有实质性租赁物
   */
  materialLeaseItem: boolean
}

/**
 * 接口 [债项评级修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/4699) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/update`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountUpdateResponse {
  /**
   * 债项评级id
   */
  id?: number
}

/**
 * 接口 [债项评级准入校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4711) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/accessCheck`
 * @更新时间 `2024-06-18 18:45:40`
 */
export interface AmountAccessCheckRequest {
  /**
   * 主承租人ID
   */
  clientId: number
  /**
   * 债项类型：是否有实质性租赁物
   */
  materialLeaseItem: boolean
}

/**
 * 接口 [债项评级准入校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4711) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/accessCheck`
 * @更新时间 `2024-06-18 18:45:40`
 */
export interface AmountAccessCheckResponse {
  /**
   * 债项评级id
   */
  id?: number
}

/**
 * 接口 [债项评级列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3919) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/page`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountPageRequest {
  /**
   * 项目评审id
   */
  projReviewId: number
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
 * 接口 [债项评级列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3919) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/page`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountPageResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * proj_code
     */
    projCode?: string
    /**
     * proj_name
     */
    projName?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 主承租人名称
     */
    clientName?: string
    /**
     * 主承租人信用代码
     */
    clientUscCode?: string
    /**
     * 模型名称
     */
    modelName?: string
    /**
     * 模型编号
     */
    modelCode?: string
    /**
     * 项目限额
     */
    projQuota?: number
    /**
     * 发起时间
     */
    createTime?: string
    /**
     * 生效时间
     */
    effectTime?: string
    /**
     * 失效时间
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
 * 接口 [债项评级删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/4009) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/delete`
 * @更新时间 `2024-06-14 14:07:13`
 */
export interface AmountDeleteRequest {
  /**
   * 评级id
   */
  id: number
}

/**
 * 接口 [债项评级删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/4009) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/delete`
 * @更新时间 `2024-06-14 14:07:13`
 */
export interface AmountDeleteResponse {
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
 * 接口 [债项评级新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/3913) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/add`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountAddRequest {
  /**
   * 评审id
   */
  projReviewId: number
  /**
   * 项目名称
   */
  projName: string
  /**
   * 项目编号
   */
  projCode: string
  /**
   * 主承租人ID
   */
  clientId: number
  /**
   * 主承租人统一社会信用代码
   */
  clientUscCode: string
  /**
   * 评估主体ID
   */
  evaluationSubjectId: number
  /**
   * 评估主体名称
   */
  evaluationSubjectName: string
  /**
   * 模型名称
   */
  name: string
  /**
   * 模型编号
   */
  code: string
  /**
   * 债项类型：是否为项目制
   */
  projSystem: boolean
  /**
   * 债项类型：是否有实质性租赁物
   */
  materialLeaseItem: boolean
}

/**
 * 接口 [债项评级新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/3913) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/add`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountAddResponse {
  /**
   * 债项评级id
   */
  id?: number
  /**
   * 是否已经存在评级
   */
  isExist?: boolean
}

/**
 * 接口 [债项评级评估主体下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/3907) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/lesseeInfo`
 * @更新时间 `2024-06-13 16:52:50`
 */
export interface AmountLesseeInfoRequest {
  /**
   * 评审id
   */
  projReviewId: number
}

/**
 * 接口 [债项评级评估主体下拉框↗](http://yapi.zswltec.com:3000/project/11/interface/api/3907) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/lesseeInfo`
 * @更新时间 `2024-06-13 16:52:50`
 */
export interface AmountLesseeInfoResponse {
  /**
   * 评审id
   */
  projReviewId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 主承租人id
   */
  clientId?: number
  /**
   * 主承租人名称
   */
  clientName?: string
  /**
   * 主承租人统一社会信用代码
   */
  clientUscCode?: string
  /**
   * 评估主体下拉框
   */
  evaluationSubjectList?: {
    /**
     * 客户id
     */
    evaluationSubjectId?: number
    /**
     * 客户名称
     */
    evaluationSubjectName?: string
    /**
     * 客户统一社会信用代码
     */
    evaluationSubjectUscCode?: string
  }[]
  /**
   * 评估主体ID
   */
  evaluationSubjectId?: number
  /**
   * 评估主体名称
   */
  evaluationSubjectName?: string
  /**
   * 评估主体统一社会信用代码
   */
  evaluationSubjectUscCode?: string
}

/**
 * 接口 [债项评级详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/4705) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/detail`
 * @更新时间 `2024-06-18 18:45:40`
 */
export interface AmountDetailRequest {
  /**
   * 债项评级id
   */
  id: number
}

/**
 * 接口 [债项评级详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/4705) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/detail`
 * @更新时间 `2024-06-18 18:45:40`
 */
export interface AmountDetailResponse {
  /**
   * 债项评级id
   */
  id?: number
  reportId?: number
  snapshotId?: number
  clientId?: number
  clientUscCode?: string
  projReviewId?: number
  projCode?: string
  projName?: string
  evaluationSubjectId?: number
  evaluationSubjectName?: string
  projSystem?: boolean
  materialLeaseItem?: boolean
  modelCode?: string
  modelName?: string
  projQuota?: number
  belongDeptId?: number
  belongSponsorUserId?: number
  processStatus?: string
  ratingStatus?: boolean
  effectTime?: string
  abandonTime?: string
}

/**
 * 接口 [债项评级项目信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3925) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/projInfo`
 * @更新时间 `2024-06-13 16:52:50`
 */
export interface AmountProjInfoRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [债项评级项目信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/3925) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/projInfo`
 * @更新时间 `2024-06-13 16:52:50`
 */
export interface AmountProjInfoResponse {
  /**
   * 评审id
   */
  projReviewId?: number
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 主承租人id
   */
  clientId?: number
  /**
   * 主承租人名称
   */
  clientName?: string
  /**
   * 评估主体id
   */
  evaluationSubjectId?: number
  /**
   * 评估主体名称
   */
  evaluationSubjectName?: string
  /**
   * 评估主体区域
   */
  evaluationSubjectAreaName?: string
  /**
   * 评估主体营业收入
   */
  evaluationSubjectOperatingIncome?: number
  /**
   * 租赁类型
   */
  leaseTypes?: string
  /**
   * 资金用途
   */
  fundsPurpose?: string
  /**
   * 行业分类
   */
  projectClassify?: string
  /**
   * 地区分类
   */
  regionalProjectClassify?: string
}

/**
 * 接口 [客户评级新增校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4735) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/addCheck`
 * @更新时间 `2024-06-19 15:19:15`
 */
export interface AmountAddCheckRequest {
  /**
   * 评审id
   */
  projReviewId: number
  /**
   * 评估主体ID
   */
  evaluationSubjectId: number
}

/**
 * 接口 [客户评级新增校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/4735) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/addCheck`
 * @更新时间 `2024-06-19 15:19:15`
 */
export interface AmountAddCheckResponse {
  /**
   * 是否已经存在评级
   */
  isExist?: boolean
}

/**
 * 接口 [指标审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/4717) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/indexApproval`
 * @更新时间 `2024-06-19 15:19:16`
 */
export interface AmountIndexApprovalRequest {
  /**
   * id
   */
  id?: number
  /**
   * 审批详情
   */
  ratingApprovalRSPList?: {
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
  }[]
}

/**
 * 接口 [指标审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/4717) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/indexApproval`
 * @更新时间 `2024-06-19 15:19:16`
 */
export interface AmountIndexApprovalResponse {
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
 * 接口 [提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/3955) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/effect`
 * @更新时间 `2024-06-13 16:52:51`
 */
export interface AmountEffectRequest {
  /**
   * 债项评级id
   */
  id: number
}

/**
 * 接口 [提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/3955) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/effect`
 * @更新时间 `2024-06-13 16:52:51`
 */
export interface AmountEffectResponse {
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
 * 接口 [确认完成评级\/保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/3943) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/finish`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountFinishRequest {
  /**
   * 债项评级id
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
 * 接口 [确认完成评级\/保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/3943) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/finish`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountFinishResponse {
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
 * 接口 [自动匹配模型↗](http://yapi.zswltec.com:3000/project/11/interface/api/3901) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/modelMatch`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountModelMatchRequest {
  /**
   * 债项类型：是否为项目制
   */
  projSystem: boolean
  /**
   * 评估主体ID
   */
  evaluationSubjectId?: number
}

/**
 * 接口 [自动匹配模型↗](http://yapi.zswltec.com:3000/project/11/interface/api/3901) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/modelMatch`
 * @更新时间 `2024-06-18 10:59:36`
 */
export interface AmountModelMatchResponse {
  /**
   * 模型编号
   */
  code?: string
  /**
   * 模型名称
   */
  name?: string
}

/**
 * 接口 [评级报告↗](http://yapi.zswltec.com:3000/project/11/interface/api/3949) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/report`
 * @更新时间 `2024-06-19 15:19:16`
 */
export interface AmountReportRequest {
  /**
   * 债项评级id
   */
  id: number
}

/**
 * 接口 [评级报告↗](http://yapi.zswltec.com:3000/project/11/interface/api/3949) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/report`
 * @更新时间 `2024-06-19 15:19:16`
 */
export interface AmountReportResponse {
  /**
   * 历史评级信息
   */
  historyInfo?: {
    /**
     * id
     */
    id?: number
    clientId?: number
    clientUscCode?: string
    projReviewId?: number
    projCode?: string
    projName?: string
    evaluationSubjectId?: number
    projSystem?: boolean
    materialLeaseItem?: boolean
    modelCode?: string
    modelName?: string
    projQuota?: number
    belongDeptId?: number
    processStatus?: string
    ratingStatus?: boolean
    effectTime?: string
    abandonTime?: string
    deleted?: boolean
  }
  /**
   * 评估基准
   */
  evaluateBaseList?: {
    KEY?: {
      /**
       * 指标名称
       */
      fieldName?: string
      /**
       * 分组名称
       */
      groupName?: string
      /**
       * 指标值
       */
      value?: {}
      /**
       * 描述
       */
      fieldComment?: string
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
  }
  /**
   * 增信措施
   */
  creditMeasureListMap?: {
    KEY?: {
      /**
       * 指标名称
       */
      fieldName?: string
      /**
       * 描述
       */
      fieldComment?: string
      /**
       * 分组名称
       */
      groupName?: string
      /**
       * 指标字段值
       */
      value?: {}
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
  }
  /**
   * 限额
   */
  quota?: {
    /**
     * 债项评级模型类型
     */
    modelType?: string
    /**
     * 客户限额
     */
    clientQuota?: string
    /**
     * 项目限额
     */
    projQuota?: string
    /**
     * 集团限额
     */
    groupQuota?: string
    /**
     * 集团剩余可用额度
     */
    groupSurplusQuota?: string
    /**
     * 评估主体限额
     */
    evaluationSubjectQuota?: string
  }
  /**
   * 评估主体评级
   */
  scoreRSP?: {
    /**
     * 评估主体名称
     */
    evaluationSubjectName?: string
    /**
     * 评估主体信用代码
     */
    evaluationSubjectUscCode?: string
    /**
     * 评估主体等级
     */
    finalScore?: string
  }
}

/**
 * 接口 [试算↗](http://yapi.zswltec.com:3000/project/11/interface/api/3931) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/execute`
 * @更新时间 `2024-06-19 15:19:15`
 */
export interface AmountExecuteRequest {
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
 * 接口 [试算↗](http://yapi.zswltec.com:3000/project/11/interface/api/3931) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/execute`
 * @更新时间 `2024-06-19 15:19:15`
 */
export interface AmountExecuteResponse {
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
 * 接口 [问卷获取↗](http://yapi.zswltec.com:3000/project/11/interface/api/3937) 的 **请求类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/paramInfo`
 * @更新时间 `2024-06-19 15:19:15`
 */
export interface AmountParamInfoRequest {
  /**
   * 债项评级id
   */
  id: number
}

/**
 * 接口 [问卷获取↗](http://yapi.zswltec.com:3000/project/11/interface/api/3937) 的 **返回类型**
 *
 * @分类 [债项评级-基本信息接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_986)
 * @请求头 `POST /rating/amount/paramInfo`
 * @更新时间 `2024-06-19 15:19:15`
 */
export interface AmountParamInfoResponse {
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
