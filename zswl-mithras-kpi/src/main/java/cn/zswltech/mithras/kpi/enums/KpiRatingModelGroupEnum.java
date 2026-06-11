package cn.zswltech.mithras.kpi.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户评级内评等级与ecl引擎分组枚举映射
 */
@AllArgsConstructor
@Getter
public enum KpiRatingModelGroupEnum implements PullDown {
    client_qxj_service("政信"),
    client_djs_service("政信"),
    client_fzzy_service("非制造业"),
    client_xny_service("新能源"),
    client_S4_1_m_d_f("制造业"),
    client_hymx("航运");

    private final String display;


    public static KpiRatingModelGroupEnum find(String name) {
        for (KpiRatingModelGroupEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return this.display;
    }
}
