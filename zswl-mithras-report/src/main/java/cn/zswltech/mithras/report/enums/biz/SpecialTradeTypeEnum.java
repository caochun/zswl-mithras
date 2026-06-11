package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.foundation.metadata.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 账户表业务类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:30 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crSpecialTradeType")
public enum SpecialTradeTypeEnum implements PullDown {

    CHANGE_EXTENSION("11", "展期"),
    SETTLE_IN_ADVANCE("12", "提前结清"),
    ;

    private String value;
    private String display;

    private static Map<String, SpecialTradeTypeEnum> nameMap;

    static {
        nameMap = Stream.of(SpecialTradeTypeEnum.values()).collect(Collectors.toMap(SpecialTradeTypeEnum::name, Function.identity(), (k1, k2)->k1));
    }

    public static String convert(String originCode) {
        return Optional.ofNullable(nameMap.get(originCode)).map(SpecialTradeTypeEnum::getValue).orElse(null);
    }

    public static SpecialTradeTypeEnum getByValue(String value) {
        for (SpecialTradeTypeEnum anEnum : SpecialTradeTypeEnum.values()) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }
        }
        return null;
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
