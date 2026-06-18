/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [\/black\/gray\/approval\/awaiting\/list↗](http://yapi.zswltec.com:3000/project/10/interface/api/16777) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/awaiting/list`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface AwaitingListRequest {
  page: string
  pageSize: string
  ids?: string
}

/**
 * 接口 [\/black\/gray\/approval\/awaiting\/list↗](http://yapi.zswltec.com:3000/project/10/interface/api/16777) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/awaiting/list`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface AwaitingListResponse {
  total?: number
  list?: {
    /**
     * 事件id
     */
    id?: number
    /**
     * 事件名称
     */
    eventName?: string
    /**
     * 事件状态
     */
    eventStatus?: string
    /**
     * 事件认定日期
     */
    admitDate?: string
    /**
     * 事件发生日期
     */
    occurDate?: string
    /**
     * 发生机构
     */
    occurOrg?: string
    /**
     * 业务类型
     */
    bizType?: string
    /**
     * 事件涉及主要风险
     */
    majorRiskType?: string
    /**
     * 事件涉及其他风险
     */
    otherRiskType?: string[]
    /**
     * 事件总金额（万元）
     */
    totalAmount?: number
    /**
     * 年内收回目标（万元）
     */
    yearRecoveredAmount?: number
    /**
     * 预计风险敞口金额（万元）
     */
    exposureAmount?: number
    /**
     * 实际损失金额（万元）
     */
    lossAmount?: number
    /**
     * 当期收回金额，存量事件报送使用
     */
    currentRecoverableAmount?: number
    /**
     * 累计计提减值损失（万元）
     */
    totalLossAmount?: number
    /**
     * 累计核销金额，存量事件报送使用
     */
    writeOffAmount?: number
    /**
     * 待收回金额，存量事件报送使用
     */
    needRecoverableAmount?: number
    /**
     * 累计收回金额（万元）
     */
    totalRecoveredAmount?: number
    /**
     * 风险事件发生后至今取得处置成果
     */
    dealResult?: string
    /**
     * 处置状态
     */
    dealStatus?: string
    /**
     * 当期处置举措及进展，存量事件报送使用
     */
    currentDealProgress?: string
    /**
     * 预计处置完成时间
     */
    dealEndDate?: string
    /**
     * 下一步处置计划
     */
    nextDealPlan?: string
    /**
     * 处置责任机构
     */
    dealOrg?: string
    /**
     * 处置责任部门
     */
    dealDept?: string
    /**
     * 处置负责人
     */
    dealAccount?: string
    /**
     * 处置相关附件标识列表
     */
    dealFileKeys?: string[]
    /**
     * 是否涉及追责
     */
    accountable?: boolean
    /**
     * 追责举措方案
     */
    dutyDesc?: string
    /**
     * 未追责原因
     */
    unDutyDesc?: string
    /**
     * 追责结果
     */
    dutyResult?: string
    /**
     * 下一步追责计划
     */
    nextDutyPlan?: string
    /**
     * 是否纳入绩效考核
     */
    assess?: boolean
    /**
     * 考核方式及结果
     */
    assessResult?: string
    /**
     * 风险等级
     */
    riskLevel?: string
    /**
     * 国资委风险事件等级
     */
    eventLevel?: string
    /**
     * 修改时间
     */
    gmtUpdate?: string
    /**
     * 审批任务id
     */
    auditTaskId?: number
    /**
     * 审批状态
     */
    approvalStatus?: number
    /**
     * 当前处理人
     */
    currentOperator?: string
  }[]
}

