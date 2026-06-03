package cn.zswltech.mithras.finance.enums.third;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

@Getter
public enum FinancialAccountAgeSendStatusStatus implements PullDown {
    NEW("新建"), SUCCESS("苍穹推送成功"), FAIL("苍穹推送失败"), PUSH_IN_PROGRESS("推送中");

    FinancialAccountAgeSendStatusStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static FinancialAccountAgeSendStatusStatus of(String code) {
        for (FinancialAccountAgeSendStatusStatus value : FinancialAccountAgeSendStatusStatus.values()) {
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
