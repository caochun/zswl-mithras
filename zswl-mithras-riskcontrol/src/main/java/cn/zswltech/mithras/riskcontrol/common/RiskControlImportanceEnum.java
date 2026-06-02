package cn.zswltech.mithras.riskcontrol.common;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

public enum RiskControlImportanceEnum implements PullDown {
    FCC0000002QB("零星"),
    FCC0000002QC("一星"),
    FCC0000002QD("二星"),
    FCC0000002QE("三星"),
   ;

    public String display;

    RiskControlImportanceEnum(String display) {
        this.display = display;
    }

    public static RiskControlImportanceEnum of(String name) {
        for (RiskControlImportanceEnum value : RiskControlImportanceEnum.values()) {
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
