package cn.zswltech.mithras.service.enums.collection;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @create: 2022-08-18
 **/
@Getter
public enum CollectionWriteOffStatusEnum implements PullDown {
    UNCOLLECTION("未收款"),
    TO_BE_WRITE_OFF("待核销"),
    PORTION_WRITTEN_OFF("部分核销"),
    WRITE_OFF_COMPLETED("核销完毕");
    public final String display;
    private static Map<String, CollectionWriteOffStatusEnum> map;
    static {
        map = Stream.of(CollectionWriteOffStatusEnum.values()).collect(Collectors.toMap(CollectionWriteOffStatusEnum::name, e -> e));
    }
    CollectionWriteOffStatusEnum(String display){
        this.display = display;
    }

    public static CollectionWriteOffStatusEnum of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }

    public static boolean isReceived(CollectionWriteOffStatusEnum status) {
        return status.equals(PORTION_WRITTEN_OFF) || status.equals(WRITE_OFF_COMPLETED);
    }
}
