/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [FTP计息变更-申请信息-保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/27253) 的 **请求类型**
 *
 * @分类 [FTP计息变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3824)
 * @请求头 `POST /new/ftp/interest/change/apply/save`
 * @更新时间 `2025-03-09 11:17:07`
 */
export interface ApplySaveRequest {
  /**
   * FTP计息变更申请记录id
   */
  id?: number
  /**
   * FTP考核信息
   */
  ftpAssessmentInfoList: {
    /**
     * 借据id
     */
    receiptId: number
    /**
     * FTP计息变更开始日期，yyyy-MM-dd
     */
    effectDate: string
    /**
     * FTP计息变更差额调整日期，yyyy-MM-dd
     */
    ftpInterestDiffDate: string
    /**
     * id
     */
    id?: number
    /**
     * 付款id
     */
    paymentId?: number
    /**
     * 基础价格
     */
    basePrice?: number
    /**
     * 山区调整
     */
    mountainAdjustment?: number
    /**
     * 评级调整
     */
    gradeAdjustment?: number
    /**
     * 评级调整
     */
    guidePrice?: number
    /**
     * 是否质押
     */
    pledgePrice?: number
    /**
     * 手工调整
     */
    handAdjustment?: number
    /**
     * 考核价格
     */
    assessmentPrice?: number
    /**
     * 票据价格
     */
    ticketPrice?: number
    /**
     * 备注
     */
    remark?: string
    /**
     * 杭甬特殊调整
     */
    hangyongSpecialAdjustment?: number
  }[]
}

/**
 * 接口 [FTP计息变更-申请信息-保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/27253) 的 **返回类型**
 *
 * @分类 [FTP计息变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3824)
 * @请求头 `POST /new/ftp/interest/change/apply/save`
 * @更新时间 `2025-03-09 11:17:07`
 */
export type ApplySaveResponse = number

/**
 * 接口 [FTP计息变更-申请信息-提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/27247) 的 **请求类型**
 *
 * @分类 [FTP计息变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3824)
 * @请求头 `POST /new/ftp/interest/change/apply/submit`
 * @更新时间 `2025-03-04 17:24:08`
 */
export interface ApplySubmitRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [FTP计息变更-申请信息-提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/27247) 的 **返回类型**
 *
 * @分类 [FTP计息变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3824)
 * @请求头 `POST /new/ftp/interest/change/apply/submit`
 * @更新时间 `2025-03-04 17:24:08`
 */
export type ApplySubmitResponse = string

/**
 * 接口 [FTP计息变更-申请信息-详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/27259) 的 **请求类型**
 *
 * @分类 [FTP计息变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3824)
 * @请求头 `POST /new/ftp/interest/change/apply/detail`
 * @更新时间 `2025-03-09 11:17:07`
 */
export interface ApplyDetailRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [FTP计息变更-申请信息-详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/27259) 的 **返回类型**
 *
 * @分类 [FTP计息变更↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3824)
 * @请求头 `POST /new/ftp/interest/change/apply/detail`
 * @更新时间 `2025-03-09 11:17:07`
 */
export interface ApplyDetailResponse {
  /**
   * 申请记录id
   */
  id?: number
  /**
   * 申请用户id
   */
  applyUserId?: number
  /**
   * 申请用户名称
   */
  applyUserName?: string
  /**
   * 审批状态
   */
  approvalStatus?: string
  /**
   * FTP考核信息
   */
  ftpAssessmentInfoList?: {
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 借据id
     */
    receiptId?: number
    /**
     * 借据编号
     */
    receiptCode?: string
    /**
     * FTP计息变更开始日期
     */
    effectDate?: string
    /**
     * 备注
     */
    remark?: string
    /**
     * id
     */
    id?: number
    /**
     * 付款id
     */
    paymentId?: number
    /**
     * 基础价格
     */
    basePrice?: number
    /**
     * 山区调整
     */
    mountainAdjustment?: number
    /**
     * 评级调整
     */
    gradeAdjustment?: number
    /**
     * 评级调整
     */
    guidePrice?: number
    /**
     * 是否质押
     */
    pledgePrice?: number
    /**
     * 手工调整
     */
    handAdjustment?: number
    /**
     * 考核价格
     */
    assessmentPrice?: number
    /**
     * 票据价格
     */
    ticketPrice?: number
    /**
     * 杭甬特殊调整
     */
    hangyongSpecialAdjustment?: number
  }[]
}

/* prettier-ignore-end */
