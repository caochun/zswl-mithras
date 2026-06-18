/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [获取金融局上报所有数据字典↗](http://yapi.zswltec.com:3000/project/11/interface/api/37195) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/getAssociationDict`
 * @更新时间 `2025-09-09 08:29:31`
 */
export interface ReportGetAssociationDictRequest {}

/**
 * 接口 [获取金融局上报所有数据字典↗](http://yapi.zswltec.com:3000/project/11/interface/api/37195) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/getAssociationDict`
 * @更新时间 `2025-09-09 08:29:31`
 */
export interface ReportGetAssociationDictResponse {}

/**
 * 接口 [金融协会报送数据-上报↗](http://yapi.zswltec.com:3000/project/11/interface/api/32605) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/push`
 * @更新时间 `2025-09-10 15:12:47`
 */
export interface ReportPushRequest {
  /**
   * 上报的ids
   */
  ids: number[]
}

/**
 * 接口 [金融协会报送数据-上报↗](http://yapi.zswltec.com:3000/project/11/interface/api/32605) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/push`
 * @更新时间 `2025-09-10 15:12:47`
 */
export interface ReportPushResponse {
  importSuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-业务情况表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37279) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/businessSituation`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyBusinessSituationRequest {
  /**
   * 自增主键
   */
  id?: number
  /**
   * 行号 | 同一批次数据从1开始递增
   */
  rowNum?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 总收入_期初数（万元）
   */
  totIncmAbop?: number
  /**
   * 总收入_本期发生额（万元）
   */
  totIncmAotc?: number
  /**
   * 总收入_期末数（万元）
   */
  totIncmAeop?: number
  /**
   * 经营租赁业务收入_期初数（万元）
   */
  operLeasBusiIncmAbop?: number
  /**
   * 经营租赁业务收入_本期发生额（万元）
   */
  operLeasBusiIncmAotc?: number
  /**
   * 经营租赁业务收入_期末数（万元）
   */
  operLeasBusiIncmAeop?: number
  /**
   * 融资租赁业务收入_期初数（万元）
   */
  fnlBusiIncmAbop?: number
  /**
   * 融资租赁业务收入_本期发生额（万元）
   */
  fnlBusiIncmAotc?: number
  /**
   * 融资租赁业务收入_期末数（万元）
   */
  fnlBusiIncmAeop?: number
  /**
   * 利息收入_期初数（万元）
   */
  intrIncmAbop?: number
  /**
   * 利息收入_本期发生额（万元）
   */
  intrIncmAotc?: number
  /**
   * 利息收入_期末数（万元）
   */
  intrIncmAeop?: number
  /**
   * 费用收入_期初数（万元）
   */
  feeIncmAbop?: number
  /**
   * 费用收入_本期发生额（万元）
   */
  feeIncmAotc?: number
  /**
   * 费用收入_期末数（万元）
   */
  feeIncmAeop?: number
  /**
   * 其他收入_期初数（万元）
   */
  othIncmAbop?: number
  /**
   * 其他收入_本期发生额（万元）
   */
  othIncmAotc?: number
  /**
   * 其他收入_期末数（万元）
   */
  othIncmAeop?: number
  /**
   * 租赁资产_期初数（万元）
   */
  leasAstAbop?: number
  /**
   * 租赁资产_本期发生额（万元）
   */
  leasAstAotc?: number
  /**
   * 租赁资产_期末数（万元）
   */
  leasAstAeop?: number
  /**
   * 经营性租赁资产_期初数（万元）
   */
  operLeasAstAbop?: number
  /**
   * 经营性租赁资产_本期发生额（万元）
   */
  operLeasAstAotc?: number
  /**
   * 经营性租赁资产_期末数（万元）
   */
  operLeasAstAeop?: number
  /**
   * 融资租赁资产_期初数（万元）
   */
  finLeasAstAbop?: number
  /**
   * 融资租赁资产_本期发生额（万元）
   */
  finLeasAstAotc?: number
  /**
   * 融资租赁资产_期末数（万元）
   */
  finLeasAstAeop?: number
  /**
   * 直接租赁资产_期初数（万元）
   */
  dirtLeasAstAbop?: number
  /**
   * 直接租赁资产_本期发生额（万元）
   */
  dirtLeasAstAotc?: number
  /**
   * 直接租赁资产_期末数（万元）
   */
  dirtLeasAstAeop?: number
  /**
   * 售后回租资产_期初数（万元）
   */
  slbkAstAbop?: number
  /**
   * 售后回租资产_本期发生额（万元）
   */
  slbkAstAotc?: number
  /**
   * 售后回租资产_期末数（万元）
   */
  slbkAstAeop?: number
  /**
   * 跨省融资租赁资产余额_期初数（万元）
   */
  iprvFnlAstBalAbop?: number
  /**
   * 跨省融资租赁资产余额_本期发生额（万元）
   */
  iprvFnlAstBalAotc?: number
  /**
   * 跨省融资租赁资产余额_期末数（万元）
   */
  iprvFnlAstBalAeop?: number
  /**
   * 跨省售后回租资产余额_期初数（万元）
   */
  iprvSlbkAstBalAbop?: number
  /**
   * 跨省售后回租资产余额_本期发生额（万元）
   */
  iprvSlbkAstBalAotc?: number
  /**
   * 跨省售后回租资产余额_期末数（万元）
   */
  iprvSlbkAstBalAeop?: number
  /**
   * 融资租赁投放额_期初数（万元）
   */
  fnlRelsAbop?: number
  /**
   * 融资租赁投放额_本期发生额（万元）
   */
  fnlRelsAotc?: number
  /**
   * 融资租赁投放额_期末数（万元）
   */
  fnlRelsAeop?: number
  /**
   * 直接租赁投放额_期初数（万元）
   */
  dirtLeasRelsAbop?: number
  /**
   * 直接租赁投放额_本期发生额（万元）
   */
  dirtLeasRelsAotc?: number
  /**
   * 直接租赁投放额_期末数（万元）
   */
  dirtLeasRelsAeop?: number
  /**
   * 售后回租投放额_期初数（万元）
   */
  slbkRelsAbop?: number
  /**
   * 售后回租投放额_本期发生额（万元）
   */
  slbkRelsAotc?: number
  /**
   * 售后回租投放额_期末数（万元）
   */
  slbkRelsAeop?: number
  /**
   * 固定收益类证券投资余额_期初数（万元）
   */
  fixPayfScrIvsmAbop?: number
  /**
   * 固定收益类证券投资余额_本期发生额（万元）
   */
  fixPayfScrIvsmAotc?: number
  /**
   * 固定收益类证券投资_期末数（万元）
   */
  fixPayfScrIvsmAeop?: number
  /**
   * 国债余额_期初数（万元）
   */
  treaAbop?: number
  /**
   * 国债余额_本期发生额（万元）
   */
  treaAotc?: number
  /**
   * 国债余额_期末数（万元）
   */
  treaAeop?: number
  /**
   * 资产减值损失准备_期初数（万元）
   */
  ipoaLossAbop?: number
  /**
   * 资产减值损失准备_本期发生额（万元）
   */
  ipoaLossAotc?: number
  /**
   * 资产减值损失准备_期末数（万元）
   */
  ipoaLossAeop?: number
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId?: string
}

/**
 * 接口 [金融协会报送数据-修改-业务情况表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37279) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/businessSituation`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyBusinessSituationResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-主要业务清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/37309) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/mainBusiness`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyMainBusinessRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 序号
     */
    onum?: string
    /**
     * 合同名称
     */
    agmtName?: string
    /**
     * 合同编号
     */
    agmtNo?: string
    /**
     * 合同签订日期
     */
    agmtSignDate?: string
    /**
     * 协议到期日期
     */
    agmtMatuDate?: string
    /**
     * 合同类型code
     */
    agmtTypeCode?: string
    /**
     * 合同类型display
     */
    agmtTypeDisplay?: string
    /**
     * 租赁物类型
     */
    lasdType?: string
    /**
     * 项目行业分类code
     */
    projIndtClasCode?: string
    /**
     * 项目行业分类display
     */
    projIndtClasDisplay?: string
    /**
     * 客户姓名
     */
    custName?: string
    /**
     * 客户证件号码
     */
    custCertNum?: string
    /**
     * 客户规模code
     */
    custScalCode?: string
    /**
     * 客户规模display
     */
    custScalDisplay?: string
    /**
     * 融资租赁投放额
     */
    fnlRels?: number
    /**
     * 收回本金
     */
    wthdPrin?: number
    /**
     * 租金余额
     */
    rentBal?: number
    /**
     * 综合融资成本
     */
    cmphFinCost?: number
    /**
     * 增信情况code
     */
    udpnSituCode?: string
    /**
     * 增信情况display
     */
    udpnSituDisplay?: string
    /**
     * 增信方
     */
    udpn?: string
    /**
     * 逾期租金
     */
    ovduRent?: number
    /**
     * 逾期天数code
     */
    ovduDaysCode?: string
    /**
     * 逾期天数display
     */
    ovduDaysDisplay?: string
    /**
     * 逾期处置情况
     */
    ovduDspsProg?: string
    /**
     * 是否纳入不良
     */
    npFlag?: string
    /**
     * 是否纳入不良display
     */
    npFlagDisplay?: string
    /**
     * 不良余额
     */
    npBal?: number
    /**
     * 客户数量
     */
    custVol?: number
    /**
     * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-主要业务清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/37309) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/mainBusiness`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyMainBusinessResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-公司利润表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37285) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/companyProfit`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyCompanyProfitRequest {
  /**
   * 自增主键
   */
  id?: number
  /**
   * 行号 | 同一批次数据从1开始递增
   */
  rowNum?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 主营业务收入_本季发生额(元)
   */
  mainBusiIncmActm?: number
  /**
   * 主营业务成本_本季发生额(元)
   */
  mainBusiCostActm?: number
  /**
   * 主营业务税金及附加_本季发生额(元)
   */
  mainBusiTaxAddActm?: number
  /**
   * 主营业务利润_本季发生额(元)
   */
  mainBusiProfActm?: number
  /**
   * 其他业务利润_本季发生额(元)
   */
  othBusiProfActm?: number
  /**
   * 营业费用_本季发生额(元)
   */
  busiFeeActm?: number
  /**
   * 管理费用_本季发生额(元)
   */
  magFeeActm?: number
  /**
   * 财务费用_本季发生额(元)
   */
  finFeeActm?: number
  /**
   * 资产减值损失_本季发生额(元)
   */
  ipoaLossActm?: number
  /**
   * 信用减值损失_本季发生额(元)
   */
  credDecrLossActm?: number
  /**
   * 营业利润_本季发生额(元)
   */
  busiProfActm?: number
  /**
   * 投资收益_本季发生额(元)
   */
  ivsmPayfActm?: number
  /**
   * 营业外收入_本季发生额(元)
   */
  noprIncmActm?: number
  /**
   * 营业外支出_本季发生额(元)
   */
  noprPayActm?: number
  /**
   * 利润总额_本季发生额(元)
   */
  profGamtActm?: number
  /**
   * 所得税费用_本季发生额(元)
   */
  inctFeeActm?: number
  /**
   * 净利润_本季发生额(元)
   */
  netProfActm?: number
  /**
   * 主营业务收入_本年累计(元)
   */
  mainBusiIncmTyag?: number
  /**
   * 主营业务成本_本年累计(元)
   */
  mainBusiCostTyag?: number
  /**
   * 主营业务税金及附加_本年累计(元)
   */
  mainBusiTaxAddTyag?: number
  /**
   * 主营业务利润_本年累计(元)
   */
  mainBusiProfTyag?: number
  /**
   * 其他业务利润_本年累计(元)
   */
  othBusiProfTyag?: number
  /**
   * 营业费用_本年累计(元)
   */
  busiFeeTyag?: number
  /**
   * 管理费用_本年累计(元)
   */
  magFeeTyag?: number
  /**
   * 财务费用_本年累计(元)
   */
  finFeeTyag?: number
  /**
   * 资产减值损失_本年累计(元)
   */
  ipoaLossTyag?: number
  /**
   * 信用减值损失_本年累计(元)
   */
  credDecrLossTyag?: number
  /**
   * 营业利润_本年累计(元)
   */
  busiProfTyag?: number
  /**
   * 投资收益_本年累计(元)
   */
  ivsmPayfTyag?: number
  /**
   * 营业外收入_本年累计(元)
   */
  noprIncmTyag?: number
  /**
   * 营业外支出_本年累计(元)
   */
  noprPayTyag?: number
  /**
   * 利润总额_本年累计(元)
   */
  profGamtTyag?: number
  /**
   * 所得税费用_本年累计(元)
   */
  inctFeeTyag?: number
  /**
   * 净利润_本年累计(元)
   */
  netProfTyag?: number
  /**
   * 主营业务收入_去年同期(元)
   */
  mainBusiIncmCply?: number
  /**
   * 主营业务成本_去年同期(元)
   */
  mainBusiCostCply?: number
  /**
   * 主营业务税金及附加_去年同期(元)
   */
  mainBusiTaxAddCply?: number
  /**
   * 主营业务利润_去年同期(元)
   */
  mainBusiProfCply?: number
  /**
   * 其他业务利润_去年同期(元)
   */
  othBusiProfCply?: number
  /**
   * 营业费用_去年同期(元)
   */
  busiFeeCply?: number
  /**
   * 管理费用_去年同期(元)
   */
  magFeeCply?: number
  /**
   * 财务费用_去年同期(元)
   */
  finFeeCply?: number
  /**
   * 资产减值损失_去年同期(元)
   */
  ipoaLossCply?: number
  /**
   * 信用减值损失_去年同期(元)
   */
  credDecrLossCply?: number
  /**
   * 营业利润_去年同期(元)
   */
  busiProfCply?: number
  /**
   * 投资收益_去年同期(元)
   */
  ivsmPayfCply?: number
  /**
   * 营业外收入_去年同期(元)
   */
  noprIncmCply?: number
  /**
   * 营业外支出_去年同期(元)
   */
  noprPayCply?: number
  /**
   * 利润总额_去年同期(元)
   */
  profGamtCply?: number
  /**
   * 所得税费用_去年同期(元)
   */
  inctFeeCply?: number
  /**
   * 净利润_去年同期(元)
   */
  netProfCply?: number
  /**
   * 报表实例id | uuid
   */
  reportInstanceId?: string
}

/**
 * 接口 [金融协会报送数据-修改-公司利润表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37285) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/companyProfit`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyCompanyProfitResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-关联方信息汇总表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37315) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/relation`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyRelationRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    rowNum?: number
    /**
     * 主键
     */
    id?: number
    /**
     * 序号
     */
    onum?: string
    /**
     * 关联方名称
     */
    relpName?: string
    /**
     * 是否为本公司股东关联方
     */
    corpShahRelpFlag?: string
    /**
     * 是否为本公司股东关联方
     */
    corpShahRelpFlagDisplay?: string
    /**
     * 本公司股东名称
     */
    corpShahName?: string
    /**
     * 表内业务-关联方租赁余额_单一关联方
     */
    onblRelpLeasBalSrlp?: number
    /**
     * 表内业务-占净资产比例_单一关联方
     */
    onblOnarSrlp?: number
    /**
     * 表外业务-担保_单一关联方
     */
    ofblGuarSrlp?: number
    /**
     * 表外业务-其他_单一关联方
     */
    ofblOthSrlp?: number
    /**
     * 扣减项-合格质物_单一关联方
     */
    deitQulfSbimSrlp?: number
    /**
     * 扣减项-合格保证_单一关联方
     */
    deitQulfAsueSrlp?: number
    /**
     * 扣减项-其他_单一关联方
     */
    deitOthSrlp?: number
    /**
     * 信用风险敞口_单一关联方
     */
    credExpsSrlp?: number
    /**
     * 所在集团名称_关联方所在集团
     */
    grlpName?: string
    /**
     * 表内业务-关联方租赁余额_关联方所在集团
     */
    onblRelpLeasBalGrlp?: number
    /**
     * 表内业务-占净资产比例_关联方所在集团
     */
    onblOnarGrlp?: number
    /**
     * 表外业务-担保_关联方所在集团
     */
    ofblGuarGrlp?: number
    /**
     * 表外业务-其他_关联方所在集团
     */
    ofblOthGrlp?: number
    /**
     * 扣减项-合格质物_关联方所在集团
     */
    deitQulfSbimGrlp?: number
    /**
     * 扣减项-合格保证_关联方所在集团
     */
    deitQulfAsueGrlp?: number
    /**
     * 扣减项-其他_关联方所在集团
     */
    deitOthGrlp?: number
    /**
     * 信用风险敞口_关联方所在集团
     */
    credExpsGrlp?: number
    /**
     * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-关联方信息汇总表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37315) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/relation`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyRelationResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-基本情况统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37207) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/basicSituation`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifyBasicSituationRequest {
  /**
   * 自增主键
   */
  id?: number
  /**
   * 行号 | 同一批次数据从1开始递增
   */
  rowNum?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 法定代表人
   */
  legr?: string
  /**
   * 成立日期
   */
  setpDate?: string
  /**
   * 批准单位
   */
  aprvUnit?: string
  /**
   * 批准文号
   */
  aprvFileNum?: string
  /**
   * 营运资金(万元)
   */
  operCptl?: number
  /**
   * 国有资本(万元)
   */
  sttoCptl?: number
  /**
   * 实收资本(万元)
   */
  paidCptl?: number
  /**
   * 经济成分
   */
  econClasCode: string
  /**
   * 是否中央企业控股
   */
  ctarCorpHoldFlag?: string
  /**
   * 是否地方国企控股
   */
  lcalSoeHoldFlag?: string
  /**
   * 从业人员
   */
  prtiNum?: number
  /**
   * 注册地址
   */
  regAddr?: string
  /**
   * 实际经营地址
   */
  actlOperAddr?: string
  /**
   * 企业类别(内资/内资试点/外资)
   */
  corpClasCode?: string
  /**
   * 厂商系标志(厂商系/非厂商系)
   */
  mnfrFlag?: string
  /**
   * 上市标志(上市/非上市)
   */
  listFlag?: string
  /**
   * 分支机构数量(家)
   */
  brchInsNum?: number
  /**
   * 省外分支机构数量(家)
   */
  oprvBrchInsNum?: number
  /**
   * 省内分支机构数量(家)
   */
  wprvBrchInsNum?: number
  /**
   * 设立的其他融资租赁子公司数量
   */
  fnlChilCorpNum?: number
  /**
   * 设立的特殊项目公司（spv)
   */
  spclProjCorpSpvVol?: number
  /**
   * 分支机构地址
   */
  brchInsAddr?: string
  /**
   * 经批准的业务范围
   */
  hsapBusiScop?: string
  /**
   * 实际控制人
   */
  actlCtlr?: string
  /**
   * 实际控制人持股比例
   */
  actlCtlrHoldRati?: number
  /**
   * 公司联系人
   */
  corpConp?: string
  /**
   * 联系电话
   */
  contTel?: string
  /**
   * 联系邮箱
   */
  contMail?: string
  /**
   * 公司网址
   */
  corpWeb?: string
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId?: string
}

/**
 * 接口 [金融协会报送数据-修改-基本情况统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37207) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/basicSituation`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifyBasicSituationResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-实体经济服务表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37297) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/entityEconomyService`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyEntityEconomyServiceRequest {
  /**
   * 自增主键
   */
  id?: number
  /**
   * 行号 | 同一批次数据从1开始递增
   */
  rowNum?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 本年累计租赁业务金额_期初数(万元)
   */
  tyagLeasBusiAmtAbop?: number
  /**
   * 本年累计租赁业务金额_本期发生额(万元)
   */
  tyagLeasBusiAmtAotc?: number
  /**
   * 本年累计租赁业务金额_期末数(万元)
   */
  tyagLeasBusiAmtAeop?: number
  /**
   * 制造业租赁_期初数(万元)
   */
  tyagMnftLeasAmtAbop?: number
  /**
   * 制造业租赁_发生额(万元)
   */
  tyagMnftLeasAmtAotc?: number
  /**
   * 制造业租赁_期末数(万元)
   */
  tyagMnftLeasAmtAeop?: number
  /**
   * 产业链租赁_期初数(万元)
   */
  tyagIndtLeasAmtAbop?: number
  /**
   * 产业链租赁_发生额(万元)
   */
  tyagIndtLeasAmtAotc?: number
  /**
   * 产业链租赁_期末数(万元)
   */
  tyagIndtLeasAmtAeop?: number
  /**
   * 民生消费租赁_期初数(万元)
   */
  tyagConsLeasAmtAbop?: number
  /**
   * 民生消费租赁_发生额(万元)
   */
  tyagConsLeasAmtAotc?: number
  /**
   * 民生消费租赁_期末数(万元)
   */
  tyagConsLeasAmtAeop?: number
  /**
   * 科技金融租赁_期初数(万元)
   */
  tyagSatyLeasAmtAbop?: number
  /**
   * 科技金融租赁_发生额(万元)
   */
  tyagSatyLeasAmtAotc?: number
  /**
   * 科技金融租赁_期末数(万元)
   */
  tyagSatyLeasAmtAeop?: number
  /**
   * 绿色金融租赁_期初数(万元)
   */
  tyagGrenLeasAmtAbop?: number
  /**
   * 绿色金融租赁_发生额(万元)
   */
  tyagGrenLeasAmtAotc?: number
  /**
   * 绿色金融租赁_期末数(万元)
   */
  tyagGrenLeasAmtAeop?: number
  /**
   * 普惠金融租赁_期初数(万元)
   */
  tyagIcveLeasAmtAbop?: number
  /**
   * 普惠金融租赁_发生额(万元)
   */
  tyagIcveLeasAmtAotc?: number
  /**
   * 普惠金融租赁_期末数(万元)
   */
  tyagIcveLeasAmtAeop?: number
  /**
   * 养老金融租赁_期初数(万元)
   */
  tyagPensLeasAmtAbop?: number
  /**
   * 养老金融租赁_发生额(万元)
   */
  tyagPensLeasAmtAotc?: number
  /**
   * 养老金融租赁_期末数(万元)
   */
  tyagPensLeasAmtAeop?: number
  /**
   * 海洋金融租赁_期初数(万元)
   */
  tyagOceaLeasAmtAbop?: number
  /**
   * 海洋金融租赁_发生额(万元)
   */
  tyagOceaLeasAmtAotc?: number
  /**
   * 海洋金融租赁_期末数(万元)
   */
  tyagOceaLeasAmtAeop?: number
  /**
   * 开放金融租赁_期初数(万元)
   */
  tyagOpenLeasAmtAbop?: number
  /**
   * 开放金融租赁_发生额(万元)
   */
  tyagOpenLeasAmtAotc?: number
  /**
   * 开放金融租赁_期末数(万元)
   */
  tyagOpenLeasAmtAeop?: number
  /**
   * 服务客户数_期初数
   */
  tyagServCustNumAbop?: number
  /**
   * 服务客户数_发生额
   */
  tyagServCustNumAotc?: number
  /**
   * 服务客户数_期末数
   */
  tyagServCustNumAeop?: number
  /**
   * 历年租赁金额_期初(万元)
   */
  oyagLeasBusiAmtAbop?: number
  /**
   * 历年租赁金额_发生(万元)
   */
  oyagLeasBusiAmtAotc?: number
  /**
   * 历年租赁金额_期末(万元)
   */
  oyagLeasBusiAmtAeop?: number
  /**
   * 历年客户数_期初
   */
  oyagServCustNumAbop?: number
  /**
   * 历年客户数_发生
   */
  oyagServCustNumAotc?: number
  /**
   * 历年客户数_期末
   */
  oyagServCustNumAeop?: number
  /**
   * 实缴税金_期初(万元)
   */
  thsyTaxpAmtAbop?: number
  /**
   * 实缴税金_发生(万元)
   */
  thsyTaxpAmtAotc?: number
  /**
   * 实缴税金_期末(万元)
   */
  thsyTaxpAmtAeop?: number
  /**
   * 增值税_期初(万元)
   */
  incrTaxAbop?: number
  /**
   * 增值税_发生(万元)
   */
  incrTaxAotc?: number
  /**
   * 增值税_期末(万元)
   */
  incrTaxAeop?: number
  /**
   * 企业所得税_期初(万元)
   */
  corpInctAbop?: number
  /**
   * 企业所得税_发生(万元)
   */
  corpInctAotc?: number
  /**
   * 企业所得税_期末(万元)
   */
  corpInctAeop?: number
  /**
   * 其他税金_期初(万元)
   */
  othTaxAbop?: number
  /**
   * 其他税金_发生(万元)
   */
  othTaxAotc?: number
  /**
   * 其他税金_期末(万元)
   */
  othTaxAeop?: number
  /**
   * 历年税金_期初(万元)
   */
  otyTaxpAmtAbop?: number
  /**
   * 历年税金_发生(万元)
   */
  otyTaxpAmtAotc?: number
  /**
   * 历年税金_期末(万元)
   */
  otyTaxpAmtAeop?: number
  /**
   * 表外业务_期初(万元)
   */
  ofblAmtAbop?: number
  /**
   * 表外业务_发生(万元)
   */
  ofblAmtAotc?: number
  /**
   * 表外业务_期末(万元)
   */
  ofblAmtAeop?: number
  /**
   * 报表实例id | uuid
   */
  reportInstanceId?: string
}

/**
 * 接口 [金融协会报送数据-修改-实体经济服务表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37297) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/entityEconomyService`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyEntityEconomyServiceResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-对外融资清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/37303) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/externalFinancing`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyExternalFinancingRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 自增主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据，行号1开始进行递增（确认数据问题快速定位）
     */
    rowNum?: number
    /**
     * 企业统一社会信用代码
     */
    unifSociCredCode?: string
    /**
     * 序号
     */
    onum?: string
    /**
     * 借款余额 | 单位：万元
     */
    loanBal?: number
    /**
     * 融资业务类型 | 数据字典：evt00052
     */
    finBusiTypeCode?: string
    /**
     * 资金提供方
     */
    cptlProv?: string
    /**
     * 融资利率
     */
    finIntr?: number
    /**
     * 融资借款日期
     */
    finLoanDate?: string
    /**
     * 融资到期日期
     */
    finMatuDate?: string
    /**
     * 报表实例编号 | 格式：uuid
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-对外融资清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/37303) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/externalFinancing`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyExternalFinancingResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-最大10家客户（含集团）集中度统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37321) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/top10ClientConcentration`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyTop10ClientConcentrationRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 序号
     */
    onum?: string
    /**
     * 客户姓名
     */
    custName?: string
    /**
     * 表内业务-前十大客户租赁余额
     */
    onblToptCustLeasBal?: number
    /**
     * 表内业务-占净资产比例
     */
    onblOnar?: number
    /**
     * 表外业务-担保
     */
    ofblGuar?: number
    /**
     * 表外业务-其他
     */
    ofblOth?: number
    /**
     * 扣减项-合格质物
     */
    deitQulfSbim?: number
    /**
     * 扣减项-合格保证
     */
    deitQulfAsue?: number
    /**
     * 扣减项-其他
     */
    deitOth?: number
    /**
     * 信用风险敞口
     */
    credExps?: number
    /**
     * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-最大10家客户（含集团）集中度统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37321) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/top10ClientConcentration`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyTop10ClientConcentrationResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-涉法涉讼涉访信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37255) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/lawInvolvedVisitRelatedInfo`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyLawInvolvedVisitRelatedInfoRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 自增主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 企业统一社会信用代码
     */
    unifSociCredCode?: string
    /**
     * 序号
     */
    onum?: string
    /**
     * 信息类别
     */
    caseClasCode?: string
    /**
     * 合同名称
     */
    agmtName?: string
    /**
     * 合同编号
     */
    agmtNo?: string
    /**
     * 涉及金额(元)
     */
    invlAmt?: number
    /**
     * 是否销号
     */
    canbFlag?: string
    /**
     * 报表实例唯一标识 | uuid格式
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-涉法涉讼涉访信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37255) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/lawInvolvedVisitRelatedInfo`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyLawInvolvedVisitRelatedInfoResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-股东变更记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/37231) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/shahChangeInfo`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifyShahChangeInfoRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 自增主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 企业统一社会信用代码
     */
    unifSociCredCode?: string
    /**
     * 序号
     */
    onum?: string
    /**
     * 股东全称
     */
    shahFn?: string
    /**
     * 股东证件号码
     */
    shahCertNum?: string
    /**
     * 股东性质
     */
    shahCharCode?: string
    /**
     * 股东进入方式
     */
    shahGtoMode?: string
    /**
     * 变更前股东出资金额(万元)
     */
    altrBefShahFndrAmt?: number
    /**
     * 变更前持股比例
     */
    altrBefFndrRati?: number
    /**
     * 股权转让标志
     */
    storTranFlag?: string
    /**
     * 增减资金金额(万元)
     */
    iordCptlAmt?: number
    /**
     * 变更后股东出资金额(万元)
     */
    altrShahFndrAmt?: number
    /**
     * 变更后持股比例
     */
    altrHoldRati?: number
    /**
     * 批复文件号
     */
    aprvFileNum?: string
    /**
     * 批复时间
     */
    aprvTime?: string
    /**
     * 报表实例唯一标识 | uuid格式
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-股东变更记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/37231) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/shahChangeInfo`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifyShahChangeInfoResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-股东股权信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37225) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/shahStorInfo`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifyShahStorInfoRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 自增主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 企业统一社会信用代码
     */
    unifSociCredCode?: string
    /**
     * 序号
     */
    onum?: string
    /**
     * 股东全称
     */
    shahFn?: string
    /**
     * 统一社会信用代码/身份证号
     */
    shahCertNum?: string
    /**
     * 股东性质
     */
    shahCharCode?: string
    /**
     * 股东进入方式
     */
    shahGtoMode?: string
    /**
     * 变更前股东出资金额(万元)
     */
    altrBefShahFndrAmt?: number
    /**
     * 变更前出资比例
     */
    altrBefFndrRati?: number
    /**
     * 股权转让标志
     */
    storTranFlag?: string
    /**
     * 增减资金金额(万元)
     */
    iordCptlAmt?: number
    /**
     * 最新出资金额(万元)
     */
    lastFndrAmt?: number
    /**
     * 最新持股比例
     */
    lastHoldRati?: number
    /**
     * 批复文件号
     */
    aprvFileNum?: string
    /**
     * 批复时间
     */
    aprvTime?: string
    /**
     * 报表实例唯一标识 | uuid格式
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-股东股权信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37225) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/shahStorInfo`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifyShahStorInfoResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-资产负债表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37291) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/balanceSheetPartial`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyBalanceSheetPartialRequest {
  /**
   * 自增主键
   */
  id?: number
  /**
   * 行号 | 同一批次数据从1开始递增
   */
  rowNum?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 货币资金_年初数(元)
   */
  crcpAboy?: number
  /**
   * 交易性金融资产_年初数(元)
   */
  trdFinlAstAboy?: number
  /**
   * 衍生金融资产_年初数(元)
   */
  devdFinlAstAboy?: number
  /**
   * 应收票据_年初数(元)
   */
  recvBillAboy?: number
  /**
   * 应收账款_年初数(元)
   */
  recvAmtAboy?: number
  /**
   * 应收款项融资_年初数(元)
   */
  recvAmtFinAboy?: number
  /**
   * 预付账款_年初数(元)
   */
  piaAmtAboy?: number
  /**
   * 其他应收款总计_年初数(元)
   */
  othRecvAmtTotAboy?: number
  /**
   * 应收股利_年初数(元)
   */
  recvDivdAboy?: number
  /**
   * 应收利息_年初数(元)
   */
  recvIntrAboy?: number
  /**
   * 其他应收款_年初数(元)
   */
  othRecvAmtAboy?: number
  /**
   * 存货_年初数(元)
   */
  invAboy?: number
  /**
   * 合同资产_年初数(元)
   */
  agmtAstAboy?: number
  /**
   * 持有待售资产_年初数(元)
   */
  holdSaleAstAboy?: number
  /**
   * 一年内到期的非流动资产_年初数(元)
   */
  ncoyAboy?: number
  /**
   * 其他流动资产_年初数(元)
   */
  othLiqdAstAboy?: number
  /**
   * 流动资产合计_年初数(元)
   */
  liqdAstTotAboy?: number
  /**
   * 债权投资_年初数(元)
   */
  clamIvsmAboy?: number
  /**
   * 其他债权投资_年初数(元)
   */
  othClamIvsmAboy?: number
  /**
   * 长期应收款_年初数(元)
   */
  longRecvAmtAboy?: number
  /**
   * 长期股权投资_年初数(元)
   */
  lsriAboy?: number
  /**
   * 其他权益工具投资_年初数(元)
   */
  othEquiInstIvsmAboy?: number
  /**
   * 其他非流动金融资产_年初数(元)
   */
  othNocrFinlAstAboy?: number
  /**
   * 投资性房地产_年初数(元)
   */
  ivsmEsttAboy?: number
  /**
   * 固定资产_年初数(元)
   */
  fixAstAboy?: number
  /**
   * 在建工程_年初数(元)
   */
  udcsProjAboy?: number
  /**
   * 生产性生物资产_年初数(元)
   */
  prodBiolMatrAboy?: number
  /**
   * 油气资产_年初数(元)
   */
  olgsAstAboy?: number
  /**
   * 使用权资产_年初数(元)
   */
  useAstAboy?: number
  /**
   * 无形资产_年初数(元)
   */
  imtrAstAboy?: number
  /**
   * 开发支出_年初数(元)
   */
  devPayAboy?: number
  /**
   * 商誉_年初数(元)
   */
  gdwlAboy?: number
  /**
   * 长期待摊费用_年初数(元)
   */
  longTbatFeeAboy?: number
  /**
   * 递延所得税资产_年初数(元)
   */
  defrTaxAstAboy?: number
  /**
   * 其他非流动资产_年初数(元)
   */
  othNocrAstAboy?: number
  /**
   * 非流动资产合计_年初数(元)
   */
  nocrAstTotAboy?: number
  /**
   * 资产总计_年初数(元)
   */
  astTotAboy?: number
  /**
   * 短期借款_年初数(元)
   */
  shttLoanAboy?: number
  /**
   * 交易性金融负债_年初数(元)
   */
  trdFinlLiabAboy?: number
  /**
   * 衍生金融负债_年初数(元)
   */
  devdFinlLiabAboy?: number
  /**
   * 应付票据_年初数(元)
   */
  paybBillAboy?: number
  /**
   * 应付账款_年初数(元)
   */
  paybAmtAboy?: number
  /**
   * 预收账款_年初数(元)
   */
  ciadAmtAboy?: number
  /**
   * 合同负债_年初数(元)
   */
  agmtLiabAboy?: number
  /**
   * 应付职工薪酬_年初数(元)
   */
  paybEmpCmpsAboy?: number
  /**
   * 应交税费_年初数(元)
   */
  paycTaxFeeAboy?: number
  /**
   * 其他应付款总计_年初数(元)
   */
  othPaybAmtTotAboy?: number
  /**
   * 应付利息_年初数(元)
   */
  paybIntrAboy?: number
  /**
   * 应付股利_年初数(元)
   */
  paybDivdAboy?: number
  /**
   * 其他应付款_年初数(元)
   */
  othPaybAmtAboy?: number
  /**
   * 持有待售负债_年初数(元)
   */
  holdSaleLiabAboy?: number
  /**
   * 一年内到期的非流动负债_年初数(元)
   */
  oneyNocrLiabAboy?: number
  /**
   * 其他流动负债_年初数(元)
   */
  othLiqdLiabAboy?: number
  /**
   * 流动负债合计_年初数(元)
   */
  liqdLiabTotAboy?: number
  /**
   * 长期借款_年初数(元)
   */
  longLoanAboy?: number
  /**
   * 应付债券_年初数(元)
   */
  paybBondAboy?: number
  /**
   * 长期应付款_年初数(元)
   */
  longPaybAmtAboy?: number
  /**
   * 长期应付职工薪酬_年初数(元)
   */
  longPaybEmpCmpsAboy?: number
  /**
   * 租赁负债_年初数(元)
   */
  leasLiabAboy?: number
  /**
   * 预计负债_年初数(元)
   */
  expeLiabAboy?: number
  /**
   * 递延收益_年初数(元)
   */
  defrPayfAboy?: number
  /**
   * 递延所得税负债_年初数(元)
   */
  defrInctLiabAboy?: number
  /**
   * 其他非流动负债_年初数(元)
   */
  othNocrLiabAboy?: number
  /**
   * 非流动负债合计_年初数(元)
   */
  nocrLiabTotAboy?: number
  /**
   * 负债合计_年初数(元)
   */
  liabTotAboy?: number
  /**
   * 实收资本_年初数(元)
   */
  paidCptlAboy?: number
  /**
   * 其他权益工具_年初数(元)
   */
  othEquiInstAboy?: number
  /**
   * 资本公积_年初数(元)
   */
  cptlRsrvAboy?: number
  /**
   * 其他综合收益_年初数(元)
   */
  othCmphPayfAboy?: number
  /**
   * 专项储备_年初数(元)
   */
  spclRsrvAboy?: number
  /**
   * 盈余公积_年初数(元)
   */
  surpRsrvAboy?: number
  /**
   * 一般风险准备_年初数(元)
   */
  riskPrepAboy?: number
  /**
   * 未分配利润_年初数(元)
   */
  noAssnProfAboy?: number
  /**
   * 归属母公司所有者权益合计_年初数(元)
   */
  attrPrnCponEquiAboy?: number
  /**
   * 少数股东权益_年初数(元)
   */
  mishEquiAboy?: number
  /**
   * 所有者权益合计_年初数(元)
   */
  toeqAboy?: number
  /**
   * 负债及所有者权益合计_年初数(元)
   */
  liabToeqAboy?: number
  /**
   * 货币资金_期末数(元)
   */
  crcpAeop?: number
  /**
   * 交易性金融资产_期末数(元)
   */
  trdFinlAstAeop?: number
  /**
   * 衍生金融资产_期末数(元)
   */
  devdFinlAstAeop?: number
  /**
   * 应收票据_期末数(元)
   */
  recvBillAeop?: number
  /**
   * 应收账款_期末数(元)
   */
  recvAmtAeop?: number
  /**
   * 应收款项融资_期末数(元)
   */
  recvAmtFinAeop?: number
  /**
   * 预付账款_期末数(元)
   */
  piaAmtAeop?: number
  /**
   * 其他应收款总计_期末数(元)
   */
  othRecvAmtTotAeop?: number
  /**
   * 应收股利_期末数(元)
   */
  recvDivdAeop?: number
  /**
   * 应收利息_期末数(元)
   */
  recvIntrAeop?: number
  /**
   * 其他应收款_期末数(元)
   */
  othRecvAmtAeop?: number
  /**
   * 存货_期末数(元)
   */
  invAeop?: number
  /**
   * 合同资产_期末数(元)
   */
  agmtAstAeop?: number
  /**
   * 持有待售资产_期末数(元)
   */
  holdSaleAstAeop?: number
  /**
   * 一年内到期的非流动资产_期末数(元)
   */
  ncoyAeop?: number
  /**
   * 其他流动资产_期末数(元)
   */
  othLiqdAstAeop?: number
  /**
   * 流动资产合计_期末数(元)
   */
  liqdAstTotAeop?: number
  /**
   * 债权投资_期末数(元)
   */
  clamIvsmAeop?: number
  /**
   * 其他债权投资_期末数(元)
   */
  othClamIvsmAeop?: number
  /**
   * 长期应收款_期末数(元)
   */
  longRecvAmtAeop?: number
  /**
   * 长期股权投资_期末数(元)
   */
  lsriAeop?: number
  /**
   * 其他权益工具投资_期末数(元)
   */
  othEquiInstIvsmAeop?: number
  /**
   * 其他非流动金融资产_期末数(元)
   */
  othNocrFinlAstAeop?: number
  /**
   * 投资性房地产_期末数(元)
   */
  ivsmEsttAeop?: number
  /**
   * 固定资产_期末数(元)
   */
  fixAstAeop?: number
  /**
   * 在建工程_期末数(元)
   */
  udcsProjAeop?: number
  /**
   * 生产性生物资产_期末数(元)
   */
  prodBiolMatrAeop?: number
  /**
   * 油气资产_期末数(元)
   */
  olgsAstAeop?: number
  /**
   * 使用权资产_期末数(元)
   */
  useAstAeop?: number
  /**
   * 无形资产_期末数(元)
   */
  imtrAstAeop?: number
  /**
   * 开发支出_期末数(元)
   */
  devPayAeop?: number
  /**
   * 商誉_期末数(元)
   */
  gdwlAeop?: number
  /**
   * 长期待摊费用_期末数(元)
   */
  longTbatFeeAeop?: number
  /**
   * 递延所得税资产_期末数(元)
   */
  defrTaxAstAeop?: number
  /**
   * 其他非流动资产_期末数(元)
   */
  othNocrAstAeop?: number
  /**
   * 非流动资产合计_期末数(元)
   */
  nocrAstTotAeop?: number
  /**
   * 资产总计_期末数(元)
   */
  astTotAeop?: number
  /**
   * 短期借款_期末数(元)
   */
  shttLoanAeop?: number
  /**
   * 交易性金融负债_期末数(元)
   */
  trdFinlLiabAeop?: number
  /**
   * 衍生金融负债_期末数(元)
   */
  devdFinlLiabAeop?: number
  /**
   * 应付票据_期末数(元)
   */
  paybBillAeop?: number
  /**
   * 应付账款_期末数(元)
   */
  paybAmtAeop?: number
  /**
   * 预收账款_期末数(元)
   */
  ciadAmtAeop?: number
  /**
   * 合同负债_期末数(元)
   */
  agmtLiabAeop?: number
  /**
   * 应付职工薪酬_期末数(元)
   */
  paybEmpCmpsAeop?: number
  /**
   * 应交税费_期末数(元)
   */
  paycTaxFeeAeop?: number
  /**
   * 其他应付款总计_期末数(元)
   */
  othPaybAmtTotAeop?: number
  /**
   * 应付利息_期末数(元)
   */
  paybIntrAeop?: number
  /**
   * 应付股利_期末数(元)
   */
  paybDivdAeop?: number
  /**
   * 其他应付款_期末数(元)
   */
  othPaybAmtAeop?: number
  /**
   * 持有待售负债_期末数(元)
   */
  holdSaleLiabAeop?: number
  /**
   * 一年内到期的非流动负债_期末数(元)
   */
  oneyNocrLiabAeop?: number
  /**
   * 其他流动负债_期末数(元)
   */
  othLiqdLiabAeop?: number
  /**
   * 流动负债合计_期末数(元)
   */
  liqdLiabTotAeop?: number
  /**
   * 长期借款_期末数(元)
   */
  longLoanAeop?: number
  /**
   * 应付债券_期末数(元)
   */
  paybBondAeop?: number
  /**
   * 长期应付款_期末数(元)
   */
  longPaybAmtAeop?: number
  /**
   * 长期应付职工薪酬_期末数(元)
   */
  longPaybEmpCmpsAeop?: number
  /**
   * 租赁负债_期末数(元)
   */
  leasLiabAeop?: number
  /**
   * 预计负债_期末数(元)
   */
  expeLiabAeop?: number
  /**
   * 递延收益_期末数(元)
   */
  defrPayfAeop?: number
  /**
   * 递延所得税负债_期末数(元)
   */
  defrInctLiabAeop?: number
  /**
   * 其他非流动负债_期末数(元)
   */
  othNocrLiabAeop?: number
  /**
   * 非流动负债合计_期末数(元)
   */
  nocrLiabTotAeop?: number
  /**
   * 负债合计_期末数(元)
   */
  liabTotAeop?: number
  /**
   * 实收资本_期末数(元)
   */
  paidCptlAeop?: number
  /**
   * 其他权益工具_期末数(元)
   */
  othEquiInstAeop?: number
  /**
   * 资本公积_期末数(元)
   */
  cptlRsrvAeop?: number
  /**
   * 其他综合收益_期末数(元)
   */
  othCmphPayfAeop?: number
  /**
   * 专项储备_期末数(元)
   */
  spclRsrvAeop?: number
  /**
   * 盈余公积_期末数(元)
   */
  surpRsrvAeop?: number
  /**
   * 一般风险准备_期末数(元)
   */
  riskPrepAeop?: number
  /**
   * 未分配利润_期末数(元)
   */
  noAssnProfAeop?: number
  /**
   * 归属母公司所有者权益合计_期末数(元)
   */
  attrPrnCponEquiAeop?: number
  /**
   * 少数股东权益_期末数(元)
   */
  mishEquiAeop?: number
  /**
   * 所有者权益合计_期末数(元)
   */
  toeqAeop?: number
  /**
   * 负债及所有者权益合计_期末数(元)
   */
  liabToeqAeop?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
}

/**
 * 接口 [金融协会报送数据-修改-资产负债表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37291) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/balanceSheetPartial`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyBalanceSheetPartialResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-重大事项报告情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/37333) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/majorMattersEventReport`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyMajorMattersEventReportRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 自增主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 企业统一社会信用代码
     */
    unifSociCredCode?: string
    /**
     * 事项名称
     */
    piecName?: string
    /**
     * 重大事项说明
     */
    imprPiecExpl?: string
    /**
     * 报表实例唯一标识 | uuid格式
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-重大事项报告情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/37333) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/majorMattersEventReport`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyMajorMattersEventReportResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-重大事项报告表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37261) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/majorMattersBasicReport`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyMajorMattersBasicReportRequest {
  /**
   * 自增主键
   */
  id?: number
  /**
   * 行号 | 同一批次数据从1开始递增
   */
  rowNum?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 填报人联系方式
   */
  inftContMode?: string
  /**
   * 企业名称
   */
  corpName?: string
  /**
   * 法定资本(万元)
   */
  leglCptl?: number
  /**
   * 营业地址
   */
  busiAddr?: string
  /**
   * 公司法人名称
   */
  corpLegpName?: string
  /**
   * 分支机构数量
   */
  brchInsNum?: number
  /**
   * 董事长姓名
   */
  chrmName?: string
  /**
   * 总经理姓名
   */
  gmgrName?: string
  /**
   * 联系方式
   */
  contMode?: string
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId?: string
}

/**
 * 接口 [金融协会报送数据-修改-重大事项报告表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37261) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/majorMattersBasicReport`
 * @更新时间 `2025-09-14 16:34:46`
 */
export interface ModifyMajorMattersBasicReportResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-修改-高管信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37243) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/seniorExecutiveInfo`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifySeniorExecutiveInfoRequest {
  /**
   * 报表实例唯一标识 | uuid格式
   */
  reportInstanceId: string
  dataList?: {
    /**
     * 自增主键
     */
    id?: number
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    rowNum?: number
    /**
     * 企业统一社会信用代码
     */
    unifSociCredCode?: string
    /**
     * 序号
     */
    onum?: string
    /**
     * 姓名
     */
    name?: string
    /**
     * 证件号码
     */
    certNum?: string
    /**
     * 现任职务
     */
    currDutyCode?: string
    /**
     * 任职时间
     */
    aoffTime?: string
    /**
     * 批复文号
     */
    aprvFileNum?: string
    /**
     * 最高学历
     */
    highEduCode?: string
    /**
     * 毕业院校
     */
    gradScho?: string
    /**
     * 就读专业
     */
    spjt?: string
    /**
     * 从事金融/经济工作时间
     */
    haveFinlTime?: string
    /**
     * 联系电话
     */
    contTel?: string
    /**
     * 报表实例唯一标识 | uuid格式
     */
    reportInstanceId?: string
  }[]
}

/**
 * 接口 [金融协会报送数据-修改-高管信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37243) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/modify/seniorExecutiveInfo`
 * @更新时间 `2025-09-14 16:34:45`
 */
