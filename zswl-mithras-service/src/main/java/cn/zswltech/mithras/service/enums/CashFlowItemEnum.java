package cn.zswltech.mithras.service.enums;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingqi
 * @date 2022/8/10
 * @description 现金流项目枚举
 */
@AllArgsConstructor
@Getter
public enum CashFlowItemEnum implements PullDown {
    //CREDIT_PAYMENT("授信款"),
    EARNEST_MONEY("保证金"),
    FIRST_RENT("首期租金"),
    RETENTION_MONEY("质保金"),
    OTHERAMOUNT("服务费/咨询费"),
    RENT("租金"),
    COMMISSION("手续费"),
    // 首期利息类型仅在代码内部数据流转中使用，不会落库，也不会放给前端进行展示
    FIRST_INSTALLMENT_INTEREST("首期利息"),
//    INTEREST_BEFORE_RENT("租前息"),
    EARLY_STOP_COMPENSATION("提前终止补偿金"),
    NOMINAL_PRICE("名义价款"),
    LEASE_RENT("租赁本金");

    public final String display;

    private static Map<String, CashFlowItemEnum> map;

    //推送应收单现金流项目
    public static List<String> needPlanCollection = ListUtil.toList(FIRST_RENT.name(), RENT.name(), FIRST_INSTALLMENT_INTEREST.name(), COMMISSION.name(), OTHERAMOUNT.name(),
            NOMINAL_PRICE.name(), EARLY_STOP_COMPENSATION.name());

    static {
        map = Stream.of(CashFlowItemEnum.values()).collect(Collectors.toMap(CashFlowItemEnum::name, e -> e));
    }

    public static CashFlowItemEnum getByDisplay(String display) {
        for (CashFlowItemEnum item : values()) {
            if (Objects.equals(item.display, display)) {
                return item;
            }
        }
        return null;
    }

    public static String allDisplay() {
        CashFlowItemEnum[] cashFlowItemEnums = CashFlowItemEnum.values();
        List<String> allDisplay = new ArrayList<>(cashFlowItemEnums.length);
        for (CashFlowItemEnum cashFlowItemEnum : cashFlowItemEnums) {
            allDisplay.add(cashFlowItemEnum.getDisplay());
        }
        return Joiner.on("，").join(allDisplay);
    }

    public static CashFlowItemEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
