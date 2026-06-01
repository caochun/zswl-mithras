package cn.zswltech.mithras.service.enums.dashboard;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author frank
 * @date 2024/6/19
 * @description
 */
public enum TodoProcessEnum {
    APPROVING(1, "我发起的-申请中"),
    WITHDRAW(2, "我发起的-我的撤回"),
    FINISH(3, "我发起的-审批结束");

    private Integer type;
    private String display;
    private static Map<Integer, TodoProcessEnum> map;

    static {
        map = Stream.of(TodoProcessEnum.values()).collect(Collectors.toMap(TodoProcessEnum::getType, e -> e, (k1, k2)-> k1));
    }

    public static TodoProcessEnum getByType(Integer type) {
        return (TodoProcessEnum)map.get(type);
    }


    private TodoProcessEnum(Integer type, String display) {
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
