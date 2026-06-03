package cn.zswltech.mithras.dashboard.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DashboardExcelFileTypeEnum implements PullDown {
    ADJUST_PERSON("调整人数明细"),
    DUE_DILIGENCE("尽调明细"),
    REVIEW("评审明细"),
    VISIT("拜访明细"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static DashboardExcelFileTypeEnum find(String name) {
        for (DashboardExcelFileTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
