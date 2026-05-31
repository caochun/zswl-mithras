package cn.zswltech.mithras.service.enums.contract.text;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/11/14 17:35
 * @description 签约方式
 */
@Getter
@AllArgsConstructor
public enum SigningWayEnum implements PullDown {
    LEASE_ONLINE_SIGN("租赁线上先签"),
    OFFLINE_SIGN("线下签约"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static SigningWayEnum ofName(String name) {
        for (SigningWayEnum signingWayEnum : SigningWayEnum.values()) {
            if (signingWayEnum.name().equals(name)) {
                return signingWayEnum;
            }
        }
        return null;
    }
}
