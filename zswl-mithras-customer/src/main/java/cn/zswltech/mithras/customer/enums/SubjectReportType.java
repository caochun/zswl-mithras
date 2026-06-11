package cn.zswltech.mithras.customer.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author junke
 */
public enum SubjectReportType implements PullDown {

    ALL("全部"), LOCAL("本部"), MERGED("合并");

    SubjectReportType(String display) {
        this.display = display;
    }

    public final String display;

    public static SubjectReportType ofDisplay(String display) {
        for (SubjectReportType value : SubjectReportType.values()) {
            if (value.display.equals(display)) {
                return value;
            }
        }
        return null;
    }

    public static SubjectReportType of(String code) {
        for (SubjectReportType value : SubjectReportType.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
