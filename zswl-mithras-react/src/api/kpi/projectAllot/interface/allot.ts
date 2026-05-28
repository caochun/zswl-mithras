/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [绩效考核-项目分配-分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13094) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2548)
 * @请求头 `POST /kpi/projectdistribution/pagelist`
 * @更新时间 `2023-06-15 13:44:10`
 */
export interface ProjectdistributionPagelistRequest {
  /**
   * 分配状态
   */
  distributionStatus: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
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
}

/**
 * 接口 [绩效考核-项目分配-分页列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13094) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2548)
 * @请求头 `POST /kpi/projectdistribution/pagelist`
 * @更新时间 `2023-06-15 13:44:10`
 */
export interface ProjectdistributionPagelistResponse {
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
     * 分配状态
     */
    distributionStatus?: number
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 合同开始日期（投放日期）
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
     * 项目主办id
     */
    sponsorUserId?: number
    /**
     * 项目主办名称
     */
    sponsorUserName?: string
    /**
     * 分润比
     */
    weightInfoList?: {
      /**
       * id
       */
      id?: number
      /**
       * 分配比重类型
       */
      weightType?: string
      /**
       * 分配比重类型名称
       */
      weightTypeName?: string
      /**
       * 分配比重目标
       */
      weightTarget?: string
      /**
       * 分配比重目标名称
       */
      weightTargetName?: string
      /**
       * 分配比重值
       */
      weightValue?: number
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
 * 接口 [绩效考核-项目分配-提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13089) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2548)
 * @请求头 `POST /kpi/projectdistribution/submit`
 * @更新时间 `2023-06-15 13:44:10`
 */
export interface ProjectdistributionSubmitRequest {
  /**
   * 项目分配id
   */
  projectDistributionId: number
}

/**
 * 接口 [绩效考核-项目分配-提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13089) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2548)
 * @请求头 `POST /kpi/projectdistribution/submit`
 * @更新时间 `2023-06-15 13:44:10`
 */
export interface ProjectdistributionSubmitResponse {
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
 * 接口 [绩效考核-项目分配-查看历史↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13084) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2548)
 * @请求头 `POST /kpi/projectdistribution/history`
 * @更新时间 `2023-06-15 13:44:10`
 */
export interface ProjectdistributionHistoryRequest {
  /**
   * 项目分配id
   */
  projectDistributionId: number
}

/**
 * 接口 [绩效考核-项目分配-查看历史↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13084) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2548)
 * @请求头 `POST /kpi/projectdistribution/history`
 * @更新时间 `2023-06-15 13:44:10`
 */
export type ProjectdistributionHistoryResponse = {
  /**
   * 历史版本号
   */
  version?: string
  /**
   * 操作日期
   */
  operateDate?: string
  /**
   * 分润比重列表
   */
  weightInfoWithTagList?: {
    /**
     * 分配比重目标名称是否需要标红
     */
    weightTargetNameRed?: boolean
    /**
     * 分配比重目标值是否需要标红
     */
    weightValueRed?: boolean
    /**
     * id
     */
    id?: number
    /**
     * 分配比重类型
     */
    weightType?: string
    /**
     * 分配比重类型名称
     */
    weightTypeName?: string
    /**
     * 分配比重目标
     */
    weightTarget?: string
    /**
     * 分配比重目标名称
     */
    weightTargetName?: string
    /**
     * 分配比重值
     */
    weightValue?: number
  }[]
  /**
   * 生效年份
   */
  effectYear?: number
  /**
   * 生效月份
   */
  effectMonth?: number
  /**
   * 变更原因
   */
  changeReason?: string
}[]

/* prettier-ignore-end */
