/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [下载经济数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11947) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `GET /risk/control/score/card/downLoad`
 * @更新时间 `2023-03-07 16:21:51`
 */
export interface CardDownLoadRequest {
  /**
   * 年份 默认当年
   */
  year?: string
}

/**
 * 接口 [下载经济数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11947) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `GET /risk/control/score/card/downLoad`
 * @更新时间 `2023-03-07 16:21:51`
 */
export interface CardDownLoadResponse {
  /**
   * 文件地址
   */
  fileUrl?: string
  /**
   * 文件
   */
  id?: number
  /**
   * 归属id
   */
  belongId?: number
  /**
   * 业务类型
   */
  businessType?: string
  /**
   * 资料类型
   */
  materialsType?: string
  /**
   * 资料类型名称
   */
  materialsTypeName?: string
  /**
   * 资料子类型
   */
  materialSubType?: string
  /**
   * 资料子类型名称
   */
  materialSubTypeName?: string
  /**
   * oss上传文件名
   */
  ossFilename?: string
  /**
   * 附件名
   */
  filename?: string
  /**
   * 文件名后缀
   */
  suffix?: string
  /**
   * 文件路径
   */
  filePath?: string
  systemGenerate?: number
  /**
   * 上传时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 上传人id
   */
  createBy?: number
  /**
   * 上传人姓名
   */
  createByName?: string
  /**
   * 修改人id
   */
  updateBy?: number
  /**
   * 修改人姓名
   */
  updateByName?: string
}

/**
 * 接口 [保存计算结果↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11968) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/calculate/save`
 * @更新时间 `2023-03-17 17:08:42`
 */
export interface CalculateSaveRequest {
  /**
   * 地区
   */
  area: string
  /**
   * 省
   */
  province: string
  /**
   * 市
   */
  city: string
  /**
   * 行政级别
   */
  executiveLevel?: string
  /**
   * 区域级别
   */
  regionalLevel?: string
  /**
   * 年份
   */
  year?: number
  /**
   * 各指标打分详情
   */
  targetScoreBodies: {
    /**
     * 指标id
     */
    targetId?: number
    /**
     * 数值
     */
    data?: string
    /**
     * 得分
     */
    score?: number
  }[]
}

/**
 * 接口 [保存计算结果↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11968) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/calculate/save`
 * @更新时间 `2023-03-17 17:08:42`
 */
export interface CalculateSaveResponse {
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
 * 接口 [地区全量查找↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11850) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/area/search`
 * @更新时间 `2023-03-21 15:24:54`
 */
export interface AreaSearchRequest {
  /**
   * 类型 TitleNameEnum
   */
  areaType?: string
  /**
   * 地区名称
   */
  areaName?: string
}

/**
 * 接口 [地区全量查找↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11850) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/area/search`
 * @更新时间 `2023-03-21 15:24:54`
 */
export type AreaSearchResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 地区名称
   */
  areaName?: string
}[]

/**
 * 接口 [地区全量查找↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11991) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/area/all`
 * @更新时间 `2023-03-21 15:24:53`
 */
export interface AreaAllRequest {}

/**
 * 接口 [地区全量查找↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11991) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/area/all`
 * @更新时间 `2023-03-21 15:24:53`
 */
export type AreaAllResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 类型 省/市/地区 TitleNameEnum
   */
  areaType?: string
  /**
   * 地区名称
   */
  areaName?: string
  child?: {}[]
}[]

/**
 * 接口 [导入经济数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11837) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/import`
 * @更新时间 `2023-03-02 15:38:50`
 */
export interface CardImportRequest {
  /**
   * 经济数据文件
   */
  file: File
}

/**
 * 接口 [导入经济数据↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11837) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/import`
 * @更新时间 `2023-03-02 15:38:50`
 */
