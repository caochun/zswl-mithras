package cn.zswltech.mithras.capital.enums.writeoff;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/19 10:22
 * @description
 */
@Getter
@AllArgsConstructor
public enum WriteOffAccountTypeEnum implements PullDown {
    SUPERVISION_ACCOUNT("监管账户", 1),
    ORDINARY_ACCOUNT("普通账户", 99),
    ;

    private final String display;
    private final Integer sort;

    public static WriteOffAccountTypeEnum find(String name) {
        for (WriteOffAccountTypeEnum value : WriteOffAccountTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