export interface ModifySeniorExecutiveInfoResponse {
  modifySuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32533) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/pageList`
 * @更新时间 `2025-09-26 10:05:29`
 */
export interface ReportPageListRequest {
  /**
   * 报表名称
   */
  reportCategoryName?: string
  /**
   * 报表实例周期类型
   */
  reportPeriodCategoryList?: string[]
  /**
   * 报送状态
   */
  reportStatusList?: string[]
  /**
   * 报表类型code
   */
  reportCategoryCodeList?: string[]
  /**
   * 流程状态
   */
  processStatusList?: string[]
  /**
   * 报表实例年份
   */
  reportYear?: number
  /**
   * 报表实例周期类型
   */
  reportPeriodCategory?: string
  /**
   * 报表实例周期
   */
  reportPeriod?: number
  /**
   * 来源页面 query:普遍查询列表页面 apply:上报申请列表页面
   */
  refereePage?: string
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
 * 接口 [金融协会报送数据-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32533) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/pageList`
 * @更新时间 `2025-09-26 10:05:29`
 */
export interface ReportPageListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 报表编码
     */
    reportCategoryCode?: string
    /**
     * 报表名称
     */
    reportCategoryName?: string
    /**
     * 是否为重报数据，0-否，1-是
     */
    isRetry?: number
    /**
     * 报表实例编号
     */
    reportInstanceId?: string
    /**
     * 报表实例年份
     */
    reportYear?: number
    /**
     * 报表实例周期类型
     */
    reportPeriodCategory?: string
    /**
     * 报表实例周期
     */
    reportPeriod?: number
    /**
     * 数据来源（创建方式）
     */
    dataSource?: string
    /**
     * 批次号
     */
    batchNo?: string
    /**
     * 上报时间
     */
    reportTime?: string
    /**
     * 上报状态
     */
    reportStatus?: string
    /**
     * 流程状态
     */
    processStatus?: string
    /**
     * 创建人
     */
    createBy?: number
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
  others?: {}
}

