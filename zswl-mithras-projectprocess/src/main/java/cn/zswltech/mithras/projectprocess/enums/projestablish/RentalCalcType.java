package cn.zswltech.mithras.projectprocess.enums.projestablish;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaozhengkang
 * @description （租赁）租金计算方式
 * @since
 * @deprecated 统一使用 {@link RepayCalcType}
 */
@Deprecated
public enum RentalCalcType {
    /**
     * 等额租金
     */
    EQUIVALENT_RENTAL("等额租金"),
    /**
     * 等额本金
     */
    EQUIVALENT_CAPITAL("等额本金"),
    /**
     * 平息法
     */
    PACIFICATION("平息法"),
    IRREGULAR_REPAY("不规则还款"),
    OTHER("其他");

    public String display;
    RentalCalcType(String display){
        this.display = display;
    }

    private static Map<String, RentalCalcType> map;

    static {
        map = Stream.of(RentalCalcType.values()).collect(Collectors.toMap(RentalCalcType::name, e -> e));
    }

    public static RentalCalcType of(String bizType) {
        return map.get(bizType);
    }
}
