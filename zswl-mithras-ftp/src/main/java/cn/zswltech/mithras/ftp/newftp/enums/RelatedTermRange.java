package cn.zswltech.mithras.ftp.newftp.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/7/17
 * @description
 */
@AllArgsConstructor
@Getter
public enum RelatedTermRange implements PullDown {
    THREE_YEAR("3年内（含）"),
    THREE_TO_FIVE_YEAR("3-5年（含）"),
    MORE_THAN_FIVE_YEAR("5年以上");

    private final String display;

    public static RelatedTermRange convertFromMonthCount(Integer monthCount) {
        if (Objects.isNull(monthCount)) {
            return null;
        }
        if (monthCount <= 36) {
            return THREE_YEAR;
        } else if (monthCount > 60) {
            return MORE_THAN_FIVE_YEAR;
        } else {
            return THREE_TO_FIVE_YEAR;
        }
    }

    @Override
    public String display() {
        return display;
    }
}
