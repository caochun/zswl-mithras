/* prettier-ignore-start */
import * as Types from './interface/financialReportApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 获取金融局上报所有数据字典
  postReportGetAssociationDict: (
    data: Types.ReportGetAssociationDictRequest
  ): Promise<Types.ReportGetAssociationDictResponse> =>
    http.post('/association/report/getAssociationDict', data, { mock }),

  // 金融协会报送数据-上报
  postReportPush: (data: Types.ReportPushRequest): Promise<Types.ReportPushResponse> =>
    http.post('/association/report/push', data, { mock }),

  // 金融协会报送数据-修改-业务情况表
  postModifyBusinessSituation: (
    data: Types.ModifyBusinessSituationRequest
  ): Promise<Types.ModifyBusinessSituationResponse> =>
    http.post('/association/report/modify/businessSituation', data, { mock }),

  // 金融协会报送数据-修改-主要业务清单
  postModifyMainBusiness: (
    data: Types.ModifyMainBusinessRequest
  ): Promise<Types.ModifyMainBusinessResponse> =>
    http.post('/association/report/modify/mainBusiness', data, { mock }),

  // 金融协会报送数据-修改-公司利润表
  postModifyCompanyProfit: (
    data: Types.ModifyCompanyProfitRequest
  ): Promise<Types.ModifyCompanyProfitResponse> =>
    http.post('/association/report/modify/companyProfit', data, { mock }),

  // 金融协会报送数据-修改-关联方信息汇总表
  postModifyRelation: (data: Types.ModifyRelationRequest): Promise<Types.ModifyRelationResponse> =>
    http.post('/association/report/modify/relation', data, { mock }),

  // 金融协会报送数据-修改-基本情况统计表
  postModifyBasicSituation: (
    data: Types.ModifyBasicSituationRequest
  ): Promise<Types.ModifyBasicSituationResponse> =>
    http.post('/association/report/modify/basicSituation', data, { mock }),

  // 金融协会报送数据-修改-实体经济服务表
  postModifyEntityEconomyService: (
    data: Types.ModifyEntityEconomyServiceRequest
  ): Promise<Types.ModifyEntityEconomyServiceResponse> =>
    http.post('/association/report/modify/entityEconomyService', data, { mock }),

  // 金融协会报送数据-修改-对外融资清单
  postModifyExternalFinancing: (
    data: Types.ModifyExternalFinancingRequest
  ): Promise<Types.ModifyExternalFinancingResponse> =>
    http.post('/association/report/modify/externalFinancing', data, { mock }),

  // 金融协会报送数据-修改-最大10家客户（含集团）集中度统计表
  postModifyTop10ClientConcentration: (
    data: Types.ModifyTop10ClientConcentrationRequest
  ): Promise<Types.ModifyTop10ClientConcentrationResponse> =>
    http.post('/association/report/modify/top10ClientConcentration', data, { mock }),

  // 金融协会报送数据-修改-涉法涉讼涉访信息
  postModifyLawInvolvedVisitRelatedInfo: (
    data: Types.ModifyLawInvolvedVisitRelatedInfoRequest
  ): Promise<Types.ModifyLawInvolvedVisitRelatedInfoResponse> =>
    http.post('/association/report/modify/lawInvolvedVisitRelatedInfo', data, { mock }),

  // 金融协会报送数据-修改-股东变更记录
  postModifyShahChangeInfo: (
    data: Types.ModifyShahChangeInfoRequest
  ): Promise<Types.ModifyShahChangeInfoResponse> =>
    http.post('/association/report/modify/shahChangeInfo', data, { mock }),

  // 金融协会报送数据-修改-股东股权信息
  postModifyShahStorInfo: (
    data: Types.ModifyShahStorInfoRequest
  ): Promise<Types.ModifyShahStorInfoResponse> =>
    http.post('/association/report/modify/shahStorInfo', data, { mock }),

  // 金融协会报送数据-修改-资产负债表
  postModifyBalanceSheetPartial: (
    data: Types.ModifyBalanceSheetPartialRequest
  ): Promise<Types.ModifyBalanceSheetPartialResponse> =>
    http.post('/association/report/modify/balanceSheetPartial', data, { mock }),

  // 金融协会报送数据-修改-重大事项报告情况
  postModifyMajorMattersEventReport: (
    data: Types.ModifyMajorMattersEventReportRequest
  ): Promise<Types.ModifyMajorMattersEventReportResponse> =>
    http.post('/association/report/modify/majorMattersEventReport', data, { mock }),

  // 金融协会报送数据-修改-重大事项报告表
  postModifyMajorMattersBasicReport: (
    data: Types.ModifyMajorMattersBasicReportRequest
  ): Promise<Types.ModifyMajorMattersBasicReportResponse> =>
    http.post('/association/report/modify/majorMattersBasicReport', data, { mock }),

  // 金融协会报送数据-修改-高管信息
  postModifySeniorExecutiveInfo: (
    data: Types.ModifySeniorExecutiveInfoRequest
  ): Promise<Types.ModifySeniorExecutiveInfoResponse> =>
    http.post('/association/report/modify/seniorExecutiveInfo', data, { mock }),

  // 金融协会报送数据-列表
  postReportPageList: (data: Types.ReportPageListRequest): Promise<Types.ReportPageListResponse> =>
    http.post('/association/report/pageList', data, { mock }),

  // 金融协会报送数据-创建主表记录
  postReportCreate: (data: Types.ReportCreateRequest): Promise<Types.ReportCreateResponse> =>
    http.post('/association/report/create', data, { mock }),

  // 金融协会报送数据-删除
  postReportDelete: (data: Types.ReportDeleteRequest): Promise<Types.ReportDeleteResponse> =>
    http.post('/association/report/delete', data, { mock }),

  // 金融协会报送数据-审批列表
  postFlowApplyList: (data: Types.FlowApplyListRequest): Promise<Types.FlowApplyListResponse> =>
    http.post('/association/report/flow/applyList', data, { mock }),

  // 金融协会报送数据-导入
  postReportImport: (data: Types.ReportImportRequest): Promise<Types.ReportImportResponse> =>
    http.post('/association/report/import', data, { mock, type: 'upload', timeout: 0 }),

  // 金融协会报送数据-模板下载
  postTemplateFileUrl: (
    data: Types.TemplateFileUrlRequest
  ): Promise<Types.TemplateFileUrlResponse> =>
    http.post('/association/report/template/fileUrl', data, { mock }),

  // 金融协会报送数据-详情-业务情况表
  postDetailBusinessSituation: (
    data: Types.DetailBusinessSituationRequest
  ): Promise<Types.DetailBusinessSituationResponse> =>
    http.post('/association/report/detail/businessSituation', data, { mock }),

  // 金融协会报送数据-详情-主要业务清单
  postDetailMainBusiness: (
    data: Types.DetailMainBusinessRequest
  ): Promise<Types.DetailMainBusinessResponse> =>
    http.post('/association/report/detail/mainBusiness', data, { mock }),

  // 金融协会报送数据-详情-公司利润表
  postDetailCompanyProfit: (
    data: Types.DetailCompanyProfitRequest
  ): Promise<Types.DetailCompanyProfitResponse> =>
    http.post('/association/report/detail/companyProfit', data, { mock }),

  // 金融协会报送数据-详情-关联方信息汇总表
  postDetailRelation: (data: Types.DetailRelationRequest): Promise<Types.DetailRelationResponse> =>
    http.post('/association/report/detail/relation', data, { mock }),

  // 金融协会报送数据-详情-基本情况统计表
  postDetailBasicSituation: (
    data: Types.DetailBasicSituationRequest
  ): Promise<Types.DetailBasicSituationResponse> =>
    http.post('/association/report/detail/basicSituation', data, { mock }),

  // 金融协会报送数据-详情-实体经济服务表
  postDetailEntityEconomyService: (
    data: Types.DetailEntityEconomyServiceRequest
  ): Promise<Types.DetailEntityEconomyServiceResponse> =>
    http.post('/association/report/detail/entityEconomyService', data, { mock }),

  // 金融协会报送数据-详情-对外融资清单
  postDetailExternalFinancing: (
    data: Types.DetailExternalFinancingRequest
  ): Promise<Types.DetailExternalFinancingResponse> =>
    http.post('/association/report/detail/externalFinancing', data, { mock }),

  // 金融协会报送数据-详情-最大10家客户（含集团）集中度统计表
  postDetailTop10ClientConcentration: (
    data: Types.DetailTop10ClientConcentrationRequest
  ): Promise<Types.DetailTop10ClientConcentrationResponse> =>
    http.post('/association/report/detail/top10ClientConcentration', data, { mock }),

  // 金融协会报送数据-详情-涉法涉讼涉访信息表
  postDetailLawInvolvedVisitRelatedInfo: (
    data: Types.DetailLawInvolvedVisitRelatedInfoRequest
  ): Promise<Types.DetailLawInvolvedVisitRelatedInfoResponse> =>
    http.post('/association/report/detail/lawInvolvedVisitRelatedInfo', data, { mock }),

  // 金融协会报送数据-详情-股东股权信息一览表-股东变更记录
  postDetailShahChangeInfo: (
    data: Types.DetailShahChangeInfoRequest
  ): Promise<Types.DetailShahChangeInfoResponse> =>
    http.post('/association/report/detail/shahChangeInfo', data, { mock }),

  // 金融协会报送数据-详情-股东股权信息一览表-股东股权信息
  postDetailShahStorInfo: (
    data: Types.DetailShahStorInfoRequest
  ): Promise<Types.DetailShahStorInfoResponse> =>
    http.post('/association/report/detail/shahStorInfo', data, { mock }),

  // 金融协会报送数据-详情-资产负债表
  postDetailBalanceSheetPartial: (
    data: Types.DetailBalanceSheetPartialRequest
  ): Promise<Types.DetailBalanceSheetPartialResponse> =>
    http.post('/association/report/detail/balanceSheetPartial', data, { mock }),

  // 金融协会报送数据-详情-重大事项报告表-基本信息
  postDetailMajorMattersBasicReport: (
    data: Types.DetailMajorMattersBasicReportRequest
  ): Promise<Types.DetailMajorMattersBasicReportResponse> =>
    http.post('/association/report/detail/majorMattersBasicReport', data, { mock }),

  // 金融协会报送数据-详情-重大事项报告表-重大事项报告情况
  postDetailMajorMattersEventReport: (
    data: Types.DetailMajorMattersEventReportRequest
  ): Promise<Types.DetailMajorMattersEventReportResponse> =>
    http.post('/association/report/detail/majorMattersEventReport', data, { mock }),

  // 金融协会报送数据-详情-高管信息一览表
  postDetailSeniorExecutiveInfo: (
    data: Types.DetailSeniorExecutiveInfoRequest
  ): Promise<Types.DetailSeniorExecutiveInfoResponse> =>
    http.post('/association/report/detail/seniorExecutiveInfo', data, { mock }),

  // 金融局报送待办-重新生成
  postReportRecalculate: (
    data: Types.ReportRecalculateRequest
  ): Promise<Types.ReportRecalculateResponse> =>
    http.post('/association/report/recalculate', data, { mock }),

  // 金融局报送数据提交审批
  postFlowSubmit: (data: Types.FlowSubmitRequest): Promise<Types.FlowSubmitResponse> =>
    http.post('/association/report/flow/submit', data, { mock }),
}

/* prettier-ignore-end */
