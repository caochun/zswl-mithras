/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [judgementDocumentDetail↗](http://yapi.zswltec.com:3000/project/69/interface/api/26551) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/judgementDocumentDetail`
 * @更新时间 `2024-12-20 14:48:22`
 */
export interface DetailJudgementDocumentDetailRequest {
  id?: number
}

/**
 * 接口 [judgementDocumentDetail↗](http://yapi.zswltec.com:3000/project/69/interface/api/26551) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/judgementDocumentDetail`
 * @更新时间 `2024-12-20 14:48:22`
 */
export interface DetailJudgementDocumentDetailResponse {
  /**
   * 案号
   */
  casenumber?: string
  /**
   * 当事人类型
   */
  partykind?: string
  /**
   * 案件类型
   */
  casetype?: string
  /**
   * 判决书类型
   */
  verdicttype?: string
  /**
   * 审判结果
   */
  judgment?: string
  /**
   * 法院
   */
  courtname?: string
  /**
   * 执行标的（万元）
   */
  executetarget?: string
  /**
   * private String invalidationReason; // 失效原因
   */
  infotitle?: string
  /**
   * 案由
   */
  subjectmatter?: string
  /**
   * 胜败诉
   */
  verdict?: number
  /**
   * 发布日期
   */
  pubdate?: string
  /**
   * 审理程序
   */
  procedures?: string
  /**
   * 判决时间
   */
  refereedate?: string
  /**
   * 省份
   */
  state?: string
  /**
   * 发布时间
   */
  inserttime?: string
}

/**
 * 接口 [judicialAssistanceDetail↗](http://yapi.zswltec.com:3000/project/69/interface/api/26545) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/judicialAssistanceDetail`
 * @更新时间 `2024-12-20 14:48:22`
 */
export interface DetailJudicialAssistanceDetailRequest {
  id: number
}

/**
 * 接口 [judicialAssistanceDetail↗](http://yapi.zswltec.com:3000/project/69/interface/api/26545) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/judicialAssistanceDetail`
 * @更新时间 `2024-12-20 14:48:22`
 */
export interface DetailJudicialAssistanceDetailResponse {
  /**
   * 冻结执行事项
   */
  judicialassistevent?: string
  /**
   * 冻结执行裁定书文号
   */
  freezeno?: string
  /**
   * 冻结期限起始日期
   */
  freezestartdate?: string
  /**
   * 冻结期限截止日期
   */
  freezeenddate?: string
  /**
   * 冻结期限
   */
  freezematurity?: string
  /**
   * 续行冻结期限起始日期
   */
  keepfreezestartdate?: string
  /**
   * 续行冻结期限截止日期
   */
  keepfreezeenddate?: string
  /**
   * 解冻执行事项
   */
  cancelfreezeevent?: string
  /**
   * 解冻执行法院
   */
  cancelfreezecourt?: string
  /**
   * 解冻执行裁定书文号
   */
  cancelfreezejudgeno?: string
  /**
   * 解冻执行通知书文号
   */
  cancelfreezenoticeno?: string
  /**
   * 解冻公示日期
   */
  cancelfreezenoticedate?: string
  /**
   * 解冻结束日期
   */
  cancelfreezedate?: string
  /**
   * 失效时间
   */
  invaliddate?: string
  /**
   * 失效原因
   */
  invalidreason?: string
  /**
   * 更新时间
   */
  updateTime?: string
}

/**
 * 接口 [limitHighConsumeDetail↗](http://yapi.zswltec.com:3000/project/69/interface/api/26557) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/limitHighConsumeDetail`
 * @更新时间 `2024-12-20 14:48:23`
 */
export interface DetailLimitHighConsumeDetailRequest {
  id: number
}

/**
 * 接口 [limitHighConsumeDetail↗](http://yapi.zswltec.com:3000/project/69/interface/api/26557) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/limitHighConsumeDetail`
 * @更新时间 `2024-12-20 14:48:23`
 */
export interface DetailLimitHighConsumeDetailResponse {
  /**
   * ID
   */
  id?: number
  /**
   * 被执行人类型
   */
  peopleenforcedtype?: number
  /**
   * 被执行人名称
   */
  peopleenforced?: string
  /**
   * 被执行人ID
   */
  peopleenforcedid?: string
  /**
   * 性别
   */
  gender?: number
  /**
   * 立案时间
   */
  filingtime?: string
  /**
   * 案号
   */
  casenumber?: string
  /**
   * 执行法院
   */
  courtname?: string
  /**
   * 申请执行人
   */
  executeapplyname?: string
  /**
   * 申请执行人EID
   */
  executeapplycode?: string
  /**
   * 案由
   */
  subjectmatter?: string
  /**
   * 信息发布日期
   */
  infopubldate?: string
  /**
   * 原文内容
   */
  content?: string
  /**
   * 发布时间
   */
  inserttime?: string
  /**
   * 修改时间
   */
  updatetime?: string
}

/**
 * 接口 [企业舆情分页查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26509) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/opiInfo`
 * @更新时间 `2024-12-22 14:43:06`
 */
export interface DetailOpiInfoRequest {
  page?: number
  pageSize?: number
  /**
   * 社会统一信用代码
   */
  uscc: string
}

/**
 * 接口 [企业舆情分页查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26509) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/opiInfo`
 * @更新时间 `2024-12-22 14:43:06`
 */
export interface DetailOpiInfoResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 舆情标题
     */
    title?: string
    /**
     * 舆情发生时间
     */
    infoPublDate?: string
    /**
     * 情感重要度
     */
    emotionImportance?: string
    /**
     * 情感方向
     */
    emotionDirection?: string
    /**
     * 预警进度
     */
    warningProgress?: string
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
 * 接口 [企业风险统一视图-详情-经营风险信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26503) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryBizRisk`
 * @更新时间 `2024-12-22 14:43:06`
 */
export interface DetailQueryBizRiskRequest {
  /**
   * 企业名称
   */
  companyName: string
}

/**
 * 接口 [企业风险统一视图-详情-经营风险信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26503) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryBizRisk`
 * @更新时间 `2024-12-22 14:43:06`
 */
export interface DetailQueryBizRiskResponse {
  /**
   * 企业经营异常
   */
  exception_list?: {
    cur_count?: number
    total_count?: number
    exception_list?: {
      /**
       * 列入原因
       */
      exception_result_in?: string
      /**
       * 列出原因
       */
      exception_result_out?: string
      /**
       * 列入日期
       */
      exception_date_in?: string
      /**
       * 列出日期
       */
      exception_date_out?: string
      /**
       * 列入做出决定机构
       */
      exception_organ_in?: string
      /**
       * 创建时间
       */
      create_time?: string
      /**
       * 列出做出决定机构
       */
      exception_organ_out?: string
    }[]
  }
  /**
   * 企业行政处罚
   */
  case_info_list?: {
    cur_count?: number
    total_count?: number
    case_info_list?: {
      /**
       * 处罚日期
       */
      penalty_date?: string
      /**
       * 处罚机关名称
       */
      penalty_authority?: string
      /**
       * 企业名称、注册号、统一社会信用代码
       */
      enterprise_info?: string
      /**
       * 企业名称
       */
      enterprise_name?: string
      /**
       * 省份
       */
      province?: string
      /**
       * 发行结果公示日
       */
      result_date?: string
      /**
       * 处罚决定书文号
       */
      penalty_document_no?: string
      /**
       * 违法行为种类
       */
      illegal_situation?: string
      /**
       * 处罚内容
       */
      penalty_result?: string
      /**
       * 法人代表
       */
      decision_sign_date?: string
      /**
       * 法人代表
       */
      legal_repr?: string
      /**
       * 备注
       */
      remarks?: string
    }[]
  }
  /**
   * 企业严重违法
   */
  illegal_list?: {
    cur_count?: number
    total_count?: number
    illegal_list?: {
      /**
       * 列入原因
       */
      listreason?: string
      /**
       * 列入日期
       */
      listdate?: string
      /**
       * 列入做出决定机构
       */
      listjudgeorg?: string
      /**
       * 列出原因
       */
      removereason?: string
      /**
       * 列出日期
       */
      removedate?: string
      /**
       * 列出做出决定机构
       */
      removejudgeorg?: string
    }[]
  }
  /**
   * 重大税收违法
   */
  major_illegal_list?: {
    id?: number
    /**
     * 违法事实
     */
    illegalFact?: string
    /**
     * 案件性质
     */
    caseNature?: string
    /**
     * 发生日期
     */
    occurDate?: string
    /**
     * 更新时间
     */
    updateTime?: string
  }[]
  /**
   * 企业动产抵押信息
   */
  mortgage_list?: {
    cur_count?: number
    total_count?: number
    mortgage_list?: {
      id?: number
      /**
       * 等级编号
       */
      mortgageRegDate?: string
      /**
       * 概况种类
       */
      overviewType?: string
      /**
       * 被担保债权种类
       */
      type?: string
      /**
       * 抵押金额（万元）
       */
      overviewAmount?: string
      /**
       * 被担保债权数额
       */
      securedPrincipalClaimsBalance?: string
      /**
       * 注销日期
       */
      cancelDate?: string
    }[]
  }
  /**
   * 担保事件
   */
  guarant_event_list?: {
    cur_count?: number
    total_count?: number
    guarant_event_list?: {
      /**
       * 被担保对象
       */
      eventObjectName?: string
      /**
       * 与本公司关系
       */
      objectAssociation?: string
      /**
       * 担保业务类型
       */
      guarantBusiType?: string
      /**
       * 担保行为
       */
      guaranteeBehavior?: string
      /**
       * 担保金额
       */
      latestGuaranteeLimit?: string
      /**
       * 是否逾期
       */
      ifOverdue?: string
      /**
       * 更新事件
       */
      updateTime?: string
      /**
       * 解除日期
       */
      endDate?: string
    }[]
  }
}

/**
 * 接口 [动产抵押详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26533) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryChattelMortageDetail`
 * @更新时间 `2024-12-22 14:43:08`
 */
export interface DetailQueryChattelMortageDetailRequest {
  id: number
}

/**
 * 接口 [动产抵押详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26533) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryChattelMortageDetail`
 * @更新时间 `2024-12-22 14:43:08`
 */
export interface DetailQueryChattelMortageDetailResponse {
  id?: number
  /**
   * 动产抵押登记编号
   */
  impawnRegNumber?: string
  /**
   * 登记日期
   */
  mortgageRegDate?: string
  /**
   * 登记机关
   */
  regOrg?: string
  /**
   * 公示日期
   */
  mortgagePublDate?: string
  /**
   * 被担保债券种类
   */
  type?: string
  /**
   * 被担保债券数额(万)
   */
  securedPrincipalClaimsBalance?: string
  /**
   * 注销日期
   */
  cancelDate?: string
  /**
   * 注销原因
   */
  cancelReason?: string
  /**
   * 履行期限起始日
   */
  performanceStartDate?: string
  /**
   * 履行期限截止日
   */
  performanceEndDate?: string
  /**
   * 债务人履行债务的期限
   */
  pefper_to?: string
  /**
   * 担保范围
   */
  overviewScope?: string
  /**
   * 被担保主债权备注
   */
  overviewRemark?: string
}

/**
 * 接口 [发债信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26347) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryBondBasicInfo`
 * @更新时间 `2024-12-22 14:43:06`
 */
export interface DetailQueryBondBasicInfoRequest {
  /**
   * 社会统一信用代码
   */
  uscc: string
}

/**
 * 接口 [发债信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26347) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryBondBasicInfo`
 * @更新时间 `2024-12-22 14:43:06`
 */
export type DetailQueryBondBasicInfoResponse = {
  /**
   * 债券全称
   */
  bondFullName?: string
  /**
   * 债券代码
   */
  bondCode?: string
  /**
   * 存续状态（1-未到期 2-已到期 3-发行失败）
   */
  bondCurrentStatus?: number
  /**
   * 更新时间
   */
  updateTime?: string
}[]

/**
 * 接口 [司法协助详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26563) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /judicialAssistanceDetail`
 * @更新时间 `2024-12-22 14:43:08`
 */
export interface JudicialAssistanceDetailRequest {
  id: number
}

/**
 * 接口 [司法协助详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26563) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /judicialAssistanceDetail`
 * @更新时间 `2024-12-22 14:43:08`
 */
export interface JudicialAssistanceDetailResponse {
  /**
   * 冻结执行事项
   */
  judicialassistevent?: string
  /**
   * 冻结执行裁定书文号
   */
  freezeno?: string
  /**
   * 冻结期限起始日期
   */
  freezestartdate?: string
  /**
   * 冻结期限截止日期
   */
  freezeenddate?: string
  /**
   * 冻结期限
   */
  freezematurity?: string
  /**
   * 续行冻结期限起始日期
   */
  keepfreezestartdate?: string
  /**
   * 续行冻结期限截止日期
   */
  keepfreezeenddate?: string
  /**
   * 解冻执行事项
   */
  cancelfreezeevent?: string
  /**
   * 解冻执行法院
   */
  cancelfreezecourt?: string
  /**
   * 解冻执行裁定书文号
   */
  cancelfreezejudgeno?: string
  /**
   * 解冻执行通知书文号
   */
  cancelfreezenoticeno?: string
  /**
   * 解冻公示日期
   */
  cancelfreezenoticedate?: string
  /**
   * 解冻结束日期
   */
  cancelfreezedate?: string
  /**
   * 失效时间
   */
  invaliddate?: string
  /**
   * 失效原因
   */
  invalidreason?: string
  /**
   * 更新时间
   */
  updateTime?: string
}

/**
 * 接口 [客户风险统一视图-详情-客户信息分页查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26329) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryCustomerViewInfo`
 * @更新时间 `2024-12-22 14:43:05`
 */
export interface DetailQueryCustomerViewInfoRequest {
  page?: number
  pageSize?: number
  /**
   * 客户名称（模糊查询）
   */
  enterpriseName: string
}

/**
 * 接口 [客户风险统一视图-详情-客户信息分页查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26329) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryCustomerViewInfo`
 * @更新时间 `2024-12-22 14:43:05`
 */
export interface DetailQueryCustomerViewInfoResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一信用代码
     */
    uniformCreditCode?: string
    /**
     * 工商登记号
     */
    businessRegNo?: string
    /**
     * 法人代表
     */
    legalRepr?: string
    /**
     * 企业名称标红
     */
    enterpriseNameFlag?: string
    /**
     * 成立日期
     */
    establishDate?: string
    /**
     * 代表人类型（gtn）
     */
    legalReprDesc?: string
    /**
     * 注册地址
     */
    regAddr?: string
    /**
     * 企业唯一内码
     */
    enterpriseKey?: string
    status?: string
    /**
     * 修改时间
     */
    changeTime?: string
    /**
     * 主键关联编码
     */
    refCode?: string
    /**
     * 数据来源
     */
    sourceFlag?: string
    /**
     * 来源描述
     */
    soureDesc?: string
    /**
     * 组织机构代码
     */
    organizationCode?: string
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
 * 接口 [客户风险统一视图-详情-客户基础信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26335) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryCompanyBasicInfo`
 * @更新时间 `2024-12-22 14:43:05`
 */
export interface DetailQueryCompanyBasicInfoRequest {
  /**
   * 客户名称（模糊查询）
   */
  enterpriseName: string
}

/**
 * 接口 [客户风险统一视图-详情-客户基础信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26335) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryCompanyBasicInfo`
 * @更新时间 `2024-12-22 14:43:05`
 */
export interface DetailQueryCompanyBasicInfoResponse {
  /**
   * 类型
   */
  enterprise_type?: string
  /**
   * 登记机关
   */
  reg_org?: string
  /**
   * 是否上市企业
   */
  is_listed?: string
  /**
   * 注册资本货币名称
   */
  register_currency?: string
  /**
   * 行业代码
   */
  industry_code?: string
  /**
   * 注册地址
   */
  register_address?: string
  /**
   * 曾用名
   */
  csdc_alias?: string
  /**
   * 营业期限至
   */
  validity_term_to?: string
  /**
   * 所在省份
   */
  province?: string
  /**
   * 组织机构代码
   */
  organ_code?: string
  /**
   * 电子邮箱
   */
  email?: string
  /**
   * 注销日期
   */
  cancel_time?: string
  /**
   * 网址
   */
  website?: string
  /**
   * 所属行业
   */
  industry_name?: string
  /**
   * 核准时间
   */
  approval_date?: string
  /**
   * 统一社会信用代码
   */
  uniform_social_credit_code?: string
  /**
   * 注册资本（万）
   */
  regcapital?: string
  /**
   * 联系方式
   */
  contact_way?: string
  /**
   * 企业状态
   */
  enterprise_status?: string
  /**
   * 所在城市
   */
  reg_org_city?: string
  /**
   * 经营范围
   */
  work_range?: string
  /**
   * 成立日期
   */
  establishment_date?: string
  /**
   * 营业期限自
   */
  validity_term_from?: string
  /**
   * 法定代表人
   */
  legal_repr?: string
  /**
   * 所在地区
   */
  district?: string
  /**
   * 公司英文名称
   */
  eng_name?: string
  /**
   * 吊销日期
   */
  revoke_date?: string
  /**
   * 企业名称
   */
  enterprise_name?: string
  /**
   * 实收资本
   */
  paidin_capital?: string
  /**
   * 员工人数
   */
  work_scale?: string
  /**
   * company_code
   */
  company_code?: string
}

/**
 * 接口 [担保对象详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26527) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryGuaranteeEventDetail`
 * @更新时间 `2024-12-22 14:43:07`
 */
export interface DetailQueryGuaranteeEventDetailRequest {
  id: number
}

/**
 * 接口 [担保对象详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26527) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryGuaranteeEventDetail`
 * @更新时间 `2024-12-22 14:43:07`
 */
export interface DetailQueryGuaranteeEventDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 涉及对象类别
   */
  eventobjecttype?: string
  /**
   * 涉及对象名称
   */
  eventobjectname?: string
  /**
   * 涉及对象角色
   */
  eventobjectrole?: string
  /**
   * 与本公司的关联关系
   */
  objectassociation?: string
  /**
   * 信息来源
   */
  InfoSource?: string
  /**
   * 担保业务类型
   */
  guarantbusitype?: string
  /**
   * 事件类型
   */
  eventtype?: string
  /**
   * 担保行为
   */
  guaranteebehavior?: string
  /**
   * 事件进程
   */
  eventprocedure?: string
  /**
   * 事件内容
   */
  eventcontent?: string
  /**
   * 货币单位
   */
  currencyunit?: string
  /**
   * 担保原因
   */
  guaranteereason?: string
  /**
   * 担保余额(元)
   */
  guaranteebalance?: number
  /**
   * 最新担保余额(元)
   */
  latestguaranteesum?: number
  /**
   * 担保方式
   */
  cguaranteemethod?: string
  /**
   * 担保截止日
   */
  cguaranteenddate?: string
  /**
   * 借款方
   */
  lender?: string
  /**
   * 借款方性质
   */
  lenderattribute?: string
  /**
   * 解除日期
   */
  relievedate?: string
  /**
   * 解除方式
   */
  relievemethod?: string
  /**
   * 是否逾期
   */
  ifoverdue?: boolean
  /**
   * 逾期时间(月)
   */
  overduetime?: number
  /**
   * 担保逾期金额(元)
   */
  overduesum?: number
  /**
   * 是否违规
   */
  ifviolation?: boolean
  /**
   * 逾期描述
   */
  overduetimedesc?: string
  /**
   * 发布时间
   */
  inserttime?: string
  /**
   * 更新时间
   */
  updatetime?: string
}

