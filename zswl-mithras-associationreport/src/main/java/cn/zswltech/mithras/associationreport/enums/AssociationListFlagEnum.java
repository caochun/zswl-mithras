package cn.zswltech.mithras.associationreport.enums;


import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:56
 */
public enum AssociationListFlagEnum {
    /**
     * 上市/非上市标志
     */
    上市("上市"),
    非上市("非上市");

    AssociationListFlagEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static AssociationListFlagEnum findByChinese(String display) {
        for (AssociationListFlagEnum item : values()) {
            if (Objects.equals(display, item.display)) {
                return item;
            }
        }
        return null;
    }


    public static AssociationListFlagEnum of(String code) {
        for (AssociationListFlagEnum value : AssociationListFlagEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
