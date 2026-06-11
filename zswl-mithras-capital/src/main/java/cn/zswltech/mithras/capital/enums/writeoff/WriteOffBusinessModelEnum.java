package cn.zswltech.mithras.capital.enums.writeoff;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/18 16:18
 * @description
 */
@Getter
@AllArgsConstructor
public enum WriteOffBusinessModelEnum implements PullDown {
    PROJECT_COLLECT("项目端-收款"),
    PROJECT_PAY("项目端-付款"),
    FINANCE_COLLECT("资金端-收款"),
    FINANCE_PAY("资金端-付款"),
    ;

    private final String display;

    public static WriteOffBusinessModelEnum find(String name) {
        for (WriteOffBusinessModelEnum value : WriteOffBusinessModelEnum.values()) {
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