/**
 * 接口 [\/black\/gray\/approval\/batchApprove↗](http://yapi.zswltec.com:3000/project/10/interface/api/16783) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/batchApprove`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface ApprovalBatchApproveRequest {
  /**
   * 业务instance
   */
  instances?: {
    /**
     * 业务id
     */
    bizId?: number
    /**
     * 业务关联审批任务id
     */
    taskId?: number
  }[]
  /**
   * 审批业务类型，具体枚举 (RISK_ORG, RISK_JOB, RISK_STAFF, REPOSITORY, TRAINNING, CHECK_GROUP, CHECK_COMPANY, CHECK_INNER_TASK, CHECK_INNER_SUBMIT, RISK_REPORT_TASK: 风险合规报告的任务审批, RISK_REPORT_SUBMISSION: 风险合规报告的报送审批, RISK_INDEX_SET_ADD, RISK_INDEX_SET_SAVE, RISK_INDEX_SET_ENABLE, RISK_INDEX_SET_DISABLE, RISK_INDEX_SET_DELETE, RISK_INDEX_TASK_SUBMIT, RISK_INDEX_WARN_DEAL, RISK_INDEX_DEPARTMENT_SUBMIT, RISK_INDEX_SUBMIT_TASK, RISK_INDEX_DEAL_HANDLE, OPINION_HANDLE, OPI_ORG_HANDLE, OPI_CUS_HANDLE, RELATION_PARTY: 关联交易管理：关联方和关联交易, RELATION_TRADE, RISK_EVENT: 风险事件, RISK_EVENT_UPDATE_TASK, CONCENTRATION)
   */
  bizType?: string
  /**
   * 下一审批人
   */
  auditUser?: string
  /**
   * 操作备注，可包含审批意见，或提交审批，撤回时记录信息
   */
  suggest?: string
  /**
   * 审批类型，仅用于审批这一操作 (1, 2)
   */
  audit?: number
}

/**
 * 接口 [\/black\/gray\/approval\/batchApprove↗](http://yapi.zswltec.com:3000/project/10/interface/api/16783) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/batchApprove`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface ApprovalBatchApproveResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [\/black\/gray\/approval\/batchWithdraw↗](http://yapi.zswltec.com:3000/project/10/interface/api/16765) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/batchWithdraw`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface ApprovalBatchWithdrawRequest {
  /**
   * 业务instance
   */
  instances?: {
    /**
     * 业务id
     */
    bizId?: number
    /**
     * 业务关联审批任务id
     */
    taskId?: number
  }[]
  /**
   * 审批业务类型，具体枚举 (RISK_ORG, RISK_JOB, RISK_STAFF, REPOSITORY, TRAINNING, CHECK_GROUP, CHECK_COMPANY, CHECK_INNER_TASK, CHECK_INNER_SUBMIT, RISK_REPORT_TASK: 风险合规报告的任务审批, RISK_REPORT_SUBMISSION: 风险合规报告的报送审批, RISK_INDEX_SET_ADD, RISK_INDEX_SET_SAVE, RISK_INDEX_SET_ENABLE, RISK_INDEX_SET_DISABLE, RISK_INDEX_SET_DELETE, RISK_INDEX_TASK_SUBMIT, RISK_INDEX_WARN_DEAL, RISK_INDEX_DEPARTMENT_SUBMIT, RISK_INDEX_SUBMIT_TASK, RISK_INDEX_DEAL_HANDLE, OPINION_HANDLE, OPI_ORG_HANDLE, OPI_CUS_HANDLE, RELATION_PARTY: 关联交易管理：关联方和关联交易, RELATION_TRADE, RISK_EVENT: 风险事件, RISK_EVENT_UPDATE_TASK, CONCENTRATION)
   */
  bizType?: string
  /**
   * 下一审批人
   */
  auditUser?: string
  /**
   * 操作备注，可包含审批意见，或提交审批，撤回时记录信息
   */
  suggest?: string
  /**
   * 审批类型，仅用于审批这一操作 (1, 2)
   */
  audit?: number
}

