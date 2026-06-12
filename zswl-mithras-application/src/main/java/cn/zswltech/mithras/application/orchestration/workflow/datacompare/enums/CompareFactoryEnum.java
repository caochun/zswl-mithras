package cn.zswltech.mithras.application.orchestration.workflow.datacompare.enums;

/**
 * @create: 2022-08-03
 **/
public enum CompareFactoryEnum {
    corpAddressInfo("法人公司地址信息"),
    corpBankAccount("法人银行账户"),
    corpBondInfo("法人公司发债及评级信息"),
    corpCommerceInfo("法人工商信息"),
    corpContactInfo("法人联系人"),
    corpRelatedEnterprise("法人关联企业"),
    corpShareholderInfo("法人股东信息"),
    normalBankAccount("自然人银行账户"),
    normalBaseInfo("自然人基本信息"),
    normalSpouse("自然人配偶信息"),
    projEstablishBaseInfo("立项明细基础信息"),
    projEstablishAocPrice("债权转让报价方案"),
    projEstablishFactoringPrice("保理报价方案"),
    projEstablishLeasePrice("租赁报价方案"),
    projReviewBaseInfo("项目评审明细基础信息"),
    projReviewAocPrice("项目评审债权转让报价方案"),
    projReviewFactoringPrice("项目评审保理报价方案"),
    projReviewLeasePrice("项目评审租赁报价方案"),
    projReviewCashFlowPlan("项目评审现金流计划"),
    projPricingBaseInfo("项目定价明细基础信息"),
    projPricingAocPrice("项目定价债权转让报价方案"),
    projPricingFactoringPrice("项目定价保理报价方案"),
    projPricingLeasePrice("项目定价租赁报价方案"),
    projPricingCashFlowPlan("项目定价现金流计划"),
    paymentBaseInfo("付款申请明细基础信息"),
    paymentPlanedDetail("付款申请付款计划清单"),
    paymentQuestionnaire("付款申请问卷调查"),
    contractActualRent("合同-实际租金列表"),
    contractActualRentChildren("合同-实际租金列表"),
    contractAccountZLSK("租赁合同-收款账户表列表"),
    contractAccountZZSK("转租赁合同-收款账户表列表"),
    contractAccountBLSK("保理合同-收款账户表列表"),
    contractAccountBLHK("保理合同-回款账户表列表"),
    contractAccountZRSK("债权转让合同-收款账户表列表"),
    contractAccountZRHK("债权转让合同-回款账户表列表"),
    contractTenantry("合同-租赁报价方案"),
    contractPledge("合同-质押措施列表"),
    contractMortgage("合同-抵押措施列表"),
    contractGuarantor("合同-担保措施列表"),
    contractLeaseItem("租赁物清单列表"),
    contractEstimateRent("合同-概算租金表"),
    contractAocPrice("合同-债权转让报价方案"),
    contractFactoringPrice("合同-保理报价方案"),
    contractLeasePrice("合同-租赁报价方案"),
    contractBaseInfo("合同-明细基础信息"),
    contractSettlePlan("合同-结清方案"),
    groupCreditEstablishBaseInfo("集团授信立项-基本信息"),
    groupCreditReviewBaseInfo("集团授信评审-基本信息"),

    ftpQuarterlyGuidance("ftp季度指导"),
    ftpQuarterlyBasePricing("ftp季度指导基础报价"),
    ftpQuarterlyMonthPricing("ftp季度指导按月报价"),
    ftpQuarterlyEnterprisePricing("ftp季度指导按企业性质报价"),
    ftpQuarterlyCustomerPrincipalPricing("客户主体计价"),
    ftpMonthlyGuidance("ftp月度指导"),
    ftpMonthlyPricing("ftp月度指导定价"),
    ftpMonthlyValuation("ftp月度指导计价"),
    newFtpDescriptionText("新ftp文本描述"),
    newFtpDeduction("新ftp推导信息"),
    newFtpMonthlyGuidanceExt("新ftp月度指导扩展信息"),
    treasuryBondYield("10年期国债收益率"),
    shiborInterestRate("1年期SHIBOR利率"),
    guaranteeCostPricing("担保成本"),
    financingCost("融资成本"),
    lprPricing("lpr"),

    fundReceiptRepayBaseInfo("资金收付款基本信息"),
    fundReceiptRepayPledge("资金收付款质押明细"),
    fundReceiptRepayBorrowing("资金收付款借款流入"),
    fundReceiptRepayCashDeposit("资金收付款保证金明细"),
    fundReceiptRepayExpense("资金收付款费用一览表列表"),
    fundReceiptRepayCashFlow("资金收付款本金利息一览表列表"),
    fundReceiptRepayReceiptAccount("资金收付款收款账户"),
    fundReceiptRepayRepayAccount("资金收付款还款账户"),

    fundFinancingBaseInfo("融资管理-基本信息"),
    fundFinancingPledge("融资管理-质押明细"),
    fundFinancingPlan("融资管理-融资方案"),
    fundFinancingRepayEstimate("融资管理-还款概算表"),
    fundFinancingRepayActual("融资管理-还款实际表"),
    fundFinancingCollectAccount("融资管理-对方收款账户"),
    fundFinancingPayAccount("融资管理-我方还款账户"),
    policyInfo("保单管理-基本信息"),
    kpiProjectDistributionWeight("项目分配表"),


    ;



    CompareFactoryEnum(String display){
        this.display = display;
    }

    public final String display;
}
