package cn.zswltech.mithras.associationreport.enums;


import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;

import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:56
 */
public enum AssociationMnfrFlagEnum {
    /**
     * 厂商系标志
     */
    厂商系("厂商系"),
    非厂商系("非厂商系");

    AssociationMnfrFlagEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static AssociationMnfrFlagEnum findByChinese(String display) {
        for (AssociationMnfrFlagEnum item : values()) {
            if (Objects.equals(display, item.display)) {
                return item;
            }
        }
        return null;
    }

    public static AssociationMnfrFlagEnum of(String code) {
        for (AssociationMnfrFlagEnum value : AssociationMnfrFlagEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
