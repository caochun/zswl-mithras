package cn.zswltech.mithras.service.enums.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LPRTypeEnum {

    ONE_YEAR("一年期","5"),
    FIVE_YEAR("五年期","13"),
    ;


    public final String display;
    public final String financialSystemCode;

    public static LPRTypeEnum of(String code) {
        for (LPRTypeEnum value : LPRTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
