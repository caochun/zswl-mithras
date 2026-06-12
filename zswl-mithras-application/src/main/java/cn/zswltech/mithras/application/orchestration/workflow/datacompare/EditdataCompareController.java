package cn.zswltech.mithras.application.orchestration.workflow.datacompare;

import cn.zswltech.mithras.api.datacompare.EditdataCompareApi;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentDetailReq;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListREQ;
import cn.zswltech.mithras.dto.client.commerceinfo.CorpCommerceInfoDetailREQ;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBankAccountListREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.client.normal.NormalSpouseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.shareholder.CorpShareholderInfoListREQ;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import cn.zswltech.mithras.dto.contract.account.ContractAccountListREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.leaseitem.ContractLeaseItemListREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.settle.ContractSettlePlanDetailREQ;
import cn.zswltech.mithras.dto.datacompare.ContractPriceDetailCompareRSP;
import cn.zswltech.mithras.dto.datacompare.ProjEstablishPriceCompareRSP;
import cn.zswltech.mithras.dto.datacompare.ProjReviewPriceCompareRSP;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.ext.FileListVersionREQ;
import cn.zswltech.mithras.dto.ftp.FtpGuidanceIdReq;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.collectaccount.FundFinancingCollectAccountListREQ;
import cn.zswltech.mithras.dto.fund.financing.payaccount.FundFinancingPayAccountListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListREQ;
import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.groupcreditreview.baseinfo.GroupCreditReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpCommonDetailReq;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailReq;
import cn.zswltech.mithras.dto.policy.PolicyInfoDetailCompareREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projpricing.cashflowplan.ProjPricingCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailREQ;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.ProjReviewCashFlowPlanListREQ;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailREQ;
import cn.zswltech.mithras.dto.version.DiffFile;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.dto.version.DiffValueList;
import java.util.List;
import java.util.Map;

@RestController
public class EditdataCompareController implements EditdataCompareApi {
    @Resource
    private EditdataCompareApplicationService editdataCompareApplicationService;

    @Override
    public R<Map<String, DiffValue>> projBaseInfoEditdataCompare(ProjEstablishBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.projBaseInfoEditdataCompare(req);
    }

