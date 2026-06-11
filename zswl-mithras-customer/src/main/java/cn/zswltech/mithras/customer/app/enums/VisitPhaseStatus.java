package cn.zswltech.mithras.customer.app.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @author luyi
 */
public enum VisitPhaseStatus implements PullDown {

    MARKETING_VISIT("营销拜访"),
    ON_SITE_DUE_DILIGENCE("现场尽调"),
    AFTER_LEASE_CHECK_PLAN("租后检查"),
    CONTRACT_SIGN_OFFLINE("合同面签")
    ;

    VisitPhaseStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static VisitPhaseStatus of(String code) {
        for (VisitPhaseStatus value : VisitPhaseStatus.values()) {
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
