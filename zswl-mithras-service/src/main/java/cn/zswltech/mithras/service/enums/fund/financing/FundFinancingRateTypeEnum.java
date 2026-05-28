package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaozhengkang
 * @description 资金端利率类型枚举
 * @since
 */
@Getter
@AllArgsConstructor
public enum FundFinancingRateTypeEnum implements PullDown {
    /**
     * 固定利率
     */
    FIXED("固定利率"),
    /**
     * 即时浮动
     */
    INSTANT_FLOAT("即时浮动"),
    /**
     * 按月浮动
     */
    MONTH_FLOAT("按月浮动"),
    /**
     * 按季浮动
     */
    QUARTER_FLOAT("按季浮动"),
    /**
     * 按半年浮动
     */
    HALF_YEAR_FLOAT("按半年浮动"),
    /**
     * 按年浮动
     */
    YEAR_FLOAT("按年浮动"),

    ;


    public String display;

    private static Map<String, FundFinancingRateTypeEnum> map;

    static {
        map = Stream.of(FundFinancingRateTypeEnum.values()).collect(Collectors.toMap(FundFinancingRateTypeEnum::name, e -> e));
    }

    public static FundFinancingRateTypeEnum of(String bizType) {
        return map.get(bizType);
    }

    @Override
    public String display() {
        return display;
    }
}
