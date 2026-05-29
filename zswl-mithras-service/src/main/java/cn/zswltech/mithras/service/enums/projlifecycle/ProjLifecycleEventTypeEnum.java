package cn.zswltech.mithras.service.enums.projlifecycle;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;

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
