package cn.zswltech.mithras.service.enums.projlifecycle;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Objects;

/**
 * @create: 2022-10-21
 **/
public enum ProjStageEnum implements PullDown {
    PROJESTABLISH_STAGE("立项阶段"),
    PROJREVIEW_STAGE("评审阶段"),
    CONTRACT_STAGE("合同与投放阶段"),
    CONTRACTSETTLE_STAGE("结清阶段"),
    ;

    ProjStageEnum(String display){
        this.display = display;
    }
    public String display;
    @Override
    public String display() {
        return display;
    }

    public static ProjStageEnum find(String name) {
        for (ProjStageEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
