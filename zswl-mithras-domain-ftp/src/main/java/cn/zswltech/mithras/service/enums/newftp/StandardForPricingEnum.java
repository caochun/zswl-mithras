package cn.zswltech.mithras.service.enums.newftp;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/3/25/10:07
 * @description
 */
@Getter
@AllArgsConstructor
public enum StandardForPricingEnum implements PullDown {
    /**
     * 金融市场波动计价标准
     */
    TEN_YEARS_TREASURY_BOND("十年期国债收益率波动计价标准"),
    ONE_YEAR_SHIBOR_RATE("一年期 shibor 利率波动计价标准 "),
    LPR_WAVE("LPR波动计价标准"),
    COST_TRENDS("成本趋势波动计价标准");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
