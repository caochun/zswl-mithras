package cn.zswltech.mithras.service.enums.dashboard;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum DashboardPledgeTypeEnum implements PullDown {
    PLEDGE_SUPERVISE("质押和监管"),
    PLEDGE("质押"),
    SUPERVISE("监管"),
    NEITHER("无质押/监管")
        ;

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static DashboardPledgeTypeEnum findByName(String name) {
        for (DashboardPledgeTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

}
