package cn.zswltech.mithras.application.orchestration.workflow.datacompare;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractAccountService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.contract.core.ContractPledgeService;
import cn.zswltech.mithras.contract.core.ContractMortgageService;
import cn.zswltech.mithras.contract.core.ContractGuarantorService;
import cn.zswltech.mithras.contract.versioning.service.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractMortgageLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractPledgeLibService;
import cn.zswltech.mithras.leaseholdproperty.application.contract.ContractLeaseItemService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.mithras.api.client.*;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractRentApi;
import cn.zswltech.mithras.api.contract.ContractSettleApi;
import cn.zswltech.mithras.api.ftp.FtpMonthlyGuidanceApi;
import cn.zswltech.mithras.api.ftp.FtpQuarterlyGuidanceApi;
import cn.zswltech.mithras.api.fund.financing.*;
import cn.zswltech.mithras.api.fund.receiptrepay.*;
import cn.zswltech.mithras.api.newftp.NewFtpBaseInfoApi;
import cn.zswltech.mithras.api.newftp.draft.NewFtpMonthlyDeductionDraftApi;
import cn.zswltech.mithras.api.newftp.draft.NewFtpMonthlyGuidanceExtDraftApi;
import cn.zswltech.mithras.api.payment.PaymentApi;
import cn.zswltech.mithras.api.payment.PaymentDetailReq;
import cn.zswltech.mithras.api.payment.PaymentQuestionnaireApi;
import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListReq;
import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.api.policy.PolicyInfoApi;
import cn.zswltech.mithras.api.projpricing.ProjPricingCashFlowPlanApi;
import cn.zswltech.mithras.api.projreview.ProjReviewCashFlowPlanApi;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListRSP;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailRSP;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListRSP;
import cn.zswltech.mithras.dto.client.normal.*;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListREQ;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListRSP;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListREQ;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListRSP;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.guarantor.ContractGuarantorListRSP;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListREQ;
import cn.zswltech.mithras.dto.contract.mortgage.ContractMortgageListRSP;
import cn.zswltech.mithras.dto.contract.pledge.ContractPledgeListRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualListRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualRSP;
import cn.zswltech.mithras.dto.contract.rent.ContractRentEstimateListRSP;
import cn.zswltech.mithras.dto.contract.settle.ContractSettlePlanDetailREQ;
import cn.zswltech.mithras.dto.contract.settle.ContractSettlePlanDetailRSP;
import cn.zswltech.mithras.dto.contract.tenantry.ContractTenantryListRSP;
import cn.zswltech.mithras.dto.datacompare.ContractPriceDetailCompareRSP;
import cn.zswltech.mithras.dto.datacompare.ProjEstablishPriceCompareRSP;
import cn.zswltech.mithras.dto.datacompare.ProjReviewPriceCompareRSP;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.ext.FileListVersionREQ;
import cn.zswltech.mithras.dto.ftp.*;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.FundFinancingCollectAccountListREQ;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.FundFinancingCollectAccountListRSP;
import cn.zswltech.mithras.dto.fund.financing.payaccount.FundFinancingPayAccountListREQ;
import cn.zswltech.mithras.dto.fund.financing.payaccount.FundFinancingPayAccountListRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightRSP;
import cn.zswltech.mithras.dto.newftp.*;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailCompareREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListRSP;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.version.DiffFile;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.dto.version.DiffValueList;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.workflow.datacompare.EditdataCompareApplicationService;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionWeightApplicationService;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.workflow.datacompare.enums.CompareFactoryEnum;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItemLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.contract.*;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactoryCreator;
import cn.zswltech.mithras.application.orchestration.groupcredit.establish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.groupcredit.review.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.customer.versioning.CorpAddressInfoLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractBaseInfoLibService;
import cn.zswltech.mithras.policy.versioning.service.PolicyInfoLibService;
import cn.zswltech.mithras.projectprocess.versioning.projpricing.ProjPricingBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.versioning.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.FileService;
import cn.zswltech.mithras.ftp.newftp.controller.draft.NewFtpLprPricingDraftController;
import cn.zswltech.mithras.ftp.newftp.controller.draft.NewFtpShiborInterestRateDraftController;
import cn.zswltech.mithras.ftp.newftp.controller.draft.NewFtpTreasuryBondYieldDraftController;
import cn.zswltech.mithras.ftp.newftp.controller.draft.*;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import cn.zswltech.mithras.contract.versioning.service.ContractAccountLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractLeaseItemLibService;
import cn.zswltech.mithras.contract.versioning.service.ContractTenantryLibService;
import cn.zswltech.mithras.contract.core.ContractPriceService;

/**
 * @create: 2022-08-02
 **/

@Service
public class EditdataCompareFacade implements EditdataCompareApplicationService {