    @Override
    public R<ProjEstablishPriceCompareRSP> projPriceEditdataCompare(ProjEstablishPriceDetailREQ req) {
        return editdataCompareApplicationService.projPriceEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> normalSpouseEditdataCompare(NormalSpouseListREQ req) {
        return editdataCompareApplicationService.normalSpouseEditdataCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> normalBaseInfoEditdataCompare(NormalBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.normalBaseInfoEditdataCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> corpCommerceEditdataCompare(CorpCommerceInfoDetailREQ req) {
        return editdataCompareApplicationService.corpCommerceEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> normalBankAccountEditdataCompare(NormalBankAccountListREQ req) {
        return editdataCompareApplicationService.normalBankAccountEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpShareholderInfoEditdataCompare(CorpShareholderInfoListREQ req) {
        return editdataCompareApplicationService.corpShareholderInfoEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpRelatedEnterpriseEditdataCompare(CorpRelatedEnterpriseListREQ req) {
        return editdataCompareApplicationService.corpRelatedEnterpriseEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpContactEditdataCompare(CorpContactInfoListREQ req) {
        return editdataCompareApplicationService.corpContactEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpBondInfoEditdataCompare(CorpBondInfoListREQ req) {
        return editdataCompareApplicationService.corpBondInfoEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpBankAccountEditdataCompare(CorpBankAccountListREQ req) {
        return editdataCompareApplicationService.corpBankAccountEditdataCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> corpAddressEditdataCompare(CorpAddressInfoListREQ req) {
        return editdataCompareApplicationService.corpAddressEditdataCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> projReviewBaseInfoEditdataCompare(ProjReviewBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.projReviewBaseInfoEditdataCompare(req);
    }

    @Override
    public R<ProjReviewPriceCompareRSP> projReviewPriceEditdataCompare(ProjReviewPriceDetailREQ req) {
        return editdataCompareApplicationService.projReviewPriceEditdataCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> projReviewCashFlowPlanEditdataCompare(ProjReviewCashFlowPlanListREQ req) {
        return editdataCompareApplicationService.projReviewCashFlowPlanEditdataCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> projPricingBaseInfoEditdataCompare(ProjPricingBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.projPricingBaseInfoEditdataCompare(req);
    }

    @Override
    public R<ProjReviewPriceCompareRSP> projPricingPriceEditdataCompare(ProjPricingPriceDetailREQ req) {
        return editdataCompareApplicationService.projPricingPriceEditdataCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> projPricingCashFlowPlanEditdataCompare(ProjPricingCashFlowPlanListREQ req) {
        return editdataCompareApplicationService.projPricingCashFlowPlanEditdataCompare(req);
    }

    @Override
    public R<Map<String, Object>> paymentBaseInfoEditDataCompare(PaymentDetailReq req) {
        return editdataCompareApplicationService.paymentBaseInfoEditDataCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValueList>>> listActualRentCompare(ContractSingleIdREQ req) {
        return editdataCompareApplicationService.listActualRentCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> accountListCompare(ContractAccountListREQ req) {
        return editdataCompareApplicationService.accountListCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> tenantryListCompare(ContractIdListREQ req) {
        return editdataCompareApplicationService.tenantryListCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> pledgeListCompare(ContractIdListREQ req) {
        return editdataCompareApplicationService.pledgeListCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> mortgageListCompare(ContractIdListREQ req) {
        return editdataCompareApplicationService.mortgageListCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> guarantorListCompare(ContractIdListREQ req) {
        return editdataCompareApplicationService.guarantorListCompare(req);
    }

    @Override
    public R<Map<String, Object>> listLeaseItemCompare(ContractLeaseItemListREQ req) {
        return editdataCompareApplicationService.listLeaseItemCompare(req);
    }

    @Override
    public R<Map<String, DiffValueList>> listEstimateRentCompare(ContractSingleIdREQ req) {
        return editdataCompareApplicationService.listEstimateRentCompare(req);
    }

    @Override
    public R<ContractPriceDetailCompareRSP> priceDetailCompare(ContractPriceDetailREQ req) {
        return editdataCompareApplicationService.priceDetailCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> baseInfoDetailCompare(ContractBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.baseInfoDetailCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> getLatestSettlePlanCompare(ContractSettlePlanDetailREQ req) {
        return editdataCompareApplicationService.getLatestSettlePlanCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> groupCreditEstablishBaseInfoDetailCompare(GroupCreditEstablishBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.groupCreditEstablishBaseInfoDetailCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> groupCreditReviewBaseInfoDetailCompare(GroupCreditReviewBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.groupCreditReviewBaseInfoDetailCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> compareDesc(NewFtpDetailReq req) {
        return editdataCompareApplicationService.compareDesc(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> compareDeduction(NewFtpDetailReq req) {
        return editdataCompareApplicationService.compareDeduction(req);
    }

    @Override
    public R<Map<String, DiffValue>> compareExt(NewFtpDetailReq req) {
        return editdataCompareApplicationService.compareExt(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyBasePricingCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpQuarterlyBasePricingCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyCustomerPricingCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpQuarterlyCustomerPricingCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyMonthPricingCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpQuarterlyMonthPricingCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpQuarterlyEnterprisePricingCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpQuarterlyEnterprisePricingCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> ftpMonthlyGuidanceCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpMonthlyGuidanceCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpMonthlyPricingCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpMonthlyPricingCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> ftpMonthlyValuationCompare(FtpGuidanceIdReq req) {
        return editdataCompareApplicationService.ftpMonthlyValuationCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fileListCompare(FileListREQ req) {
        return editdataCompareApplicationService.fileListCompare(req);
    }

    @Override
    public R<List<Pair<String, List<Map<String, DiffValue>>>>> fileListGroupCompare(FileListREQ req) {
        return editdataCompareApplicationService.fileListGroupCompare(req);
    }

    @Override
    public R<List<Pair<String, List<Map<String, DiffValue>>>>> fileListGroupCompareV2(FileListREQ req) {
        return editdataCompareApplicationService.fileListGroupCompareV2(req);
    }

    @Override
    public R<List<Pair<String, List<DiffFile>>>> fileListVersionCompare(FileListVersionREQ req) {
        return editdataCompareApplicationService.fileListVersionCompare(req);
    }

    @Override
    public R<Map<String, DiffValue>> fundReceiptRepayBaseInfoDetailCompare(FundReceiptRepayBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.fundReceiptRepayBaseInfoDetailCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundReceiptRepayBaseInfoPledgeDetailCompare(FundReceiptRepayBaseInfoDetailREQ req) {
        return editdataCompareApplicationService.fundReceiptRepayBaseInfoPledgeDetailCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayBorrowingListCompare(FundReceiptRepayBorrowingListREQ req) {
        return editdataCompareApplicationService.fundReceiptRepayBorrowingListCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayCashDepositListCompare(FundReceiptRepayCashDepositListREQ req) {
        return editdataCompareApplicationService.fundReceiptRepayCashDepositListCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayCashFlowListCompare(FundReceiptRepayCashFlowListREQ req) {
        return editdataCompareApplicationService.fundReceiptRepayCashFlowListCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> fundReceiptRepayExpenseListCompare(FundReceiptRepayExpenseListREQ req) {
        return editdataCompareApplicationService.fundReceiptRepayExpenseListCompare(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> list(FundReceiptAccountListREQ req) {
        return editdataCompareApplicationService.list(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> list(FundRepayAccountListREQ req) {
        return editdataCompareApplicationService.list(req);
    }

    @Override
    public R<Map<String, DiffValue>> fundFinancingBaseInfoDetailCompare(SingleFinancingIdREQ req) {
        return editdataCompareApplicationService.fundFinancingBaseInfoDetailCompare(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingPledgeList(FundFinancingPledgeListREQ req) {
        return editdataCompareApplicationService.fundFinancingPledgeList(req);
    }

    @Override
    public R<Map<String, DiffValue>> fundFinancingPlanDetail(SingleFinancingIdREQ req) {
        return editdataCompareApplicationService.fundFinancingPlanDetail(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingEstimateList(SingleFinancingIdREQ req) {
        return editdataCompareApplicationService.fundFinancingEstimateList(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingActualList(SingleFinancingIdREQ req) {
        return editdataCompareApplicationService.fundFinancingActualList(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingCollectionAccountList(FundFinancingCollectAccountListREQ req) {
        return editdataCompareApplicationService.fundFinancingCollectionAccountList(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> fundFinancingPayAccountList(FundFinancingPayAccountListREQ req) {
        return editdataCompareApplicationService.fundFinancingPayAccountList(req);
    }

    @Override
    public R<Map<String, DiffValue>> policyInfoDetail(PolicyInfoDetailCompareREQ req) {
        return editdataCompareApplicationService.policyInfoDetail(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpTreasuryBondYield(NewFtpCommonDetailReq req) {
        return editdataCompareApplicationService.newFtpTreasuryBondYield(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpShiborInterest(NewFtpCommonDetailReq req) {
        return editdataCompareApplicationService.newFtpShiborInterest(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpLprPricingList(NewFtpCommonDetailReq req) {
        return editdataCompareApplicationService.newFtpLprPricingList(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpGuaranteeCostPricingList(NewFtpCommonDetailReq req) {
        return editdataCompareApplicationService.newFtpGuaranteeCostPricingList(req);
    }

    @Override
    public R<PageR<Map<String, DiffValue>>> newFtpFinancingCostDraftList(NewFtpCommonDetailReq req) {
        return editdataCompareApplicationService.newFtpFinancingCostDraftList(req);
    }

    @Override
    public R<List<Map<String, DiffValue>>> kpiProjectdistributionCompare(KpiProjectDistributionWeightREQ req) {
        return editdataCompareApplicationService.kpiProjectdistributionCompare(req);
    }
}
