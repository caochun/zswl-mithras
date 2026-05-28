package cn.zswltech.mithras.service.enums.riskcontrol;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum AreaTypeEnum implements PullDown {

    NO_PARTITION("不分区"),
    FIRST("一类地区"),
    SECOND("二类地区"),
    THIRD("三类地区"),
    FOURTH("四类地区");


    public String display;

    AreaTypeEnum(String display) {
        this.display = display;
    }

    public static AreaTypeEnum of(String name) {
        for (AreaTypeEnum value : AreaTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static AreaTypeEnum change(String grade){
        switch (grade){
            case "1" :
                return AreaTypeEnum.FIRST;
            case "2" :
                return AreaTypeEnum.SECOND;
            case "3" :
                return AreaTypeEnum.THIRD;
            case "4" :
                return AreaTypeEnum.FOURTH;
            default:
                return AreaTypeEnum.NO_PARTITION;
        }
    }

    @Override
    public String display() {
        return display;
    }
}
