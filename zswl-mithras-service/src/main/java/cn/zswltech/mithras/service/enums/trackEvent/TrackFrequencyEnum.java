package cn.zswltech.mithras.service.enums.trackEvent;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 跟踪事项
 */
@AllArgsConstructor
@Getter
public enum TrackFrequencyEnum implements PullDown {

    ONCE("单次",1),
    WEEK("周",2),
    MONTH("月",3),
    SEASON("季度",4),
    ;


    public final String display;
    public final int sort;

    @Override
    public String display() {
        return display;
    }

    public static TrackFrequencyEnum find(String name) {
        for (TrackFrequencyEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

}
