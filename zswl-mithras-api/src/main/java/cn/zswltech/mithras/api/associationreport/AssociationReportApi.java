package cn.zswltech.mithras.api.associationreport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.associationreport.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Api(tags = "金融协会报送数据-相关接口")
@RequestMapping(path = "/association/report")
public interface AssociationReportApi {

    @ApiOperation("获取金融局上报所有数据字典")
    @PostMapping(path = "/getAssociationDict")
    R<Map<String, Map<String, String>> > getAssociationDict();

    @ApiOperation("金融协会报送数据-上报")
    @PostMapping(path = "/push")
    R<AssociationReportImportRSP> push(@RequestBody @Valid AssociationReportPushREQ req);

    @ApiOperation("金融协会报送数据-模板下载")
    @PostMapping(path = "/template/fileUrl")
    R<String> getTemplateFileUrl(@RequestBody @Valid AssociationReportTemplateDownloadREQ req);

    @ApiOperation("金融协会报送数据-创建主表记录")
    @PostMapping(path = "/create")
    R<String> create(@RequestBody @Valid AssociationReportCreateREQ req);

    @ApiOperation("金融协会报送数据-导入")
    @PostMapping(path = "/import")
    R<AssociationReportImportRSP> importExcel(@Valid AssociationReportImportREQ req);

    @ApiOperation("金融协会报送数据-列表")
    @PostMapping(path = "/pageList")
    R<PageR<AssociationReportListRSP>> pageList(@RequestBody @Valid AssociationReportListREQ req);

