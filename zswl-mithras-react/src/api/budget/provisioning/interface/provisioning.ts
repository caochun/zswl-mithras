/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13489) 的 **请求类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/modify`
 * @更新时间 `2023-06-20 09:45:47`
 */
export interface InfoModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 计提比例-手动修改
   */
  withdrawalRatioHand?: number
}

/**
 * 接口 [修改绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13489) 的 **返回类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/modify`
 * @更新时间 `2023-06-20 09:45:47`
 */
export type InfoModifyResponse = null

/**
 * 接口 [刷新绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13504) 的 **请求类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/refresh`
 * @更新时间 `2023-06-20 09:45:46`
 */
export interface InfoRefreshRequest {
  /**
   * 创建月份
   */
  provisionDate?: string
}

/**
 * 接口 [刷新绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13504) 的 **返回类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/refresh`
 * @更新时间 `2023-06-20 09:45:46`
 */
export interface InfoRefreshResponse {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [新增绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13484) 的 **请求类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/add`
 * @更新时间 `2023-06-20 09:45:46`
 */
export interface InfoAddRequest {
  /**
   * 创建月份
   */
  provisionDate?: string
}

/**
 * 接口 [新增绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13484) 的 **返回类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/add`
 * @更新时间 `2023-06-20 09:45:46`
 */
export interface InfoAddResponse {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [确认绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13719) 的 **请求类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/effect`
 * @更新时间 `2023-06-20 09:45:46`
 */
export interface InfoEffectRequest {
  /**
   * 创建月份
   */
  provisionDate?: string
}

/**
 * 接口 [确认绩效-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13719) 的 **返回类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/effect`
 * @更新时间 `2023-06-20 09:45:46`
 */
export type InfoEffectResponse = null

/**
 * 接口 [绩效-拨备表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13494) 的 **请求类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/list`
 * @更新时间 `2023-06-20 09:45:47`
 */
export interface InfoListRequest {
  /**
   * 创建月份
   */
  provisionDate?: string
  page?: number
  pageSize?: number
  version?: string
}

/**
 * 接口 [绩效-拨备表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13494) 的 **返回类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/list`
 * @更新时间 `2023-06-20 09:45:47`
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
     * 创建月份
     */
    provisionDate?: string
    /**
     * 拨备状态
     */
    provisionStatus?: string
    /**
     * 创建人
     */
    createBy?: string
    /**
     * 创建时间
     */
    createTime?: string
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
    key?: {}
  }
}

/**
 * 接口 [绩效详情-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13499) 的 **请求类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/detail`
 * @更新时间 `2023-06-20 09:45:47`
 */
export interface InfoDetailRequest {
  id: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 利润所属部门id
   */
  profitBelongDeptId?: number
  /**
   * 客户id
   */
  clientId?: number
}

/**
 * 接口 [绩效详情-拨备表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/13499) 的 **返回类型**
 *
 * @分类 [KpiProvisionBaseInfoApi↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2628)
 * @请求头 `POST /kpi/provision/base/info/detail`
 * @更新时间 `2023-06-20 09:45:47`
 */
export interface InfoDetailResponse {
  /**
   * 创建月份
   */
  provisionDate?: string
  /**
   * 数据列表
   */
  provisionBaseInfoList?: {
    /**
     * 查询集合
     */
    list?: {
      /**
       * id
       */
      id?: number
      /**
       * 业务类型。租赁、保理、转租赁
       */
      bizType?: string
      /**
       * 租赁类型。直租、回租、经营性租赁
       */
      leaseType?: string
      /**
       * 项目类别
       */
      projClassify?: string
      /**
       * 利润所属部门id
       */
      profitBelongDeptId?: number
      /**
       * 利润所属部门name
       */
      profitBelongDeptName?: number
      /**
       * 项目主办id
       */
      clientId?: number
      /**
       * 客户name
       */
      clientName?: number
      /**
       * 合同id
       */
      contractId?: number
      /**
       * 合同编号
       */
      contractCode?: string
      /**
       * 到期日
       */
      endDate?: string
      /**
       * 剩余期限
       */
      residualMaturity?: string
      /**
       * 剩余本金
       */
      remainingPrincipal?: number
      /**
       * 保证金余额
       */
      earnestBalance?: number
      /**
       * 敞口
       */
      exposure?: number
      /**
       * 风险等级
       */
      riskLevel?: string
      /**
       * 计提比例
       */
      withdrawalRatio?: number
      /**
       * 计提比例-配置
       */
      withdrawalRatioConfig?: string
      /**
       * 计提比例-手动修改
       */
      withdrawalRatioHand?: number
      /**
       * 本月风险余额
       */
      profitCurrent?: number
      /**
       * 上月风险余额
       */
      profitTotal?: number
      /**
       * 本月风险金计提/转回
       */
      bonusCurrent?: number
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
      key?: {}
    }
  }
}

/* prettier-ignore-end */