/**
 * 接口 [\/black\/gray\/approval\/batchWithdraw↗](http://yapi.zswltec.com:3000/project/10/interface/api/16765) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/batchWithdraw`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface ApprovalBatchWithdrawResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [\/black\/gray\/approval\/list↗](http://yapi.zswltec.com:3000/project/10/interface/api/16771) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/list`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface ApprovalListRequest {
  /**
   * 事件名称
   */
  eventName?: string
  /**
   * 发生机构
   */
  occurOrg?: string
  /**
   * 风险等级 (COMMON: 一般风险事件, GREATER: 较大风险事件, IMPORT: 重大风险事件，应为important, NOT_INCLUDED: 不纳入)
   */
  riskLevel?: string
  eventLevel?: string
  /**
   * 事件状态 (ADD: 新增, UPDATE: 更新)
   */
  eventStatus?: string
  /**
   * 审批状态 (WAIT, AUDIT, WITHDRAW, REJECT, FINISH)
   */
  approvalStatus?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 处置状态
   */
  dealStatus?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [\/black\/gray\/approval\/list↗](http://yapi.zswltec.com:3000/project/10/interface/api/16771) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/list`
 * @更新时间 `2023-12-01 15:02:19`
 */
export interface ApprovalListResponse {
  total?: number
  list?: {
    /**
     * 事件id
     */
    id?: number
    /**
     * 事件名称
     */
    eventName?: string
    /**
     * 事件认定日期
     */
    admitDate?: string
    /**
     * 事件发生日期
     */
    occurDate?: string
    /**
     * 风险等级 (COMMON: 一般风险事件, GREATER: 较大风险事件, IMPORT: 重大风险事件，应为important, NOT_INCLUDED: 不纳入)
     */
    riskLevel?: string
    /**
     * 国资委风险事件等级
     */
    eventLevel?: string
    /**
     * 事件状态 (ADD: 新增, UPDATE: 更新)
     */
    eventStatus?: string
    /**
     * 审批状态，为 ("待提交": WAIT, "审批中": AUDIT, "已撤回": WITHDRAW, "已退回": REJECT, "已完成": FINISH, WAIT, AUDIT, WITHDRAW, REJECT, FINISH)
     */
    approvalStatus?: number
    /**
     * 审批状态，为
     */
    approvalStatusString?: string
    /**
     * 处置状态 (NO_DEAL: 未处置, DEALING: 处置中, DEALT: 处置完成)
     */
    dealStatus?: string
    /**
     * 发生机构
     */
    occurOrg?: string
    /**
     * 发生部门
     */
    occurDept?: string
    /**
     * 当前处理人账号
     */
    currentOperator?: string
    /**
     * 当前处理人用户名
     */
    currentOperatorName?: string
    /**
     * 修改时间
     */
    gmtUpdate?: string
    /**
     * 审批任务id
     */
    auditTaskId?: number
    /**
     * 上一处理人
     */
    preOperator?: string
    /**
     * 审批备注
     */
    latestMsg?: string
    /**
     * 是否超时，0：未超时，1：已超时
     */
    timeout?: number
    /**
     * 报送截止日期
     */
    endTime?: string
    /**
     * 采取处置举措进展和成果
     */
    dealResult?: string
    /**
     * 任务类型
     */
    taskType?: string
    /**
     * 备注
     */
    remark?: string
  }[]
}

/**
 * 接口 [黑灰名单主任务-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/73) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/manual/task/auditList`
 * @更新时间 `2024-01-19 16:02:02`
 */
export interface TaskAuditListRequest {
  id?: string
  /**
   * 任务编号
   */
  taskNum?: string
  /**
   * 数据时点，即用户提交任务时所在的月份
   */
  timePoint?: string
  /**
   * 机构code
   */
  orgCode?: string
  /**
   * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
   */
  auditStatus?: string
  businessSource?: string
  /**
   * 当前操作人
   */
  currentOperator?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单主任务-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/73) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/manual/task/auditList`
 * @更新时间 `2024-01-19 16:02:02`
 */
export interface TaskAuditListResponse {
  total?: number
  list?: {
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 修改时间
     */
    updateTime?: string
    /**
     * 创建人
     */
    createdBy?: string
    /**
     * 更新人
     */
    updatedBy?: string
    /**
     * 审批流任务id，冗余，便于查找audit_task记录
     */
    auditTaskId?: number
    /**
     * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
     */
    auditStatus?: number
    /**
     * 上一操作人，冗余，方便判断能否撤回
     */
    preOperator?: string
    /**
     * 审批流当前操作人，冗余，方便进行用户数据权限过滤
     */
    currentOperator?: string
    /**
     * id
     */
    id?: number
    /**
     * 任务编号，唯一索引，保存时生成，机构缩写8位年月日至少3位自增数，如gdrf20230329001，理论位数应该是15位，考虑到后三位自增数极端情况下不一定够用，所以预留32位
     */
    taskNum?: string
    /**
     * 上层任务id
     */
    parentTaskId?: number
    /**
     * 机构code
     */
    orgId?: string
    /**
     * 子任务-派发到的部门code
     */
    subTaskDeptCode?: string
    /**
     * 数据时点，即用户提交任务时所在的月份
     */
    timePoint?: string
    /**
     * 定期任务报送截止时间
     */
    deadline?: string
    /**
     * 定期任务是否超时
     */
    overtimeFlag?: number
    /**
     * 上传导入文件
     */
    uploadFile?: string
    /**
     * 关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务
     */
    taskType?: string
    /**
     * 子任务-退回说明
     */
    retractSuggest?: string
    /**
     * 子任务-关闭标记
     */
    isClosed?: number
    /**
     * 派发人退回标记
     */
    isRetract?: number
    /**
     * 子任务-派发人
     */
    assigner?: string
    /**
     * 子任务-指定接受角色
     */
    assignSubmitRole?: string
    /**
     * 子任务-指定接收处理人
     */
    assignSubmitUser?: string
    /**
     * 最后提交时间
     */
    submitTime?: string
    /**
     * 子任务-派发时间
     */
    assignTime?: string
    /**
     * 最后审批时间
     */
    auditTime?: string
    /**
     * 1=页面录入，2=openapi对接
     */
    source?: number
    /**
     * 创建时间
     */
    gmtCreate?: string
    /**
     * 更新时间
     */
    gmtUpdate?: string
  }[]
}

/**
 * 接口 [黑灰名单主任务审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/67) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/manual/task`
 * @更新时间 `2024-01-19 16:01:27`
 */
export interface ManualTaskRequest {
  /**
   * id
   */
  id?: number
  /**
   * 下一审批人
   */
  auditUser?: string
}

/**
 * 接口 [黑灰名单主任务审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/67) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/manual/task`
 * @更新时间 `2024-01-19 16:01:27`
 */
export interface ManualTaskResponse {
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务关联审批任务id
   */
  taskId?: number
}

/**
 * 接口 [黑灰名单入库-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/16921) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/warehouse/auditList`
 * @更新时间 `2023-12-15 16:53:10`
 */
export interface WarehouseAuditListRequest {
  id?: string
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间开始 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeTo?: string
  /**
   * 审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成] (WAIT, AUDIT, WITHDRAW, REJECT, FINISH)
   */
  auditStatus?: string
  /**
   * 当前操作人
   */
  currentOperator?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单入库-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/16921) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/warehouse/auditList`
 * @更新时间 `2023-12-15 16:53:10`
 */
export interface WarehouseAuditListResponse {
  total?: number
  list?: {
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 修改时间
     */
    updateTime?: string
    /**
     * 创建人
     */
    createdBy?: string
    /**
     * 修改人
     */
    updatedBy?: string
    /**
     * 审批任务id
     */
    auditTaskId?: number
    /**
     * 审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成] (WAIT, AUDIT, WITHDRAW, REJECT, FINISH)
     */
    auditStatus?: number
    /**
     * 上一处理人
     */
    preOperator?: string
    /**
     * 当前审批人
     */
    currentOperator?: string
    /**
     * id
     */
    id?: number
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一社会信用代码
     */
    unifiedSocialCreditCode?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 风险规模（万元）
     */
    riskScale?: string
    /**
     * 所属集团
     */
    membershipGroup?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    source?: string
    /**
     * 申请时间
     */
    applyTime?: string
    /**
     * 入库原因
     */
    applyReason?: string
    /**
     * 申请机构
     */
    applyOrganization?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
  }[]
}

/**
 * 接口 [黑灰名单出库-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/16897) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/manual/outbound/auditList`
 * @更新时间 `2024-01-18 14:29:46`
 */
export interface OutboundAuditListRequest {
  id?: string
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 业务类型
   */
  proposedBusinessType?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间开始 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeTo?: string
  /**
   * 审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成] (WAIT, AUDIT, WITHDRAW, REJECT, FINISH)
   */
  auditStatus?: string
  /**
   * 当前操作人
   */
  currentOperator?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单出库-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/16897) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/manual/outbound/auditList`
 * @更新时间 `2024-01-18 14:29:46`
 */
export interface OutboundAuditListResponse {
  total?: number
  list?: {
    /**
     * 申请时间
     */
    createTime?: string
    /**
     * 修改时间
     */
    updateTime?: string
    /**
     * 创建人
     */
    createdBy?: string
    /**
     * 修改人
     */
    updatedBy?: string
    /**
     * 审批任务id
     */
    auditTaskId?: number
    /**
     * 记录状态
     */
    auditStatus?: number
    /**
     * 上一处理人
     */
    preOperator?: string
    /**
     * 当前处理人
     */
    currentOperator?: string
    /**
     * id
     */
    id?: number
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一社会信用代码
     */
    unifiedSocialCreditCode?: string
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 申请机构
     */
    applyOrganization?: string
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    /**
     * 入库时间
     */
    warehouseTime?: string
    /**
     * 出库时间
     */
    planOutboundTime?: string
    /**
     * 入库类型
     */
    source?: string
    /**
     * 黑灰名单入库机构
     */
    warehouseOrganization?: string
    createBy?: number
  }[]
}

/**
 * 接口 [黑灰名单库入库审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/16801) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/warehouse/submit`
 * @更新时间 `2023-12-05 15:32:39`
 */
export interface WarehouseSubmitRequest {
  /**
   * id
   */
  id?: number
  /**
   * 下一审批人
   */
  auditUser?: string
}

/**
 * 接口 [黑灰名单库入库审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/16801) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/warehouse/submit`
 * @更新时间 `2023-12-05 15:32:39`
 */
export interface WarehouseSubmitResponse {
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务关联审批任务id
   */
  taskId?: number
}

/**
 * 接口 [黑灰名单库出库审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/16825) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/manual/outbound`
 * @更新时间 `2023-12-05 20:10:30`
 */
export interface ManualOutboundRequest {
  /**
   * id
   */
  id?: number
  /**
   * 下一审批人
   */
  auditUser?: string
}

/**
 * 接口 [黑灰名单库出库审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/16825) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/manual/outbound`
 * @更新时间 `2023-12-05 20:10:30`
 */
export interface ManualOutboundResponse {
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务关联审批任务id
   */
  taskId?: number
}

/**
 * 接口 [黑灰名单库突破审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/16831) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/break/business`
 * @更新时间 `2023-12-05 20:10:30`
 */
export interface BreakBusinessRequest {
  /**
   * id
   */
  id?: number
  /**
   * 下一审批人
   */
  auditUser?: string
}

/**
 * 接口 [黑灰名单库突破审批↗](http://yapi.zswltec.com:3000/project/10/interface/api/16831) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `POST /black/gray/approval/break/business`
 * @更新时间 `2023-12-05 20:10:30`
 */
export interface BreakBusinessResponse {
  /**
   * 业务id
   */
  bizId?: number
  /**
   * 业务关联审批任务id
   */
  taskId?: number
}

/**
 * 接口 [黑灰名单突破-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/16975) 的 **请求类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/break/business/auditList`
 * @更新时间 `2023-12-15 16:53:20`
 */
export interface BusinessAuditListRequest {
  id?: string
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 报告机构
   */
  applyOrganization?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 申请时间开始 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeFrom?: string
  /**
   * 申请时间结束 : yyyy-MM-dd HH:mm:ss
   */
  applyTimeTo?: string
  /**
   * 审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成] (WAIT, AUDIT, WITHDRAW, REJECT, FINISH)
   */
  auditStatus?: string
  /**
   * 当前操作人
   */
  currentOperator?: string
  page?: string
  pageSize?: string
}

/**
 * 接口 [黑灰名单突破-审批查询↗](http://yapi.zswltec.com:3000/project/10/interface/api/16975) 的 **返回类型**
 *
 * @分类 [黑灰名单审批控制层↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3777)
 * @请求头 `GET /black/gray/approval/break/business/auditList`
 * @更新时间 `2023-12-15 16:53:20`
 */
export interface BusinessAuditListResponse {
  total?: number
  list?: {
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 修改时间
     */
    updateTime?: string
    /**
     * 创建人
     */
    createdBy?: string
    /**
     * 修改人
     */
    updatedBy?: string
    /**
     * 审批任务id
     */
    auditTaskId?: number
    /**
     * 突破流程状态
     */
    auditStatus?: number
    /**
     * 上一处理人
     */
    preOperator?: string
    /**
     * 当前处理人
     */
    currentOperator?: string
    /**
     * id
     */
    id?: number
    /**
     * 黑灰名单id
     */
    blackGrayId?: number
    /**
     * 拟开展业务类型
     */
    proposedBusinessType?: string
    /**
     * 原计划出库时间
     */
    planOutboundTime?: string
    /**
     * 拟开展业务规模（万元）
     */
    proposeBusinessScale?: number
    /**
     * 申请原因
     */
    applyReason?: string
  }[]
}

/* prettier-ignore-end */
