package cn.zswltech.mithras.service.enums.newftp;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/26 16:13
 */
public enum NewFtpSubModule {
    /**
     *
     */
    MONTHLY_DEDUCTION("月度推导"),
    MONTHLY_GUIDANCE("月度指导"),
    MONTHLY_GUIDANCE_EXT("月度指导EXT"),
    QUARTERLY_PRICING("季度报价"),
    QUARTERLY_PRICING_EXT("季度报价EXT"),
    DESCRIPTION_TEXT("描述性文本"),
    TREASURY_BOND_YIELD("十年期国债收益率"),
    SHIBOR_INTEREST("一年期shibor利率"),
    LPR_PRICING("lpr"),
    GUARANTEE_COST_PRICING("担保成本"),
    FINANCING_COST_PRICING("融资成本"),
    ;

    private final String display;

    NewFtpSubModule(String display) {
        this.display = display;
    }

}
