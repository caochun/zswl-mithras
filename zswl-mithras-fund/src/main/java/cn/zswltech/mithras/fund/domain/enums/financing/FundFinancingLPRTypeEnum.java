package cn.zswltech.mithras.fund.domain.enums.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zswl
 */
@Getter
@AllArgsConstructor
public enum FundFinancingLPRTypeEnum implements PullDown {

    /**
     * 固定利率
     */
    FIXED("固定利率"),
    /**
     * 一年期
     */
    ONE_YEAR("一年期LPR"),
    /**
     * 五年期
     */
    FIVE_YEAR("五年期LPR");


    public final String display;

    public static FundFinancingLPRTypeEnum of(String code) {
        for (FundFinancingLPRTypeEnum value : FundFinancingLPRTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