/**
 * 接口 [法律诉讼↗](http://yapi.zswltec.com:3000/project/69/interface/api/26539) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryLitigation`
 * @更新时间 `2024-12-22 14:43:08`
 */
export interface DetailQueryLitigationRequest {
  uscc: string
}

/**
 * 接口 [法律诉讼↗](http://yapi.zswltec.com:3000/project/69/interface/api/26539) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryLitigation`
 * @更新时间 `2024-12-22 14:43:08`
 */
export interface DetailQueryLitigationResponse {
  /**
   * 立案信息
   */
  caseInformations?: {
    id?: number
    /**
     * 立案号
     */
    caseNumber?: string
    /**
     * 案件状态
     */
    caseStatus?: string
    /**
     * 立案缘由
     */
    caseReason?: string
    /**
     * 立案日期
     */
    caseDate?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 是否历史
     */
    isHistory?: string
  }[]
  /**
   * 被执行信息
   */
  executedInformations?: {
    id?: number
    /**
     * 案号
     */
    caseNo?: string
    /**
     * 执行标的（元）
     */
    target?: string
    /**
     * 立案时间
     */
    caseDate?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 是否历史
     */
    isHistory?: string
  }[]
  /**
   * 司法协助
   */
  judicialAssistances?: {
    id?: number
    /**
     * 通知文书号
     */
    noticeDocumentNumber?: string
    /**
     * 股权数额
     */
    stockAmount?: string
    /**
     * 状态
     */
    status?: string
    /**
     * 公示日期
     */
    publicationDate?: string
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 是否历史
     */
    ifHistory?: number
  }[]
  /**
   * 判决文书
   */
  judgmentDocuments?: {
    id?: number
    /**
     * 案号
     */
    caseNo?: string
    /**
     * 案由
     */
    caseReason?: string
    /**
     * 当事人类型
     */
    partyType?: number
    /**
     * 胜败诉
     */
    winLose?: number
    /**
     * 审案程序
     */
    caseProgram?: string
    /**
     * 审判结果
     */
    judgmentResult?: string
  }[]
  /**
   * 限制高消费
   */
  restrictionHighConsumptions?: {
    id?: number
    /**
     * 被执行人姓名
     */
    executedName?: string
    /**
     * 被执行人类型
     */
    executedType?: number
    /**
     * 案号
     */
    caseNo?: string
    /**
     * 案由
     */
    caseReason?: string
    /**
     * 立案时间
     */
    caseDate?: string
  }[]
}

