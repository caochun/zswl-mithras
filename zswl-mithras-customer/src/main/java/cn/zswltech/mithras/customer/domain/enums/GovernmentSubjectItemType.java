package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author junke
 */
public enum GovernmentSubjectItemType implements PullDown {
    GOV_CAPITAL_BALANCE("资产负债表"),
    INCOME_EXPEND("收入支出表");

    GovernmentSubjectItemType(String sheetName) {
        this.sheetName = sheetName;
    }

    public final String sheetName;

    public static GovernmentSubjectItemType of(String name) {
        for (GovernmentSubjectItemType value : GovernmentSubjectItemType.values()) {
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