    @Resource
    private EditdataCompareFactoryCreator factoryCreator;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishPriceService projEstablishPriceService;
    @Resource
    private NormalSpouseApi normalSpouseApi;
    @Resource
    private NormalBaseInfoApi normalBaseInfoApi;
    @Resource
    private NormalBankAccountApi normalBankAccountApi;
    @Resource
    private CorpAddressInfoApi corpAddressInfoApi;
    @Resource
    private CorpAddressInfoLibService corpAddressInfoLibService;
    @Resource
    private CorpBankAccountApi corpBankAccountApi;
    @Resource
    private CorpBondInfoApi corpBondInfoApi;
    @Resource
    private CorpCommerceInfoApi corpCommerceInfoApi;
    @Resource
    private CorpContactInfoApi corpContactInfoApi;
    @Resource
    private CorpRelatedEnterpriseApi corpRelatedEnterpriseApi;
    @Resource
    private CorpShareholderInfoApi corpShareholderInfoApi;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ProjReviewCashFlowPlanApi projReviewCashFlowPlanApi;
    @Resource
    private PaymentApi paymentApi;
    @Resource
    private PaymentQuestionnaireApi paymentQuestionnaireApi;
    @Resource
    private ContractRentApi contractRentApi;
    @Resource
    private ContractAccountService contractAccountService;
    @Resource
    private ContractAccountLibService contractAccountLibService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private ContractTenantryLibService contractTenantryLibService;
    @Resource
    private ContractPledgeService contractPledgeService;
    @Resource
    private ContractPledgeLibService contractPledgeLibService;
    @Resource
    private ContractMortgageService contractMortgageService;
    @Resource
    private ContractMortgageLibService contractMortgageLibService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractLeaseItemService contractLeaseItemService;
    @Resource
    private ContractLeaseItemLibService contractLeaseItemLibService;
    @Resource
    private ContractPriceService priceService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractSettleApi contractSettleApi;
    @Resource
    private GroupCreditEstablishBaseInfoService groupCreditEstablishBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;
    @Resource
    private FundFinancingBaseInfoApi fundFinancingBaseInfoApi;
    @Resource
    private FundFinancingPledgeApi fundFinancingPledgeApi;
    @Resource
    private FundFinancingPlanApi fundFinancingPlanApi;
    @Resource
    private FundFinancingRepayEstimateApi fundFinancingRepayEstimateApi;
    @Resource
    private FundFinancingRepayActualApi fundFinancingRepayActualApi;
    @Resource
    private FundFinancingCollectAccountApi fundFinancingCollectAccountApi;
    @Resource
    private FundFinancingPayAccountApi fundFinancingPayAccountApi;

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FileService fileService;
    @Resource
    private FundReceiptRepayBorrowingApi fundReceiptRepayBorrowingApi;
    @Resource
    private FundReceiptRepayCashDepositApi fundReceiptRepayCashDepositApi;
    @Resource
    private FundReceiptRepayExpenseApi fundReceiptRepayExpenseApi;
    @Resource
    private FundReceiptAccountApi fundReceiptAccountApi;
    @Resource
    private FundRepayAccountApi fundRepayAccountApi;
    @Resource
    private FundReceiptRepayCashFlowApi fundReceiptRepayCashFlowApi;
    @Resource
    private FundReceiptRepayBaseInfoApi fundReceiptRepayBaseInfoApi;
    @Resource
    private PolicyInfoApi policyInfoApi;
    @Resource
    private PolicyInfoLibService policyInfoLibService;

    //new Ftp
    @Resource
    private NewFtpTreasuryBondYieldDraftController newFtpTreasuryBondYieldController;
    @Resource
    private NewFtpShiborInterestRateDraftController newFtpShiborInterestRateController;
    @Resource
    private NewFtpLprPricingDraftController newFtpLprPricingController;
    @Resource
    private NewFtpGuaranteeCostPricingDraftController newFtpGuaranteeCostPricingDraftController;
    @Resource
    private NewFtpFinancingCostPricingDraftController newFtpFinancingCostPricingDraftController;

    @Resource
    private ProjPricingBaseInfoService projPricingBaseInfoService;
    @Resource
    private ProjPricingBaseInfoLibService projPricingBaseInfoLibService;
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private ProjPricingCashFlowPlanApi projPricingCashFlowPlanApi;
    @Resource
    private KpiProjectDistributionWeightApplicationService kpiProjectDistributionWeightApplicationService;

    @Override
    public R<Map<String, DiffValue>> projBaseInfoEditdataCompare(ProjEstablishBaseInfoDetailREQ req) {
        R<ProjEstablishBaseInfoListRSP> detail = projEstablishBaseInfoService.detail(req);
        return R.ok(compare(CompareFactoryEnum.projEstablishBaseInfo.name(), detail, req.getId(), req.getVersion()));
    }

