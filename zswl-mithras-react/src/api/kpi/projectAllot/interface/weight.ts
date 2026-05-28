/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [绩效考核-项目分配-分配比重-保存↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13074) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配-分配比重↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2543)
 * @请求头 `POST /kpi/projectdistribution/weight/save`
 * @更新时间 `2023-06-15 13:43:56`
 */
export interface WeightSaveRequest {
  /**
   * 项目分配id
   */
  projectDistributionId?: number
  /**
   * 生效年份
   */
  year?: number
  /**
   * 生效月份
   */
  month?: number
  /**
   * 分配比重信息
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
}

/**
 * 接口 [绩效考核-项目分配-分配比重-保存↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13074) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配-分配比重↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2543)
 * @请求头 `POST /kpi/projectdistribution/weight/save`
 * @更新时间 `2023-06-15 13:43:56`
 */
export interface WeightSaveResponse {
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
 * 接口 [绩效考核-项目分配-分配比重-详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13164) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配-分配比重↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2543)
 * @请求头 `POST /kpi/projectdistribution/weight/detail`
 * @更新时间 `2023-06-15 17:37:54`
 */
export interface WeightDetailRequest {
  /**
   * 项目分配id
   */
  projectDistributionId: number
}

/**
 * 接口 [绩效考核-项目分配-分配比重-详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13164) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配-分配比重↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2543)
 * @请求头 `POST /kpi/projectdistribution/weight/detail`
 * @更新时间 `2023-06-15 17:37:54`
 */
export interface WeightDetailResponse {
  /**
   * 项目分配id
   */
  projectDistributionId?: number
  /**
   * 生效年份
   */
  year?: number
  /**
   * 生效月份
   */
  month?: number
  /**
   * 分配比重信息
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
}

/* prettier-ignore-end */
