package cn.zswltech.mithras.service.enums.fund.financing;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
//每月、每季度、每年、每365天
public enum LprArrangeModeEnum implements PullDown {
    //    NOW("即时生效","1"),
//    MONTH("每月","2"),
//    QUARTER("每季度","3"),
//    YEAR("每年","5"),
//    DAY_365("每365天","5"),
//    HALF_YEAR("每半年","4"),
    MONTH_ONE("每1个月","2",5),
    MONTH_THREE("每3个月","3",4),
    MONTH_SIX("每6个月","4",3),
    MONTH_DECEMBER("每12个月","5",1),
    YEAR("每年","5",2),
    ;
    private final String display;
    private final String financialSystemCode;
    private final int sortCode;

    @Override
    public String display() {
        return this.display;
    }

    public int getSortCode() {
        return sortCode;
    }

    public static LprArrangeModeEnum of(String code) {
        for (LprArrangeModeEnum value : LprArrangeModeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
