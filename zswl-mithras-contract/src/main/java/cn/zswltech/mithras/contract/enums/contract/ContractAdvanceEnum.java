package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ContractAdvanceEnum implements PullDown {

    SETTLE_NORMAL("正常结清"),

    SETTLE_IN_ADVANCE("提前结清")

    ;
    ContractAdvanceEnum(String display) {
        this.display = display;
    }

    public final String display;

    private static Map<String, ContractAdvanceEnum> map;

    static {
        map = Stream.of(ContractAdvanceEnum.values()).collect(Collectors.toMap(ContractAdvanceEnum::name, c -> c));
    }

    public static ContractAdvanceEnum of (String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
