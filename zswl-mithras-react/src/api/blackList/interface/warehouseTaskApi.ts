/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改黑灰名单任务表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17845) 的 **请求类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/modify`
 * @更新时间 `2024-01-19 10:21:52`
 */
export interface TaskModifyRequest {
  /**
   * id
   */
  id?: number
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
  uploadFileList?: string[]
  /**
   * 关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务
   */
  taskType?: string
  /**
   * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
   */
  auditStatus?: number
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
   * 审批流任务id，冗余，便于查找audit_task记录
   */
  auditTaskId?: number
  /**
   * 审批流当前操作人，冗余，方便进行用户数据权限过滤
   */
  currentOperator?: string
  /**
   * 上一操作人，冗余，方便判断能否撤回
   */
  preOperator?: string
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
}

/**
 * 接口 [修改黑灰名单任务表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17845) 的 **返回类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/modify`
 * @更新时间 `2024-01-19 10:21:52`
 */
export interface TaskModifyResponse {
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
 * 接口 [删除黑灰名单任务表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17857) 的 **请求类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/remove`
 * @更新时间 `2024-01-16 14:14:40`
 */
export interface TaskRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除黑灰名单任务表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17857) 的 **返回类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/remove`
 * @更新时间 `2024-01-16 14:14:40`
 */
export interface TaskRemoveResponse {
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
 * 接口 [新增黑灰名单任务表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17839) 的 **请求类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/add`
 * @更新时间 `2024-01-19 10:21:52`
 */
export interface TaskAddRequest {
  /**
   * 任务编号
   */
  taskNum?: string
  /**
   * 上层任务id
   */
  parentTaskId?: number
  /**
   * 机构code
   */
  orgCode?: string
  /**
   * 来源
   */
  businessSource: string
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
   * 定期任务是否超时，0 不超时，1超时
   */
  overtimeFlag?: number
  /**
   * 上传导入文件
   */
  uploadFileList?: string[]
  /**
   * 关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务
   */
  taskType?: string
  /**
   * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
   */
  auditStatus?: number
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
   * 审批流任务id，冗余，便于查找audit_task记录
   */
  auditTaskId?: number
  /**
   * 审批流当前操作人，冗余，方便进行用户数据权限过滤
   */
  currentOperator?: string
  /**
   * 上一操作人，冗余，方便判断能否撤回
   */
  preOperator?: string
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
   * 创建人
   */
  createdBy?: string
  /**
   * 创建时间
   */
  gmtCreate?: string
  /**
   * 更新人
   */
  updatedBy?: string
  /**
   * 更新时间
   */
  gmtUpdate?: string
}

/**
 * 接口 [新增黑灰名单任务表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17839) 的 **返回类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/add`
 * @更新时间 `2024-01-19 10:21:52`
 */
export type TaskAddResponse = number

/**
 * 接口 [黑灰名单任务表列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17851) 的 **请求类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/list`
 * @更新时间 `2024-01-19 10:04:14`
 */
export interface TaskListRequest {
  /**
   * 来源
   */
  businessSource?: string
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
  auditStatus?: number
  /**
   * 定期任务是否超时，0 不超时，1超时
   */
  overtimeFlag?: number
  page?: number
  pageSize?: number
}

/**
 * 接口 [黑灰名单任务表列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/17851) 的 **返回类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/list`
 * @更新时间 `2024-01-19 10:04:14`
 */
export interface TaskListResponse {
  total?: number
  list?: {
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
     * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
     */
    auditStatus?: number
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
     * 审批流任务id，冗余，便于查找audit_task记录
     */
    auditTaskId?: number
    /**
     * 审批流当前操作人，冗余，方便进行用户数据权限过滤
     */
    currentOperator?: string
    /**
     * 上一操作人，冗余，方便判断能否撤回
     */
    preOperator?: string
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
     * 创建人
     */
    createdBy?: string
    /**
     * 创建时间
     */
    gmtCreate?: string
    /**
     * 更新人
     */
    updatedBy?: string
    /**
     * 更新时间
     */
    gmtUpdate?: string
  }[]
}

/**
 * 接口 [黑灰名单任务表详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/49) 的 **请求类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/detail`
 * @更新时间 `2024-01-19 10:21:52`
 */
export interface TaskDetailRequest {
  /**
   * 任务id
   */
  id: number
}

/**
 * 接口 [黑灰名单任务表详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/49) 的 **返回类型**
 *
 * @分类 [黑灰名单任务表-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_4074)
 * @请求头 `POST /black/gray/warehouse/task/detail`
 * @更新时间 `2024-01-19 10:21:52`
 */
export interface TaskDetailResponse {
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
  orgCode?: string
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
  uploadFileList?: string[]
  /**
   * 关联交易任务类型 timed=定时生成任务， not_timed=非定时生成自由上报任务
   */
  taskType?: string
  /**
   * 审批状态 0=wait， 1=ing， 2=withdraw， 3=reject， 4=finish
   */
  auditStatus?: number
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
   * 审批流任务id，冗余，便于查找audit_task记录
   */
  auditTaskId?: number
  /**
   * 审批流当前操作人，冗余，方便进行用户数据权限过滤
   */
  currentOperator?: string
  /**
   * 上一操作人，冗余，方便判断能否撤回
   */
  preOperator?: string
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
   * 创建人
   */
  createdBy?: string
  /**
   * 创建时间
   */
  gmtCreate?: string
  /**
   * 更新人
   */
  updatedBy?: string
  /**
   * 更新时间
   */
  gmtUpdate?: string
}

/* prettier-ignore-end */