/**
 * 接口 [金融协会报送数据-创建主表记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/32545) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/create`
 * @更新时间 `2025-09-26 10:05:29`
 */
export interface ReportCreateRequest {
  /**
   * 报表类型code
   */
  reportCategoryCode: string
  /**
   * 年份
   */
  year: number
  /**
   * 周期类型code
   */
  periodCategory: string
  /**
   * 周期
   */
  period: number
  /**
   * 初始数据来源
   */
  dataSource?: string
  /**
   * 是否展示在列表
   */
  isShow?: number
}

/**
 * 接口 [金融协会报送数据-创建主表记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/32545) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/create`
 * @更新时间 `2025-09-26 10:05:29`
 */
export type ReportCreateResponse = string

/**
 * 接口 [金融协会报送数据-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/32539) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/delete`
 * @更新时间 `2025-04-22 14:51:23`
 */
export interface ReportDeleteRequest {
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
 * 接口 [金融协会报送数据-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/32539) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/delete`
 * @更新时间 `2025-04-22 14:51:23`
 */
export interface ReportDeleteResponse {
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
 * 接口 [金融协会报送数据-审批列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37363) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/flow/applyList`
 * @更新时间 `2025-09-19 14:44:56`
 */
export interface FlowApplyListRequest {
  /**
   * 主键id,对应工作流中的businessKey的值
   */
  id: number
  /**
   * 报表类型code
   */
  reportCategoryCodeList?: string[]
  /**
   * 报表实例周期类型
   */
  reportPeriodCategoryList?: string[]
  reportInstanceIdList?: string[]
}

/**
 * 接口 [金融协会报送数据-审批列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37363) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/flow/applyList`
 * @更新时间 `2025-09-19 14:44:56`
 */
export type FlowApplyListResponse = {
  /**
   * 主键id
   */
  id?: number
  /**
   * 报表编码
   */
  reportCategoryCode?: string
  /**
   * 报表名称
   */
  reportCategoryName?: string
  /**
   * 是否为重报数据，0-否，1-是
   */
  isRetry?: number
  /**
   * 报表实例编号
   */
  reportInstanceId?: string
  /**
   * 报表实例年份
   */
  reportYear?: number
  /**
   * 报表实例周期类型
   */
  reportPeriodCategory?: string
  /**
   * 报表实例周期
   */
  reportPeriod?: number
  /**
   * 数据来源（创建方式）
   */
  dataSource?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 上报状态
   */
  reportStatus?: string
  /**
   * 流程状态
   */
  processStatus?: string
  /**
   * 创建人
   */
  createBy?: number
  /**
   * 创建时间
   */
  createTime?: string
}[]

/**
 * 接口 [金融协会报送数据-导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/32527) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/import`
 * @更新时间 `2025-04-24 19:00:44`
 */
export interface ReportImportRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 导入文件
   */
  file?: File
}

