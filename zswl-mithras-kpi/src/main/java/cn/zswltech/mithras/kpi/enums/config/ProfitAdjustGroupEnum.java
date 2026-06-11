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
public enum ProfitAdjustGroupEnum implements PullDown {
    BEYOND("超额奖励区间"),
    REACH("绩效达标区间"),
    DEDUCTION("绩效扣减区间");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
