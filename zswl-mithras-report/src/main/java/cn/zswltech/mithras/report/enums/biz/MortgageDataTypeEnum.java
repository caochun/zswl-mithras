package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 抵押物类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:30 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crMortgageDataType")
public enum MortgageDataTypeEnum implements PullDown {

    REAL_ESTATE("11", "房产"),
    LAND_USE_RIGHTS("12", "土地使用权(包含土地附着物)"),
    TRANSPORTATION("13", "交通运输设备"),
    MECHANICAL("14", "机器设备"),
    OTHER("99", "其他"),
    ;

    private String value;
    private String display;

    private static Map<String, MortgageDataTypeEnum> displayMap;

    static {
        displayMap = Stream.of(MortgageDataTypeEnum.values()).collect(Collectors.toMap(MortgageDataTypeEnum::getDisplay, Function.identity(), (k1, k2)->k1));
    }

    public static String convert(String originCode) {
        return displayMap.getOrDefault(originCode, OTHER).value;
    }

    public static MortgageDataTypeEnum getByValue(String value) {
        for (MortgageDataTypeEnum anEnum : MortgageDataTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return OTHER;
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
