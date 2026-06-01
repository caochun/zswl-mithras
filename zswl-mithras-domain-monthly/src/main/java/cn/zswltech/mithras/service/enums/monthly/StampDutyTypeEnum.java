package cn.zswltech.mithras.service.enums.monthly;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StampDutyTypeEnum implements PullDown {

    PROJ("项目端"),
    FIN_FIN("资金端-融资"),
    FIN_DIRECT_FIN("资金端-直融"),
    ;


    public final String display;

    @Override
    public String display() {
        return display;
    }


    public static StampDutyTypeEnum find(String name) {
        for (StampDutyTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
