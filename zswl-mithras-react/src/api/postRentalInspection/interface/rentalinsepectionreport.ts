/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [下载指定的单个项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10728) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/single/download`
 * @更新时间 `2022-11-20 09:32:04`
 */
export interface SingleDownloadRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [下载指定的单个项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10728) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/single/download`
 * @更新时间 `2022-11-20 09:32:04`
 */
export interface SingleDownloadResponse {}

/**
 * 接口 [下载指定的批量项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10732) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/batch/download`
 * @更新时间 `2022-11-20 09:32:04`
 */
export interface BatchDownloadRequest {
  /**
   * 业务数据id列表
   */
  ids: number[]
}

/**
 * 接口 [下载指定的批量项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10732) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/batch/download`
 * @更新时间 `2022-11-20 09:32:04`
 */
export interface BatchDownloadResponse {}

/**
 * 接口 [下载指定部门的项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10736) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/dept/download`
 * @更新时间 `2022-11-22 15:09:53`
 */
export interface DeptDownloadRequest {
  /**
   * 租后检查计划id
   */
  planId: number
  /**
   * 业务部门id
   */
  deptId: number
}

/**
 * 接口 [下载指定部门的项目报告↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10736) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/dept/download`
 * @更新时间 `2022-11-22 15:09:53`
 */
export interface DeptDownloadResponse {}

/**
 * 接口 [上传非公用事业类型报告的检查附件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10636) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/file/upload`
 * @更新时间 `2022-11-19 17:09:19`
 */
export interface FileUploadRequest {
  /**
   * 检查计划项目id
   */
  id: string
  /**
   * 上传文件
   */
  file: File
  /**
   * 文件类型
   */
  fileType: string
}

/**
 * 接口 [上传非公用事业类型报告的检查附件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10636) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/file/upload`
 * @更新时间 `2022-11-19 17:09:19`
 */
export interface FileUploadResponse {
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
 * 接口 [保存客户财务报表快照数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10936) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/finance/snapshot/save`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface SnapshotSaveRequest {
  /**
   * 检查计划项目id
   */
  checkPlanProjectId: number
  /**
   * 客户id
   */
  clientId: number
  /**
   * 客户在项目中的身份 承租人-LESSEE 担保人-GUARANTOR
   */
  clientProjectIdentity: string
  /**
   * 报表类型
   */
  subjectType: string
  /**
   * 查询条件json数据
   */
  queryJsonData: string
  /**
   * 查询结果json数据
   */
  resultJsonData?: string
}

/**
 * 接口 [保存客户财务报表快照数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10936) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/finance/snapshot/save`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface SnapshotSaveResponse {
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
 * 接口 [保存非公用事业补充说明信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10608) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/extra/save`
 * @更新时间 `2022-11-19 11:39:10`
 */
export interface ExtraSaveRequest {
  /**
   * 检查计划项目id
   */
  checkPlanProjectId: number
  /**
   * 补充说明内容列表
   */
  contentList: {
    /**
     * id
     */
    id?: number
    /**
     * 模板id
     */
    templateId: number
    /**
     * 检查结果
     */
    checkResult: number
    /**
     * 备注
     */
    remark?: string
  }[]
}

/**
 * 接口 [保存非公用事业补充说明信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10608) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/extra/save`
 * @更新时间 `2022-11-19 11:39:10`
 */
export interface ExtraSaveResponse {
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
 * 接口 [保存项目检查报告基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10560) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/base/save`
 * @更新时间 `2022-11-22 15:09:53`
 */
export interface BaseSaveRequest {
  /**
   * 基本信息id
   */
  id?: number
  /**
   * 检查计划中的项目id
   */
  checkPlanProjectId: number
  /**
   * 检查时段开始 yyyy-MM-dd
   */
  checkPeriodStart?: string
  /**
   * 检查时段结束 yyyy-MM-dd
   */
  checkPeriodEnd?: string
  /**
   * 检查日期
   */
  checkDate: string
  /**
   * 主要受访人员
   */
  mainPerson?: string
  /**
   * 职务
   */
  mainPersonJob?: string
  /**
   * 联系方式
   */
  mainPersonContactWay?: string
  /**
   * 合同金额
   */
  contractAmount?: number
  /**
   * 风险敞口
   */
  riskExposure?: number
  /**
   * 到期日 yyyy-MM-dd
   */
  deadline?: string
  /**
   * 下次还款日
   */
  nextRepayDate?: string
  /**
   * 下次还款金额
   */
  nextRepayAmount?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 行业
   */
  industry?: string
}

/**
 * 接口 [保存项目检查报告基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10560) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/base/save`
 * @更新时间 `2022-11-22 15:09:53`
 */
export type BaseSaveResponse = number

/**
 * 接口 [保存项目检查报告检查内容↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10568) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/content/save`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface ContentSaveRequest {
  /**
   * 检查计划项目id
   */
  checkPlanProjectId: number
  /**
   * 检查内容/总结列表
   */
  contentList: {
    /**
     * 检查内容/总结id
     */
    id?: number
    /**
     * 检查内容/总结模板id
     */
    templateId: number
    /**
     * 填写/选择的检查内容/总结
     */
    content?: string
  }[]
}

/**
 * 接口 [保存项目检查报告检查内容↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10568) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/content/save`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface ContentSaveResponse {
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
 * 接口 [保存项目检查报告检查总结↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10572) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/summary/save`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface SummarySaveRequest {
  /**
   * 检查计划项目id
   */
  checkPlanProjectId: number
  /**
   * 检查内容/总结列表
   */
  contentList: {
    /**
     * 检查内容/总结id
     */
    id?: number
    /**
     * 检查内容/总结模板id
     */
    templateId: number
    /**
     * 填写/选择的检查内容/总结
     */
    content?: string
  }[]
}

/**
 * 接口 [保存项目检查报告检查总结↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10572) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/summary/save`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface SummarySaveResponse {
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
 * 接口 [获取客户财务报表快照数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10924) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/finance/snapshot`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface FinanceSnapshotRequest {
  /**
   * 检查项目id
   */
  checkPlanProjectId: number
  /**
   * 客户id不能为空
   */
  clientId: number
  /**
   * 客户在项目中的身份 承租人-LESSEE 担保人-GUARANTOR
   */
  clientProjectIdentity: string
  /**
   * 财务报表类型
   */
  subjectType: string
}

