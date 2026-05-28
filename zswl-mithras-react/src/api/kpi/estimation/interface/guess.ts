/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [绩效-项目测算表-人员列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13189) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/people/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export interface GuesspeopleListRequest {
  contractCode?: string
  projName?: string
  deptId?: number
  calculateDate?: string
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
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
 * 接口 [绩效-项目测算表-人员列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13189) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/people/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export type GuesspeopleListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * kpiProjGuessId
   */
  kpiProjGuessId?: number
  /**
   * 分配类型
   */
  divideType?: string
  /**
   * 分配类型名称
   */
  divideTypeName?: string
  /**
   * 分配目标id
   */
  divideTarget?: number
  /**
   * 分配目标名称
   */
  divideTargetName?: string
  /**
   * 分润-当期值
   */
  profitCurrent?: number
  /**
   * 分润-累计值
   */
  profitTotal?: number
  /**
   * 奖金-当期值
   */
  bonusCurrent?: number
  /**
   * 奖金-累计值
   */
  bonusTotal?: number
  /**
   * 核算月份
   */
  calculateDate?: string
  /**
   * 分配年
   */
  divideYear?: number
  /**
   * 分配月
   */
  divideMonth?: number
}[]

/**
 * 接口 [绩效-项目测算表-人员详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/13209) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/people/detail`
 * @更新时间 `2024-09-29 14:47:04`
 */