export interface CardImportResponse {
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
 * 接口 [查询地区得分详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11971) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/calculate/detail`
 * @更新时间 `2023-03-21 10:19:37`
 */
export interface CalculateDetailRequest {
  /**
   * 地区
   */
  area?: string
  /**
   * 省
   */
  province?: string
  /**
   * 市
   */
  city?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 年份
   */
  year?: number
}

/**
 * 接口 [查询地区得分详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11971) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/calculate/detail`
 * @更新时间 `2023-03-21 10:19:37`
 */
export interface CalculateDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 地区
   */
  area?: string
  /**
   * 省
   */
  province?: string
  /**
   * 市
   */
  city?: string
  /**
   * 行政级别
   */
  executiveLevel?: string
  /**
   * 区域级别
   */
  regionalLevel?: string
  /**
   * 年份
   */
  year?: number
  /**
   * 总分
   */
  totalPoints?: number
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 修改时间
   */
  updateTime?: string
  /**
   * 指标打分详情
   */
  calculateDetailBodies?: {
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
     * 数值
     */
    data?: string
    /**
     * 得分
     */
    score?: number
  }[]
}

/**
 * 接口 [生效↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11838) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/effect`
 * @更新时间 `2023-03-02 17:27:56`
 */
export interface CardEffectRequest {
  /**
   * 评分卡id
   */
  id: number
}

/**
 * 接口 [生效↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11838) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/effect`
 * @更新时间 `2023-03-02 17:27:56`
 */
export interface CardEffectResponse {
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
 * 接口 [获取评分卡信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11972) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/change/card`
 * @更新时间 `2023-03-17 17:08:42`
 */
export interface ChangeCardRequest {
  /**
   * 客户id
   */
  clientId: number
  /**
   * 分控行业分类
   */
  suitTrade?: string
}

/**
 * 接口 [获取评分卡信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11972) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/change/card`
 * @更新时间 `2023-03-17 17:08:42`
 */
export interface ChangeCardResponse {
  /**
   * 地区id
   */
  areaId?: number
  /**
   * 评分卡id
   */
  cardId?: number
}

/**
 * 接口 [计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11970) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/calculate`
 * @更新时间 `2023-03-15 16:21:44`
 */
export interface CardCalculateRequest {
  /**
   * 客户id
   */
  clientId: number
  /**
   * 分控行业分类
   */
  suitTrade?: string
}

/**
 * 接口 [计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11970) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/calculate`
 * @更新时间 `2023-03-15 16:21:44`
 */
export interface CardCalculateResponse {
  /**
   * 地区id
   */
  areaId?: number
  /**
   * 评分卡id
   */
  cardId?: number
  /**
   * 地区
   */
  area?: string
  /**
   * 省
   */
  province?: string
  /**
   * 市
   */
  city?: string
  /**
   * 行政级别
   */
  executiveLevel?: string
  /**
   * 区域级别
   */
  regionalLevel?: string
  /**
   * 总分
   */
  totalPoints?: number
  tryCalculateBodies?: {
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
     * 数值
     */
    data?: number
    /**
     * 得分
     */
    score?: number
  }[]
}

/**
 * 接口 [试计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11851) 的 **请求类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/try/calculate`
 * @更新时间 `2023-03-17 17:08:42`
 */
export interface TryCalculateRequest {
  /**
   * 地区id
   */
  areaId: number
  /**
   * 评分卡id
   */
  cardId: number
}

/**
 * 接口 [试计算↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11851) 的 **返回类型**
 *
 * @分类 [风控策略-评分卡接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2172)
 * @请求头 `POST /risk/control/score/card/try/calculate`
 * @更新时间 `2023-03-17 17:08:42`
 */
export interface TryCalculateResponse {
  /**
   * 地区id
   */
  areaId?: number
  /**
   * 评分卡id
   */
  cardId?: number
  /**
   * 地区
   */
  area?: string
  /**
   * 省
   */
  province?: string
  /**
   * 市
   */
  city?: string
  /**
   * 行政级别
   */
  executiveLevel?: string
  /**
   * 区域级别
   */
  regionalLevel?: string
  /**
   * 总分
   */
  totalPoints?: number
  tryCalculateBodies?: {
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
     * 数值
     */
    data?: number
    /**
     * 得分
     */
    score?: number
  }[]
}

/* prettier-ignore-end */
