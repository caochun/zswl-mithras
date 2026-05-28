package cn.zswltech.mithras.api.datacompare;

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
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * @create: 2022-08-02
 **/

@Api(tags = "编辑区数据版本对比-接口")
public interface EditdataCompareApi {
    @ApiOperation("立项基本信息-对比")
    @PostMapping("/proj/establish/base/info/detail/compare")
    R<Map<String, DiffValue>> projBaseInfoEditdataCompare(@RequestBody @Valid ProjEstablishBaseInfoDetailREQ req);

    @ApiOperation("报价方案-对比")
    @PostMapping("/proj/establish/price/detail/compare")
    R<ProjEstablishPriceCompareRSP> projPriceEditdataCompare(@RequestBody @Valid ProjEstablishPriceDetailREQ req);

    @ApiOperation("自然人配偶信息-对比")
    @PostMapping("/normal/spouse/detail/compare")
    R<PageR<Map<String, DiffValue>>> normalSpouseEditdataCompare(@RequestBody @Valid NormalSpouseListREQ req);

    @ApiOperation("自然人基本信息-对比")
    @PostMapping("/normal/base/info/detai/compare")
    R<Map<String, DiffValue>> normalBaseInfoEditdataCompare(@RequestBody @Valid NormalBaseInfoDetailREQ req);

    @ApiOperation("法人工商信息-对比")
    @PostMapping("/corp/commerce/detail/compare")
    R<Map<String, DiffValue>> corpCommerceEditdataCompare(@RequestBody @Valid CorpCommerceInfoDetailREQ req);

    @ApiOperation("自然人银行卡信息-对比")
    @PostMapping("/normal/bank/account/list/compare")
    R<PageR<Map<String, DiffValue>>> normalBankAccountEditdataCompare(@RequestBody @Valid NormalBankAccountListREQ req);
    @ApiOperation("股东信息-对比")
    @PostMapping("/corp/shareholder/info/list/compare")
    R<PageR<Map<String, DiffValue>>> corpShareholderInfoEditdataCompare(@RequestBody @Valid CorpShareholderInfoListREQ req);

    @ApiOperation("关联企业-对比")
    @PostMapping("/corp/related/enterprise/list/compare")
    R<PageR<Map<String, DiffValue>>> corpRelatedEnterpriseEditdataCompare(@RequestBody @Valid CorpRelatedEnterpriseListREQ req);

    @ApiOperation("联系人信息-对比")
    @PostMapping("/corp/contact/list/compare")
    R<PageR<Map<String, DiffValue>>> corpContactEditdataCompare(@RequestBody @Valid CorpContactInfoListREQ req);

    @ApiOperation("发债及评级信息-对比")
    @PostMapping("/corp/bond/info/list/compare")
    R<PageR<Map<String, DiffValue>>> corpBondInfoEditdataCompare(@RequestBody @Valid CorpBondInfoListREQ req);

    @ApiOperation("银行账号信息-对比")
    @PostMapping("/corp/bank/account/list/compare")
    R<PageR<Map<String, DiffValue>>> corpBankAccountEditdataCompare(@RequestBody @Valid CorpBankAccountListREQ req);

    @ApiOperation("地址信息-对比")
    @PostMapping("/corp/address/list/compare")
    R<PageR<Map<String, DiffValue>>> corpAddressEditdataCompare(@RequestBody @Valid CorpAddressInfoListREQ req);

    @ApiOperation("项目评审基本信息-对比")
    @PostMapping("/proj/review/base/info/detail/compare")
    R<Map<String, DiffValue>> projReviewBaseInfoEditdataCompare(@RequestBody @Valid ProjReviewBaseInfoDetailREQ req);

    @ApiOperation("项目评审报价方案-对比")
    @PostMapping("/proj/review/price/detail/compare")
    R<ProjReviewPriceCompareRSP> projReviewPriceEditdataCompare(@RequestBody @Valid ProjReviewPriceDetailREQ req);

    @ApiOperation("获取现金流计划表-对比")
    @PostMapping("/proj/review/cashflowplan/list/compare")
    R<List<Map<String, DiffValue>>> projReviewCashFlowPlanEditdataCompare(@RequestBody @Valid ProjReviewCashFlowPlanListREQ req);

    @ApiOperation("项目定价基本信息-对比")
    @PostMapping("/proj/pricing/base/info/detail/compare")
    R<Map<String, DiffValue>> projPricingBaseInfoEditdataCompare(@RequestBody @Valid ProjPricingBaseInfoDetailREQ req);

