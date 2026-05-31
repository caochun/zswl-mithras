package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 担保措施
 **/
public enum PledgeMeasuresTypeEnum implements PullDown {

    EQUITY_PLEDGE("股权质押"),
    ACCOUNTS_RECEIVABLE_PLEDGE("应收账款质押"),
    MORTGAGE_GUARANTEE("抵押担保"),
    OTHER("其他");

    public String display;

    PledgeMeasuresTypeEnum(String display) {
        this.display = display;
    }

    public static PledgeMeasuresTypeEnum of(String code) {
        for (PledgeMeasuresTypeEnum value : PledgeMeasuresTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @JsonValue
    public String getDisplay() {
        return display;
    }

    @Override
    public String display() {
        return display;
    }
}
