package cn.zswltech.mithras.foundation.enums;

/**
 * @author luyi
 */
public enum InfoOperation{
    ADD("新增"), MODIFY("修改"), REMOVE("删除");


    InfoOperation(String display) {
        this.display = display;
    }

    public final String display;

    public static InfoOperation of(String code) {
        for (InfoOperation value : InfoOperation.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
