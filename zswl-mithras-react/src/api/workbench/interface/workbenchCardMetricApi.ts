/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [工作台-卡片指标配置↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12954) 的 **请求类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `GET /workbench/card/metric/config`
 * @更新时间 `2023-06-09 16:21:53`
 */
export interface MetricConfigRequest {}

/**
 * 接口 [工作台-卡片指标配置↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12954) 的 **返回类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `GET /workbench/card/metric/config`
 * @更新时间 `2023-06-09 16:21:53`
 */
export type MetricConfigResponse = {
  /**
   * 指标名称
   */
  metricName?: string
  /**
   * 是否选中，ture选中，false未选中
   */
  selected?: boolean
}[]

/**
 * 接口 [工作台-当前用户角色列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12309) 的 **请求类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `GET /workbench/metric/listrole`
 * @更新时间 `2023-06-09 16:21:53`
 */
export interface MetricListroleRequest {}

/**
 * 接口 [工作台-当前用户角色列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12309) 的 **返回类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `GET /workbench/metric/listrole`
 * @更新时间 `2023-06-09 16:21:53`
 */
export type MetricListroleResponse = {
  /**
   * 下拉展示文本
   */
  label?: string
  /**
   * 下拉选项值
   */
  value?: string
}[]

/**
 * 接口 [工作台-卡片指标列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12314) 的 **请求类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `POST /workbench/card/metric/list`
 * @更新时间 `2023-06-09 16:21:53`
 */
export interface MetricListRequest {
  /**
   * 指标单位
   */
  currentRoleCode: string
}

/**
 * 接口 [工作台-卡片指标列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12314) 的 **返回类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `POST /workbench/card/metric/list`
 * @更新时间 `2023-06-09 16:21:53`
 */
export type MetricListResponse = {
  /**
   * 指标名称
   */
  metricName?: string
  /**
   * 值
   */
  value?: string
  /**
   * 指标单位
   */
  unit?: string
  /**
   * 指标单位
   */
  unitDisplay?: string
  /**
   * 是否为饼图
   */
  pieChart?: boolean
}[]

/**
 * 接口 [工作台-卡片指标配置↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12959) 的 **请求类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `POST /workbench/card/metric/config/modify`
 * @更新时间 `2023-06-09 16:21:53`
 */
export type ConfigModifyRequest = {
  /**
   * 指标名称
   */
  metricName?: string
  /**
   * 是否选中，ture选中，false未选中
   */
  selected?: boolean
}[]

/**
 * 接口 [工作台-卡片指标配置↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12959) 的 **返回类型**
 *
 * @分类 [workbench-card-metric-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2323)
 * @请求头 `POST /workbench/card/metric/config/modify`
 * @更新时间 `2023-06-09 16:21:53`
 */
export interface ConfigModifyResponse {}

/* prettier-ignore-end */
