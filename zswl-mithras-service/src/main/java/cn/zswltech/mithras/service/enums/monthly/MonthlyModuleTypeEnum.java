package cn.zswltech.mithras.service.enums.monthly;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MonthlyModuleTypeEnum implements PullDown {

    AIR("实际利率法"),
    RP("剩余本金法"),
    STAMP_DUTY_PROJ("印花税-项目端"),
    STAMP_DUTY_FIN("印花税-资金端"),
    COST("成本计提"),
    ;


    public final String display;

    @Override
    public String display() {
        return display;
    }


    public static MonthlyModuleTypeEnum find(String name) {
        for (MonthlyModuleTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