/**
 * 接口 [金融协会报送数据-导入↗](http://yapi.zswltec.com:3000/project/11/interface/api/32527) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/import`
 * @更新时间 `2025-04-24 19:00:44`
 */
export interface ReportImportResponse {
  importSuccess?: boolean
  errorMessageList?: string[]
}

/**
 * 接口 [金融协会报送数据-模板下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/32599) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/template/fileUrl`
 * @更新时间 `2025-04-22 19:47:10`
 */
export interface TemplateFileUrlRequest {
  /**
   * 报表类型code
   */
  reportCategoryCode: string
}

/**
 * 接口 [金融协会报送数据-模板下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/32599) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/template/fileUrl`
 * @更新时间 `2025-04-22 19:47:10`
 */
export type TemplateFileUrlResponse = string

/**
 * 接口 [金融协会报送数据-详情-业务情况表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32557) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/businessSituation`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailBusinessSituationRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-业务情况表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32557) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/businessSituation`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailBusinessSituationResponse {
  /**
   * id
   */
  id?: number
  /**
   * 总收入_期初数（万元）
   */
  totIncmAbop?: number
  /**
   * 总收入_本期发生额（万元）
   */
  totIncmAotc?: number
  /**
   * 总收入_期末数（万元）
   */
  totIncmAeop?: number
  /**
   * 经营租赁业务收入_期初数（万元）
   */
  operLeasBusiIncmAbop?: number
  /**
   * 经营租赁业务收入_本期发生额（万元）
   */
  operLeasBusiIncmAotc?: number
  /**
   * 经营租赁业务收入_期末数（万元）
   */
  operLeasBusiIncmAeop?: number
  /**
   * 融资租赁业务收入_期初数（万元）
   */
  fnlBusiIncmAbop?: number
  /**
   * 融资租赁业务收入_本期发生额（万元）
   */
  fnlBusiIncmAotc?: number
  /**
   * 融资租赁业务收入_期末数（万元）
   */
  fnlBusiIncmAeop?: number
  /**
   * 利息收入_期初数（万元）
   */
  intrIncmAbop?: number
  /**
   * 利息收入_本期发生额（万元）
   */
  intrIncmAotc?: number
  /**
   * 利息收入_期末数（万元）
   */
  intrIncmAeop?: number
  /**
   * 费用收入_期初数（万元）
   */
  feeIncmAbop?: number
  /**
   * 费用收入_本期发生额（万元）
   */
  feeIncmAotc?: number
  /**
   * 费用收入_期末数（万元）
   */
  feeIncmAeop?: number
  /**
   * 其他收入_期初数（万元）
   */
  othIncmAbop?: number
  /**
   * 其他收入_本期发生额（万元）
   */
  othIncmAotc?: number
  /**
   * 其他收入_期末数（万元）
   */
  othIncmAeop?: number
  /**
   * 租赁资产_期初数（万元）
   */
  leasAstAbop?: number
  /**
   * 租赁资产_本期发生额（万元）
   */
  leasAstAotc?: number
  /**
   * 租赁资产_期末数（万元）
   */
  leasAstAeop?: number
  /**
   * 经营性租赁资产_期初数（万元）
   */
  operLeasAstAbop?: number
  /**
   * 经营性租赁资产_本期发生额（万元）
   */
  operLeasAstAotc?: number
  /**
   * 经营性租赁资产_期末数（万元）
   */
  operLeasAstAeop?: number
  /**
   * 融资租赁资产_期初数（万元）
   */
  finLeasAstAbop?: number
  /**
   * 融资租赁资产_本期发生额（万元）
   */
  finLeasAstAotc?: number
  /**
   * 融资租赁资产_期末数（万元）
   */
  finLeasAstAeop?: number
  /**
   * 直接租赁资产_期初数（万元）
   */
  dirtLeasAstAbop?: number
  /**
   * 直接租赁资产_本期发生额（万元）
   */
  dirtLeasAstAotc?: number
  /**
   * 直接租赁资产_期末数（万元）
   */
  dirtLeasAstAeop?: number
  /**
   * 售后回租资产_期初数（万元）
   */
  slbkAstAbop?: number
  /**
   * 售后回租资产_本期发生额（万元）
   */
  slbkAstAotc?: number
  /**
   * 售后回租资产_期末数（万元）
   */
  slbkAstAeop?: number
  /**
   * 跨省融资租赁资产余额_期初数（万元）
   */
  iprvFnlAstBalAbop?: number
  /**
   * 跨省融资租赁资产余额_本期发生额（万元）
   */
  iprvFnlAstBalAotc?: number
  /**
   * 跨省融资租赁资产余额_期末数（万元）
   */
  iprvFnlAstBalAeop?: number
  /**
   * 跨省售后回租资产余额_期初数（万元）
   */
  iprvSlbkAstBalAbop?: number
  /**
   * 跨省售后回租资产余额_本期发生额（万元）
   */
  iprvSlbkAstBalAotc?: number
  /**
   * 跨省售后回租资产余额_期末数（万元）
   */
  iprvSlbkAstBalAeop?: number
  /**
   * 融资租赁投放额_期初数（万元）
   */
  fnlRelsAbop?: number
  /**
   * 融资租赁投放额_本期发生额（万元）
   */
  fnlRelsAotc?: number
  /**
   * 融资租赁投放额_期末数（万元）
   */
  fnlRelsAeop?: number
  /**
   * 直接租赁投放额_期初数（万元）
   */
  dirtLeasRelsAbop?: number
  /**
   * 直接租赁投放额_本期发生额（万元）
   */
  dirtLeasRelsAotc?: number
  /**
   * 直接租赁投放额_期末数（万元）
   */
  dirtLeasRelsAeop?: number
  /**
   * 售后回租投放额_期初数（万元）
   */
  slbkRelsAbop?: number
  /**
   * 售后回租投放额_本期发生额（万元）
   */
  slbkRelsAotc?: number
  /**
   * 售后回租投放额_期末数（万元）
   */
  slbkRelsAeop?: number
  /**
   * 固定收益类证券投资余额_期初数（万元）
   */
  fixPayfScrIvsmAbop?: number
  /**
   * 固定收益类证券投资余额_本期发生额（万元）
   */
  fixPayfScrIvsmAotc?: number
  /**
   * 固定收益类证券投资_期末数（万元）
   */
  fixPayfScrIvsmAeop?: number
  /**
   * 国债余额_期初数（万元）
   */
  treaAbop?: number
  /**
   * 国债余额_本期发生额（万元）
   */
  treaAotc?: number
  /**
   * 国债余额_期末数（万元）
   */
  treaAeop?: number
  /**
   * 资产减值损失准备_期初数（万元）
   */
  ipoaLossAbop?: number
  /**
   * 资产减值损失准备_本期发生额（万元）
   */
  ipoaLossAotc?: number
  /**
   * 资产减值损失准备_期末数（万元）
   */
  ipoaLossAeop?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}

/**
 * 接口 [金融协会报送数据-详情-主要业务清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/32581) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/mainBusiness`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailMainBusinessRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-主要业务清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/32581) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/mainBusiness`
 * @更新时间 `2025-09-17 08:37:09`
 */
export type DetailMainBusinessResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 合同名称
   */
  agmtName?: string
  /**
   * 合同编号
   */
  agmtNo?: string
  /**
   * 合同签订日期
   */
  agmtSignDate?: string
  /**
   * 协议到期日期
   */
  agmtMatuDate?: string
  /**
   * 合同类型code
   */
  agmtTypeCode?: string
  /**
   * 合同类型display
   */
  agmtTypeDisplay?: string
  /**
   * 租赁物类型
   */
  lasdType?: string
  /**
   * 项目行业分类code
   */
  projIndtClasCode?: string
  /**
   * 项目行业分类display
   */
  projIndtClasDisplay?: string
  /**
   * 客户姓名
   */
  custName?: string
  /**
   * 客户证件号码
   */
  custCertNum?: string
  /**
   * 客户规模code
   */
  custScalCode?: string
  /**
   * 客户规模display
   */
  custScalDisplay?: string
  /**
   * 融资租赁投放额
   */
  fnlRels?: number
  /**
   * 收回本金
   */
  wthdPrin?: number
  /**
   * 租金余额
   */
  rentBal?: number
  /**
   * 综合融资成本
   */
  cmphFinCost?: number
  /**
   * 增信情况code
   */
  udpnSituCode?: string
  /**
   * 增信情况display
   */
  udpnSituDisplay?: string
  /**
   * 增信方
   */
  udpn?: string
  /**
   * 逾期租金
   */
  ovduRent?: number
  /**
   * 逾期天数code
   */
  ovduDaysCode?: string
  /**
   * 逾期天数display
   */
  ovduDaysDisplay?: string
  /**
   * 逾期处置情况
   */
  ovduDspsProg?: string
  /**
   * 是否纳入不良
   */
  npFlag?: string
  /**
   * 是否纳入不良display
   */
  npFlagDisplay?: string
  /**
   * 不良余额
   */
  npBal?: number
  /**
   * 客户数量
   */
  custVol?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-公司利润表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32551) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/companyProfit`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailCompanyProfitRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-公司利润表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32551) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/companyProfit`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailCompanyProfitResponse {
  /**
   * id
   */
  id?: number
  /**
   * 主营业务收入_本季发生额(元)
   */
  mainBusiIncmActm?: number
  /**
   * 主营业务收入_本年累计(元)
   */
  mainBusiIncmTyag?: number
  /**
   * 主营业务收入_去年同期(元)
   */
  mainBusiIncmCply?: number
  /**
   * 主营业务成本_本季发生额(元)
   */
  mainBusiCostActm?: number
  /**
   * 主营业务成本_本年累计(元)
   */
  mainBusiCostTyag?: number
  /**
   * 主营业务成本_去年同期(元)
   */
  mainBusiCostCply?: number
  /**
   * 主营业务税金及附加_本季发生额(元)
   */
  mainBusiTaxAddActm?: number
  /**
   * 主营业务税金及附加_本年累计(元)
   */
  mainBusiTaxAddTyag?: number
  /**
   * 主营业务税金及附加_去年同期(元)
   */
  mainBusiTaxAddCply?: number
  /**
   * 主营业务利润_本季发生额(元)
   */
  mainBusiProfActm?: number
  /**
   * 主营业务利润_本年累计(元)
   */
  mainBusiProfTyag?: number
  /**
   * 主营业务利润_去年同期(元)
   */
  mainBusiProfCply?: number
  /**
   * 其他业务利润_本季发生额(元)
   */
  othBusiProfActm?: number
  /**
   * 其他业务利润_本年累计(元)
   */
  othBusiProfTyag?: number
  /**
   * 其他业务利润_去年同期(元)
   */
  othBusiProfCply?: number
  /**
   * 营业费用_本季发生额(元)
   */
  busiFeeActm?: number
  /**
   * 营业费用_本年累计(元)
   */
  busiFeeTyag?: number
  /**
   * 营业费用_去年同期(元)
   */
  busiFeeCply?: number
  /**
   * 管理费用_本季发生额(元)
   */
  magFeeActm?: number
  /**
   * 管理费用_本年累计(元)
   */
  magFeeTyag?: number
  /**
   * 管理费用_去年同期(元)
   */
  magFeeCply?: number
  /**
   * 财务费用_本季发生额(元)
   */
  finFeeActm?: number
  /**
   * 财务费用_本年累计(元)
   */
  finFeeTyag?: number
  /**
   * 财务费用_去年同期(元)
   */
  finFeeCply?: number
  /**
   * 资产减值损失_本季发生额(元)
   */
  ipoaLossActm?: number
  /**
   * 资产减值损失_本年累计(元)
   */
  ipoaLossTyag?: number
  /**
   * 资产减值损失_去年同期(元)
   */
  ipoaLossCply?: number
  /**
   * 信用减值损失_本季发生额(元)
   */
  credDecrLossActm?: number
  /**
   * 信用减值损失_本年累计(元)
   */
  credDecrLossTyag?: number
  /**
   * 信用减值损失_去年同期(元)
   */
  credDecrLossCply?: number
  /**
   * 营业利润_本季发生额(元)
   */
  busiProfActm?: number
  /**
   * 营业利润_本年累计(元)
   */
  busiProfTyag?: number
  /**
   * 营业利润_去年同期(元)
   */
  busiProfCply?: number
  /**
   * 投资收益_本季发生额(元)
   */
  ivsmPayfActm?: number
  /**
   * 投资收益_本年累计(元)
   */
  ivsmPayfTyag?: number
  /**
   * 投资收益_去年同期(元)
   */
  ivsmPayfCply?: number
  /**
   * 营业外收入_本季发生额(元)
   */
  noprIncmActm?: number
  /**
   * 营业外收入_本年累计(元)
   */
  noprIncmTyag?: number
  /**
   * 营业外收入_去年同期(元)
   */
  noprIncmCply?: number
  /**
   * 营业外支出_本季发生额(元)
   */
  noprPayActm?: number
  /**
   * 营业外支出_本年累计(元)
   */
  noprPayTyag?: number
  /**
   * 营业外支出_去年同期(元)
   */
  noprPayCply?: number
  /**
   * 利润总额_本季发生额(元)
   */
  profGamtActm?: number
  /**
   * 利润总额_本年累计(元)
   */
  profGamtTyag?: number
  /**
   * 利润总额_去年同期(元)
   */
  profGamtCply?: number
  /**
   * 所得税费用_本季发生额(元)
   */
  inctFeeActm?: number
  /**
   * 所得税费用_本年累计(元)
   */
  inctFeeTyag?: number
  /**
   * 所得税费用_去年同期(元)
   */
  inctFeeCply?: number
  /**
   * 净利润_本季发生额(元)
   */
  netProfActm?: number
  /**
   * 净利润_本年累计(元)
   */
  netProfTyag?: number
  /**
   * 净利润_去年同期(元)
   */
  netProfCply?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}

