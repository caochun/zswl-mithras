package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author junke
 */
public enum SubjectItemType implements PullDown {
    CAPITAL_BALANCE("资产负债表"),
    PROFIT("利润表"),
    CASH_FLOW("现金流量表"),
    BIZ_INDEX("业务指标表");

    SubjectItemType(String sheetName) {
        this.sheetName = sheetName;
    }

    public final String sheetName;

    public static SubjectItemType of(String name) {
        for (SubjectItemType value : SubjectItemType.values()) {
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
