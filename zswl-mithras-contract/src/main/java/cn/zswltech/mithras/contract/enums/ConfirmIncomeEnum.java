package cn.zswltech.mithras.contract.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhouning
 * @date 2024/07/17
 * @description 确认收入
 */
@AllArgsConstructor
@Getter
public enum ConfirmIncomeEnum implements PullDown {
    //CREDIT_PAYMENT("授信款"),
    EARNEST_MONEY("保证金"),
    FIRST_RENT("首期租金"),
    RETENTION_MONEY("质保金"),
    OTHERAMOUNT("服务费/咨询费"),
    COMMISSION("手续费"),
    // 首期利息类型仅在代码内部数据流转中使用，不会落库，也不会放给前端进行展示
    FIRST_INSTALLMENT_INTEREST("首期利息");

    public final String display;

    private static Map<String, ConfirmIncomeEnum> map;

    public static List<String> needPlanCollection = Arrays.asList(FIRST_RENT.name(), FIRST_INSTALLMENT_INTEREST.name(), COMMISSION.name(), OTHERAMOUNT.name());

    static {
        map = Stream.of(ConfirmIncomeEnum.values()).collect(Collectors.toMap(ConfirmIncomeEnum::name, e -> e, (k1,k2)->k1));
    }

    public static ConfirmIncomeEnum getByDisplay(String display) {
        for (ConfirmIncomeEnum item : values()) {
            if (Objects.equals(item.display, display)) {
                return item;
            }
        }
        return null;
    }

    public static String allDisplay() {
        ConfirmIncomeEnum[] cashFlowItemEnums = ConfirmIncomeEnum.values();
        List<String> allDisplay = new ArrayList<>(cashFlowItemEnums.length);
        for (ConfirmIncomeEnum cashFlowItemEnum : cashFlowItemEnums) {
            allDisplay.add(cashFlowItemEnum.getDisplay());
        }
        return String.join("，", allDisplay);
    }

    public static ConfirmIncomeEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
