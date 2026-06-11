package cn.zswltech.mithras.finance.enums.third;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.Getter;

@Getter
public enum FinancialAccountAgeRecordStatus implements PullDown {
    NEW("新建"), CLOSED("关闭"), FINISH("完成");

    FinancialAccountAgeRecordStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static FinancialAccountAgeRecordStatus of(String code) {
        for (FinancialAccountAgeRecordStatus value : FinancialAccountAgeRecordStatus.values()) {
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