/**
 * 接口 [获取客户财务报表快照数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10924) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/finance/snapshot`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface FinanceSnapshotResponse {
  /**
   * 查询条件数据
   */
  queryData?: {
    /**
     * 客户id
     */
    clientId: number
    /**
     * sheet类型
     */
    subjectType: string
    /**
     * 报告期
     */
    quarter?: number
    /**
     * 最新
     */
    latest?: boolean
    /**
     * 年-从
     */
    yearFrom?: number
    /**
     * 年-到
     */
    yearTo?: number
    /**
     * 报表类型
     */
    reportType?: string
    /**
     * 数据显示维度；
     */
    displayDimensions: string[]
    /**
     * 小数位数
     */
    decimalCount?: number
    /**
     * 金额单位
     */
    unit?: number
  }
  /**
   * 查询结果数据
   */
  resultData?: {
    /**
     * sheet名称
     */
    subjectType?: string
    /**
     * 报告类型
     */
    reportType?: string
    /**
     * 年份
     */
    year?: number
    /**
     * 报告期
     */
    quarter?: number
    /**
     * 科目列表
     */
    itemList?: {
      id?: number
      /**
       * 科目代码
       */
      subjectCode?: string
      /**
       * 科目名称
       */
      subjectName?: string
      /**
       * 科目值，已经扩大10000倍
       */
      subjectValue?: number
      /**
       * 百分比，同金额，已经扩大10000倍
       */
      subjectPercent?: number
      /**
       * 同比，已经扩大10000倍
       */
      subjectOverYear?: number
      /**
       * 科目值，已转字符串
       */
      subjectValueStr?: string
      /**
       * 百分比str
       */
      subjectPercentStr?: string
      /**
       * 同比百分比str
       */
      subjectOverYearStr?: string
    }[]
  }[]
}

/**
 * 接口 [获取检查报告中用户上传的财务数据文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10984) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/file/finance/list`
 * @更新时间 `2022-11-26 14:09:01`
 */
export interface FinanceListRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取检查报告中用户上传的财务数据文件↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10984) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/file/finance/list`
 * @更新时间 `2022-11-26 14:09:01`
 */
export type FinanceListResponse = {
  key?: string
  value?: {
    /**
     * 报告文件id
     */
    id?: number
    /**
     * 报告名称
     */
    typeName?: string
    /**
     * 材料类型
     */
    materialsType?: string
    /**
     * 材料子类型
     */
    materialsSubType?: string
    /**
     * 文档名称
     */
    fileName?: string
    /**
     * 上传人
     */
    creator?: string
    /**
     * 上传时间
     */
    createTime?: string
    /**
     * 排序优先级
     */
    sort?: number
  }[]
}[]

/**
 * 接口 [获取非公用事业类型报告的检查附件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10640) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/file/list`
 * @更新时间 `2022-11-24 11:47:20`
 */
