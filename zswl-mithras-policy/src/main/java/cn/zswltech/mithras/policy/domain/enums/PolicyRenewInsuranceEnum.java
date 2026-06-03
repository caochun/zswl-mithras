package cn.zswltech.mithras.policy.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2023-06-16
 **/
public enum PolicyRenewInsuranceEnum implements PullDown {
    RENEWAL_UPON_EXPIRATION("到期续保"),
    NO_NEED_TO_RENEW("无需续保")
    ;
    PolicyRenewInsuranceEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyRenewInsuranceEnum of(String code) {
        for (PolicyRenewInsuranceEnum value : PolicyRenewInsuranceEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static PolicyRenewInsuranceEnum find(String code) {
        for (PolicyRenewInsuranceEnum value : PolicyRenewInsuranceEnum.values()) {
            if (value.display().equals(code)) {
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