/**
 * 接口 [金融协会报送数据-详情-关联方信息汇总表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32593) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/relation`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailRelationRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-关联方信息汇总表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32593) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/relation`
 * @更新时间 `2025-09-17 08:37:09`
 */
export type DetailRelationResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 关联方名称
   */
  relpName?: string
  /**
   * 是否为本公司股东关联方
   */
  corpShahRelpFlag?: string
  /**
   * 是否为本公司股东关联方
   */
  corpShahRelpFlagDisplay?: string
  /**
   * 本公司股东名称
   */
  corpShahName?: string
  /**
   * 表内业务-关联方租赁余额_单一关联方
   */
  onblRelpLeasBalSrlp?: number
  /**
   * 表内业务-占净资产比例_单一关联方
   */
  onblOnarSrlp?: number
  /**
   * 表外业务-担保_单一关联方
   */
  ofblGuarSrlp?: number
  /**
   * 表外业务-其他_单一关联方
   */
  ofblOthSrlp?: number
  /**
   * 扣减项-合格质物_单一关联方
   */
  deitQulfSbimSrlp?: number
  /**
   * 扣减项-合格保证_单一关联方
   */
  deitQulfAsueSrlp?: number
  /**
   * 扣减项-其他_单一关联方
   */
  deitOthSrlp?: number
  /**
   * 信用风险敞口_单一关联方
   */
  credExpsSrlp?: number
  /**
   * 所在集团名称_关联方所在集团
   */
  grlpName?: string
  /**
   * 表内业务-关联方租赁余额_关联方所在集团
   */
  onblRelpLeasBalGrlp?: number
  /**
   * 表内业务-占净资产比例_关联方所在集团
   */
  onblOnarGrlp?: number
  /**
   * 表外业务-担保_关联方所在集团
   */
  ofblGuarGrlp?: number
  /**
   * 表外业务-其他_关联方所在集团
   */
  ofblOthGrlp?: number
  /**
   * 扣减项-合格质物_关联方所在集团
   */
  deitQulfSbimGrlp?: number
  /**
   * 扣减项-合格保证_关联方所在集团
   */
  deitQulfAsueGrlp?: number
  /**
   * 扣减项-其他_关联方所在集团
   */
  deitOthGrlp?: number
  /**
   * 信用风险敞口_关联方所在集团
   */
  credExpsGrlp?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-基本情况统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37201) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/basicSituation`
 * @更新时间 `2025-09-17 08:37:08`
 */
export interface DetailBasicSituationRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-基本情况统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37201) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/basicSituation`
 * @更新时间 `2025-09-17 08:37:08`
 */
export interface DetailBasicSituationResponse {
  /**
   * id
   */
  id?: number
  /**
   * 企业统一社会信用代码
   */
  unifSociCredCode?: string
  /**
   * 法定代表人
   */
  legr?: string
  /**
   * 成立日期
   */
  setpDate?: string
  /**
   * 批准单位
   */
  aprvUnit?: string
  /**
   * 批准文号
   */
  aprvFileNum?: string
  /**
   * 营运资金(万元)
   */
  operCptl?: number
  /**
   * 国有资本(万元)
   */
  sttoCptl?: number
  /**
   * 实收资本(万元)
   */
  paidCptl?: number
  /**
   * 经济成分code
   */
  econClasCode?: string
  /**
   * 经济成分Display
   */
  econClasDisplay?: string
  /**
   * 是否中央企业控股
   */
  ctarCorpHoldFlag?: string
  /**
   * 是否中央企业控股Display
   */
  ctarCorpHoldFlagDisplay?: string
  /**
   * 是否地方国企控股
   */
  lcalSoeHoldFlag?: string
  /**
   * 是否地方国企控股Display
   */
  lcalSoeHoldFlagDisplay?: string
  /**
   * 从业人员
   */
  prtiNum?: number
  /**
   * 注册地址
   */
  regAddr?: string
  /**
   * 实际经营地址
   */
  actlOperAddr?: string
  /**
   * 企业类别(内资/内资试点/外资)
   */
  corpClasCode?: string
  /**
   * 企业类别Display
   */
  corpClasDisplay?: string
  /**
   * 厂商系标志(厂商系/非厂商系)
   */
  mnfrFlag?: string
  /**
   * 上市标志(上市/非上市)
   */
  listFlag?: string
  /**
   * 分支机构数量(家)
   */
  brchInsNum?: number
  /**
   * 省外分支机构数量(家)
   */
  oprvBrchInsNum?: number
  /**
   * 省内分支机构数量(家)
   */
  wprvBrchInsNum?: number
  /**
   * 设立的其他融资租赁子公司数量
   */
  fnlChilCorpNum?: number
  /**
   * 设立的特殊项目公司（spv)
   */
  spclProjCorpSpvVol?: number
  /**
   * 分支机构地址
   */
  brchInsAddr?: string
  /**
   * 经批准的业务范围
   */
  hsapBusiScop?: string
  /**
   * 实际控制人
   */
  actlCtlr?: string
  /**
   * 实际控制人持股比例
   */
  actlCtlrHoldRati?: number
  /**
   * 公司联系人
   */
  corpConp?: string
  /**
   * 联系电话
   */
  contTel?: string
  /**
   * 联系邮箱
   */
  contMail?: string
  /**
   * 公司网址
   */
  corpWeb?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}

/**
 * 接口 [金融协会报送数据-详情-实体经济服务表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32569) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/entityEconomyService`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailEntityEconomyServiceRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-实体经济服务表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32569) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/entityEconomyService`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailEntityEconomyServiceResponse {
  /**
   * id
   */
  id?: number
  /**
   * 本年累计租赁业务金额_期初数(万元)
   */
  tyagLeasBusiAmtAbop?: number
  /**
   * 本年累计租赁业务金额_本期发生额(万元)
   */
  tyagLeasBusiAmtAotc?: number
  /**
   * 本年累计租赁业务金额_期末数(万元)
   */
  tyagLeasBusiAmtAeop?: number
  /**
   * 制造业租赁_期初数(万元)
   */
  tyagMnftLeasAmtAbop?: number
  /**
   * 制造业租赁_发生额(万元)
   */
  tyagMnftLeasAmtAotc?: number
  /**
   * 制造业租赁_期末数(万元)
   */
  tyagMnftLeasAmtAeop?: number
  /**
   * 产业链租赁_期初数(万元)
   */
  tyagIndtLeasAmtAbop?: number
  /**
   * 产业链租赁_发生额(万元)
   */
  tyagIndtLeasAmtAotc?: number
  /**
   * 产业链租赁_期末数(万元)
   */
  tyagIndtLeasAmtAeop?: number
  /**
   * 民生消费租赁_期初数(万元)
   */
  tyagConsLeasAmtAbop?: number
  /**
   * 民生消费租赁_发生额(万元)
   */
  tyagConsLeasAmtAotc?: number
  /**
   * 民生消费租赁_期末数(万元)
   */
  tyagConsLeasAmtAeop?: number
  /**
   * 科技金融租赁_期初数(万元)
   */
  tyagSatyLeasAmtAbop?: number
  /**
   * 科技金融租赁_发生额(万元)
   */
  tyagSatyLeasAmtAotc?: number
  /**
   * 科技金融租赁_期末数(万元)
   */
  tyagSatyLeasAmtAeop?: number
  /**
   * 绿色金融租赁_期初数(万元)
   */
  tyagGrenLeasAmtAbop?: number
  /**
   * 绿色金融租赁_发生额(万元)
   */
  tyagGrenLeasAmtAotc?: number
  /**
   * 绿色金融租赁_期末数(万元)
   */
  tyagGrenLeasAmtAeop?: number
  /**
   * 普惠金融租赁_期初数(万元)
   */
  tyagIcveLeasAmtAbop?: number
  /**
   * 普惠金融租赁_发生额(万元)
   */
  tyagIcveLeasAmtAotc?: number
  /**
   * 普惠金融租赁_期末数(万元)
   */
  tyagIcveLeasAmtAeop?: number
  /**
   * 养老金融租赁_期初数(万元)
   */
  tyagPensLeasAmtAbop?: number
  /**
   * 养老金融租赁_发生额(万元)
   */
  tyagPensLeasAmtAotc?: number
  /**
   * 养老金融租赁_期末数(万元)
   */
  tyagPensLeasAmtAeop?: number
  /**
   * 海洋金融租赁_期初数(万元)
   */
  tyagOceaLeasAmtAbop?: number
  /**
   * 海洋金融租赁_发生额(万元)
   */
  tyagOceaLeasAmtAotc?: number
  /**
   * 海洋金融租赁_期末数(万元)
   */
  tyagOceaLeasAmtAeop?: number
  /**
   * 开放金融租赁_期初数(万元)
   */
  tyagOpenLeasAmtAbop?: number
  /**
   * 开放金融租赁_发生额(万元)
   */
  tyagOpenLeasAmtAotc?: number
  /**
   * 开放金融租赁_期末数(万元)
   */
  tyagOpenLeasAmtAeop?: number
  /**
   * 服务客户数_期初数
   */
  tyagServCustNumAbop?: number
  /**
   * 服务客户数_发生额
   */
  tyagServCustNumAotc?: number
  /**
   * 服务客户数_期末数
   */
  tyagServCustNumAeop?: number
  /**
   * 历年租赁金额_期初(万元)
   */
  oyagLeasBusiAmtAbop?: number
  /**
   * 历年租赁金额_发生(万元)
   */
  oyagLeasBusiAmtAotc?: number
  /**
   * 历年租赁金额_期末(万元)
   */
  oyagLeasBusiAmtAeop?: number
  /**
   * 历年客户数_期初
   */
  oyagServCustNumAbop?: number
  /**
   * 历年客户数_发生
   */
  oyagServCustNumAotc?: number
  /**
   * 历年客户数_期末
   */
  oyagServCustNumAeop?: number
  /**
   * 实缴税金_期初(万元)
   */
  thsyTaxpAmtAbop?: number
  /**
   * 实缴税金_发生(万元)
   */
  thsyTaxpAmtAotc?: number
  /**
   * 实缴税金_期末(万元)
   */
  thsyTaxpAmtAeop?: number
  /**
   * 增值税_期初(万元)
   */
  incrTaxAbop?: number
  /**
   * 增值税_发生(万元)
   */
  incrTaxAotc?: number
  /**
   * 增值税_期末(万元)
   */
  incrTaxAeop?: number
  /**
   * 企业所得税_期初(万元)
   */
  corpInctAbop?: number
  /**
   * 企业所得税_发生(万元)
   */
  corpInctAotc?: number
  /**
   * 企业所得税_期末(万元)
   */
  corpInctAeop?: number
  /**
   * 其他税金_期初(万元)
   */
  othTaxAbop?: number
  /**
   * 其他税金_发生(万元)
   */
  othTaxAotc?: number
  /**
   * 其他税金_期末(万元)
   */
  othTaxAeop?: number
  /**
   * 历年税金_期初(万元)
   */
  otyTaxpAmtAbop?: number
  /**
   * 历年税金_发生(万元)
   */
  otyTaxpAmtAotc?: number
  /**
   * 历年税金_期末(万元)
   */
  otyTaxpAmtAeop?: number
  /**
   * 表外业务_期初(万元)
   */
  ofblAmtAbop?: number
  /**
   * 表外业务_发生(万元)
   */
  ofblAmtAotc?: number
  /**
   * 表外业务_期末(万元)
   */
  ofblAmtAeop?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}