export interface GuesspeopleDetailRequest {
  contractId?: number
  calculateDateYear?: number
  calculateDateMonth?: number
  deptId?: number
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
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
 * 接口 [绩效-项目测算表-人员详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/13209) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/people/detail`
 * @更新时间 `2024-09-29 14:47:04`
 */
export interface GuesspeopleDetailResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * kpiProjGuessId
     */
    kpiProjGuessId?: number
    /**
     * 分配类型
     */
    divideType?: string
    /**
     * 分配类型名称
     */
    divideTypeName?: string
    /**
     * 分配目标id
     */
    divideTarget?: number
    /**
     * 分配目标名称
     */
    divideTargetName?: string
    /**
     * 分润-当期值
     */
    profitCurrent?: number
    /**
     * 分润-累计值
     */
    profitTotal?: number
    /**
     * 奖金-当期值
     */
    bonusCurrent?: number
    /**
     * 奖金-累计值
     */
    bonusTotal?: number
    /**
     * 核算月份
     */
    calculateDate?: string
    /**
     * 分配年
     */
    divideYear?: number
    /**
     * 分配月
     */
    divideMonth?: number
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
 * 接口 [绩效-项目测算表-合同列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22993) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/contract/list`
 * @更新时间 `2024-09-30 10:38:22`
 */
export interface GuesscontractListRequest {
  contractCode?: string
  projName?: string
  deptId?: number
  calculateDate?: string
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
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
 * 接口 [绩效-项目测算表-合同列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22993) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/contract/list`
 * @更新时间 `2024-09-30 10:38:22`
 */
export interface GuesscontractListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 合同id
     */
    contractId?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 项目来源
     */
    projSource?: string
    /**
     * 项目类型
     */
    projClassify?: string
    /**
     * 投放日
     */
    contractStartDate?: string
    /**
     * 所属部门id
     */
    belongDeptId?: number
    /**
     * 所属部门名称
     */
    belongDeptName?: string
    /**
     * 所属主办id
     */
    sponsorUserId?: number
    /**
     * 所属主办名称
     */
    sponsorUserName?: string
    /**
     * 项目利润-当期值
     */
    profitCurrent?: number
    /**
     * 项目利润-累计值
     */
    profitTotal?: number
    /**
     * 项目利润-累计值
     */
    profitAdjust?: number
    /**
     * 奖金-当期值
     */
    bonusCurrent?: number
    /**
     * 奖金-调整值
     */
    bonusAdjust?: number
    /**
     * 奖金-累计值
     */
    bonusTotal?: number
    /**
     * 核算月份
     */
    calculateDate?: string
    calculateDateYear?: number
    calculateDateMonth?: number
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
 * 接口 [绩效-项目测算表-合同详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/13194) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/contract/detail`
 * @更新时间 `2024-09-30 10:38:23`
 */
export interface GuesscontractDetailRequest {
  contractId?: number
  calculateDateYear?: number
  calculateDateMonth?: number
  deptId?: number
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
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
 * 接口 [绩效-项目测算表-合同详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/13194) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/contract/detail`
 * @更新时间 `2024-09-30 10:38:23`
 */
export interface GuesscontractDetailResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 合同id
     */
    contractId?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 占比
     */
    divideWeight?: number
    /**
     * 项目类型
     */
    projClassify?: string
    /**
     * 项目来源
     */
    projSource?: string
    /**
     * 合同开始日期（投放日期）
     */
    contractStartDate?: string
    /**
     * 合同终止时间
     */
    contractEndDate?: string
    /**
     * 所属部门id
     */
    belongDeptId?: number
    /**
     * 所属部门名称
     */
    belongDeptName?: string
    /**
     * 提奖比例
     */
    awardRatio?: number
    /**
     * 利润-当期值
     */
    profitCurrent?: number
    /**
     * 利润-累计值
     */
    profitTotal?: number
    /**
     * 项目本年累计投放金额
     */
    projPaymentYearAmount?: number
    /**
     * 合同本月累计投放金额
     */
    contractPaymentMonthAmount?: number
    /**
     * 核算月份
     */
    calculateDate?: string
    calculateDateYear?: number
    calculateDateMonth?: number
    /**
     * 分润比
     */
    weightInfoList?: {
      /**
       * id
       */
      id?: number
      /**
       * 占比
       */
      weightValue?: number
      /**
       * 分配比重类型
       */
      divideType?: string
      /**
       * 分配比重类型名称
       */
      divideTypeName?: string
      /**
       * 分配比重目标
       */
      divideTarget?: number
      /**
       * 分配比重目标名称
       */
      divideTargetName?: string
      /**
       * 利润-当期值
       */
      profitCurrent?: number
      /**
       * 利润-累计值
       */
      profitTotal?: number
      /**
       * 利润-调整值
       */
      profitAdjust?: number
      /**
       * 奖金-当期值
       */
      bonusCurrent?: number
      /**
       * 奖金-累计值
       */
      bonusTotal?: number
      /**
       * bonus_adjust
       */
      bonusAdjust?: number
      /**
       * payment_current
       */
      paymentCurrent?: number
      /**
       * payment_total
       */
      paymentTotal?: number
      /**
       * divide_year
       */
      divideYear?: number
      /**
       * divide_month
       */
      divideMonth?: number
    }[]
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
 * 接口 [绩效-项目测算表-时间列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13179) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/time/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export interface GuesstimeListRequest {
  contractCode?: string
  projName?: string
  deptId?: number
  calculateDate?: string
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
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
 * 接口 [绩效-项目测算表-时间列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13179) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/time/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export type GuesstimeListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 合同id
   */
  contractId?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目来源
   */
  projSource?: string
  /**
   * 项目类型
   */
  projClassify?: string
  /**
   * 投放日
   */
  contractStartDate?: string
  /**
   * 所属部门id
   */
  belongDeptId?: number
  /**
   * 所属部门名称
   */
  belongDeptName?: string
  /**
   * 所属主办id
   */
  sponsorUserId?: number
  /**
   * 所属主办名称
   */
  sponsorUserName?: string
  /**
   * 项目利润-当期值
   */
  profitCurrent?: number
  /**
   * 项目利润-累计值
   */
  profitTotal?: number
  /**
   * 项目利润-累计值
   */
  profitAdjust?: number
  /**
   * 奖金-当期值
   */
  bonusCurrent?: number
  /**
   * 奖金-调整值
   */
  bonusAdjust?: number
  /**
   * 奖金-累计值
   */
  bonusTotal?: number
  /**
   * 核算月份
   */
  calculateDate?: string
  calculateDateYear?: number
  calculateDateMonth?: number
}[]

/**
 * 接口 [绩效-项目测算表-测算↗](http://yapi.zswltec.com:3000/project/11/interface/api/13174) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/calculate`
 * @更新时间 `2024-09-29 14:49:21`
 */
export interface ProjguessCalculateRequest {
  /**
   * 核算月份
   */
  calculateDate: string
  contractIds?: number[]
}

/**
 * 接口 [绩效-项目测算表-测算↗](http://yapi.zswltec.com:3000/project/11/interface/api/13174) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/calculate`
 * @更新时间 `2024-09-29 14:49:21`
 */
export interface ProjguessCalculateResponse {
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
 * 接口 [绩效-项目测算表-部门列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13184) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/dept/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export interface GuessdeptListRequest {
  contractCode?: string
  projName?: string
  deptId?: number
  calculateDate?: string
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
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
 * 接口 [绩效-项目测算表-部门列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/13184) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/dept/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export type GuessdeptListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 合同id
   */
  contractId?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目来源
   */
  projSource?: string
  /**
   * 项目类型
   */
  projClassify?: string
  /**
   * 投放日
   */
  contractStartDate?: string
  /**
   * 所属部门id
   */
  belongDeptId?: number
  /**
   * 所属部门名称
   */
  belongDeptName?: string
  /**
   * 所属主办id
   */
  sponsorUserId?: number
  /**
   * 所属主办名称
   */
  sponsorUserName?: string
  /**
   * 项目利润-当期值
   */
  profitCurrent?: number
  /**
   * 项目利润-累计值
   */
  profitTotal?: number
  /**
   * 项目利润-累计值
   */
  profitAdjust?: number
  /**
   * 奖金-当期值
   */
  bonusCurrent?: number
  /**
   * 奖金-调整值
   */
  bonusAdjust?: number
  /**
   * 奖金-累计值
   */
  bonusTotal?: number
  /**
   * 核算月份
   */
  calculateDate?: string
  calculateDateYear?: number
  calculateDateMonth?: number
}[]

/**
 * 接口 [绩效-项目测算表-部门池列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22735) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/dept/pool/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export interface DeptpoolListRequest {
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
 * 接口 [绩效-项目测算表-部门池列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22735) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/dept/pool/list`
 * @更新时间 `2024-09-29 14:47:04`
 */
export type DeptpoolListResponse = {
  /**
   * 核算月份
   */
  calculateDate?: string
  /**
   * 利润-部门池
   */
  profitCurrent?: number
  /**
   * 奖金-部门池
   */
  bonusCurrent?: number
  /**
   * 投放-部门池
   */
  paymentCurrent?: number
  /**
   * 合计奖金-部门池
   */
  amount?: number
  /**
   * 分配年
   */
  divideYear?: number
  /**
   * 分配月
   */
  divideMonth?: number
}[]

/**
 * 接口 [绩效-项目测算表-部门池详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22741) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/dept/pool/detail`
 * @更新时间 `2024-10-09 09:25:02`
 */
export interface DeptpoolDetailRequest {
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
 * 接口 [绩效-项目测算表-部门池详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22741) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/dept/pool/detail`
 * @更新时间 `2024-10-09 09:25:02`
 */
export type DeptpoolDetailResponse = {
  /**
   * 分配类型
   */
  divideType?: string
  /**
   * 分配类型名称
   */
  divideTypeName?: string
  /**
   * 分配目标id
   */
  divideTarget?: number
  /**
   * 分配目标名称
   */
  divideTargetName?: string
  /**
   * 利润-部门池
   */
  profitTotal?: number
  /**
   * 奖金-部门池
   */
  bonusTotal?: number
  /**
   * 投放-部门池
   */
  paymentTotal?: number
  /**
   * 投放-部门池
   */
  paymentTotalAmount?: number
  /**
   * 合计奖金-部门池
   */
  amount?: number
}[]

/**
 * 接口 [绩效-项目测算表-项目经理列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22711) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/list`
 * @更新时间 `2024-10-09 09:25:02`
 */
export interface ProjmanagerListRequest {}

/**
 * 接口 [绩效-项目测算表-项目经理列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22711) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/list`
 * @更新时间 `2024-10-09 09:25:02`
 */
export type ProjmanagerListResponse = {
  /**
   * 核算月份
   */
  calculateDate?: string
  /**
   * 利润-当期值
   */
  profitCurrent?: number
  /**
   * 利润-累计值
   */
  profitTotal?: number
  /**
   * 主办奖金-当期值
   */
  bonusCurrent?: number
  /**
   * 主办投放-当期值
   */
  paymentCurrent?: number
  /**
   * 协办奖金-当期值
   */
  bonusCurrentDeputy?: number
  /**
   * 主办投放-当期值
   */
  paymentCurrentDeputy?: number
  /**
   * 分配年
   */
  calculateDateYear?: number
  /**
   * 分配月
   */
  calculateDateMonth?: number
}[]

/**
 * 接口 [绩效-项目测算表-项目经理利润完成率列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22717) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/completion/list`
 * @更新时间 `2024-09-30 10:38:23`
 */
export interface ManagercompletionListRequest {}

/**
 * 接口 [绩效-项目测算表-项目经理利润完成率列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/22717) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/completion/list`
 * @更新时间 `2024-09-30 10:38:23`
 */
export type ManagercompletionListResponse = {
  /**
   * 核算月份
   */
  calculateDate?: string
  /**
   * 利润-当期值
   */
  profitCurrent?: number
  /**
   * 利润-累计值
   */
  profitTotal?: number
  /**
   * 主办奖金-当期值-本年存量
   */
  bonusCurrent?: number
  /**
   * 主办投放-当期值-本年存量
   */
  paymentCurrent?: number
  /**
   * 协办奖金-当期值-本年存量
   */
  bonusCurrentDeputy?: number
  /**
   * 主办投放-当期值-本年存量
   */
  paymentCurrentDeputy?: number
  /**
   * 主办奖金-当期值-本年新增
   */
  bonusCurrentAdd?: number
  /**
   * 主办投放-当期值-本年新增
   */
  paymentCurrentAdd?: number
  /**
   * 协办奖金-当期值-本年新增
   */
  bonusCurrentDeputyAdd?: number
  /**
   * 主办投放-当期值-本年新增
   */
  paymentCurrentDeputyAdd?: number
  /**
   * 分配年
   */
  divideYear?: number
  /**
   * 分配月
   */
  divideMonth?: number
}[]

/**
 * 接口 [绩效-项目测算表-项目经理利润完成率详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22729) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/completion/detail`
 * @更新时间 `2024-10-09 09:25:02`
 */
export interface ManagercompletionDetailRequest {
  /**
   * 分配年
   */
  calculateDateYear?: number
  /**
   * 分配月
   */
  calculateDateMonth?: number
  contractCode?: string
  projName?: string
  /**
   * 所属部门
   */
  deptId?: number
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
  /**
   * 项目类别 KpiProjectClassifyEnum#name()
   */
  projClassify?: string
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
 * 接口 [绩效-项目测算表-项目经理利润完成率详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22729) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/completion/detail`
 * @更新时间 `2024-10-09 09:25:02`
 */
export type ManagercompletionDetailResponse = {
  deptId?: number
  /**
   * 考核部门
   */
  deptName?: string
  /**
   * 分配类型
   */
  divideType?: string
  /**
   * 分配类型名称
   */
  divideTypeName?: string
  /**
   * 分配目标id
   */
  divideTarget?: number
  /**
   * 分配目标名称
   */
  divideTargetName?: string
  /**
   * 本年存量产业类
   */
  industryStock?: {
    /**
     * 主办奖金-当期值
     */
    bonusCurrent?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrent?: number
    /**
     * 协办奖金-当期值
     */
    bonusCurrentDeputy?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrentDeputy?: number
    /**
     * 推荐人奖金-当期值
     */
    bonusCurrentReference?: number
    /**
     * 推荐人投放-当期值
     */
    paymentCurrentReference?: number
  }
  /**
   * 本年存量公共事业类
   */
  publicStock?: {
    /**
     * 主办奖金-当期值
     */
    bonusCurrent?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrent?: number
    /**
     * 协办奖金-当期值
     */
    bonusCurrentDeputy?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrentDeputy?: number
    /**
     * 推荐人奖金-当期值
     */
    bonusCurrentReference?: number
    /**
     * 推荐人投放-当期值
     */
    paymentCurrentReference?: number
  }
  /**
   * 本年存量利润合计
   */
  bonusAmountStock?: number
  /**
   * 本年存量投放合计
   */
  paymentAmountStock?: number
  /**
   * 本年新增产业类
   */
  industryAdd?: {
    /**
     * 主办奖金-当期值
     */
    bonusCurrent?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrent?: number
    /**
     * 协办奖金-当期值
     */
    bonusCurrentDeputy?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrentDeputy?: number
    /**
     * 推荐人奖金-当期值
     */
    bonusCurrentReference?: number
    /**
     * 推荐人投放-当期值
     */
    paymentCurrentReference?: number
  }
  /**
   * 本年新增公共事业类
   */
  publicAdd?: {
    /**
     * 主办奖金-当期值
     */
    bonusCurrent?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrent?: number
    /**
     * 协办奖金-当期值
     */
    bonusCurrentDeputy?: number
    /**
     * 主办投放-当期值
     */
    paymentCurrentDeputy?: number
    /**
     * 推荐人奖金-当期值
     */
    bonusCurrentReference?: number
    /**
     * 推荐人投放-当期值
     */
    paymentCurrentReference?: number
  }
  /**
   * 本年新增利润合计
   */
  bonusAmountAdd?: number
  /**
   * 本年新增投放合计
   */
  paymentAmountAdd?: number
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
}[]

/**
 * 接口 [绩效-项目测算表-项目经理详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22723) 的 **请求类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/detail`
 * @更新时间 `2024-10-09 09:25:02`
 */
export interface ProjmanagerDetailRequest {
  /**
   * 分配年
   */
  calculateDateYear?: number
  /**
   * 分配月
   */
  calculateDateMonth?: number
  contractCode?: string
  projName?: string
  /**
   * 所属部门
   */
  deptId?: number
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
  /**
   * 项目类别 KpiProjectClassifyEnum#name()
   */
  projClassify?: string
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
 * 接口 [绩效-项目测算表-项目经理详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/22723) 的 **返回类型**
 *
 * @分类 [绩效-项目测算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3278)
 * @请求头 `POST /kpi/proj/guess/proj/manager/detail`
 * @更新时间 `2024-10-09 09:25:02`
 */
export type ProjmanagerDetailResponse = {
  /**
   * 所属部门
   */
  deptId?: number
  /**
   * 所属部门名称
   */
  deptName?: string
  /**
   * 分配比重类型 KpiProjectWeightTypeEnum
   */
  divideType?: string
  divideTargetId?: number
  /**
   * 主办奖金-当期值
   */
  bonusCurrent?: number
  /**
   * 主办投放-当期值
   */
  paymentCurrent?: number
  /**
   * 协办奖金-当期值
   */
  bonusCurrentDeputy?: number
  /**
   * 主办投放-当期值
   */
  paymentCurrentDeputy?: number
  /**
   * 推荐人奖金-当期值
   */
  bonusCurrentReference?: number
  /**
   * 推荐人投放-当期值
   */
  paymentCurrentReference?: number
  /**
   * 合计
   */
  amount?: number
}[]

/* prettier-ignore-end */