/**
 * 接口 [股东信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26341) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryStockHolders`
 * @更新时间 `2024-12-22 14:43:05`
 */
export interface DetailQueryStockHoldersRequest {
  /**
   * 客户名称（模糊查询）
   */
  enterpriseName: string
}

/**
 * 接口 [股东信息查询↗](http://yapi.zswltec.com:3000/project/69/interface/api/26341) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/queryStockHolders`
 * @更新时间 `2024-12-22 14:43:05`
 */
export type DetailQueryStockHoldersResponse = {
  /**
   * 实缴
   */
  actul_capital?: number
  /**
   * 认缴
   */
  capital?: number
  /**
   * 出资日期
   */
  capital_date?: string
  /**
   * 币种：1000-美元, 1100-港元, 1420-人民币, 3000-欧元
   */
  currency_code?: number
  /**
   * 持股数量
   */
  shares_holding?: number
  /**
   * uuid
   */
  uuid?: string
  /**
   * 节点分类 ac=疑似实控人, be=最终受益人, up=上级股东, down=对外投资, directors=高管
   */
  nodeCategory?: string
  /**
   * 编码
   */
  code?: string
  /**
   * 名称
   */
  name?: string
  /**
   * 控股比例
   */
  ratio?: string
  /**
   * 职务名称
   */
  jobName?: string
  /**
   * 类型 1=自然人，2=企业、证券
   */
  type?: number
  /**
   * 是否能继续展开
   */
  hasNode?: boolean
}[]

