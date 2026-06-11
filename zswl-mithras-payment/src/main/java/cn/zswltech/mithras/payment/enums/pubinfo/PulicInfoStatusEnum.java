package cn.zswltech.mithras.payment.enums.pubinfo;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/10 14:55
 * @description
 */
@Getter
@AllArgsConstructor
public enum PulicInfoStatusEnum implements PullDown {
    CONFIRMED("已确认"),
    ;
    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static PulicInfoStatusEnum find(String name) {
        for (PulicInfoStatusEnum value : PulicInfoStatusEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
