package cn.zswltech.mithras.service.enums;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.enums.trackEvent.TrackTaskBizSourceEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OverdueTypeEnum implements PullDown {

    OVERDUE("逾期"),
    NOT_OVERDUE("未逾期"),
    ;


    public final String display;

    @Override
    public String display() {
        return display;
    }


    public static OverdueTypeEnum find(String name) {
        for (OverdueTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