/**
 * 接口 [舆情统计↗](http://yapi.zswltec.com:3000/project/69/interface/api/26515) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/opiStatistc`
 * @更新时间 `2024-12-22 14:43:07`
 */
export interface DetailOpiStatistcRequest {
  page?: number
  pageSize?: number
  /**
   * 社会统一信用代码
   */
  uscc: string
}

/**
 * 接口 [舆情统计↗](http://yapi.zswltec.com:3000/project/69/interface/api/26515) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/opiStatistc`
 * @更新时间 `2024-12-22 14:43:07`
 */
export interface DetailOpiStatistcResponse {
  /**
   * 负面舆情数量
   */
  negativeCount?: number
  /**
   * 正面舆情数量
   */
  positiveCount?: number
  /**
   * 中性舆情数量
   */
  publicCount?: number
}

/**
 * 接口 [获取地区onomy数据↗](http://yapi.zswltec.com:3000/project/69/interface/api/26317) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/areaEconomy`
 * @更新时间 `2024-12-22 14:43:04`
 */
export interface DetailAreaEconomyRequest {
  /**
   * 社会统一信用代码
   */
  uscc: string
  /**
   * 数据年份
   */
  dataYear: number
}

/**
 * 接口 [获取地区onomy数据↗](http://yapi.zswltec.com:3000/project/69/interface/api/26317) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/areaEconomy`
 * @更新时间 `2024-12-22 14:43:04`
 */