    @ApiOperation("项目定价报价方案-对比")
    @PostMapping("/proj/pricing/price/detail/compare")
    R<ProjReviewPriceCompareRSP> projPricingPriceEditdataCompare(@RequestBody @Valid ProjPricingPriceDetailREQ req);

    @ApiOperation("获取定价现金流计划表-对比")
    @PostMapping("/proj/pricing/cashflowplan/list/compare")
    R<List<Map<String, DiffValue>>> projPricingCashFlowPlanEditdataCompare(@RequestBody @Valid ProjPricingCashFlowPlanListREQ req);

    @ApiOperation("付款申请-比对")
    @PostMapping("/payment/baseinfo/compare")
    R<Map<String, Object>> paymentBaseInfoEditDataCompare(@RequestBody @Valid PaymentDetailReq req);


    @ApiOperation("实际租金表-对比")
    @PostMapping("/contract/rent/actual/list/compare")
    R<List<Map<String, DiffValueList>>> listActualRentCompare(@RequestBody @Valid ContractSingleIdREQ req);

    @ApiOperation("合同-收款账户表列表-对比")
    @PostMapping("/contract/account/list/compare")
    R<List<Map<String, DiffValue>>> accountListCompare(@RequestBody @Valid ContractAccountListREQ req);

    @ApiOperation("合同-承租人表列表-对比")
    @PostMapping("/contract/tenantry/list/compare")
    R<List<Map<String, DiffValue>>> tenantryListCompare(@RequestBody @Valid ContractIdListREQ req);


