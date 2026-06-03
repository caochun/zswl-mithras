package cn.zswltech.mithras.customer.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author junke
 */
public enum SubjectItemDisplayDimension implements PullDown {

    BASE("基础数据"), PERCENT("百分比"), OVER_YEAR("同比");

    SubjectItemDisplayDimension(String display) {
        this.display = display;
    }

    public final String display;

    public static SubjectItemDisplayDimension of(String code) {
        for (SubjectItemDisplayDimension value : SubjectItemDisplayDimension.values()) {
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