    @ApiOperation("金融协会报送数据-删除")
    @PostMapping(path = "/delete")
    R<Void> delete(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("金融协会报送数据-详情-基本情况统计表")
    @PostMapping(path = "/detail/basicSituation")
    R<AssociationDetailBasicSituationRSP> detailBasicSituation(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-基本情况统计表")
    @PostMapping(path = "/modify/basicSituation")
    R<AssociationReportModifyRSP> modifyBasicSituation(@RequestBody @Valid AssociationBasicSituationModifyREQ req);

    @ApiOperation("金融协会报送数据-详情-股东股权信息一览表-股东股权信息")
    @PostMapping(path = "/detail/shahStorInfo")
    R<List<AssociationDetailShahStorInfoRSP>> detailShahStorInfo(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-股东股权信息")
    @PostMapping(path = "/modify/shahStorInfo")
    R<AssociationReportModifyRSP> modifyShahStorInfo(@RequestBody @Valid AssociationShahStorInfoModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-股东股权信息一览表-股东变更记录")
    @PostMapping(path = "/detail/shahChangeInfo")
    R<List<AssociationDetailShahChangeInfoRSP>> detailShahChangeInfo(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-股东变更记录")
    @PostMapping(path = "/modify/shahChangeInfo")
    R<AssociationReportModifyRSP> modifyShahChangeInfo(@RequestBody @Valid AssociationShahChangeInfoModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-高管信息一览表")
    @PostMapping(path = "/detail/seniorExecutiveInfo")
    R<List<AssociationDetailSeniorExecutiveInfoRSP>> detailSeniorExecutiveInfo(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-高管信息")
    @PostMapping(path = "/modify/seniorExecutiveInfo")
    R<AssociationReportModifyRSP> modifySeniorExecutiveInfo(@RequestBody @Valid AssociationSeniorExecutiveInfoModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-涉法涉讼涉访信息表")
    @PostMapping(path = "/detail/lawInvolvedVisitRelatedInfo")
    R<List<AssociationDetailLawInvolvedVisitRelatedInfoRSP>> detailLawInvolvedVisitRelatedInfo(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-涉法涉讼涉访信息")
    @PostMapping(path = "/modify/lawInvolvedVisitRelatedInfo")
    R<AssociationReportModifyRSP> modifyLawInvolvedVisitRelatedInfo(@RequestBody @Valid AssociationLawInvolvedVisitRelatedInfoModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-重大事项报告表-基本信息")
    @PostMapping(path = "/detail/majorMattersBasicReport")
    R<AssociationDetailMajorMattersBasicReportRSP> detailMajorMattersBasicReport(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-重大事项报告表")
    @PostMapping(path = "/modify/majorMattersBasicReport")
    R<AssociationReportModifyRSP> modifyMajorMattersBasicReport(@RequestBody @Valid AssociationMajorMattersBasicReportModifyREQ req);

    @ApiOperation("金融协会报送数据-详情-重大事项报告表-重大事项报告情况")
    @PostMapping(path = "/detail/majorMattersEventReport")
    R<List<AssociationDetailMajorMattersEventReportRSP>> detailMajorMattersEventReport(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-重大事项报告情况")
    @PostMapping(path = "/modify/majorMattersEventReport")
    R<AssociationReportModifyRSP> modifyMajorMattersEventReport(@RequestBody @Valid AssociationMajorMattersEventReportModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-业务情况表")
    @PostMapping(path = "/detail/businessSituation")
    R<AssociationDetailBusinessSituationRSP> detailBusinessSituation(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-业务情况表")
    @PostMapping(path = "/modify/businessSituation")
    R<AssociationReportModifyRSP> modifyBusinessSituation(@RequestBody @Valid AssociationBusinessSituationModifyREQ req);

    @ApiOperation("金融协会报送数据-详情-公司利润表")
    @PostMapping(path = "/detail/companyProfit")
    R<AssociationDetailProfitRSP> detailCompanyProfit(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-公司利润表")
    @PostMapping(path = "/modify/companyProfit")
    R<AssociationReportModifyRSP> modifyCompanyProfit(@RequestBody @Valid AssociationCompanyProfitStatementModifyREQ req);

    @ApiOperation("金融协会报送数据-详情-资产负债表")
    @PostMapping(path = "/detail/balanceSheetPartial")
    R<AssociationDetailBalanceSheetPartialRSP> detailBalanceSheetPartial(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-资产负债表")
    @PostMapping(path = "/modify/balanceSheetPartial")
    R<AssociationReportModifyRSP> modifyBalanceSheetPartial(@RequestBody @Valid AssociationBalanceSheetPartialModifyREQ req);

    @ApiOperation("金融协会报送数据-详情-实体经济服务表")
    @PostMapping(path = "/detail/entityEconomyService")
    R<AssociationDetailEntityEconomyServiceRSP> detailEntityEconomyService(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-实体经济服务表")
    @PostMapping(path = "/modify/entityEconomyService")
    R<AssociationReportModifyRSP> modifyEntityEconomyService(@RequestBody @Valid AssociationEntityEconomyServiceModifyREQ req);

    @ApiOperation("金融协会报送数据-详情-对外融资清单")
    @PostMapping(path = "/detail/externalFinancing")
    R<List<AssociationDetailExternalFinancingRSP>> detailExternalFinancing(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-对外融资清单")
    @PostMapping(path = "/modify/externalFinancing")
    R<AssociationReportModifyRSP> modifyExternalFinancing(@RequestBody @Valid AssociationExternalFinancingModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-主要业务清单")
    @PostMapping(path = "/detail/mainBusiness")
    R<List<AssociationDetailMainBusinessRSP>> detailMainBusiness(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-主要业务清单")
    @PostMapping(path = "/modify/mainBusiness")
    R<AssociationReportModifyRSP> modifyMainBusiness(@RequestBody @Valid AssociationMainBusinessModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-关联方信息汇总表")
    @PostMapping(path = "/detail/relation")
    R<List<AssociationDetailRelationRSP>> detailRelation(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-关联方信息汇总表")
    @PostMapping(path = "/modify/relation")
    R<AssociationReportModifyRSP> modifyRelation(@RequestBody @Valid AssociationRelationModifyListREQ req);

    @ApiOperation("金融协会报送数据-详情-最大10家客户（含集团）集中度统计表")
    @PostMapping(path = "/detail/top10ClientConcentration")
    R<List<AssociationDetailTop10ClientConcentrationRSP>> detailTop10ClientConcentration(@RequestBody @Valid AssociationReportDetailREQ req);

    @ApiOperation("金融协会报送数据-修改-最大10家客户（含集团）集中度统计表")
    @PostMapping(path = "/modify/top10ClientConcentration")
    R<AssociationReportModifyRSP> modifyTop10ClientConcentration(@RequestBody @Valid AssociationTop10ClientConcentrationModifyListREQ req);

    @ApiOperation("金融局报送数据提交审批")
    @PostMapping("/flow/submit")
    R<AssociationReportImportRSP> reportApplySubmit(@RequestBody @Valid ReportApplySubmitREQ req);

    @ApiOperation("金融协会报送数据-审批列表")
    @PostMapping(path = "/flow/applyList")
    R<List<AssociationReportListRSP>> applyList(@RequestBody @Valid AssociationReportApplyREQ req);

    @ApiOperation("金融局报送待办-重新生成")
    @PostMapping(path = "/recalculate")
    R<Void> recalculate(@RequestBody @Valid SinglePkREQ req);
}
