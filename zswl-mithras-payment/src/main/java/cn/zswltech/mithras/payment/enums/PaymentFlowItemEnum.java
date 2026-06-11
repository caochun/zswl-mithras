package cn.zswltech.mithras.payment.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum PaymentFlowItemEnum implements PullDown {
    CREDIT_PAYMENT("投放款"),
    EARNEST_MONEY("保证金"),
    RETENTION_MONEY("质保金");

    public final String display;

    private static Map<String, PaymentFlowItemEnum> map;

    static {
        map = Stream.of(PaymentFlowItemEnum.values()).collect(Collectors.toMap(PaymentFlowItemEnum::name, e -> e));
    }

    public static PaymentFlowItemEnum getByDisplay(String display) {
        for (PaymentFlowItemEnum item : values()) {
            if (Objects.equals(item.display, display)) {
                return item;
            }
        }
        return null;
    }

    public static String allDisplay() {
        PaymentFlowItemEnum[] cashFlowItemEnums = PaymentFlowItemEnum.values();
        List<String> allDisplay = new ArrayList<>(cashFlowItemEnums.length);
        for (PaymentFlowItemEnum cashFlowItemEnum : cashFlowItemEnums) {
            allDisplay.add(cashFlowItemEnum.getDisplay());
        }
        return String.join("，", allDisplay);
    }

    public static PaymentFlowItemEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
