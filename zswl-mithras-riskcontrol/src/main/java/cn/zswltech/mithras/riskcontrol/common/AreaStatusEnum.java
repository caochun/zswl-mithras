package cn.zswltech.mithras.riskcontrol.common;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum AreaStatusEnum implements PullDown {

    NO_PARTITION("不分区"),
    PARTITION("分区"),
    ;


    public String display;

    AreaStatusEnum(String display) {
        this.display = display;
    }

    public static AreaStatusEnum of(String name) {
        for (AreaStatusEnum value : AreaStatusEnum.values()) {
            if (value.name().equals(name)) {
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
