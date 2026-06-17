package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 租金催收视角下的收款核销状态。
 */
@AllArgsConstructor
@Getter
public enum RentCollectionWriteOffStatus implements PullDown {
    UNCOLLECTION("未收款"),
    TO_BE_WRITE_OFF("待核销"),
    PORTION_WRITTEN_OFF("部分核销"),
    WRITE_OFF_COMPLETED("核销完毕");

    private final String display;

    private static final Map<String, RentCollectionWriteOffStatus> MAP =
            Stream.of(values()).collect(Collectors.toMap(RentCollectionWriteOffStatus::name, e -> e));

    public static RentCollectionWriteOffStatus of(String name) {
        return MAP.get(name);
    }

    public static boolean isWriteOffCompleted(String name) {
        return WRITE_OFF_COMPLETED.name().equals(name);
    }

    @Override
    public String display() {
        return display;
    }
}
