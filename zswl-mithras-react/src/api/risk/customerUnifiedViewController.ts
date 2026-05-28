/* prettier-ignore-start */
import * as Types from './interface/customerUnifiedViewController'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // judgementDocumentDetail
  postDetailJudgementDocumentDetail: (
    data: Types.DetailJudgementDocumentDetailRequest
  ): Promise<Types.DetailJudgementDocumentDetailResponse> =>
    http.post('/customer/view/detail/judgementDocumentDetail', data, { mock }),

  // judicialAssistanceDetail
  postDetailJudicialAssistanceDetail: (
    data: Types.DetailJudicialAssistanceDetailRequest
  ): Promise<Types.DetailJudicialAssistanceDetailResponse> =>
    http.post('/customer/view/detail/judicialAssistanceDetail', data, { mock }),

  // limitHighConsumeDetail
  postDetailLimitHighConsumeDetail: (
    data: Types.DetailLimitHighConsumeDetailRequest
  ): Promise<Types.DetailLimitHighConsumeDetailResponse> =>
    http.post('/customer/view/detail/limitHighConsumeDetail', data, { mock }),

  // 企业舆情分页查询
  postDetailOpiInfo: (data: Types.DetailOpiInfoRequest): Promise<Types.DetailOpiInfoResponse> =>
    http.post('/customer/view/detail/opiInfo', data, { mock }),

  // 企业风险统一视图-详情-经营风险信息查询
  postDetailQueryBizRisk: (
    data: Types.DetailQueryBizRiskRequest
  ): Promise<Types.DetailQueryBizRiskResponse> =>
    http.post('/customer/view/detail/queryBizRisk', data, { mock }),

  // 动产抵押详情
  postDetailQueryChattelMortageDetail: (
    data: Types.DetailQueryChattelMortageDetailRequest
  ): Promise<Types.DetailQueryChattelMortageDetailResponse> =>
    http.post('/customer/view/detail/queryChattelMortageDetail', data, { mock }),

  // 发债信息查询
  postDetailQueryBondBasicInfo: (
    data: Types.DetailQueryBondBasicInfoRequest
  ): Promise<Types.DetailQueryBondBasicInfoResponse> =>
    http.post('/customer/view/detail/queryBondBasicInfo', data, { mock }),

  // 司法协助详情
  postJudicialAssistanceDetail: (
    data: Types.JudicialAssistanceDetailRequest
  ): Promise<Types.JudicialAssistanceDetailResponse> =>
    http.post('/judicialAssistanceDetail', data, { mock }),

  // 客户风险统一视图-详情-客户信息分页查询
  postDetailQueryCustomerViewInfo: (
    data: Types.DetailQueryCustomerViewInfoRequest
  ): Promise<Types.DetailQueryCustomerViewInfoResponse> =>
    http.post('/customer/view/detail/queryCustomerViewInfo', data, { mock }),

  // 客户风险统一视图-详情-客户基础信息查询
  postDetailQueryCompanyBasicInfo: (
    data: Types.DetailQueryCompanyBasicInfoRequest
  ): Promise<Types.DetailQueryCompanyBasicInfoResponse> =>
    http.post('/customer/view/detail/queryCompanyBasicInfo', data, { mock }),

  // 担保对象详情
  postDetailQueryGuaranteeEventDetail: (
    data: Types.DetailQueryGuaranteeEventDetailRequest
  ): Promise<Types.DetailQueryGuaranteeEventDetailResponse> =>
    http.post('/customer/view/detail/queryGuaranteeEventDetail', data, { mock }),

  // 法律诉讼
  postDetailQueryLitigation: (
    data: Types.DetailQueryLitigationRequest
  ): Promise<Types.DetailQueryLitigationResponse> =>
    http.post('/customer/view/detail/queryLitigation', data, { mock }),

  // 股东信息查询
  postDetailQueryStockHolders: (
    data: Types.DetailQueryStockHoldersRequest
  ): Promise<Types.DetailQueryStockHoldersResponse> =>
    http.post('/customer/view/detail/queryStockHolders', data, { mock }),

  // 舆情统计
  postDetailOpiStatistc: (
    data: Types.DetailOpiStatistcRequest
  ): Promise<Types.DetailOpiStatistcResponse> =>
    http.post('/customer/view/detail/opiStatistc', data, { mock }),

  // 获取地区onomy数据
  postDetailAreaEconomy: (
    data: Types.DetailAreaEconomyRequest
  ): Promise<Types.DetailAreaEconomyResponse> =>
    http.post('/customer/view/detail/areaEconomy', data, { mock }),

  // 获取地州onomy数据
  postDetailCtzReginEconomy: (
    data: Types.DetailCtzReginEconomyRequest
  ): Promise<Types.DetailCtzReginEconomyResponse> =>
    http.post('/customer/view/detail/ctzReginEconomy', data, { mock }),

  // 裁判文书详情
  postJudgementDocumentDetail: (
    data: Types.JudgementDocumentDetailRequest
  ): Promise<Types.JudgementDocumentDetailResponse> =>
    http.post('/judgementDocumentDetail', data, { mock }),

  // 重大税收违法详情
  postDetailMajortax: (data: Types.DetailMajortaxRequest): Promise<Types.DetailMajortaxResponse> =>
    http.post('/customer/view/detail/majortax', data, { mock }),

  // 限制高消费详情
  postLimitHighConsumeDetail: (
    data: Types.LimitHighConsumeDetailRequest
  ): Promise<Types.LimitHighConsumeDetailResponse> =>
    http.post('/limitHighConsumeDetail', data, { mock }),
  // 区域评级
  postAreaRating: (data: any): Promise<any> =>
    http.post('/customer/view/detail/areaRating', data, { mock }),
}

/* prettier-ignore-end */
