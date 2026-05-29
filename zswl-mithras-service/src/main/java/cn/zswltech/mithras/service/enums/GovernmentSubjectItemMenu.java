package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @author junke
 */
public enum GovernmentSubjectItemMenu implements PullDown {
    GOV_CAPITAL_BALANCE("资产负债表"),
    PROFIT("利润表"),
    CASH_FLOW("现金流量表");

    GovernmentSubjectItemMenu(String sheetName) {
        this.sheetName = sheetName;
    }

    public final String sheetName;

    public static GovernmentSubjectItemMenu of(String name) {
        for (GovernmentSubjectItemMenu value : GovernmentSubjectItemMenu.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return sheetName;
    }
}
