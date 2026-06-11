package cn.zswltech.mithras.customer.enums.app;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author luyi
 */
public enum VisitRecordStatus implements PullDown {

    PASSED("已通过"),
    INVALID("已作废")
    ;

    VisitRecordStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static VisitRecordStatus of(String code) {
        for (VisitRecordStatus value : VisitRecordStatus.values()) {
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
