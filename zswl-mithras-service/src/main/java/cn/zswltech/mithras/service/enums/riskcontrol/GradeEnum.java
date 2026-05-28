package cn.zswltech.mithras.service.enums.riskcontrol;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum GradeEnum implements PullDown {

    LINEAR("线性记分"),
    OPTION("选项记分"),
   ;


    public String display;

    GradeEnum(String display) {
        this.display = display;
    }

    public static GradeEnum of(String name) {
        for (GradeEnum value : GradeEnum.values()) {
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
