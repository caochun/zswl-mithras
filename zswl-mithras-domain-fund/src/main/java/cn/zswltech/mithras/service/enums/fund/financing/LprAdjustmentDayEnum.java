package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
//结束前一个自然日、第一个结息日
public enum LprAdjustmentDayEnum implements PullDown {

    CARRY_INTEREST_DAY("对应起息日")
//    INTEREST_SETTLE_DAY("第一个结息日"),
    ;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static LprAdjustmentDayEnum of(String code) {
        for (LprAdjustmentDayEnum value : LprAdjustmentDayEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
