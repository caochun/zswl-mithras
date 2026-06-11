package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 质押物类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:30 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crPledgeDataType")
public enum PledgeDataTypeEnum implements PullDown {

    CASH("11", "现金及其等价物"),
    BILL("12", "票据"),
    POLICY("13", "保单"),
    BOND("14", "债券"),
    EQUITY("15", "股权"),
    FUND("21", "基金"),
    PRECIOUS_METAL("22", "贵金属"),
    ACCOUNTS("23", "应收账款"),
    CURRENT_ASSETS("24", "流动资产"),
    INTANGIBLE_ASSETS("25", "无形资产"),
    AGRICULTURAL_SUBSTANCES("26", "涉农质物"),
    OTHER("90", "其他"),
    ;

    private String value;
    private String display;

    private static Map<String, PledgeDataTypeEnum> displayMap;

    static {
        displayMap = Stream.of(PledgeDataTypeEnum.values()).collect(Collectors.toMap(PledgeDataTypeEnum::getDisplay, Function.identity(), (k1, k2)->k1));
    }

    public static String convert(String originCode) {
        return displayMap.getOrDefault(originCode, OTHER).value;
    }

    public static PledgeDataTypeEnum getByValue(String value) {
        return Stream.of(PledgeDataTypeEnum.values()).filter(e -> e.value.equals(value)).findFirst().orElse(OTHER);
    }

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return value;
    }

}
