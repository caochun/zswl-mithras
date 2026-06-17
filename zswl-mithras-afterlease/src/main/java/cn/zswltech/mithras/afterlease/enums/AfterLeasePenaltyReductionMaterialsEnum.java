package cn.zswltech.mithras.afterlease.enums;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AfterLeasePenaltyReductionMaterialsEnum implements PullDown, IMaterialsTypeConvert {
    OVERDUE_COLLECTION("逾期催收"),
    DEDUCTION_INTEREST("罚息减免材料");

    private final String display;

    @Override
    public String businessModule() {
        return "OVERDUE_COLLECTION_REDUCTION";
    }

    @Override
    public String display() {
        return display;
    }
}
