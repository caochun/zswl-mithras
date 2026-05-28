/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [集团授信立项信息版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10796) 的 **请求类型**
 *
 * @分类 [集团授信立项管理-版本管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1894)
 * @请求头 `POST /group/credit/establish/compare/preVersion`
 * @更新时间 `2022-11-21 11:43:46`
 */
export interface ComparePreVersionRequest {
  /**
   * 版本id
   */
  id: number
}

/**
 * 接口 [集团授信立项信息版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10796) 的 **返回类型**
 *
 * @分类 [集团授信立项管理-版本管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1894)
 * @请求头 `POST /group/credit/establish/compare/preVersion`
 * @更新时间 `2022-11-21 11:43:46`
 */
export interface ComparePreVersionResponse {
  /**
   * 旧版本数据
   */
  oldData?: {
    KEY?: {}[]
  }
  /**
   * 新版本数据
   */
  newData?: {
    KEY?: {
      KEY?: {
        beforeValue?: {}
        value?: {}
        isChange?: boolean
      }
    }[]
  }
  /**
   * 模块change标志
   */
  moduleChanged?: {
    KEY?: boolean
  }
}

/**
 * 接口 [集团授信立项信息版本表列↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10792) 的 **请求类型**
 *
 * @分类 [集团授信立项管理-版本管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1894)
 * @请求头 `POST /group/credit/establish/version/list`
 * @更新时间 `2022-11-21 11:43:41`
 */
export interface VersionListRequest {
  /**
   * 主数据ID
   */
  mainId?: number
  /**
   * 模块类型
   */
  module?: string
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
 * 接口 [集团授信立项信息版本表列↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10792) 的 **返回类型**
 *
 * @分类 [集团授信立项管理-版本管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1894)
 * @请求头 `POST /group/credit/establish/version/list`
 * @更新时间 `2022-11-21 11:43:41`
 */
export interface VersionListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 主数据id
     */
    mainId?: number
    /**
     * 版本号
     */
    version?: string
    /**
     * 版本类型
     */
    type?: number
    /**
     * 是否可和上版本比较
     */
    canCompare?: number
    /**
     * 业务模块枚举
     */
    module?: string
    createTime?: string
    createBy?: number
    updateTime?: string
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
 * 接口 [集团授信立项信息生效（或提交审批）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10788) 的 **请求类型**
 *
 * @分类 [集团授信立项管理-版本管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1894)
 * @请求头 `POST /group/credit/establish/effect`
 * @更新时间 `2022-11-21 11:43:39`
 */
export interface EstablishEffectRequest {
  /**
   * 立项Id
   */
  id: number
}

/**
 * 接口 [集团授信立项信息生效（或提交审批）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10788) 的 **返回类型**
 *
 * @分类 [集团授信立项管理-版本管理接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1894)
 * @请求头 `POST /group/credit/establish/effect`
 * @更新时间 `2022-11-21 11:43:39`
 */
export interface EstablishEffectResponse {
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
