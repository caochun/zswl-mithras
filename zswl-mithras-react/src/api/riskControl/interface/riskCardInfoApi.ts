/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改评分卡基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11826) 的 **请求类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/modify`
 * @更新时间 `2023-03-02 15:39:01`
 */
export interface InfoModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 评分卡名称
   */
  scorecardName?: string
  /**
   * 适用行业
   */
  suitTrade?: string
  /**
   * 省内省外
   */
  provinceSeat?: string
  /**
   * 年份
   */
  year?: number
  /**
   * 说明
   */
  content?: string
  /**
   * 状态，0禁用，1启用 YesOrNoNumberEnum
   */
  status?: number
}

/**
 * 接口 [修改评分卡基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11826) 的 **返回类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/modify`
 * @更新时间 `2023-03-02 15:39:01`
 */
export interface InfoModifyResponse {
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
 * 接口 [删除评分卡基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11824) 的 **请求类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/remove`
 * @更新时间 `2023-02-28 14:55:19`
 */
export interface InfoRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除评分卡基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11824) 的 **返回类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/remove`
 * @更新时间 `2023-02-28 14:55:19`
 */
export interface InfoRemoveResponse {
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
 * 接口 [新增评分卡基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11823) 的 **请求类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/add`
 * @更新时间 `2023-03-07 15:29:52`
 */
export interface InfoAddRequest {
  /**
   * 评分卡名称
   */
  scorecardName: string
  /**
   * 适用行业
   */
  suitTrade: string
  /**
   * 省内省外 ProvinceTypeEnum
   */
  provinceSeat: string
  /**
   * 年份
   */
  year: number
  /**
   * 说明
   */
  content?: string
  /**
   * 状态，0禁用，1启用 YesOrNoNumberEnum
   */
  status?: number
}

/**
 * 接口 [新增评分卡基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11823) 的 **返回类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/add`
 * @更新时间 `2023-03-07 15:29:52`
 */
export interface InfoAddResponse {
  /**
   * 评分卡id
   */
  id?: number
  /**
   * 评分卡名称
   */
  scorecardName?: string
  /**
   * 适用行业
   */
  suitTrade?: string
  /**
   * 省内省外 ProvinceTypeEnum
   */
  provinceSeat?: string
  /**
   * 年份
   */
  year?: number
  /**
   * 说明
   */
  content?: string
  /**
   * 状态，0禁用，1启用 YesOrNoNumberEnum
   */
  status?: number
}

/**
 * 接口 [评分卡基本信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11825) 的 **请求类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/list`
 * @更新时间 `2023-03-07 15:29:54`
 */
export interface InfoListRequest {
  /**
   * 评分卡名称
   */
  scorecardName?: string
  /**
   * 适用行业
   */
  suitTrade?: string
  /**
   * 状态，0禁用，1启用 YesOrNoNumberEnum
   */
  status?: number
  /**
   * 更新开始时间
   */
  updateTimeBegin?: string
  /**
   * 更新结束时间
   */
  updateTimeEnd?: string
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
 * 接口 [评分卡基本信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11825) 的 **返回类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/list`
 * @更新时间 `2023-03-07 15:29:54`
 */
export interface InfoListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 评分卡名称
     */
    scorecardName?: string
    /**
     * 适用行业
     */
    suitTrade?: string
    /**
     * 省内省外
     */
    provinceSeat?: string
    /**
     * 年份
     */
    year?: number
    /**
     * 状态，0禁用，1启用 YesOrNoNumberEnum
     */
    status?: number
    /**
     * 说明
     */
    content?: string
    /**
     * createTime
     */
    createTime?: string
    /**
     * create_by
     */
    createBy?: number
    /**
     * update_time
     */
    updateTime?: string
    /**
     * update_by
     */
    updateBy?: number
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

/**
 * 接口 [评分卡基本信息详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11933) 的 **请求类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/detail`
 * @更新时间 `2023-03-07 15:32:01`
 */
export interface InfoDetailRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [评分卡基本信息详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11933) 的 **返回类型**
 *
 * @分类 [评分卡基本信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2166)
 * @请求头 `POST /risk/control/score/card/base/info/detail`
 * @更新时间 `2023-03-07 15:32:01`
 */
export interface InfoDetailResponse {
  /**
   * 评分卡id
   */
  id?: number
  /**
   * 评分卡名称
   */
  scorecardName?: string
  /**
   * 适用行业
   */
  suitTrade?: string
  /**
   * 省内省外 ProvinceTypeEnum
   */
  provinceSeat?: string
  /**
   * 年份
   */
  year?: number
  /**
   * 说明
   */
  content?: string
  /**
   * 状态，0禁用，1启用 YesOrNoNumberEnum
   */
  status?: number
}

/* prettier-ignore-end */