export interface DetailAreaEconomyResponse {
  /**
   * 主键id
   */
  id?: number
  /**
   * 区域编码
   */
  areaUniCode?: number
  /**
   * 数据年份
   */
  dataYear?: number
  /**
   * 人口同比变化(%)
   */
  residentPopYoyRatio?: number
  /**
   * 常住人口(万人)
   */
  residentPop?: number
  /**
   * 户籍人口(万人)
   */
  householdRegisteredPop?: number
  /**
   * gdp(万元)
   */
  gdp?: number
  /**
   * gdp增速(%)
   */
  gdpGrowth?: number
  /**
   * 人均GDP(元)
   */
  perCapitaGdp?: number
  /**
   * 第一产业gdp(万元)
   */
  primarySectorGdp?: number
  /**
   * 第二产业gdp(万元)
   */
  secondSectorGdp?: number
  /**
   * 第三产业gdp(万元)
   */
  tertiarySectorGdp?: number
  /**
   * 一般公共预算收入(万元)
   */
  generalPublicBudgetIncome?: number
  /**
   * 一般公共预算支出(万元)
   */
  generalPublicBudgetExpend?: number
  /**
   * 财政自给率(%)
   */
  finSelfSufficiencyRatio?: number
  /**
   * 税收收入(万元)
   */
  taxIncome?: number
  /**
   * 税收收入占比(%)
   */
  taxIncomeRatio?: number
  /**
   * 转移性收入(万元)
   */
  transferIncome?: number
  /**
   * 上级补助收入(万元)
   */
  higherAuthorityGrant?: number
  /**
   * 转移支付收入(万元)
   */
  transferPayIncome?: number
  /**
   * 政府性基金支出(万元)
   */
  govFundExpend?: number
  /**
   * 政府性基金收入(万元)
   */
  govFundIncome?: number
  /**
   * 土地出让收入(万元)
   */
  landTransferIncome?: number
  /**
   * 国有资本经营收入(万元)
   */
  stateCapitalOperatingIncome?: number
  /**
   * 国有资本经营支出(万元)
   */
  stateCapitalOperatingExpend?: number
  /**
   * 地方政府债务余额(万元)
   */
  localGovDebtBalance?: number
  /**
   * 地方政府债务限额(万元)
   */
  localGovDebtQuota?: number
  /**
   * 政府一般债务余额(万)
   */
  govGeneralDebtBalance?: number
  /**
   * 政府专项债务余额(万)
   */
  govSpecialDebtBalance?: number
  /**
   * 本年土地成交价款(万元)
   */
  thisYearLandTradeAmount?: number
  /**
   * 商品房销售面积(万m^2)
   */
  commerceHouseSalesArea?: number
  /**
   * 商品房销售额(万元)
   */
  commerceHouseSales?: number
  /**
   * 金融机构各项存款余额(万元)
   */
  finInstCnyDepositBalance?: number
  /**
   * 金融机构各项贷款余额(万元)
   */
  finInstCnyLoanBalance?: number
  /**
   * 金融机构各项存款余额(本外币)
   */
  finInstFcyDepositBalance?: number
  /**
   * 金融机构各项贷款余额(本外币)
   */
  finInstFcyLoanBalance?: number
  /**
   * 固定资产投资(万元)
   */
  fixedAssetsInvest?: number
  /**
   * 固定资产投资增速(%)
   */
  fixedAssetsInvestGrowth?: number
  /**
   * 房地产投资(万元)
   */
  realEstateDevInvest?: number
  /**
   * 房地产投资增速(%)
   */
  realEstateDevInvestGrowth?: number
  /**
   * 社会消费品零售总额(单位:万元)
   */
  goodsRetailSales?: number
  /**
   * 隐性债务(城投有息债务)(单位:万元)
   */
  hideDebt?: number
  /**
   * 地方政府债务/gdp(负债率)(单位:%)
   */
  localGovDebtDivideGdp?: number
  /**
   * 地方政府债务率(单位:%)
   */
  localGovDebtRate?: number
  /**
   * 债务率(单位:%)
   */
  debtRate?: number
  /**
   * 地方政府综合财力(万元)
   */
  govComprehensiveFinance?: number
  /**
   * 地区隐性债务率(城投有息债务合计/地方综合财力)(单位:%)
   */
  hiddenDebtRatio?: number
  /**
   * 尾部隐性债务(城投有息债务)(单位:万元)
   */
  tailHideDebt?: number
  /**
   * 分配股利(万元)
   */
  distributionDividend?: number
  /**
   * 尾部分配股利(万元)
   */
  tailDistributionDividend?: number
  /**
   * 工业总产值(单位:万元)
   */
  industrialOutput?: number
  /**
   * 城镇居民人均可支配收入(单位:元)
   */
  townPcdi?: number
  /**
   * 工业增加值(单位:万元)
   */
  industrialAddedValue?: number
  /**
   * 进口总额(万美元)(单位:万美元)
   */
  totalImportUsd?: number
  /**
   * 出口总额(万美元)(单位:万美元)
   */
  totalExportUsd?: number
  /**
   * 进出口总额(万美元)(单位:万美元)
   */
  totalImportExportUsd?: number
  /**
   * 社会消费品零售总额增速(单位:%)
   */
  goodsRetailSalesGrowth?: number
  /**
   * dm城投隐性债务(dm城投有利息债务)(万元)
   */
  dmUdicHiddenDebt?: number
  /**
   * 是否删除
   */
  deleted?: number
  /**
   * 更新时间id
   */
  updateTimeId?: number
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
}

