package cn.zswltech.mithras.capital.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/6/4/10:41
 * @description
 */
@Getter
@AllArgsConstructor
public enum FinanceWriteOffTypeEnum {
    AUTO_WRITE_OFF("自动核销", 3),
    COMPLETE_WRITE_OFF("全部核销", 4),
    HAND_WRITE_OFF("手工核销", 5),
    AUTO_HAND_WRITE_OFF("手工+自动", 6),
    ;

    final String display;
    final Integer sort;

    public static FinanceWriteOffTypeEnum of(String name) {
        for (FinanceWriteOffTypeEnum value : FinanceWriteOffTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
