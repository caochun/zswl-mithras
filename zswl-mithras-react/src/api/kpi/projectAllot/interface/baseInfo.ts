/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [绩效考核-项目分配-基本信息-修改↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13099) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配-基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2553)
 * @请求头 `POST /kpi/projectdistribution/baseinfo/modify`
 * @更新时间 `2023-06-15 13:46:05`
 */
export interface BaseinfoModifyRequest {
  /**
   * 项目分配id
   */
  projectDistributionId: number
  /**
   * 利润归属部门id
   */
  profitBelongDeptId: number
}

/**
 * 接口 [绩效考核-项目分配-基本信息-修改↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13099) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配-基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2553)
 * @请求头 `POST /kpi/projectdistribution/baseinfo/modify`
 * @更新时间 `2023-06-15 13:46:05`
 */
export interface BaseinfoModifyResponse {
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
 * 接口 [绩效考核-项目分配-基本信息-详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13104) 的 **请求类型**
 *
 * @分类 [绩效考核-项目分配-基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2553)
 * @请求头 `POST /kpi/projectdistribution/baseinfo/detail`
 * @更新时间 `2023-06-15 13:46:05`
 */
export interface BaseinfoDetailRequest {
  /**
   * 项目分配id
   */
  projectDistributionId: number
}

/**
 * 接口 [绩效考核-项目分配-基本信息-详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13104) 的 **返回类型**
 *
 * @分类 [绩效考核-项目分配-基本信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2553)
 * @请求头 `POST /kpi/projectdistribution/baseinfo/detail`
 * @更新时间 `2023-06-15 13:46:05`
 */
export interface BaseinfoDetailResponse {
  /**
   * 项目分配id
   */
  projectDistributionId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 项目类型
   */
  projClassify?: string
  /**
   * 项目来源
   */
  projSource?: string
  /**
   * 合同开始时间
   */
  contractStartDate?: string
  /**
   * 合同终止时间
   */
  contractEndDate?: string
  /**
   * 利润所属部门id
   */
  belongDeptId?: number
  /**
   * 利润所属部门名称
   */
  belongDeptName?: string
}

/* prettier-ignore-end */
