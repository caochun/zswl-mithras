/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [保融流水表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/4681) 的 **请求类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/list`
 * @更新时间 `2024-06-18 10:24:59`
 */
export interface RecordListRequest {
  /**
   * 保融流水id
   */
  bruid?: string
  /**
   * 客户名称
   */
  orgName?: string
  /**
   * 账户编码
   */
  accountnumber?: string
  /**
   * 对方账号
   */
  oppositeaccountnumber?: string
  /**
   * 对方户名
   */
  oppositeaccountname?: string
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
  sourceScene?: string
}

/**
 * 接口 [保融流水表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/4681) 的 **返回类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/list`
 * @更新时间 `2024-06-18 10:24:59`
 */
export interface RecordListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 序号
     */
    rn?: number
    /**
     * 保融流水id
     */
    bruid?: string
    /**
     * 客户名称
     */
    orgName?: string
    /**
     * 账户编码 == 银行账号
     */
    accountnumber?: string
    /**
     * 唯一标识
     */
    transseq?: string
    /**
     * 交易日期时间
     */
    tradedatetime?: string
    /**
     * 交易日期
     */
    tradedate?: string
    /**
     * 交易时间
     */
    tradetime?: string
    /**
     * 起息日期
     */
    qixiriqi?: string
    /**
     * 交易方向1支出，2收入 1. 交易方向=支出，则“交易金额”对应“付款金额”；2. 交易方向=收入，则“交易金额”对应“收款金额”。
     */
    moneyway?: string
    /**
     * 交易金额(毫厘)
     */
    amount?: number
    /**
     * 当前余额（毫厘）
     */
    currentbalance?: number
    /**
     * 更新日期时间
     */
    lastmodifiedon?: string
    /**
     * 对账码
     */
    checkcode?: string
    /**
     * 用途
     */
    purpose?: string
    /**
     * 备注 用途 + 备注，拼接后 对应 “摘要”
     */
    comments?: string
    /**
     * 对方账号
     */
    oppositeaccountnumber?: string
    /**
     * 对方户名
     */
    oppositeaccountname?: string
    /**
     * 对方银行
     */
    oppositebank?: string
    /**
     * 票据号
     */
    billcode?: string
    /**
     * 票据类型
     */
    billtype?: string
    /**
     * 核对批号
     */
    checkbatchno?: string
    /**
     * 银行流水号
     */
    bankserialnumber?: string
    /**
     * 资金系统单据号
     */
    notecode?: string
    /**
     * 银行业务参考号
     */
    bankbusref?: string
    /**
     * 电子回单编号
     */
    receiptcode?: string
    /**
     * 业务回单类型
     */
    receiptbustypno?: string
    /**
     * 回单个性化信息
     */
    receiptinfo?: string
    /**
     * 企业业务参考号
     */
    busref?: string
    /**
     * 是否忽略，0：未忽略，1：已忽略，默认0
     */
    ignoreFlag?: number
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
    KEY?: {}
  }
}

/**
 * 接口 [保融流水表统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/4579) 的 **请求类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/count`
 * @更新时间 `2024-06-18 10:23:04`
 */
export interface RecordCountRequest {}

/**
 * 接口 [保融流水表统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/4579) 的 **返回类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/count`
 * @更新时间 `2024-06-18 10:23:04`
 */
export interface RecordCountResponse {
  /**
   * id
   */
  notIgnoredCount?: number
}

/**
 * 接口 [删除保融流水表↗](http://yapi.zswltec.com:3000/project/11/interface/api/4585) 的 **请求类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/remove`
 * @更新时间 `2024-06-17 15:34:15`
 */
export interface RecordRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除保融流水表↗](http://yapi.zswltec.com:3000/project/11/interface/api/4585) 的 **返回类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/remove`
 * @更新时间 `2024-06-17 15:34:15`
 */
export interface RecordRemoveResponse {
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
 * 接口 [忽略保融流水表↗](http://yapi.zswltec.com:3000/project/11/interface/api/4591) 的 **请求类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/ignore`
 * @更新时间 `2024-06-17 15:34:28`
 */
export interface RecordIgnoreRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [忽略保融流水表↗](http://yapi.zswltec.com:3000/project/11/interface/api/4591) 的 **返回类型**
 *
 * @分类 [保融流水表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1184)
 * @请求头 `POST /br/flow/record/ignore`
 * @更新时间 `2024-06-17 15:34:28`
 */
export interface RecordIgnoreResponse {
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
