package cn.zswltech.mithras.projectprocess.projlifecycle.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * @create: 2022-10-26
 **/
public enum ProjLifecycleEventTypeEnum implements PullDown {
    APPROVAL("审批流程"),
    ;

    ProjLifecycleEventTypeEnum(String display){
        this.display = display;
    }
    public String display;

    public static ProjLifecycleEventTypeEnum of(String code) {
        for (ProjLifecycleEventTypeEnum value : ProjLifecycleEventTypeEnum.values()) {
            if (value.name().equals(code)) {
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