/**
 * 接口 [金融协会报送数据-详情-对外融资清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/32575) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/externalFinancing`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailExternalFinancingRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-对外融资清单↗](http://yapi.zswltec.com:3000/project/11/interface/api/32575) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/externalFinancing`
 * @更新时间 `2025-09-17 08:37:09`
 */
export type DetailExternalFinancingResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 借款余额 | 单位：万元
   */
  loanBal?: number
  /**
   * 融资业务类型 | 数据字典：evt00052
   */
  finBusiTypeCode?: string
  /**
   * 融资业务类型展示文案
   */
  finBusiTypeCodeDisplay?: string
  /**
   * 资金提供方
   */
  cptlProv?: string
  /**
   * 融资利率
   */
  finIntr?: number
  /**
   * 融资借款日期
   */
  finLoanDate?: string
  /**
   * 融资到期日期
   */
  finMatuDate?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-最大10家客户（含集团）集中度统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32587) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/top10ClientConcentration`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailTop10ClientConcentrationRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-最大10家客户（含集团）集中度统计表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32587) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/top10ClientConcentration`
 * @更新时间 `2025-09-17 08:37:09`
 */
export type DetailTop10ClientConcentrationResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 客户姓名
   */
  custName?: string
  /**
   * 表内业务-前十大客户租赁余额
   */
  onblToptCustLeasBal?: number
  /**
   * 表内业务-占净资产比例
   */
  onblOnar?: number
  /**
   * 表外业务-担保
   */
  ofblGuar?: number
  /**
   * 表外业务-其他
   */
  ofblOth?: number
  /**
   * 扣减项-合格质物
   */
  deitQulfSbim?: number
  /**
   * 扣减项-合格保证
   */
  deitQulfAsue?: number
  /**
   * 扣减项-其他
   */
  deitOth?: number
  /**
   * 信用风险敞口
   */
  credExps?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-涉法涉讼涉访信息表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37237) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/lawInvolvedVisitRelatedInfo`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailLawInvolvedVisitRelatedInfoRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-涉法涉讼涉访信息表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37237) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/lawInvolvedVisitRelatedInfo`
 * @更新时间 `2025-09-17 08:37:09`
 */
export type DetailLawInvolvedVisitRelatedInfoResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 信息类别
   */
  caseClasCode?: string
  /**
   * 信息类别Display
   */
  caseClasDisplay?: string
  /**
   * 合同名称
   */
  agmtName?: string
  /**
   * 合同编号
   */
  agmtNo?: string
  /**
   * 涉及金额(元)
   */
  invlAmt?: number
  /**
   * 是否销号
   */
  canbFlag?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-股东股权信息一览表-股东变更记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/37219) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/shahChangeInfo`
 * @更新时间 `2025-09-17 08:37:08`
 */
export interface DetailShahChangeInfoRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-股东股权信息一览表-股东变更记录↗](http://yapi.zswltec.com:3000/project/11/interface/api/37219) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/shahChangeInfo`
 * @更新时间 `2025-09-17 08:37:08`
 */
export type DetailShahChangeInfoResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 股东全称
   */
  shahFn?: string
  /**
   * 股东证件号码
   */
  shahCertNum?: string
  /**
   * 股东性质
   */
  shahCharCode?: string
  /**
   * 股东性质Display
   */
  shahCharDisplay?: string
  /**
   * 股东进入方式
   */
  shahGtoMode?: string
  /**
   * 变更前股东出资金额(万元)
   */
  altrBefShahFndrAmt?: number
  /**
   * 变更前持股比例
   */
  altrBefFndrRati?: number
  /**
   * 股权转让标志
   */
  storTranFlag?: string
  /**
   * 股权转让标志Display
   */
  storTranFlagDisplay?: string
  /**
   * 增减资金金额(万元)
   */
  iordCptlAmt?: number
  /**
   * 变更后股东出资金额(万元)
   */
  altrShahFndrAmt?: number
  /**
   * 变更后持股比例
   */
  altrHoldRati?: number
  /**
   * 批复文件号
   */
  aprvFileNum?: string
  /**
   * 批复时间
   */
  aprvTime?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-股东股权信息一览表-股东股权信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37213) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/shahStorInfo`
 * @更新时间 `2025-09-17 08:37:08`
 */
export interface DetailShahStorInfoRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-股东股权信息一览表-股东股权信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37213) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/shahStorInfo`
 * @更新时间 `2025-09-17 08:37:08`
 */
