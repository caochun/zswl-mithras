/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [工作台-五级分类饼图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12409) 的 **请求类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/pie/fivelevel`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface PieFivelevelRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-五级分类饼图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12409) 的 **返回类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/pie/fivelevel`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface PieFivelevelResponse {
  /**
   * 饼图标题
   */
  title?: string
  /**
   * 饼图数据 ,PieDataVO
   */
  data?: {
    /**
     * 块-名称
     */
    name?: string
    /**
     * 块-值
     */
    value?: string
    /**
     * 块-占比
     */
    rate?: string
  }[]
}

/**
 * 接口 [工作台-当前角色table列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12399) 的 **请求类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/tabs`
 * @更新时间 `2023-05-10 14:55:39`
 */
export interface ChartTabsRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-当前角色table列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12399) 的 **返回类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/tabs`
 * @更新时间 `2023-05-10 14:55:39`
 */
export type ChartTabsResponse = string[]

/**
 * 接口 [工作台-融资成本饼图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12414) 的 **请求类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/pie/financecost`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface PieFinancecostRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-融资成本饼图↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12414) 的 **返回类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/pie/financecost`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface PieFinancecostResponse {
  /**
   * 饼图标题
   */
  title?: string
  /**
   * 饼图数据 ,PieDataVO
   */
  data?: {
    /**
     * 块-名称
     */
    name?: string
    /**
     * 块-值
     */
    value?: string
    /**
     * 块-占比
     */
    rate?: string
  }[]
}

/**
 * 接口 [工作台-雷达图指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12404) 的 **请求类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/radar`
 * @更新时间 `2023-05-10 14:55:39`
 */
export interface ChartRadarRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-雷达图指标↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12404) 的 **返回类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/radar`
 * @更新时间 `2023-05-10 14:55:39`
 */
export interface ChartRadarResponse {
  /**
   * 雷达图标题
   */
  title?: string
  /**
   * 雷达图数据，key为max的表示最大值，其余的为雷达图的值(该参数为map)
   */
  indicator?: {
    /**
     * String
     */
    mapKey?: {}
    /**
     * List<RadarDataVO>
     */
    mapValue?: {}
  }
}

/**
 * 接口 [工作台-项目立项列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12419) 的 **请求类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/list/projestablish`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface ListProjestablishRequest {
  /**
   * 业务部门id
   */
  deptId?: number
}

/**
 * 接口 [工作台-项目立项列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12419) 的 **返回类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/list/projestablish`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface ListProjestablishResponse {
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 项目主办
   */
  projectOrganizer?: string
  /**
   * 部门名称
   */
  deptName?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 授信金额，万元，小数点后2位
   */
  applyCreditAmount?: string
}

/**
 * 接口 [工作台-项目评审列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12424) 的 **请求类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/list/projreview`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface ListProjreviewRequest {
  /**
   * 业务部门id
   */
  deptId?: number
}

/**
 * 接口 [工作台-项目评审列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12424) 的 **返回类型**
 *
 * @分类 [workbench-little-chart-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2343)
 * @请求头 `POST /workbench/little/chart/list/projreview`
 * @更新时间 `2023-05-10 14:55:40`
 */
export interface ListProjreviewResponse {
  /**
   * 项目名称
   */
  projectName?: string
  /**
   * 项目主办
   */
  projectOrganizer?: string
  /**
   * 部门名称
   */
  deptName?: string
  /**
   * 业务类型
   */
  bizType?: string
  /**
   * 授信金额，万元，小数点后2位
   */
  applyCreditAmount?: string
}

/* prettier-ignore-end */
