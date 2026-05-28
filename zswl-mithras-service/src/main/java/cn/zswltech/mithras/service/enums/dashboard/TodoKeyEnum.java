package cn.zswltech.mithras.service.enums.dashboard;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.service.enums.projlifecycle.ProcessEventDescEnum;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author frank
 * @date 2024/6/19
 * @description
 */
public enum TodoKeyEnum {
    WAIT_APPROVE(1, "我收到的-待审批"),
    APPROVE_RETURN(2, "我发起的-审批退回"),
    WAIT_INITIATE(3, "我发起的-待发起");

    private Integer type;
    private String display;
    private static Map<Integer, TodoKeyEnum> map;

    static {
        map = Stream.of(TodoKeyEnum.values()).collect(Collectors.toMap(TodoKeyEnum::getType, e -> e, (k1, k2)-> k1));
    }

    public static TodoKeyEnum getByType(Integer type) {
        return (TodoKeyEnum)map.get(type);
    }


    private TodoKeyEnum(Integer type, String display) {
        this.type = type;
        this.display = display;
    }

    public Integer getType() {
        return this.type;
    }

    public String getDisplay() {
        return this.display;
    }
}
