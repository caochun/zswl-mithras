/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改黑灰名单库-入库原因参数配置↗](http://yapi.zswltec.com:3000/project/10/interface/api/31) 的 **请求类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/modify`
 * @更新时间 `2024-01-18 14:31:16`
 */
export interface ConfigModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 规则名称
   */
  ruleName?: string
  /**
   * 层级 0金控定义
   */
  level?: number
  /**
   * 所属金控类型主id，即一级id
   */
  mainId?: number
  /**
   * 状态 0启用，1禁用
   */
  status?: number
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部
   */
  source?: string
  /**
   * 适用业务类型
   */
  suitBusiness?: string[]
  /**
   * 适用机构
   */
  suitOrg?: string[]
}

/**
 * 接口 [修改黑灰名单库-入库原因参数配置↗](http://yapi.zswltec.com:3000/project/10/interface/api/31) 的 **返回类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/modify`
 * @更新时间 `2024-01-18 14:31:16`
 */
export interface ConfigModifyResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [删除黑灰名单库-入库原因参数配置↗](http://yapi.zswltec.com:3000/project/10/interface/api/43) 的 **请求类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/remove`
 * @更新时间 `2024-01-19 14:38:33`
 */
export interface ConfigRemoveRequest {
  /**
   * ids
   */
  ids: number[]
}

/**
 * 接口 [删除黑灰名单库-入库原因参数配置↗](http://yapi.zswltec.com:3000/project/10/interface/api/43) 的 **返回类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/remove`
 * @更新时间 `2024-01-19 14:38:33`
 */
export interface ConfigRemoveResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [新增黑灰名单库-入库原因参数配置↗](http://yapi.zswltec.com:3000/project/10/interface/api/37) 的 **请求类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/add`
 * @更新时间 `2024-01-18 14:31:41`
 */
export interface ConfigAddRequest {
  /**
   * 规则名称
   */
  ruleName: string
  /**
   * 父id
   */
  parentId?: number
  /**
   * 所属金控类型主id，即一级id
   */
  mainId?: number
  /**
   * 黑灰标识
   */
  blackGrayType: string
  /**
   * 来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部
   */
  source: string
  /**
   * 适用业务类型
   */
  suitBusiness: string[]
  /**
   * 适用机构
   */
  suitOrg: string[]
}

/**
 * 接口 [新增黑灰名单库-入库原因参数配置↗](http://yapi.zswltec.com:3000/project/10/interface/api/37) 的 **返回类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/add`
 * @更新时间 `2024-01-18 14:31:41`
 */
export interface ConfigAddResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [黑灰名单库-入库原因参数配置列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/61) 的 **请求类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/list`
 * @更新时间 `2024-01-19 14:42:18`
 */
export interface ConfigListRequest {
  /**
   * 规则名称
   */
  ruleName?: string
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部
   */
  source?: string
  /**
   * 关联部门
   */
  suitOrg?: string
  /**
   * 关联部门
   */
  suitBusiness?: string
  /**
   * 状态 0启用，1禁用
   */
  status?: number
  page?: number
  pageSize?: number
}

/**
 * 接口 [黑灰名单库-入库原因参数配置列表↗](http://yapi.zswltec.com:3000/project/10/interface/api/61) 的 **返回类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/list`
 * @更新时间 `2024-01-19 14:42:18`
 */
export interface ConfigListResponse {
  total?: number
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 维护机构
     */
    orgCode?: string
    /**
     * 规则编号
     */
    ruleNumber?: string
    /**
     * 递增序列
     */
    ruleSequence?: number
    /**
     * 规则名称
     */
    ruleName?: string
    /**
     * 层级 0金控定义
     */
    level?: number
    /**
     * 父id
     */
    parentId?: number
    /**
     * 所属金控类型主id，即一级id
     */
    mainId?: number
    /**
     * 状态 0启用，1禁用
     */
    status?: number
    /**
     * 黑灰标识
     */
    blackGrayType?: string
    /**
     * 来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部
     */
    source?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 创建人、发起人
     */
    createBy?: number
    /**
     * 最后更新人id
     */
    updateBy?: number
  }[]
}

/**
 * 接口 [黑灰名单库-入库原因参数配置启用停用接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/55) 的 **请求类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/switch`
 * @更新时间 `2024-01-19 14:38:33`
 */
export interface ConfigSwitchRequest {
  /**
   * 状态 0启用，1禁用
   */
  status?: number
  /**
   * ids
   */
  ids: number[]
}

/**
 * 接口 [黑灰名单库-入库原因参数配置启用停用接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/55) 的 **返回类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/switch`
 * @更新时间 `2024-01-19 14:38:33`
 */
export interface ConfigSwitchResponse {
  /**
   * 成功标记
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 状态信息
   */
  message?: string
}

/**
 * 接口 [黑灰名单库-入库原因参数配置详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/25) 的 **请求类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/detail`
 * @更新时间 `2024-01-19 14:41:46`
 */
export interface ConfigDetailRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [黑灰名单库-入库原因参数配置详情↗](http://yapi.zswltec.com:3000/project/10/interface/api/25) 的 **返回类型**
 *
 * @分类 [黑灰名单库-入库原因参数配置-接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_8)
 * @请求头 `POST /black/gray/warehouse/rule/config/detail`
 * @更新时间 `2024-01-19 14:41:46`
 */
export interface ConfigDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 维护机构
   */
  orgCode?: string
  /**
   * 规则编号
   */
  ruleNumber?: string
  /**
   * 递增序列
   */
  ruleSequence?: number
  /**
   * 规则名称
   */
  ruleName?: string
  /**
   * 层级 0金控定义
   */
  level?: number
  /**
   * 父id
   */
  parentId?: number
  /**
   * 所属金控类型主id，即一级id
   */
  mainId?: number
  /**
   * 状态 0启用，1禁用
   */
  status?: number
  /**
   * 黑灰标识
   */
  blackGrayType?: string
  /**
   * 来源 BlackGraySourceEnum EXTERNAL_APPROVAL 外部，INTERNAL_APPROVAL 内部
   */
  source?: string
  /**
   * 适用业务类型
   */
  suitBusiness: string[]
  /**
   * 适用机构
   */
  suitOrg: string[]
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 创建人、发起人
   */
  createBy?: number
  /**
   * 最后更新人id
   */
  updateBy?: number
}

/* prettier-ignore-end */
