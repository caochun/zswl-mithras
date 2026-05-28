/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改担保信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11230) 的 **请求类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/modify`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoModifyRequest {
  /**
   * id
   */
  id?: string
  /**
   * 总担保额度
   */
  totalGuaranteeLimit?: string
  /**
   * 担保生效时间from : yyyy-MM-dd
   */
  effectiveTimeFrom?: string
  /**
   * 担保生效时间to : yyyy-MM-dd
   */
  effectiveTimeTo?: string
  /**
   * 额度是否可循环
   */
  recyclable?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 删除文件id
   */
  delFileIds?: string
  /**
   * 新增文件列表
   */
  addFiles?: string
}

/**
 * 接口 [修改担保信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11230) 的 **返回类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/modify`
 * @更新时间 `2022-12-20 16:28:59`
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
 * 接口 [删除担保信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11231) 的 **请求类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/remove`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoRemoveRequest {
  /**
   * id
   */
  ids: number[]
}

/**
 * 接口 [删除担保信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11231) 的 **返回类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/remove`
 * @更新时间 `2022-12-20 16:28:59`
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
 * 接口 [担保信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11227) 的 **请求类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/list`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoListRequest {
  /**
   * 所属担保机构id
   */
  agencyId: number
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
}

/**
 * 接口 [担保信息列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11227) 的 **返回类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/list`
 * @更新时间 `2022-12-20 16:28:59`
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
     * 担保机构id
     */
    agencyId?: number
    /**
     * 担保编号
     */
    guaranteeCode?: string
    /**
     * 总担保额度
     */
    totalGuaranteeLimit?: number
    /**
     * 已使用额度
     */
    usedGuaranteeLimit?: number
    /**
     * 担保生效时间from
     */
    effectiveTimeFrom?: string
    /**
     * 担保生效时间to
     */
    effectiveTimeTo?: string
    /**
     * 额度是否可循环
     */
    recyclable?: boolean
    /**
     * 资料名称
     */
    materialName?: string
    /**
     * 备注
     */
    remark?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 文件列表
     */
    fileListS?: {
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
       * 资料子类型
       */
      materialSubType?: string
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

/**
 * 接口 [担保信息详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11229) 的 **请求类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/detail`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoDetailRequest {
  id?: number
}

/**
 * 接口 [担保信息详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11229) 的 **返回类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/detail`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoDetailResponse {
  /**
   * 担保机构id
   */
  agencyId?: number
  /**
   * 总担保额度
   */
  totalGuaranteeLimit?: number
  /**
   * 担保生效时间from
   */
  effectiveTimeFrom?: string
  /**
   * 担保生效时间to
   */
  effectiveTimeTo?: string
  /**
   * 额度是否可循环
   */
  recyclable?: boolean
  /**
   * 备注
   */
  remark?: string
  /**
   * 相关资料
   */
  fileListRSPS?: {
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
     * 资料子类型
     */
    materialSubType?: string
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
  }[]
}

/**
 * 接口 [新增担保信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11228) 的 **请求类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/add`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoAddRequest {
  /**
   * 担保机构id
   */
  agencyId?: string
  /**
   * 总担保额度
   */
  totalGuaranteeLimit?: string
  /**
   * 担保生效时间from : yyyy-MM-dd
   */
  effectiveTimeFrom?: string
  /**
   * 担保生效时间to : yyyy-MM-dd
   */
  effectiveTimeTo?: string
  /**
   * 额度是否可循环
   */
  recyclable?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 文件列表
   */
  files?: string
}

/**
 * 接口 [新增担保信息↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11228) 的 **返回类型**
 *
 * @分类 [担保信息-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1963)
 * @请求头 `POST /fund/guarantee/info/add`
 * @更新时间 `2022-12-20 16:28:59`
 */
export interface InfoAddResponse {
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

/* prettier-ignore-end */
