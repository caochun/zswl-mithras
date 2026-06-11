package cn.zswltech.mithras.projectprocess.enums.projreview;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectFixedValueBasisEnum implements PullDown {

    //，符合租赁业务评估管理细则、租赁物管理办法要求
    STANDARD("以评估价值为准"),

    OTHER("其他");

    public String display;

    ProjectFixedValueBasisEnum(String display) {
        this.display = display;
    }

    public static ProjectFixedValueBasisEnum of(String code) {
        for (ProjectFixedValueBasisEnum value : ProjectFixedValueBasisEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @JsonValue
    public String getDisplay() {
        return display;
    }

    @Override
    public String display() {
        return display;
    }
}
