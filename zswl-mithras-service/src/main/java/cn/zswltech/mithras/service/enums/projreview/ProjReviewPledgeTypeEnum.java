package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 抵押类型
 **/
public enum ProjReviewPledgeTypeEnum implements PullDown {

    MOVABLE_PROPERTY("动产抵押"),
    REAL_ESTATE("不动产抵押"),
    ;

    public String display;

    ProjReviewPledgeTypeEnum(String display) {
        this.display = display;
    }

    public static cn.zswltech.mithras.service.enums.contract.PledgeTypeEnum of(String code) {
        for (cn.zswltech.mithras.service.enums.contract.PledgeTypeEnum value : cn.zswltech.mithras.service.enums.contract.PledgeTypeEnum.values()) {
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
