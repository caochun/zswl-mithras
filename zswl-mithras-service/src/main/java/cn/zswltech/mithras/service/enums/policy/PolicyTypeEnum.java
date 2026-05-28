package cn.zswltech.mithras.service.enums.policy;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * 财产一切险
 * 财产保险->财产综合险
 * 机器损失险 -> 工程机械设备综合险
 * 车辆损失险，第三者责任险 -》车辆商业险
 * 船壳一切险
 * 沿海内河船舶保险
 * 沿海内河船东保障和赔偿责任保险
 * 沿海船舶燃油污染责任保险
 * 战争及罢工险
 * 其他
 **/
public enum PolicyTypeEnum implements PullDown {
    PROPERTY_ALL_RISKS_INSURANCE("财产一切险"),
    PROPERTY_INSURANCE("财产综合险"),
    MACHINERY_LOSS_INSURANCE("工程机械设备综合险"),
    VEHICLE_LOSS_INSURANCE("车辆商业险"),
    HULL_ALL_RISKv("船壳一切险"),
    COASTAL_INLAND_SHIP_INSURANCE("沿海内河船舶保险"),
    COASTAL_PROTECTION_COMPENSATION_INSURANCE("沿海内河船东保障和赔偿责任保险"),
    FUEL_POLLUTION_INSURANCE("沿海船舶燃油污染责任保险"),
    WAR_STRIKE_INSURANCE("战争及罢工险"),
    OTHER("其他"),
    ;
    PolicyTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyTypeEnum of(String code) {
        for (PolicyTypeEnum value : PolicyTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static PolicyTypeEnum find(String code) {
        for (PolicyTypeEnum value : PolicyTypeEnum.values()) {
            if (value.display().equals(code)) {
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