export interface FileListRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取非公用事业类型报告的检查附件列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10640) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/file/list`
 * @更新时间 `2022-11-24 11:47:20`
 */
export type FileListResponse = {
  key?: string
  value?: {
    /**
     * 报告文件id
     */
    id?: number
    /**
     * 报告名称
     */
    typeName?: string
    /**
     * 材料类型
     */
    materialsType?: string
    /**
     * 材料子类型
     */
    materialsSubType?: string
    /**
     * 文档名称
     */
    fileName?: string
    /**
     * 上传人
     */
    creator?: string
    /**
     * 上传时间
     */
    createTime?: string
    /**
     * 排序优先级
     */
    sort?: number
  }[]
}[]

/**
 * 接口 [获取非公用事业补充说明信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10604) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/extra/get`
 * @更新时间 `2022-11-18 17:10:00`
 */
export interface ExtraGetRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取非公用事业补充说明信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10604) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/nonpublic/extra/get`
 * @更新时间 `2022-11-18 17:10:00`
 */
export type ExtraGetResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 模板id
   */
  templateId?: number
  /**
   * 模板条目
   */
  templateTitle?: string
  /**
   * 检查结果
   */
  checkResult?: number
  /**
   * 备注
   */
  remark?: string
}[]

/**
 * 接口 [获取项目检查报告基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10556) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/base/get`
 * @更新时间 `2022-11-23 16:03:03`
 */
export interface BaseGetRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取项目检查报告基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10556) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/base/get`
 * @更新时间 `2022-11-23 16:03:03`
 */
export interface BaseGetResponse {
  /**
   * 基本信息id
   */
  id?: number
  /**
   * 检查计划中的项目id
   */
  checkPlanProjectId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 行业
   */
  industry?: string
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 项目主办名称
   */
  projectSponsorName?: string
  /**
   * 项目协办名称
   */
  projectCosponsorNames?: string[]
  /**
   * 检查时段开始 yyyy-MM-dd
   */
  checkPeriodStart?: string
  /**
   * 检查时段结束 yyyy-MM-dd
   */
  checkPeriodEnd?: string
  /**
   * 检查日期
   */
  checkDate?: string
  /**
   * 主要受访人员
   */
  mainPerson?: string
  /**
   * 职务
   */
  mainPersonJob?: string
  /**
   * 联系方式
   */
  mainPersonContactWay?: string
  /**
   * 合同金额
   */
  contractAmount?: number
  /**
   * 风险敞口
   */
  riskExposure?: number
  /**
   * 到期日 yyyy-MM-dd
   */
  deadline?: string
  /**
   * 下次付款日期
   */
  nextRepayDate?: string
  /**
   * 下次付款金额
   */
  nextRepayAmount?: number
  /**
   * 协查风控经理
   */
  riskManagerName?: string
  /**
   * 检查形式
   */
  checkWay?: string
}

/**
 * 接口 [获取项目检查报告检查内容↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10552) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/content/get`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface ContentGetRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取项目检查报告检查内容↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10552) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/content/get`
 * @更新时间 `2022-11-25 11:36:41`
 */
export type ContentGetResponse = {
  /**
   * 内容/总结分组名称
   */
  groupName?: string
  /**
   * 内容/总结列表
   */
  contentList?: {
    /**
     * 内容/总结模板条目id
     */
    templateId?: number
    /**
     * 内容/总结条目名称
     */
    templateTitle?: string
    /**
     * 内容/总结输入类型
     */
    templateContentInputType?: string
    /**
     * 输入类型枚举（下拉框、单选框等）
     */
    templateOptionList?: {
      label?: string
      value?: string
    }[]
    /**
     * 内容/总结id
     */
    id?: number
    /**
     * 内容
     */
    content?: string
  }[]
}[]

/**
 * 接口 [获取项目检查报告检查总结↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10532) 的 **请求类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/summary/get`
 * @更新时间 `2022-11-25 11:36:41`
 */
export interface SummaryGetRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [获取项目检查报告检查总结↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10532) 的 **返回类型**
 *
 * @分类 [租后检查项目报告相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1858)
 * @请求头 `POST /afterlease/check/project/report/summary/get`
 * @更新时间 `2022-11-25 11:36:41`
 */
export type SummaryGetResponse = {
  /**
   * 内容/总结分组名称
   */
  groupName?: string
  /**
   * 内容/总结列表
   */
  contentList?: {
    /**
     * 内容/总结模板条目id
     */
    templateId?: number
    /**
     * 内容/总结条目名称
     */
    templateTitle?: string
    /**
     * 内容/总结输入类型
     */
    templateContentInputType?: string
    /**
     * 输入类型枚举（下拉框、单选框等）
     */
    templateOptionList?: {
      label?: string
      value?: string
    }[]
    /**
     * 内容/总结id
     */
    id?: number
    /**
     * 内容
     */
    content?: string
  }[]
}[]

/* prettier-ignore-end */
