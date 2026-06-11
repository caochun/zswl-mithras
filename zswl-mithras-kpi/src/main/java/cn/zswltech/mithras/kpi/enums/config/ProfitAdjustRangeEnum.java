package cn.zswltech.mithras.kpi.enums.config;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
@Getter
@AllArgsConstructor
public enum ProfitAdjustRangeEnum implements PullDown {
    RANGE1(ProfitAdjustGroupEnum.BEYOND, "[100,null)",  "100% <= P"),
    RANGE2(ProfitAdjustGroupEnum.REACH, "[90,100]",  "90% <= P < 100%"),
    RANGE3(ProfitAdjustGroupEnum.DEDUCTION, "[70,90)",  "70% <= P < 90%"),
    RANGE4(ProfitAdjustGroupEnum.DEDUCTION, "(null,70)",  "P < 70%");

    private final ProfitAdjustGroupEnum group;
    private final String formula;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
