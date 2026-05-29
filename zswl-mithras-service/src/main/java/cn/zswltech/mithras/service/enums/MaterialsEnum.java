package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;

public enum MaterialsEnum implements PullDown, IMaterialsTypeConvert {

    OVERDUE_COLLECTION("逾期催收"),
    DEDUCTION_INTEREST("罚息减免材料"),
    NEW_DEDUCTION_INTEREST("罚息减免材料"),
//    ASSET_CLASSIFY_REVIEW("五级分类复核审批材料"),
//    ASSET_CLASSIFY_CHECK_REPORT("五级分类检查报告材料"),
//    FUND_GUARANTEE_MANAGEMENT("资金-担保材料")
    ;

    MaterialsEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static MaterialsEnum of(String code) {
        for (MaterialsEnum value : MaterialsEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String businessModule() {
        return "OVERDUE_COLLECTION_REDUCTION";
    }

    @Override
    public String display() {
        return display;
    }
}
