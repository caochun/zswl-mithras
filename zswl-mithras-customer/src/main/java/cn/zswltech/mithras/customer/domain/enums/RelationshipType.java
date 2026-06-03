package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author junke
 */
public enum RelationshipType implements PullDown {
    INVEST("对外投资");

    RelationshipType(String display) {
        this.display = display;
    }

    public final String display;

    public static RelationshipType of(String code) {
        for (RelationshipType value : RelationshipType.values()) {
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