    @ApiOperation("合同-质押措施列表-对比")
    @PostMapping("/contract/pledge/list/compare")
    R<List<Map<String, DiffValue>>> pledgeListCompare(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("合同-抵押措施列表-对比")
    @PostMapping("/contract/mortgage/list/compare")
    R<List<Map<String, DiffValue>>> mortgageListCompare(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("合同-担保措施列表-对比")
    @PostMapping("/contract/guarantor/list/compare")
    R<List<Map<String, DiffValue>>> guarantorListCompare(@RequestBody @Valid ContractIdListREQ req);

    @ApiOperation("合同-租赁物清单列表-对比")
    @PostMapping("/contract/leaseitem/list/compare")
    R<Map<String, Object>> listLeaseItemCompare(@RequestBody @Valid ContractLeaseItemListREQ req);

    @ApiOperation("合同-概算租金表-对比")
    @PostMapping("/contract/rent/estimate/list/compare")
    R<Map<String, DiffValueList>> listEstimateRentCompare(@RequestBody @Valid ContractSingleIdREQ req);

    @ApiOperation("合同-报价方案-对比")
    @PostMapping("/contract/price/detail/compare")
    R<ContractPriceDetailCompareRSP> priceDetailCompare(@RequestBody @Valid ContractPriceDetailREQ req);

    @ApiOperation("合同-基本信息详情-对比")
    @PostMapping("/contract/base/info/detail/compare")
    R<Map<String, DiffValue>> baseInfoDetailCompare(@RequestBody @Valid ContractBaseInfoDetailREQ req);


    @ApiOperation("合同-最近一次结清方案-对比")
    @PostMapping("/contract/settle/plan/latest/get/compare")
    R<Map<String, DiffValue>> getLatestSettlePlanCompare(@RequestBody @Valid ContractSettlePlanDetailREQ req);

    @ApiOperation("集团授信立项-基本信息-对比")
    @PostMapping("/group/credit/establish/base/info/detail/compare")
    R<Map<String, DiffValue>> groupCreditEstablishBaseInfoDetailCompare(@RequestBody @Valid GroupCreditEstablishBaseInfoDetailREQ req);

    @ApiOperation("集团授信评审-基本信息-对比")
    @PostMapping("/group/credit/review/base/info/detail/compare")
    R<Map<String, DiffValue>> groupCreditReviewBaseInfoDetailCompare(@RequestBody @Valid GroupCreditReviewBaseInfoDetailREQ req);


    @ApiOperation("流程详情页面内比对接口-描述信息")
    @PostMapping("/new/ftp/flow/detail/compare/description")
    R<List<Map<String, DiffValue>>> compareDesc(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("流程详情页面内比对接口-月度推导表")
    @PostMapping("/new/ftp/flow/detail/compare/deduction")
    R<List<Map<String, DiffValue>>> compareDeduction(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("流程详情页面内比对接口-月度指导扩展")
    @PostMapping("/new/ftp/flow/detail/compare/ext")
    R<Map<String, DiffValue>> compareExt(@RequestBody @Valid NewFtpDetailReq req);

    @ApiOperation("季度指导-基本报价-对比")
    @PostMapping("/ftp/quarterly/base/pricing/compare")
    R<List<Map<String, DiffValue>>> ftpQuarterlyBasePricingCompare(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导-客户主体计价-对比")
    @PostMapping("/ftp/quarterly/customer/pricing/compare")
    R<List<Map<String, DiffValue>>> ftpQuarterlyCustomerPricingCompare(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导-按月报价-对比")
    @PostMapping("/ftp/quarterly/month/pricing/compare")
    R<List<Map<String, DiffValue>>> ftpQuarterlyMonthPricingCompare(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("季度指导-按企业报价-对比")
    @PostMapping("/ftp/quarterly/enterprise/pricing/compare")
    R<List<Map<String, DiffValue>>> ftpQuarterlyEnterprisePricingCompare(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("ftp月度指导-对比")
    @PostMapping("/ftp/monthly/guidance/detail/compare")
    R<Map<String, DiffValue>> ftpMonthlyGuidanceCompare(@RequestBody @Valid FtpGuidanceIdReq req);
    @ApiOperation("ftp月度指导-pricing-对比")
    @PostMapping("/ftp/monthly/pricing/compare")
    R<List<Map<String, DiffValue>>> ftpMonthlyPricingCompare(@RequestBody @Valid FtpGuidanceIdReq req);
    @ApiOperation("ftp月度指导-valuation-对比")
    @PostMapping("/ftp/monthly/valuation/compare")
    R<List<Map<String, DiffValue>>> ftpMonthlyValuationCompare(@RequestBody @Valid FtpGuidanceIdReq req);

    @ApiOperation("文件列表-对比")
    @PostMapping("/file/list/compare")
    R<PageR<Map<String, DiffValue>>> fileListCompare(@RequestBody @Valid FileListREQ req);

    @ApiOperation("文件列表分组-对比")
    @PostMapping("/file/list/group/compare")
    R<List<Pair<String, List<Map<String, DiffValue>>>>> fileListGroupCompare(@RequestBody @Valid FileListREQ req);

    @ApiOperation("文件列表分组-对比v2")
    @PostMapping("/file/list/group/compare/v2")
    R<List<Pair<String, List<Map<String, DiffValue>>>>> fileListGroupCompareV2(@RequestBody @Valid FileListREQ req);

    @ApiOperation("文件列表版本-对比")
    @PostMapping("/file/list/version/compare")
    R<List<Pair<String, List<DiffFile>>>> fileListVersionCompare(@RequestBody @Valid FileListVersionREQ req);

    @ApiOperation("资金收付款基本信息详情-对比")
    @PostMapping("/fund/receipt/repay/base/info/detail/compare")
    R<Map<String, DiffValue>> fundReceiptRepayBaseInfoDetailCompare(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

    @ApiOperation("资金收付款质押明细-对比")
    @PostMapping("/fund/receipt/repay/base/info/pledge/detail/compare")
    R<List<Map<String, DiffValue>>> fundReceiptRepayBaseInfoPledgeDetailCompare(@RequestBody @Valid FundReceiptRepayBaseInfoDetailREQ req);

    @ApiOperation("资金收付款借款流入-对比")
    @PostMapping("/fund/receipt/repay/borrowing/list/compare")
    R<PageR<Map<String, DiffValue>>> fundReceiptRepayBorrowingListCompare(@RequestBody @Valid FundReceiptRepayBorrowingListREQ req);

    @ApiOperation("资金收付款保证金明细列表-对比")
    @PostMapping("/fund/receipt/repay/cash/deposit/list/compare")
    R<PageR<Map<String, DiffValue>>> fundReceiptRepayCashDepositListCompare(@RequestBody @Valid FundReceiptRepayCashDepositListREQ req);

    @ApiOperation("资金收付款本金利息一览表列表-对比")
    @PostMapping("/fund/receipt/repay/cash/flow/list/compare")
    R<PageR<Map<String, DiffValue>>> fundReceiptRepayCashFlowListCompare(@RequestBody @Valid FundReceiptRepayCashFlowListREQ req);

    @ApiOperation("资金收付款费用一览表列表-对比")
    @PostMapping("/fund/receipt/repay/expense/list/compare")
    R<PageR<Map<String, DiffValue>>> fundReceiptRepayExpenseListCompare(@RequestBody @Valid FundReceiptRepayExpenseListREQ req);

    @ApiOperation("资金收付款对方账户信息列表-对比")
    @PostMapping("/fund/receipt/account/list/compare")
    R<PageR<Map<String, DiffValue>>> list(@RequestBody @Valid FundReceiptAccountListREQ req);

    @ApiOperation("资金收付款我方付款账户列表-对比")
    @PostMapping("/fund/repay/account/list/compare")
    R<PageR<Map<String, DiffValue>>> list(@RequestBody @Valid FundRepayAccountListREQ req);

    @ApiOperation("融资管理-基本信息详情-对比")
    @PostMapping("/fund/financing/base/info/detail/compare")
    R<Map<String, DiffValue>> fundFinancingBaseInfoDetailCompare(@RequestBody @Valid SingleFinancingIdREQ req);
    @ApiOperation("融资管理-获取质押列表-对比")
    @PostMapping(path = "/fund/financing/pledge/list/compare")
    R<List<Map<String, DiffValue>>> fundFinancingPledgeList(@RequestBody @Valid FundFinancingPledgeListREQ req);
    @ApiOperation("融资管理-融资方案详情-对比")
    @PostMapping(path = "/fund/financing/plan/detail/compare")
    R<Map<String, DiffValue>> fundFinancingPlanDetail(@RequestBody @Valid SingleFinancingIdREQ req);
    @ApiOperation("融资管理-还款概算表列表-对比")
    @PostMapping("/fund/financing/repay/estimate/list/compare")
    R<List<Map<String, DiffValue>>> fundFinancingEstimateList(@RequestBody @Valid SingleFinancingIdREQ req);
    @ApiOperation("融资管理-还款实际表列表-对比")
    @PostMapping(path = "/fund/financing/repay/actual/list/compare")
    R<List<Map<String, DiffValue>>> fundFinancingActualList(@RequestBody @Valid SingleFinancingIdREQ req);
    @ApiOperation("融资管理-对方收款账户列表-对比")
    @PostMapping(path = "/fund/financing/collection/account/list/compare")
    R<List<Map<String, DiffValue>>> fundFinancingCollectionAccountList(@RequestBody @Valid FundFinancingCollectAccountListREQ req);
    @ApiOperation("融资管理-还款账户-对比")
    @PostMapping(path = "/fund/financing/pay/account/list/compare")
    R<List<Map<String, DiffValue>>> fundFinancingPayAccountList(@RequestBody @Valid FundFinancingPayAccountListREQ req);
    @ApiOperation("保单管理-保单详情-对比")
    @PostMapping(path = "/policy/info/detail/compare")
    R<Map<String, DiffValue>> policyInfoDetail(@RequestBody @Valid PolicyInfoDetailCompareREQ req);

    //ftp比对接口
    @ApiOperation("ftp定价比对-十年期国债收益率")
    @PostMapping("/new/ftp/treasury/bond/yield/compare")
    R<PageR<Map<String, DiffValue>>> newFtpTreasuryBondYield(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("ftp定价比对-1年期SHIBOR利率列表")
    @PostMapping("/new/ftp/shibor/interest/rate/compare")
    R<PageR<Map<String, DiffValue>>> newFtpShiborInterest(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("ftp定价比对-LPR列表")
    @PostMapping("/new/ftp/lpr/pricing/compare")
    R<PageR<Map<String, DiffValue>>> newFtpLprPricingList(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("ftp定价比对-担保成本列表")
    @PostMapping("/new/ftp/guarantee/cost/pricing/compare")
    R<PageR<Map<String, DiffValue>>> newFtpGuaranteeCostPricingList(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("ftp定价比对-融资成本列表")
    @PostMapping("/new/ftp/financing/cost/draft/compare")
    R<PageR<Map<String, DiffValue>>> newFtpFinancingCostDraftList(@RequestBody @Valid NewFtpCommonDetailReq req);

    @ApiOperation("绩效考核-项目分配-基本信息-比对")
    @PostMapping("/kpi/projectdistribution/compare")
    R<List<Map<String, DiffValue>>> kpiProjectdistributionCompare(@RequestBody @Valid KpiProjectDistributionWeightREQ req);


}