/**
 * 接口 [获取地州onomy数据↗](http://yapi.zswltec.com:3000/project/69/interface/api/26323) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/ctzReginEconomy`
 * @更新时间 `2024-12-22 14:43:04`
 */
export interface DetailCtzReginEconomyRequest {
  /**
   * 社会统一信用代码
   */
  uscc: string
  /**
   * 数据年份
   */
  dataYear: number
}

/**
 * 接口 [获取地州onomy数据↗](http://yapi.zswltec.com:3000/project/69/interface/api/26323) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/ctzReginEconomy`
 * @更新时间 `2024-12-22 14:43:04`
 */
export interface DetailCtzReginEconomyResponse {
  /**
   * id
   */
  id?: number
  /**
   * 区域编码
   */
  areaUniCode?: number
  /**
   * 区域名称
   */
  areaUniName?: string
  /**
   * 数据年份
   */
  dataYear?: number
  /**
   * 地区城投债余额(单位:亿元)
   */
  ctzDebtBalance?: number
  /**
   * 地区城投平台数量(单位:个)
   */
  ctzComCount?: number
  /**
   * 地区隐性债务(单位:亿元)
   */
  hideDebt?: number
  /**
   * 地区隐性债务率(单位:%)
   */
  hideDebtRate?: number
  /**
   * 财政自给率(单位:%)
   */
  finSelfSufficiencyRate?: number
  /**
   * 负债率(单位:%)
   */
  debtRatio?: number
  /**
   * 债务率(单位:%)
   */
  debtRate?: number
  /**
   * 债务率(宽口径，本级，%)
   */
  debtRateLocal?: number
  /**
   * 债务率(宽口径，全辖，%)
   */
  debtRateFull?: number
  /**
   * 税收收入占比(%)
   */
  taxRevenueRate?: number
  /**
   * 一般公共预算收入增速(单位：%)
   */
  gpBudgetRevenueAdd?: number
  /**
   * 地方综合财力（单位：亿元)
   */
  comprehensiveResources?: number
  /**
   * 删除状态 1 删除 0正常
   */
  isDeleted?: number
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 更新时间
   */
  updateTime?: string
}

