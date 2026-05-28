package cn.zswltech.mithras.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 客户版本 类型
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:04 PM
 */
@AllArgsConstructor
@Getter
public enum VersionTypeEnum {

    /**
     * 直接生效
     */
    EFFECT(1),

    /**
     * 审批通过后新增版本
     */
    APPROVAL(2),
    ;

    private Integer type;

    private static Map<Integer, VersionTypeEnum> map;

    static {
        map = Stream.of(VersionTypeEnum.values()).collect(Collectors.toMap(VersionTypeEnum::getType, e -> e));
    }

    public static VersionTypeEnum getByType(Integer type) {
        return map.get(type);
    }

}
