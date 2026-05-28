/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [考核部门设置-保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/4771) 的 **请求类型**
 *
 * @分类 [kpi-parameter-config-controller↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1280)
 * @请求头 `POST /contractAssessDept/save`
 * @更新时间 `2024-06-20 17:00:44`
 */
export interface ContractAssessDeptSaveRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 考核部门
   */
  assessDept?: number
  /**
   * 请求体list
   */
  contractAssessDeptlDtoList?: {
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 考核部门id
     */
    assessDept?: number
  }[]
}

/**
 * 接口 [考核部门设置-保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/4771) 的 **返回类型**
 *
 * @分类 [kpi-parameter-config-controller↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1280)
 * @请求头 `POST /contractAssessDept/save`
 * @更新时间 `2024-06-20 17:00:44`
 */
export interface ContractAssessDeptSaveResponse {
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
 * 接口 [考核部门设置-详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/4777) 的 **请求类型**
 *
 * @分类 [kpi-parameter-config-controller↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1280)
 * @请求头 `POST /contractAssessDept/get`
 * @更新时间 `2024-06-20 17:03:23`
 */
export interface ContractAssessDeptGetRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 考核部门
   */
  assessDept?: number
  /**
   * 请求体list
   */
  contractAssessDeptlDtoList?: {
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 考核部门id
     */
    assessDept?: number
  }[]
}

/**
 * 接口 [考核部门设置-详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/4777) 的 **返回类型**
 *
 * @分类 [kpi-parameter-config-controller↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1280)
 * @请求头 `POST /contractAssessDept/get`
 * @更新时间 `2024-06-20 17:03:23`
 */
export type ContractAssessDeptGetResponse = {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 考核部门
   */
  assessDept?: number
}[]

/* prettier-ignore-end */