export type DetailShahStorInfoResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 股东全称
   */
  shahFn?: string
  /**
   * 统一社会信用代码/身份证号
   */
  shahCertNum?: string
  /**
   * 股东性质
   */
  shahCharCode?: string
  /**
   * 股东性质Display
   */
  shahCharDisplay?: string
  /**
   * 股东进入方式
   */
  shahGtoMode?: string
  /**
   * 变更前股东出资金额(万元)
   */
  altrBefShahFndrAmt?: number
  /**
   * 变更前出资比例
   */
  altrBefFndrRati?: number
  /**
   * 股权转让标志
   */
  storTranFlag?: string
  /**
   * 股权转让标志Display
   */
  storTranFlagDisplay?: string
  /**
   * 增减资金金额(万元)
   */
  iordCptlAmt?: number
  /**
   * 最新出资金额(万元)
   */
  lastFndrAmt?: number
  /**
   * 最新持股比例
   */
  lastHoldRati?: number
  /**
   * 批复文件号
   */
  aprvFileNum?: string
  /**
   * 批复时间
   */
  aprvTime?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-资产负债表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32563) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/balanceSheetPartial`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailBalanceSheetPartialRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-资产负债表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32563) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/balanceSheetPartial`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailBalanceSheetPartialResponse {
  /**
   * id
   */
  id?: number
  /**
   * 货币资金_年初数(元)
   */
  crcpAboy?: number
  /**
   * 交易性金融资产_年初数(元)
   */
  trdFinlAstAboy?: number
  /**
   * 衍生金融资产_年初数(元)
   */
  devdFinlAstAboy?: number
  /**
   * 应收票据_年初数(元)
   */
  recvBillAboy?: number
  /**
   * 应收账款_年初数(元)
   */
  recvAmtAboy?: number
  /**
   * 应收款项融资_年初数(元)
   */
  recvAmtFinAboy?: number
  /**
   * 预付账款_年初数(元)
   */
  piaAmtAboy?: number
  /**
   * 其他应收款总计_年初数(元)
   */
  othRecvAmtTotAboy?: number
  /**
   * 应收股利_年初数(元)
   */
  recvDivdAboy?: number
  /**
   * 应收利息_年初数(元)
   */
  recvIntrAboy?: number
  /**
   * 其他应收款_年初数(元)
   */
  othRecvAmtAboy?: number
  /**
   * 存货_年初数(元)
   */
  invAboy?: number
  /**
   * 合同资产_年初数(元)
   */
  agmtAstAboy?: number
  /**
   * 持有待售资产_年初数(元)
   */
  holdSaleAstAboy?: number
  /**
   * 一年内到期的非流动资产_年初数(元)
   */
  ncoyAboy?: number
  /**
   * 其他流动资产_年初数(元)
   */
  othLiqdAstAboy?: number
  /**
   * 流动资产合计_年初数(元)
   */
  liqdAstTotAboy?: number
  /**
   * 非流动资产_年初数(元)
   */
  NoLiqdAstTotAboy?: number
  /**
   * 债权投资_年初数(元)
   */
  clamIvsmAboy?: number
  /**
   * 其他债权投资_年初数(元)
   */
  othClamIvsmAboy?: number
  /**
   * 长期应收款_年初数(元)
   */
  longRecvAmtAboy?: number
  /**
   * 长期股权投资_年初数(元)
   */
  lsriAboy?: number
  /**
   * 其他权益工具投资_年初数(元)
   */
  othEquiInstIvsmAboy?: number
  /**
   * 其他非流动金融资产_年初数(元)
   */
  othNocrFinlAstAboy?: number
  /**
   * 投资性房地产_年初数(元)
   */
  ivsmEsttAboy?: number
  /**
   * 固定资产_年初数(元)
   */
  fixAstAboy?: number
  /**
   * 在建工程_年初数(元)
   */
  udcsProjAboy?: number
  /**
   * 生产性生物资产_年初数(元)
   */
  prodBiolMatrAboy?: number
  /**
   * 油气资产_年初数(元)
   */
  olgsAstAboy?: number
  /**
   * 使用权资产_年初数(元)
   */
  useAstAboy?: number
  /**
   * 无形资产_年初数(元)
   */
  imtrAstAboy?: number
  /**
   * 开发支出_年初数(元)
   */
  devPayAboy?: number
  /**
   * 商誉_年初数(元)
   */
  gdwlAboy?: number
  /**
   * 长期待摊费用_年初数(元)
   */
  longTbatFeeAboy?: number
  /**
   * 递延所得税资产_年初数(元)
   */
  defrTaxAstAboy?: number
  /**
   * 其他非流动资产_年初数(元)
   */
  othNocrAstAboy?: number
  /**
   * 非流动资产合计_年初数(元)
   */
  nocrAstTotAboy?: number
  /**
   * 资产总计_年初数(元)
   */
  astTotAboy?: number
  /**
   * 短期借款_年初数(元)
   */
  shttLoanAboy?: number
  /**
   * 交易性金融负债_年初数(元)
   */
  trdFinlLiabAboy?: number
  /**
   * 衍生金融负债_年初数(元)
   */
  devdFinlLiabAboy?: number
  /**
   * 应付票据_年初数(元)
   */
  paybBillAboy?: number
  /**
   * 应付账款_年初数(元)
   */
  paybAmtAboy?: number
  /**
   * 预收账款_年初数(元)
   */
  ciadAmtAboy?: number
  /**
   * 合同负债_年初数(元)
   */
  agmtLiabAboy?: number
  /**
   * 应付职工薪酬_年初数(元)
   */
  paybEmpCmpsAboy?: number
  /**
   * 应交税费_年初数(元)
   */
  paycTaxFeeAboy?: number
  /**
   * 其他应付款总计_年初数(元)
   */
  othPaybAmtTotAboy?: number
  /**
   * 应付利息_年初数(元)
   */
  paybIntrAboy?: number
  /**
   * 应付股利_年初数(元)
   */
  paybDivdAboy?: number
  /**
   * 其他应付款_年初数(元)
   */
  othPaybAmtAboy?: number
  /**
   * 持有待售负债_年初数(元)
   */
  holdSaleLiabAboy?: number
  /**
   * 一年内到期的非流动负债_年初数(元)
   */
  oneyNocrLiabAboy?: number
  /**
   * 其他流动负债_年初数(元)
   */
  othLiqdLiabAboy?: number
  /**
   * 流动负债合计_年初数(元)
   */
  liqdLiabTotAboy?: number
  /**
   * 长期借款_年初数(元)
   */
  longLoanAboy?: number
  /**
   * 应付债券_年初数(元)
   */
  paybBondAboy?: number
  /**
   * 长期应付款_年初数(元)
   */
  longPaybAmtAboy?: number
  /**
   * 长期应付职工薪酬_年初数(元)
   */
  longPaybEmpCmpsAboy?: number
  /**
   * 租赁负债_年初数(元)
   */
  leasLiabAboy?: number
  /**
   * 预计负债_年初数(元)
   */
  expeLiabAboy?: number
  /**
   * 递延收益_年初数(元)
   */
  defrPayfAboy?: number
  /**
   * 递延所得税负债_年初数(元)
   */
  defrInctLiabAboy?: number
  /**
   * 其他非流动负债_年初数(元)
   */
  othNocrLiabAboy?: number
  /**
   * 非流动负债合计_年初数(元)
   */
  nocrLiabTotAboy?: number
  /**
   * 负债合计_年初数(元)
   */
  liabTotAboy?: number
  /**
   * 实收资本_年初数(元)
   */
  paidCptlAboy?: number
  /**
   * 其他权益工具_年初数(元)
   */
  othEquiInstAboy?: number
  /**
   * 资本公积_年初数(元)
   */
  cptlRsrvAboy?: number
  /**
   * 其他综合收益_年初数(元)
   */
  othCmphPayfAboy?: number
  /**
   * 专项储备_年初数(元)
   */
  spclRsrvAboy?: number
  /**
   * 盈余公积_年初数(元)
   */
  surpRsrvAboy?: number
  /**
   * 一般风险准备_年初数(元)
   */
  riskPrepAboy?: number
  /**
   * 未分配利润_年初数(元)
   */
  noAssnProfAboy?: number
  /**
   * 归属母公司所有者权益合计_年初数(元)
   */
  attrPrnCponEquiAboy?: number
  /**
   * 少数股东权益_年初数(元)
   */
  mishEquiAboy?: number
  /**
   * 所有者权益合计_年初数(元)
   */
  toeqAboy?: number
  /**
   * 负债及所有者权益合计_年初数(元)
   */
  liabToeqAboy?: number
  /**
   * 货币资金_期末数(元)
   */
  crcpAeop?: number
  /**
   * 交易性金融资产_期末数(元)
   */
  trdFinlAstAeop?: number
  /**
   * 衍生金融资产_期末数(元)
   */
  devdFinlAstAeop?: number
  /**
   * 应收票据_期末数(元)
   */
  recvBillAeop?: number
  /**
   * 应收账款_期末数(元)
   */
  recvAmtAeop?: number
  /**
   * 应收款项融资_期末数(元)
   */
  recvAmtFinAeop?: number
  /**
   * 预付账款_期末数(元)
   */
  piaAmtAeop?: number
  /**
   * 其他应收款总计_期末数(元)
   */
  othRecvAmtTotAeop?: number
  /**
   * 应收股利_期末数(元)
   */
  recvDivdAeop?: number
  /**
   * 应收利息_期末数(元)
   */
  recvIntrAeop?: number
  /**
   * 其他应收款_期末数(元)
   */
  othRecvAmtAeop?: number
  /**
   * 存货_期末数(元)
   */
  invAeop?: number
  /**
   * 合同资产_期末数(元)
   */
  agmtAstAeop?: number
  /**
   * 持有待售资产_期末数(元)
   */
  holdSaleAstAeop?: number
  /**
   * 一年内到期的非流动资产_期末数(元)
   */
  ncoyAeop?: number
  /**
   * 其他流动资产_期末数(元)
   */
  othLiqdAstAeop?: number
  /**
   * 流动资产合计_期末数(元)
   */
  liqdAstTotAeop?: number
  /**
   * 债权投资_期末数(元)
   */
  clamIvsmAeop?: number
  /**
   * 其他债权投资_期末数(元)
   */
  othClamIvsmAeop?: number
  /**
   * 长期应收款_期末数(元)
   */
  longRecvAmtAeop?: number
  /**
   * 长期股权投资_期末数(元)
   */
  lsriAeop?: number
  /**
   * 其他权益工具投资_期末数(元)
   */
  othEquiInstIvsmAeop?: number
  /**
   * 其他非流动金融资产_期末数(元)
   */
  othNocrFinlAstAeop?: number
  /**
   * 投资性房地产_期末数(元)
   */
  ivsmEsttAeop?: number
  /**
   * 固定资产_期末数(元)
   */
  fixAstAeop?: number
  /**
   * 在建工程_期末数(元)
   */
  udcsProjAeop?: number
  /**
   * 生产性生物资产_期末数(元)
   */
  prodBiolMatrAeop?: number
  /**
   * 油气资产_期末数(元)
   */
  olgsAstAeop?: number
  /**
   * 使用权资产_期末数(元)
   */
  useAstAeop?: number
  /**
   * 无形资产_期末数(元)
   */
  imtrAstAeop?: number
  /**
   * 开发支出_期末数(元)
   */
  devPayAeop?: number
  /**
   * 商誉_期末数(元)
   */
  gdwlAeop?: number
  /**
   * 长期待摊费用_期末数(元)
   */
  longTbatFeeAeop?: number
  /**
   * 递延所得税资产_期末数(元)
   */
  defrTaxAstAeop?: number
  /**
   * 其他非流动资产_期末数(元)
   */
  othNocrAstAeop?: number
  /**
   * 非流动资产合计_期末数(元)
   */
  nocrAstTotAeop?: number
  /**
   * 资产总计_期末数(元)
   */
  astTotAeop?: number
  /**
   * 短期借款_期末数(元)
   */
  shttLoanAeop?: number
  /**
   * 交易性金融负债_期末数(元)
   */
  trdFinlLiabAeop?: number
  /**
   * 衍生金融负债_期末数(元)
   */
  devdFinlLiabAeop?: number
  /**
   * 应付票据_期末数(元)
   */
  paybBillAeop?: number
  /**
   * 应付账款_期末数(元)
   */
  paybAmtAeop?: number
  /**
   * 预收账款_期末数(元)
   */
  ciadAmtAeop?: number
  /**
   * 合同负债_期末数(元)
   */
  agmtLiabAeop?: number
  /**
   * 应付职工薪酬_期末数(元)
   */
  paybEmpCmpsAeop?: number
  /**
   * 应交税费_期末数(元)
   */
  paycTaxFeeAeop?: number
  /**
   * 其他应付款总计_期末数(元)
   */
  othPaybAmtTotAeop?: number
  /**
   * 应付利息_期末数(元)
   */
  paybIntrAeop?: number
  /**
   * 应付股利_期末数(元)
   */
  paybDivdAeop?: number
  /**
   * 其他应付款_期末数(元)
   */
  othPaybAmtAeop?: number
  /**
   * 持有待售负债_期末数(元)
   */
  holdSaleLiabAeop?: number
  /**
   * 一年内到期的非流动负债_期末数(元)
   */
  oneyNocrLiabAeop?: number
  /**
   * 其他流动负债_期末数(元)
   */
  othLiqdLiabAeop?: number
  /**
   * 流动负债合计_期末数(元)
   */
  liqdLiabTotAeop?: number
  /**
   * 长期借款_期末数(元)
   */
  longLoanAeop?: number
  /**
   * 应付债券_期末数(元)
   */
  paybBondAeop?: number
  /**
   * 长期应付款_期末数(元)
   */
  longPaybAmtAeop?: number
  /**
   * 长期应付职工薪酬_期末数(元)
   */
  longPaybEmpCmpsAeop?: number
  /**
   * 租赁负债_期末数(元)
   */
  leasLiabAeop?: number
  /**
   * 预计负债_期末数(元)
   */
  expeLiabAeop?: number
  /**
   * 递延收益_期末数(元)
   */
  defrPayfAeop?: number
  /**
   * 递延所得税负债_期末数(元)
   */
  defrInctLiabAeop?: number
  /**
   * 其他非流动负债_期末数(元)
   */
  othNocrLiabAeop?: number
  /**
   * 非流动负债合计_期末数(元)
   */
  nocrLiabTotAeop?: number
  /**
   * 负债合计_期末数(元)
   */
  liabTotAeop?: number
  /**
   * 实收资本_期末数(元)
   */
  paidCptlAeop?: number
  /**
   * 其他权益工具_期末数(元)
   */
  othEquiInstAeop?: number
  /**
   * 资本公积_期末数(元)
   */
  cptlRsrvAeop?: number
  /**
   * 其他综合收益_期末数(元)
   */
  othCmphPayfAeop?: number
  /**
   * 专项储备_期末数(元)
   */
  spclRsrvAeop?: number
  /**
   * 盈余公积_期末数(元)
   */
  surpRsrvAeop?: number
  /**
   * 一般风险准备_期末数(元)
   */
  riskPrepAeop?: number
  /**
   * 未分配利润_期末数(元)
   */
  noAssnProfAeop?: number
  /**
   * 归属母公司所有者权益合计_期末数(元)
   */
  attrPrnCponEquiAeop?: number
  /**
   * 少数股东权益_期末数(元)
   */
  mishEquiAeop?: number
  /**
   * 所有者权益合计_期末数(元)
   */
  toeqAeop?: number
  /**
   * 负债及所有者权益合计_期末数(元)
   */
  liabToeqAeop?: number
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}

/**
 * 接口 [金融协会报送数据-详情-重大事项报告表-基本信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37267) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/majorMattersBasicReport`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailMajorMattersBasicReportRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-重大事项报告表-基本信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37267) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/majorMattersBasicReport`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailMajorMattersBasicReportResponse {
  /**
   * id
   */
  id?: number
  /**
   * 填报人联系方式
   */
  inftContMode?: string
  /**
   * 企业名称
   */
  corpName?: string
  /**
   * 法定资本(万元)
   */
  leglCptl?: number
  /**
   * 营业地址
   */
  busiAddr?: string
  /**
   * 公司法人名称
   */
  corpLegpName?: string
  /**
   * 分支机构数量
   */
  brchInsNum?: number
  /**
   * 董事长姓名
   */
  chrmName?: string
  /**
   * 总经理姓名
   */
  gmgrName?: string
  /**
   * 联系方式
   */
  contMode?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}

/**
 * 接口 [金融协会报送数据-详情-重大事项报告表-重大事项报告情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/37273) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/majorMattersEventReport`
 * @更新时间 `2025-09-17 08:37:09`
 */
export interface DetailMajorMattersEventReportRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-重大事项报告表-重大事项报告情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/37273) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/majorMattersEventReport`
 * @更新时间 `2025-09-17 08:37:09`
 */
export type DetailMajorMattersEventReportResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 事项名称
   */
  piecName?: string
  /**
   * 重大事项说明
   */
  imprPiecExpl?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融协会报送数据-详情-高管信息一览表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37249) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/seniorExecutiveInfo`
 * @更新时间 `2025-09-17 08:37:08`
 */
export interface DetailSeniorExecutiveInfoRequest {
  /**
   * 报表实例id
   */
  reportInstanceId: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [金融协会报送数据-详情-高管信息一览表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37249) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/detail/seniorExecutiveInfo`
 * @更新时间 `2025-09-17 08:37:08`
 */
export type DetailSeniorExecutiveInfoResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 序号
   */
  onum?: string
  /**
   * 姓名
   */
  name?: string
  /**
   * 证件号码
   */
  certNum?: string
  /**
   * 现任职务
   */
  currDutyCode?: string
  /**
   * 现任职务Display
   */
  currDutyDisplay?: string
  /**
   * 任职时间
   */
  aoffTime?: string
  /**
   * 批复文号
   */
  aprvFileNum?: string
  /**
   * 最高学历
   */
  highEduCode?: string
  /**
   * 最高学历Display
   */
  highEduDisplay?: string
  /**
   * 毕业院校
   */
  gradScho?: string
  /**
   * 就读专业
   */
  spjt?: string
  /**
   * 从事金融/经济工作时间
   */
  haveFinlTime?: string
  /**
   * 联系电话
   */
  contTel?: string
  /**
   * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
   */
  reportInstanceId?: string
  /**
   * 报表实例周期格式：yyyyqq
   */
  reportInstancePeriod?: string
  /**
   * 批次号
   */
  batchNo?: string
  /**
   * 版本号
   */
  version?: string
  /**
   * 操作标识 新增数据为insert，更新数据记录时值为update
   */
  op?: string
  /**
   * 上报时间
   */
  reportTime?: string
  /**
   * 写入时间
   */
  writeTime?: string
}[]

/**
 * 接口 [金融局报送待办-重新生成↗](http://yapi.zswltec.com:3000/project/11/interface/api/37483) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/recalculate`
 * @更新时间 `2025-09-26 10:05:30`
 */
export interface ReportRecalculateRequest {
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
 * 接口 [金融局报送待办-重新生成↗](http://yapi.zswltec.com:3000/project/11/interface/api/37483) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/recalculate`
 * @更新时间 `2025-09-26 10:05:30`
 */
export interface ReportRecalculateResponse {
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
 * 接口 [金融局报送数据提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/37339) 的 **请求类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/flow/submit`
 * @更新时间 `2025-09-17 17:27:49`
 */
export interface FlowSubmitRequest {
  /**
   * 报表实例唯一标识（数组）
   */
  reportInstanceIdList: string[]
}

/**
 * 接口 [金融局报送数据提交审批↗](http://yapi.zswltec.com:3000/project/11/interface/api/37339) 的 **返回类型**
 *
 * @分类 [金融协会报送数据-相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4556)
 * @请求头 `POST /association/report/flow/submit`
 * @更新时间 `2025-09-17 17:27:49`
 */
export interface FlowSubmitResponse {
  importSuccess?: boolean
  errorMessageList?: string[]
}

/* prettier-ignore-end */