    @Override
    public R<ProjEstablishPriceCompareRSP> projPriceEditdataCompare(@Valid ProjEstablishPriceDetailREQ req) {
        ProjEstablishPriceDetailRSP detail = projEstablishPriceService.detail(req.getProjEstablishId(), req.getVersion());
        ProjEstablishPriceCompareRSP rsp = new ProjEstablishPriceCompareRSP();
        if (detail.getLeasePriceRSP() != null) {
            rsp.setLeasePriceRSP(compare(CompareFactoryEnum.projEstablishLeasePrice.name(), R.ok(detail.getLeasePriceRSP()), req.getProjEstablishId(), req.getVersion()));
        }
        if (detail.getAocPriceRSP() != null) {
            rsp.setAocPriceRSP(compare(CompareFactoryEnum.projEstablishAocPrice.name(), R.ok(detail.getAocPriceRSP()), req.getProjEstablishId(), req.getVersion()));
        }
        if (detail.getFactoringPriceRSP() != null) {
            rsp.setFactoringPriceRSP(compare(CompareFactoryEnum.projEstablishFactoringPrice.name(), R.ok(detail.getFactoringPriceRSP()), req.getProjEstablishId(), req.getVersion()));
        }

        return R.ok(rsp);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> normalSpouseEditdataCompare(@Valid NormalSpouseListREQ req) {
        R<PageR<NormalSpouseListRSP>> detail = normalSpouseApi.detail(req);
        return R.ok(pageCompare(CompareFactoryEnum.normalSpouse.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> normalBaseInfoEditdataCompare(@Valid NormalBaseInfoDetailREQ req) {
        R<NormalBaseInfoDetailRSP> detail = normalBaseInfoApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.normalBaseInfo.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> corpCommerceEditdataCompare(@Valid CorpCommerceInfoDetailREQ req) {
        R<CorpCommerceInfoDetailRSP> detail = corpCommerceInfoApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.corpCommerceInfo.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> normalBankAccountEditdataCompare(@Valid NormalBankAccountListREQ req) {
        R<PageR<NormalBankAccountListRSP>> detail = normalBankAccountApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.normalBankAccount.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpShareholderInfoEditdataCompare(@Valid CorpShareholderInfoListREQ req) {
        R<PageR<CorpShareholderInfoListRSP>> detail = corpShareholderInfoApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.corpShareholderInfo.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpRelatedEnterpriseEditdataCompare(@Valid CorpRelatedEnterpriseListREQ req) {
        R<PageR<CorpRelatedEnterpriseListRSP>> detail = corpRelatedEnterpriseApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.corpRelatedEnterprise.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpContactEditdataCompare(@Valid CorpContactInfoListREQ req) {
        R<PageR<CorpContactInfoListRSP>> detail = corpContactInfoApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.corpContactInfo.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpBondInfoEditdataCompare(@Valid CorpBondInfoListREQ req) {
        R<PageR<CorpBondInfoListRSP>> detail = corpBondInfoApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.corpBondInfo.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpBankAccountEditdataCompare(@Valid CorpBankAccountListREQ req) {
        R<PageR<CorpBankAccountListRSP>> detail = corpBankAccountApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.corpBankAccount.name(), detail, req.getClientId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpAddressEditdataCompare(@Valid CorpAddressInfoListREQ req) {
        PageR<CorpAddressInfoListRSP> addressPage = StringUtils.isBlank(req.getVersion()) ? corpAddressInfoApi.list(req).getData() : corpAddressInfoLibService.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.corpAddressInfo.name(), R.ok(addressPage), req.getClientId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> projReviewBaseInfoEditdataCompare(@Valid ProjReviewBaseInfoDetailREQ req) {
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            throw new MithrasException("流程id为必填参数");
        }
        ProjReviewBaseInfoDetailRSP detail = StringUtils.isBlank(req.getVersion()) ? projReviewBaseInfoService.detail(req.getId(), null) : projReviewBaseInfoLibService.detail(req.getId(), req.getVersion());
        return R.ok(compare(CompareFactoryEnum.projReviewBaseInfo.name(), R.ok(detail), req.getId(), req.getVersion(), VersionTypeConstants.NORMAL));
    }

    @Override
    public R<ProjReviewPriceCompareRSP> projReviewPriceEditdataCompare(@Valid ProjReviewPriceDetailREQ req) {
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            throw new MithrasException("流程id为必填参数");
        }
        ProjReviewPriceDetailRSP detail = StringUtils.isBlank(req.getVersion()) ?
                projReviewPriceService.detail(req.getId()) :
                projReviewPriceService.detailVersion(req.getId(), req.getVersion());
        int versionType = VersionTypeConstants.NORMAL;
        ProjReviewPriceCompareRSP rsp = new ProjReviewPriceCompareRSP();
        if (detail.getLeasePriceDetailRSP() != null) {
            rsp.setLeasePriceDetailRSP(compare(CompareFactoryEnum.projReviewLeasePrice.name(), R.ok(detail.getLeasePriceDetailRSP()), req.getId(), req.getVersion(), versionType));
        }
        if (detail.getAocPriceDetailRSP() != null) {
            rsp.setAocPriceDetailRSP(compare(CompareFactoryEnum.projReviewAocPrice.name(), R.ok(detail.getAocPriceDetailRSP()), req.getId(), req.getVersion(), versionType));
        }
        if (detail.getFactoringPriceDetailRSP() != null) {
            rsp.setFactoringPriceDetailRSP(compare(CompareFactoryEnum.projReviewFactoringPrice.name(), R.ok(detail.getFactoringPriceDetailRSP()), req.getId(), req.getVersion(), versionType));
        }
        return R.ok(rsp);
    }

    @Override
    public R<List<Map<String, DiffValue>>> projReviewCashFlowPlanEditdataCompare(@Valid ProjReviewCashFlowPlanListREQ req) {
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            throw new MithrasException("流程id为必填参数");
        }
        R<List<ProjReviewCashFlowPlanListRSP>> list = projReviewCashFlowPlanApi.list(req);
        return R.ok(compareList(CompareFactoryEnum.projReviewCashFlowPlan.name(), list, req.getId(), req.getVersion(), VersionTypeConstants.NORMAL));
    }

    @Override
    public R<Map<String, DiffValue>> projPricingBaseInfoEditdataCompare(ProjPricingBaseInfoDetailREQ req) {
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            throw new MithrasException("流程id为必填参数");
        }
        ProjPricingBaseInfoDetailRSP detail = StringUtils.isBlank(req.getVersion()) ? projPricingBaseInfoService.detail(req.getId(), null) : projPricingBaseInfoLibService.detail(req.getId(), req.getVersion());
        return R.ok(compare(CompareFactoryEnum.projPricingBaseInfo.name(), R.ok(detail), req.getId(), req.getVersion(), VersionTypeConstants.NORMAL));
    }

    @Override
    public R<ProjReviewPriceCompareRSP> projPricingPriceEditdataCompare(ProjPricingPriceDetailREQ req) {
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            throw new MithrasException("流程id为必填参数");
        }
        ProjPricingPriceDetailRSP detail = StringUtils.isBlank(req.getVersion()) ?
                projPricingPriceService.detail(req.getId()) :
                projPricingPriceService.detailVersion(req.getId(), req.getVersion());
        int versionType = VersionTypeConstants.NORMAL;
        ProjReviewPriceCompareRSP rsp = new ProjReviewPriceCompareRSP();
        if (detail.getLeasePriceDetailRSP() != null) {
            rsp.setLeasePriceDetailRSP(compare(CompareFactoryEnum.projPricingLeasePrice.name(), R.ok(detail.getLeasePriceDetailRSP()), req.getId(), req.getVersion(), versionType));
        }
        if (detail.getAocPriceDetailRSP() != null) {
            rsp.setAocPriceDetailRSP(compare(CompareFactoryEnum.projPricingAocPrice.name(), R.ok(detail.getAocPriceDetailRSP()), req.getId(), req.getVersion(), versionType));
        }
        if (detail.getFactoringPriceDetailRSP() != null) {
            rsp.setFactoringPriceDetailRSP(compare(CompareFactoryEnum.projPricingFactoringPrice.name(), R.ok(detail.getFactoringPriceDetailRSP()), req.getId(), req.getVersion(), versionType));
        }
        return R.ok(rsp);
    }

    @Override
    public R<List<Map<String, DiffValue>>> projPricingCashFlowPlanEditdataCompare(ProjPricingCashFlowPlanListREQ req) {
        if (ObjectUtil.isEmpty(req.getProcessInstanceId())) {
            throw new MithrasException("流程id为必填参数");
        }
        R<List<ProjPricingCashFlowPlanListRSP>> list = projPricingCashFlowPlanApi.list(req);
        return R.ok(compareList(CompareFactoryEnum.projPricingCashFlowPlan.name(), list, req.getId(), req.getVersion(), VersionTypeConstants.NORMAL));
    }

    @Override
    public R<Map<String, Object>> paymentBaseInfoEditDataCompare(PaymentDetailReq req) {
        R<PaymentDetailRsp> detail = paymentApi.detail(req);
        List<PlanedDetailDto> planedDetails = detail.getData().getPlanedDetails();
        detail.getData().setPlanedDetails(null);
        Map<String, DiffValue> baseInfoDiff = compare(CompareFactoryEnum.paymentBaseInfo.name(), detail, req.getId(), req.getVersion());
        List<Map<String, DiffValue>> planedDetailDiff = compareList(CompareFactoryEnum.paymentPlanedDetail.name(), R.ok(planedDetails), req.getId(), req.getVersion());
        PaymentQuestionListReq paymentQuestionListReq = new PaymentQuestionListReq();
        paymentQuestionListReq.setPaymentId(req.getId());
//        List<Map<String, DiffValue>> questions = compareList(CompareFactoryEnum.paymentQuestionnaire.name(), paymentQuestionnaireApi.list(paymentQuestionListReq), req.getId(), req.getVersion());
        Map<String, Object> res = new HashMap<>();
        res.put("baseInfo", baseInfoDiff);
        res.put("planedDetail", planedDetailDiff);
//        res.put("questions", questions);
        return R.ok(res);
    }

    @Override
    public R<List<Map<String, DiffValueList>>> listActualRentCompare(@Valid ContractSingleIdREQ req) {
        R<List<ContractRentActualListRSP>> list = contractRentApi.listActualRent(req);
        List<Map<String, DiffValue>> list1 = compareList(CompareFactoryEnum.contractActualRent.name(), list, req.getContractId(), req.getVersion());
        List<Map<String, DiffValueList>> diffValueList = null;
        if (CollectionUtil.isNotEmpty(list1)) {
            diffValueList = diffValue2diffValueList(list1);
            Map<Long, List<ContractRentActualListRSP>> collect = list.getData().stream().collect(Collectors.groupingBy(ListBaseRSP::getId));
            diffValueList.forEach(o -> {
                List<ContractRentActualRSP> actualRSPS = tableData2ContractRentActualRSP(collect.get(o.get("id").getValue()).get(0));
                List<Map<String, DiffValue>> list2 = compareList(CompareFactoryEnum.contractActualRentChildren.name(), R.ok(actualRSPS), req.getContractId(), req.getVersion());
                if (CollectionUtil.isNotEmpty(list2)) {
                    for (Map<String, DiffValue> l : list2) {
                        l.remove("receiptId");
                    }
                }
                o.get("rentActualList").setLsitMap(list2);
            });
        }
        return R.ok(diffValueList);
    }

    private List<ContractRentActualRSP> tableData2ContractRentActualRSP(ContractRentActualListRSP rsp) {
        List<ContractRentActualRSP> res = new LinkedList<>();
        rsp.getRentActualList().forEach(data -> {
            ContractRentActualRSP contractRentActualRSP = new ContractRentActualRSP();
            contractRentActualRSP.setReceiptId(rsp.getReceiptId());
            contractRentActualRSP.setCashFlowCode(data.getCashFlowCode());
            contractRentActualRSP.setDate(data.getDate());
            contractRentActualRSP.setPhase(data.getPhase());
            contractRentActualRSP.setRent(data.getRent());
            contractRentActualRSP.setPrincipal(data.getPrincipal());
            contractRentActualRSP.setInterest(data.getInterest());
            contractRentActualRSP.setRemainingPrincipal(data.getRemainingPrincipal());
            contractRentActualRSP.setId(data.getId());
            res.add(contractRentActualRSP);
        });

        return res;

    }

    @Override
    public R<List<Map<String, DiffValue>>> accountListCompare(@Valid ContractAccountListREQ req) {
        List<ContractAccountListRSP> list = StringUtils.isBlank(req.getVersion()) ? contractAccountService.list(req) : contractAccountLibService.list(req);
        // 确定需要使用的工厂名称
        String factoryName = this.ensureAccountFactoryName(req.getAccountUse());
        return R.ok(compareList(factoryName, R.ok(list), req.getContractId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> tenantryListCompare(@Valid ContractIdListREQ req) {
        List<ContractTenantryListRSP> list = StringUtils.isBlank(req.getVersion()) ? contractTenantryService.list(req) : contractTenantryLibService.list(req);
        return R.ok(compareList(CompareFactoryEnum.contractTenantry.name(), R.ok(list), req.getContractId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> pledgeListCompare(@Valid ContractIdListREQ req) {
        List<ContractPledgeListRSP> list = StringUtils.isBlank(req.getVersion()) ? contractPledgeService.list(req) : contractPledgeLibService.list(req);
        return R.ok(compareList(CompareFactoryEnum.contractPledge.name(), R.ok(list), req.getContractId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> mortgageListCompare(@Valid ContractIdListREQ req) {
        List<ContractMortgageListRSP> list = StringUtils.isBlank(req.getVersion()) ? contractMortgageService.list(req) : contractMortgageLibService.list(req);
        return R.ok(compareList(CompareFactoryEnum.contractMortgage.name(), R.ok(list), req.getContractId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> guarantorListCompare(@Valid ContractIdListREQ req) {
        List<ContractGuarantorListRSP> list = StringUtils.isBlank(req.getVersion()) ? contractGuarantorService.list(req) : contractGuarantorLibService.list(req);
        return R.ok(compareList(CompareFactoryEnum.contractGuarantor.name(), R.ok(list), req.getContractId(), req.getVersion()));
    }

    @Override
    public R<Map<String, Object>> listLeaseItemCompare(@Valid ContractLeaseItemListREQ req) {
        ContractBaseInfoLib latest = contractBaseInfoLibService.getLatest(req.getContractId());
        Map<String, Object> map = new HashMap<>();
        if (Objects.isNull(latest)) {
            map.put("changeFlag", Boolean.TRUE);
        } else {
            // 比对编辑区和版本区的id是否一致，因为是全量删除后插入的，所以id不一致肯定就发生了变化
            LambdaQueryWrapper<ContractLeaseItem> query = Wrappers.lambdaQuery();
            query.eq(ContractLeaseItem::getContractId, req.getContractId());
            query.orderByAsc(ContractLeaseItem::getId);
            query.last(StringUtil.mysqlLimitOne());
            ContractLeaseItem contractLeaseItem = contractLeaseItemService.getOne(query);
            LambdaQueryWrapper<ContractLeaseItemLib> queryLib = Wrappers.lambdaQuery();
            queryLib.eq(ContractLeaseItem::getContractId, req.getContractId());
            queryLib.eq(ContractLeaseItemLib::getVersion, latest.getVersion());
            queryLib.orderByAsc(ContractLeaseItem::getId);
            queryLib.last(StringUtil.mysqlLimitOne());
            ContractLeaseItemLib contractLeaseItemLib = contractLeaseItemLibService.getOne(queryLib);
            if (Objects.nonNull(contractLeaseItem) && Objects.nonNull(contractLeaseItemLib)) {
                map.put("changeFlag", !Objects.equals(contractLeaseItem.getId(), contractLeaseItemLib.getOriginId()));
            } else if (Objects.isNull(contractLeaseItem) && Objects.isNull(contractLeaseItemLib)) {
                map.put("changeFlag", Boolean.FALSE);
            } else {
                map.put("changeFlag", Boolean.TRUE);
            }
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, DiffValueList>> listEstimateRentCompare(@Valid ContractSingleIdREQ req) {
        R<ContractRentEstimateListRSP> list = contractRentApi.listEstimateRent(req);
        ContractBaseInfoDetailREQ baseInfoDetailREQ = new ContractBaseInfoDetailREQ();
        baseInfoDetailREQ.setId(req.getContractId());
        ContractBaseInfoDetailRSP detail = contractBaseInfoService.detail(baseInfoDetailREQ);
        Map<String, DiffValue> baseInfoCompare = compare(CompareFactoryEnum.contractBaseInfo.name(), R.ok(detail), req.getContractId(), req.getVersion());
        List<ContractRentActualRSP> actualRSPS = tableData2ContractRentActualRSP(list.getData());
        Map<String, DiffValueList> diffValueList = new HashMap<>();
        if (CollectionUtil.isNotEmpty(baseInfoCompare)) {
            DiffValueList diffValue = new DiffValueList();
            diffValue.setValue(baseInfoCompare.get("estimatedLeaseDate").getValue());
            diffValue.setBeforeValue(baseInfoCompare.get("estimatedLeaseDate").getBeforeValue());
            diffValue.setIsChange(baseInfoCompare.get("estimatedLeaseDate").getIsChange());
            diffValueList.put("planStartDate", diffValue);
            List<Map<String, DiffValue>> estimateRentCompare = compareList(CompareFactoryEnum.contractEstimateRent.name(), R.ok(actualRSPS), req.getContractId(), req.getVersion());
            if (CollectionUtil.isNotEmpty(estimateRentCompare)) {
                for (Map<String, DiffValue> l : estimateRentCompare) {
                    l.remove("receiptId");
                    l.remove("cashFlowCode");
                }
            }
            DiffValueList rentEstimateList = new DiffValueList();
            rentEstimateList.setLsitMap(estimateRentCompare);
            diffValueList.put("rentEstimateList", rentEstimateList);
        }
        return R.ok(diffValueList);
    }

    private List<ContractRentActualRSP> tableData2ContractRentActualRSP(ContractRentEstimateListRSP rsp) {
        List<ContractRentActualRSP> res = new LinkedList<>();
        rsp.getRentEstimateList().forEach(data -> {
            ContractRentActualRSP contractRentActualRSP = new ContractRentActualRSP();
            contractRentActualRSP.setDate(data.getDate());
            contractRentActualRSP.setPhase(data.getPhase());
            contractRentActualRSP.setRent(data.getRent());
            contractRentActualRSP.setPrincipal(data.getPrincipal());
            contractRentActualRSP.setInterest(data.getInterest());
            contractRentActualRSP.setRemainingPrincipal(data.getRemainingPrincipal());
            contractRentActualRSP.setId(data.getId());
            res.add(contractRentActualRSP);
        });
        return res;
    }

    @Override
    public R<ContractPriceDetailCompareRSP> priceDetailCompare(@Valid ContractPriceDetailREQ req) {
        ContractPriceDetailRSP detail = StringUtils.isBlank(req.getVersion()) ? priceService.detail(req) : priceService.detailVersion(req.getContractId(), req.getVersion());
        ContractPriceDetailCompareRSP rsp = new ContractPriceDetailCompareRSP();
        if (detail.getLeasePriceModifyRSP() != null) {
            rsp.setLeasePriceModifyRSP(compare(CompareFactoryEnum.contractLeasePrice.name(), R.ok(detail.getLeasePriceModifyRSP()), req.getContractId(), req.getVersion()));
        }
        if (detail.getAocPriceRSP() != null) {
            rsp.setAocPriceRSP(compare(CompareFactoryEnum.contractAocPrice.name(), R.ok(detail.getAocPriceRSP()), req.getContractId(), req.getVersion()));
        }
        if (detail.getFactoringPriceRSP() != null) {
            rsp.setFactoringPriceRSP(compare(CompareFactoryEnum.contractFactoringPrice.name(), R.ok(detail.getFactoringPriceRSP()), req.getContractId(), req.getVersion()));
        }
        return R.ok(rsp);
    }

    @Override
    public R<Map<String, DiffValue>> baseInfoDetailCompare(@Valid ContractBaseInfoDetailREQ req) {
        ContractBaseInfoDetailRSP detail = StringUtils.isBlank(req.getBusinessVersion()) ? contractBaseInfoService.detail(req) : contractBaseInfoLibService.detail(req.getId(), req.getBusinessVersion());
        detail.setStockContractFlag(contractBaseInfoService.getStockContractFlag(detail.getProjReviewId()));
        return R.ok(compare(CompareFactoryEnum.contractBaseInfo.name(), R.ok(detail), req.getId(), req.getBusinessVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> getLatestSettlePlanCompare(@Valid ContractSettlePlanDetailREQ req) {
        R<ContractSettlePlanDetailRSP> detail = contractSettleApi.getLatestSettlePlan(req);
        return R.ok(compare(CompareFactoryEnum.contractSettlePlan.name(), detail, req.getContractId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> groupCreditEstablishBaseInfoDetailCompare(GroupCreditEstablishBaseInfoDetailREQ req) {
        GroupCreditEstablishBaseInfoDetailRSP detail = groupCreditEstablishBaseInfoService.detail(req.getGroupCreditEstablishId(), req.getVersion());
        return R.ok(compare(CompareFactoryEnum.groupCreditEstablishBaseInfo.name(), R.ok(detail), req.getGroupCreditEstablishId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> groupCreditReviewBaseInfoDetailCompare(GroupCreditReviewBaseInfoDetailREQ req) {
        GroupCreditReviewBaseInfoDetailRSP detail = groupCreditReviewBaseInfoService.detail(req.getGroupCreditReviewId(), req.getVersion());
        return R.ok(compare(CompareFactoryEnum.groupCreditReviewBaseInfo.name(), R.ok(detail), req.getGroupCreditReviewId(), req.getVersion()));

    }

    @Resource
    private NewFtpBaseInfoApi newFtpBaseInfoApi;
    @Resource
    private NewFtpMonthlyDeductionDraftApi newFtpMonthlyDeductionApi;
    @Resource
    private NewFtpMonthlyGuidanceExtDraftApi newFtpMonthlyGuidanceExtApi;

    @Override
    public R<List<Map<String, DiffValue>>> compareDesc(NewFtpDetailReq req) {
        R<List<NewFtpDescriptionTextListRsp>> listR = newFtpBaseInfoApi.listDesc(req);
        return R.ok(compareList(CompareFactoryEnum.newFtpDescriptionText.name(), listR, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> compareDeduction(NewFtpDetailReq req) {
        NewFtpMonthlyDeductionListREQ newFtpMonthlyDeductionListREQ = new NewFtpMonthlyDeductionListREQ();
        newFtpMonthlyDeductionListREQ.setMainId(req.getMainId());
        R<List<NewFtpMonthlyDeductionListRSP>> list = newFtpMonthlyDeductionApi.list(newFtpMonthlyDeductionListREQ);
        return R.ok(compareList(CompareFactoryEnum.newFtpDeduction.name(), list, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> compareExt(NewFtpDetailReq req) {
        R<NewFtpMonthlyGuidanceExtDraftDetailRSP> detail = newFtpMonthlyGuidanceExtApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.newFtpMonthlyGuidanceExt.name(), detail, req.getMainId(), req.getVersion()));
    }

    @Resource
    private FtpQuarterlyGuidanceApi quarterlyGuidanceApi;

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyBasePricingCompare(FtpGuidanceIdReq req) {
        R<List<FtpQuarterlyBasePricingRsp>> listR = quarterlyGuidanceApi.detailPricingBase(req);
        return R.ok(compareList(CompareFactoryEnum.ftpQuarterlyBasePricing.name(), listR, req.getId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyCustomerPricingCompare(FtpGuidanceIdReq req) {
        R<List<FtpQuarterlyCustomerPrincipalPricingRsp>> listR = quarterlyGuidanceApi.detailPricingCustomer(req);
        return R.ok(compareList(CompareFactoryEnum.ftpQuarterlyCustomerPrincipalPricing.name(), listR, req.getId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyMonthPricingCompare(FtpGuidanceIdReq req) {
        R<List<FtpQuarterlyMonthPricingRsp>> listR = quarterlyGuidanceApi.detailPricingMonth(req);
        return R.ok(compareList(CompareFactoryEnum.ftpQuarterlyMonthPricing.name(), listR, req.getId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyEnterprisePricingCompare(FtpGuidanceIdReq req) {
        R<List<FtpQuarterlyEnterprisePricingRsp>> listR = quarterlyGuidanceApi.detailPricingEnterprise(req);
        return R.ok(compareList(CompareFactoryEnum.ftpQuarterlyEnterprisePricing.name(), listR, req.getId(), req.getVersion()));
    }

    @Resource
    private FtpMonthlyGuidanceApi monthlyGuidanceApi;

    @Override
    public R<Map<String, DiffValue>> ftpMonthlyGuidanceCompare(FtpGuidanceIdReq req) {
        R<FtpMonthlyGuidanceDetailRsp> detail = monthlyGuidanceApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.ftpMonthlyGuidance.name(), detail, req.getId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpMonthlyPricingCompare(FtpGuidanceIdReq req) {
        R<List<FtpMonthlyPricingRsp>> listR = monthlyGuidanceApi.detailPricing(req);
        return R.ok(compareList(CompareFactoryEnum.ftpMonthlyPricing.name(), listR, req.getId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpMonthlyValuationCompare(FtpGuidanceIdReq req) {
        R<List<FtpMonthlyValuationRsp>> listR = monthlyGuidanceApi.detailValuation(req);
        return R.ok(compareList(CompareFactoryEnum.ftpMonthlyValuation.name(), listR, req.getId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> fundFinancingBaseInfoDetailCompare(@Valid SingleFinancingIdREQ req) {
        R<FundFinancingBaseInfoDetailRSP> detailR = fundFinancingBaseInfoApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.fundFinancingBaseInfo.name(), detailR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingPledgeList(@Valid FundFinancingPledgeListREQ req) {
        R<List<FundFinancingPledgeListRSP>> listR = fundFinancingPledgeApi.list(req);
        return R.ok(compareList(CompareFactoryEnum.fundFinancingPledge.name(), listR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> fundFinancingPlanDetail(@Valid SingleFinancingIdREQ req) {
        R<FundFinancingPlanDetailRSP> detailR = fundFinancingPlanApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.fundFinancingPlan.name(), detailR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingEstimateList(@Valid SingleFinancingIdREQ req) {
        R<List<FundFinancingRepayEstimateListRSP>> listR = fundFinancingRepayEstimateApi.list(req);
        return R.ok(compareList(CompareFactoryEnum.fundFinancingRepayEstimate.name(), listR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingActualList(@Valid SingleFinancingIdREQ req) {
        R<List<FundFinancingRepayActualListRSP>> listR = fundFinancingRepayActualApi.list(req);
        return R.ok(compareList(CompareFactoryEnum.fundFinancingRepayActual.name(), listR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingCollectionAccountList(@Valid FundFinancingCollectAccountListREQ req) {
        R<List<FundFinancingCollectAccountListRSP>> listR = fundFinancingCollectAccountApi.list(req);
        return R.ok(compareList(CompareFactoryEnum.fundFinancingCollectAccount.name(), listR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingPayAccountList(@Valid FundFinancingPayAccountListREQ req) {
        R<List<FundFinancingPayAccountListRSP>> listR = fundFinancingPayAccountApi.list(req);
        if(ObjectUtil.isEmpty(listR.getData())){
            return R.ok();
        }
        return R.ok(compareList(CompareFactoryEnum.fundFinancingPayAccount.name(), listR, req.getFinancingId(), req.getVersion()));
    }

    @Override
    public R<Map<String, DiffValue>> policyInfoDetail(@Valid PolicyInfoDetailCompareREQ req) {
        PolicyInfoDetailREQ detail = new PolicyInfoDetailREQ();
        detail.setId(req.getId());
        R<PolicyInfoDetailRSP> detailR = StringUtils.isBlank(req.getVersion()) ? policyInfoApi.detail(detail) : R.ok(policyInfoLibService.detail(req.getId() ,req.getVersion()));
        return R.ok(compare(CompareFactoryEnum.policyInfo.name(), detailR, req.getId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpTreasuryBondYield(@Valid NewFtpCommonDetailReq req) {
        R<PageR<NewFtpDetailTreasuryBondYieldListRSP>> listR = newFtpTreasuryBondYieldController.treasuryBondYield(req);
        if(ObjectUtil.isEmpty(listR.getData())){
            return R.ok();
        }
        return R.ok(pageCompare(CompareFactoryEnum.treasuryBondYield.name(), listR, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpShiborInterest(@Valid NewFtpCommonDetailReq req) {
        R<PageR<NewFtpDetailTreasuryBondYieldListRSP>> listR = newFtpShiborInterestRateController.shiborInterest(req);
        if(ObjectUtil.isEmpty(listR.getData())){
            return R.ok();
        }
        return R.ok(pageCompare(CompareFactoryEnum.shiborInterestRate.name(), listR, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpLprPricingList(@Valid NewFtpCommonDetailReq req) {
        R<PageR<NewFtpDetailLprPricingListRSP>> listR = newFtpLprPricingController.lprPricingList(req);
        if(ObjectUtil.isEmpty(listR.getData())){
            return R.ok();
        }
        return R.ok(pageCompare(CompareFactoryEnum.lprPricing.name(), listR, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpGuaranteeCostPricingList(@Valid NewFtpCommonDetailReq req) {
        R<PageR<NewFtpGuaranteeCostPricingListRSP>> listR = newFtpGuaranteeCostPricingDraftController.listDraft(req);
        return R.ok(pageCompare(CompareFactoryEnum.guaranteeCostPricing.name(), listR, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpFinancingCostDraftList(@Valid NewFtpCommonDetailReq req) {
        R<PageR<NewFtpFinancingCostPricingListRSP>> listR = newFtpFinancingCostPricingDraftController.draftList(req);
        return R.ok(pageCompare(CompareFactoryEnum.financingCost.name(), listR, req.getMainId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> kpiProjectdistributionCompare(@Valid KpiProjectDistributionWeightREQ req) {
        R<KpiProjectDistributionWeightRSP> list = kpiProjectDistributionWeightApplicationService.detail(req);
        if(ObjectUtil.isEmpty(list) || ObjectUtil.isEmpty(list.getData())) {
            return R.ok();
        }
        return R.ok(compareList(CompareFactoryEnum.kpiProjectDistributionWeight.name(), R.ok(list.getData().getWeightInfoList()), req.getProjectDistributionId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fileListCompare(FileListREQ req) {
        return R.ok(fileService.fileListCompare(req));
    }

    @Override
    public R<List<Pair<String, List<Map<String, DiffValue>>>>> fileListGroupCompare(FileListREQ req) {
        return R.ok(fileService.fileListGroupCompare(req));
    }

    @Override
    public R<List<Pair<String, List<Map<String, DiffValue>>>>> fileListGroupCompareV2(FileListREQ req) {
        return R.ok(fileService.fileListGroupCompareV2(req));
    }

    @Override
    public R<List<Pair<String, List<DiffFile>>>> fileListVersionCompare(FileListVersionREQ req) {
        return R.ok(fileService.fileListVersionCompare(req));
    }

    @Override
    public R<Map<String, DiffValue>> fundReceiptRepayBaseInfoDetailCompare(FundReceiptRepayBaseInfoDetailREQ req) {
        R<FundReceiptRepayBaseInfoDetailRSP> detail = fundReceiptRepayBaseInfoApi.detail(req);
        return R.ok(compare(CompareFactoryEnum.fundReceiptRepayBaseInfo.name(), detail, req.getId(), req.getVersion()));
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundReceiptRepayBaseInfoPledgeDetailCompare(FundReceiptRepayBaseInfoDetailREQ req) {
        R<List<FundFinancingPledgeListRSP>> listR = fundReceiptRepayBaseInfoApi.pledgeDetail(req);
        return R.ok(compareList(CompareFactoryEnum.fundReceiptRepayPledge.name(), listR, req.getId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayBorrowingListCompare(FundReceiptRepayBorrowingListREQ req) {
        R<PageR<FundReceiptRepayBorrowingListRSP>> pageR = fundReceiptRepayBorrowingApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.fundReceiptRepayBorrowing.name(), pageR, req.getReceiptRepayId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayCashDepositListCompare(FundReceiptRepayCashDepositListREQ req) {
        R<PageR<FundReceiptRepayCashDepositListRSP>> pageR = fundReceiptRepayCashDepositApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.fundReceiptRepayCashDeposit.name(), pageR, req.getReceiptRepayId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayCashFlowListCompare(FundReceiptRepayCashFlowListREQ req) {
        R<PageR<FundReceiptRepayCashFlowListRSP>> pageR = fundReceiptRepayCashFlowApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.fundReceiptRepayCashFlow.name(), pageR, req.getReceiptRepayId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayExpenseListCompare(FundReceiptRepayExpenseListREQ req) {
        R<PageR<FundReceiptRepayExpenseListRSP>> pageR = fundReceiptRepayExpenseApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.fundReceiptRepayExpense.name(), pageR, req.getReceiptRepayId(), req.getVersion()));
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> list(FundReceiptAccountListREQ req) {
        R<PageR<FundReceiptAccountListRSP>> pageR = fundReceiptAccountApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.fundReceiptRepayReceiptAccount.name(), pageR, req.getReceiptRepayId(), req.getVersion()));

    }

    @Override
    public R<PageR<Map<String, DiffValue>>> list(FundRepayAccountListREQ req) {
        R<PageR<FundRepayAccountListRSP>> pageR = fundRepayAccountApi.list(req);
        return R.ok(pageCompare(CompareFactoryEnum.fundReceiptRepayRepayAccount.name(), pageR, req.getReceiptRepayId(), req.getVersion()));

    }


    private Map<String, DiffValue> compare(String factoryType, R detail, Long mainId, String version) {
        return compare(factoryType, detail, mainId, version, VersionTypeConstants.NORMAL);
    }

    private Map<String, DiffValue> compare(String factoryType, R detail, Long mainId, String version, Integer versionType) {
        if (detail.isSuccess()) {
            Map<String, DiffValue> rsp = factoryCreator.buildFactory(factoryType)
                    .createCompare(Objects.nonNull(detail.getData()) ? Collections.singletonList(detail.getData()) : new ArrayList(), version).compareone(mainId, versionType);

            return rsp;
        }
        return null;
    }

    private List<Map<String, DiffValue>> compareList(String factoryType, R<? extends List> detail, Long mainId, String version) {
        return compareList(factoryType, detail, mainId, version, VersionTypeConstants.NORMAL);
    }

    private List<Map<String, DiffValue>> compareList(String factoryType, R<? extends List> detail, Long mainId, String version, Integer versionType) {
        if (detail.isSuccess()) {
            List<Map<String, DiffValue>> rspList = factoryCreator.buildFactory(factoryType)
                    .createCompare(detail.getData(), version).comparelist(mainId, versionType);
            return rspList;
        }
        return null;
    }

    private PageR<Map<String, DiffValue>> pageCompare(String factoryType, R<? extends PageR> detail, Long mainId, String version) {
        if (detail.isSuccess() && Objects.nonNull(detail.getData())) {
            PageR data = detail.getData();
            List<Map<String, DiffValue>> rspList = factoryCreator.buildFactory(factoryType)
                    .createCompare(data.getList(), version).comparelist(mainId);
            data.setList(rspList);
            return data;
        }
        return null;
    }

    private List<Map<String, DiffValueList>> diffValue2diffValueList(List<Map<String, DiffValue>> source) {
        List<Map<String, DiffValueList>> res = new LinkedList<>();
        source.forEach(o -> {
            Map<String, DiffValueList> tmp = new HashMap<>();
            for (String key : o.keySet()) {
                DiffValueList diffValueList = new DiffValueList();
                diffValueList.setValue(o.get(key).getValue());
                diffValueList.setBeforeValue(o.get(key).getBeforeValue());
                diffValueList.setIsChange(o.get(key).getIsChange());
                tmp.put(key, diffValueList);
            }
            res.add(tmp);
        });
        return res;
    }

    private String ensureAccountFactoryName(String accountUse) {
        ContractAccountUseEnum contractAccountUse = ContractAccountUseEnum.find(accountUse);
        if (Objects.isNull(contractAccountUse)) {
            throw new MithrasException("未定义的账户用途");
        }
        switch (contractAccountUse) {
            case BLHK:
                return CompareFactoryEnum.contractAccountBLHK.name();
            case BLSK:
                return CompareFactoryEnum.contractAccountBLSK.name();
            case ZRHK:
                return CompareFactoryEnum.contractAccountZRHK.name();
            case ZRSK:
                return CompareFactoryEnum.contractAccountZRSK.name();
            case ZZSK:
                return CompareFactoryEnum.contractAccountZZSK.name();
            default:
                return CompareFactoryEnum.contractAccountZLSK.name();
        }
    }
}
