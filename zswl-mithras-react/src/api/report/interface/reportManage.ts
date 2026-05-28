/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [获取管报列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11324) 的 **请求类型**
 *
 * @分类 [管报接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1994)
 * @请求头 `GET /management/report/list`
 * @更新时间 `2023-01-04 15:32:34`
 */
export interface ReportListRequest {}

/**
 * 接口 [获取管报列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11324) 的 **返回类型**
 *
 * @分类 [管报接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1994)
 * @请求头 `GET /management/report/list`
 * @更新时间 `2023-01-04 15:32:34`
 */
export type ReportListResponse = {
  /**
   * 下拉展示文本
   */
  label?: string
  /**
   * 下拉选项值
   */
  value?: string
}[]

/* prettier-ignore-end */
