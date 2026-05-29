package cn.zswltech.mithras.service.enums.payment;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @description:
 * @author: ylzhang5
 * @date: 2025/12/09
 */
@AllArgsConstructor
@Getter
public enum CapitalSource implements PullDown {

    OWN_BALANCE("自有资金"),
    BANK_LOAN("银行贷款"),
    ;

    public final String display;

    @Override
    public String display() {
        return display;
    }

    public static CapitalSource findByName(String name) {
        for (CapitalSource item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static CapitalSource findByDisplay(String name) {
        for (CapitalSource item : values()) {
            if (Objects.equals(item.display(), name)) {
                return item;
            }
        }
        return null;
    }
}
