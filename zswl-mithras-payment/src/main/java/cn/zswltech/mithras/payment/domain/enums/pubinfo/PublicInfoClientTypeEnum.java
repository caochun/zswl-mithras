package cn.zswltech.mithras.payment.domain.enums.pubinfo;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/10 20:52
 * @description
 */
@Getter
@AllArgsConstructor
public enum PublicInfoClientTypeEnum implements PullDown {
    TENANTRY("承租人"),
    GUARANTOR("担保人"),
    MORTGAGE("抵押人"),
    PLEDGE("质押人"),
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static PublicInfoClientTypeEnum find(String name) {
        for (PublicInfoClientTypeEnum value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
