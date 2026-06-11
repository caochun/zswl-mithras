package cn.zswltech.mithras.associationreport.enums;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:56
 */
public enum YesOrNoChineseEnum {
    /**
     * 是否销号
     */
    是("是"),
    否("否");

    YesOrNoChineseEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static YesOrNoChineseEnum of(String code) {
        for (YesOrNoChineseEnum value : YesOrNoChineseEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
