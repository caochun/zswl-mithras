package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/4/9
 * @description
 */
@AllArgsConstructor
@Getter
public enum IncomeConfirmTypeEnum  implements PullDown {
    RP("剩余本金法"),
    AIR("实际利率法");

    private final String display;

    public static IncomeConfirmTypeEnum find(String name) {
        for (IncomeConfirmTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