/**
 * 接口 [裁判文书详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26569) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /judgementDocumentDetail`
 * @更新时间 `2024-12-22 14:43:09`
 */
export interface JudgementDocumentDetailRequest {
  id?: number
}

/**
 * 接口 [裁判文书详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26569) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /judgementDocumentDetail`
 * @更新时间 `2024-12-22 14:43:09`
 */
export interface JudgementDocumentDetailResponse {
  /**
   * 案号
   */
  casenumber?: string
  /**
   * 当事人类型
   */
  partykind?: string
  /**
   * 案件类型
   */
  casetype?: string
  /**
   * 判决书类型
   */
  verdicttype?: string
  /**
   * 审判结果
   */
  judgment?: string
  /**
   * 法院
   */
  courtname?: string
  /**
   * 执行标的（万元）
   */
  executetarget?: string
  /**
   * private String invalidationReason; // 失效原因
   */
  infotitle?: string
  /**
   * 案由
   */
  subjectmatter?: string
  /**
   * 胜败诉
   */
  verdict?: number
  /**
   * 发布日期
   */
  pubdate?: string
  /**
   * 审理程序
   */
  procedures?: string
  /**
   * 判决时间
   */
  refereedate?: string
  /**
   * 省份
   */
  state?: string
  /**
   * 发布时间
   */
  inserttime?: string
}

