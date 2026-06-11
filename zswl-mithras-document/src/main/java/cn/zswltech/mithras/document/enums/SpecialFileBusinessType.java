package cn.zswltech.mithras.document.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @create: 2022-07-27
 **/
public enum SpecialFileBusinessType {
    PAID_RECORD("付款记录管理"),
    FUND_GUARANTEE_INFO("财务资金管理担保信息材料"),

    ;

    public final String display;

    private static Map<String, SpecialFileBusinessType> map;

    static {
        map = Stream.of(SpecialFileBusinessType.values()).collect(Collectors.toMap(SpecialFileBusinessType::name, e -> e));
    }

    public static SpecialFileBusinessType of(String name) {
        return map.get(name);
    }

    SpecialFileBusinessType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
