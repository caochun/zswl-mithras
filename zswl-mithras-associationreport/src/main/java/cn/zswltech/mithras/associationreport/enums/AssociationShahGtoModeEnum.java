package cn.zswltech.mithras.associationreport.enums;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:56
 */
public enum AssociationShahGtoModeEnum {
    /**
     * 股东进入方式
     */
    创设("创设"),
    受让("受让");

    AssociationShahGtoModeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static AssociationShahGtoModeEnum of(String code) {
        for (AssociationShahGtoModeEnum value : AssociationShahGtoModeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
