package cn.zswltech.mithras.service.enums.riskcontrol;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum ProvinceTypeEnum implements PullDown {

    INNER("省内"),
    OUTSIDE("省外"),
    ALL("全部"),
    ;


    public String display;

    ProvinceTypeEnum(String display) {
        this.display = display;
    }

    public static ProvinceTypeEnum of(String name) {
        for (ProvinceTypeEnum value : ProvinceTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static ProvinceTypeEnum change(String province) {
        if ("浙江省".equals(province)) {
            return ProvinceTypeEnum.INNER;
        } else {
            return ProvinceTypeEnum.OUTSIDE;
        }
    }

    @Override
    public String display() {
        return display;
    }
}
