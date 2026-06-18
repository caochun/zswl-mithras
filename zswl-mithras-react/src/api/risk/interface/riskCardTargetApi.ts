/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改评分卡指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11828) 的 **请求类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/modify`
 * @更新时间 `2023-03-03 16:32:36`
 */
export interface TargetModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 评分卡id
   */
  cardId?: number
  /**
   * 指标id
   */
  targetId?: number
  /**
   * 指标名称
   */
  targetName?: string
  /**
   * 指标权重
   */
  targetWeight?: number
  /**
   * 打分类型
   */
  gradeType?: number
  /**
   * 分区类型
   */
  areaStatus?: string
  /**
   * 选项记分
   */
  optionGrade?: {
    /**
     * 优良
     */
    good?: number
    /**
     * 中等
     */
    moderate?: number
    /**
     * 较差
     */
    bad?: number
  }
  /**
   * 分区详情
   */
  areaConfig?: {
    /**
     * 城市类型 AreaTypeEnum
     */
    areaType?: string
    /**
     * 最小值
     */
    min?: number
    /**
     * 最大值
     */
    max?: number
  }[]
}

/**
 * 接口 [修改评分卡指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11828) 的 **返回类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/modify`
 * @更新时间 `2023-03-03 16:32:36`
 */
export interface TargetModifyResponse {
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
 * 接口 [删除评分卡指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11829) 的 **请求类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/remove`
 * @更新时间 `2023-02-28 14:55:30`
 */
export interface TargetRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除评分卡指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11829) 的 **返回类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/remove`
 * @更新时间 `2023-02-28 14:55:30`
 */
export interface TargetRemoveResponse {
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
 * 接口 [新增评分卡指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11827) 的 **请求类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/add`
 * @更新时间 `2023-03-03 17:04:35`
 */
export interface TargetAddRequest {
  /**
   * 评分卡id
   */
  cardId: number
  /**
   * 指标id
   */
  targetId: number
  /**
   * 指标名称
   */
  targetName: string
  /**
   * 指标权重
   */
  targetWeight: number
  /**
   * 打分类型 GradeEnum
   */
  gradeType: number
  /**
   * 分区类型
   */
  areaStatus: string
  /**
   * 选项记分
   */
  optionGrade: {
    /**
     * 优良
     */
    good: number
    /**
     * 中等
     */
    moderate: number
    /**
     * 较差
     */
    bad: number
  }
  /**
   * 分区详情
   */
  areaConfig: {
    /**
     * 城市类型 AreaTypeEnum
     */
    areaType: string
    /**
     * 最小值
     */
    min: number
    /**
     * 最大值
     */
    max: number
  }[]
}

/**
 * 接口 [新增评分卡指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11827) 的 **返回类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/add`
 * @更新时间 `2023-03-03 17:04:35`
 */
export interface TargetAddResponse {
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
 * 接口 [查询指标名称↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11831) 的 **请求类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/search`
 * @更新时间 `2023-02-28 15:29:12`
 */
export interface TargetSearchRequest {
  /**
   * 年份
   */
  year: number
  /**
   * 指标名称
   */
  targetName?: string
}

/**
 * 接口 [查询指标名称↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11831) 的 **返回类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/search`
 * @更新时间 `2023-02-28 15:29:12`
 */
export type TargetSearchResponse = {
  /**
   * 指标id
   */
  id?: number
  /**
   * 指标名称
   */
  name?: string
}[]

/**
 * 接口 [评分卡指标列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11830) 的 **请求类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/list`
 * @更新时间 `2023-03-03 16:32:36`
 */
export interface TargetListRequest {
  /**
   * 评分卡id
   */
  cardId?: number
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
 * 接口 [评分卡指标列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11830) 的 **返回类型**
 *
 * @分类 [评分卡指标-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2168)
 * @请求头 `POST /risk/control/score/card/target/list`
 * @更新时间 `2023-03-03 16:32:36`
 */
export interface TargetListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 评分卡id
     */
    cardId?: number
    /**
     * 指标id
     */
    targetId?: number
    /**
     * 指标名称
     */
    targetName?: string
    /**
     * 指标权重
     */
    targetWeight?: number
    /**
     * 打分类型
     */
    gradeType?: string
    /**
     * 分区类型
     */
    areaStatus?: number
    /**
     * 选项记分
     */
    optionGrade?: {
      /**
       * 优良
       */
      good?: number
      /**
       * 中等
       */
      moderate?: number
      /**
       * 较差
       */
      bad?: number
    }
    /**
     * 分区详情
     */
    areaConfig?: {
      /**
       * 城市类型 AreaTypeEnum
       */
      areaType?: string
      /**
       * 最小值
       */
      min?: number
      /**
       * 最大值
       */
      max?: number
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
}

/* prettier-ignore-end */
