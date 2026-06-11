package cn.zswltech.mithras.fund.enums.financing;

/**
 * @ClassName ContractLibModelEnum
 * @Description
 * @Author jackerhe
 * @Date 2022/8/23 7:18 下午
 * @Version 1.0
 **/
public enum FundFinancingLibModelEnum {
    BASE_INFO("融资基本信息"),
    PLEDGE("融资质押明细"),
    PLAN("融资方案"),
    REPAY_ESTIMATE("概算租金表"),
    REPAY_ACTUAL("实际租金表"),
    COLLECTION_ACCOUNT("对方收款账户"),
    PAY_ACCOUNT("还款账户"),
    EARLYSETTLEPLAN("提前结清计划"),
    FEE_DETAIL("费用项"),
    FINANCING_CREDIT_REF("合同授信关联表"),
    MATERIALS_LIST("资料清单");

    FundFinancingLibModelEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static FundFinancingLibModelEnum of(String code) {
        for (FundFinancingLibModelEnum value : FundFinancingLibModelEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
