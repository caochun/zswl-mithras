package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaozhengkang
 * @description 利率类型枚举
 * @since
 */
@Getter
@AllArgsConstructor
public enum RateType implements PullDown {
    /**
     * 固定利率
     */
    FIXED("固定利率","0"),
    /**
     * 浮动利率
     */
    FLOAT("浮动利率","1");


    public String display;
    public String financialSystemCode;
    RateType(String display){
        this.display = display;
    }

    private static Map<String, RateType> map;

    static {
        map = Stream.of(RateType.values()).collect(Collectors.toMap(RateType::name, e -> e));
    }

    public static RateType of(String bizType) {
        return map.get(bizType);
    }

    @Override
    public String display() {
        return display;
    }
}
