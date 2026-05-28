package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
@AllArgsConstructor
@Getter
public enum AfterLeaseCheckPlanTypeEnum implements PullDown {
    COMMONLY("一般检查计划"),
    QUARTER("季度检查计划"),
    MONTH("月度检查计划"),
    SPECIAL("专项检查计划"),
    RANDOM("抽查计划"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    private static Map<String, AfterLeaseCheckPlanTypeEnum> map;

    static {
        map = Stream.of(AfterLeaseCheckPlanTypeEnum.values()).collect(Collectors.toMap(AfterLeaseCheckPlanTypeEnum::name, c -> c));
    }

    public static AfterLeaseCheckPlanTypeEnum of (String name) {
        return map.get(name);
    }

}
