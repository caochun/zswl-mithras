package cn.zswltech.mithras.finance.enums.financeoverdue;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum OverdueRecordStatueEnum implements PullDown {
    NOT_REPORT("未推送苍穹"), REPORT_FAIL("苍穹推送失败"), REPORT_SUCCESS("苍穹推送成功");

    OverdueRecordStatueEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static OverdueRecordStatueEnum of(String code) {
        for (OverdueRecordStatueEnum value : OverdueRecordStatueEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