/**
 * 接口 [重大税收违法详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26521) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/majortax`
 * @更新时间 `2024-12-22 14:43:07`
 */
export interface DetailMajortaxRequest {
  id: number
}

/**
 * 接口 [重大税收违法详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26521) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /customer/view/detail/majortax`
 * @更新时间 `2024-12-22 14:43:07`
 */
export interface DetailMajortaxResponse {
  /**
   * id
   */
  id?: number
  /**
   * 纳税人识别号
   */
  taxnumber?: string
  /**
   * 经营地点
   */
  businessplace?: string
  /**
   * 移送公安情况
   */
  policetransfer?: string
  /**
   * 所属税务机关
   */
  taxorg?: string
  /**
   * 检查机关
   */
  checkorg?: string
  /**
   * 公示税务机关
   */
  publictaxorg?: string
  /**
   * 发生日期
   */
  occurdate?: string
  /**
   * 违法事实开始时间
   */
  illefactstartdate?: string
  /**
   * 违法事实结束时间
   */
  illefactenddate?: string
  /**
   * 发布时间
   */
  inserttime?: string
  /**
   * 更新时间
   */
  updatetime?: string
  /**
   * 案件性质
   */
  casenature?: string
  /**
   * 违法事实
   */
  illegalfact?: string
  /**
   * 法律依据及处罚
   */
  legalbasispunishment?: string
}

/**
 * 接口 [限制高消费详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26575) 的 **请求类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /limitHighConsumeDetail`
 * @更新时间 `2024-12-22 14:43:10`
 */
export interface LimitHighConsumeDetailRequest {
  id: number
}

/**
 * 接口 [限制高消费详情↗](http://yapi.zswltec.com:3000/project/69/interface/api/26575) 的 **返回类型**
 *
 * @分类 [CustomerUnifiedViewController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3638)
 * @请求头 `POST /limitHighConsumeDetail`
 * @更新时间 `2024-12-22 14:43:10`
 */
export interface LimitHighConsumeDetailResponse {
  /**
   * ID
   */
  id?: number
  /**
   * 被执行人类型
   */
  peopleenforcedtype?: number
  /**
   * 被执行人名称
   */
  peopleenforced?: string
  /**
   * 被执行人ID
   */
  peopleenforcedid?: string
  /**
   * 性别
   */
  gender?: number
  /**
   * 立案时间
   */
  filingtime?: string
  /**
   * 案号
   */
  casenumber?: string
  /**
   * 执行法院
   */
  courtname?: string
  /**
   * 申请执行人
   */
  executeapplyname?: string
  /**
   * 申请执行人EID
   */
  executeapplycode?: string
  /**
   * 案由
   */
  subjectmatter?: string
  /**
   * 信息发布日期
   */
  infopubldate?: string
  /**
   * 原文内容
   */
  content?: string
  /**
   * 发布时间
   */
  inserttime?: string
  /**
   * 修改时间
   */
  updatetime?: string
}

/* prettier-ignore-end */
