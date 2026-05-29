package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @create: 2022-11-19
 **/

public enum RentCollectionLevelEnum implements PullDown {
    LEVEL_30("业务分管领导"),
    LEVEL_60("首席风险官"),
    LEVEL_90("总经理"),
    LEVEL_MAX("公司经营管理层、律师");

    public final String display;

    private static Map<String, RentCollectionLevelEnum> map;
    static {
        map = Stream.of(RentCollectionLevelEnum.values()).collect(Collectors.toMap(RentCollectionLevelEnum::name, e -> e));
    }
    RentCollectionLevelEnum(String display){
        this.display = display;
    }

    public static RentCollectionLevelEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}