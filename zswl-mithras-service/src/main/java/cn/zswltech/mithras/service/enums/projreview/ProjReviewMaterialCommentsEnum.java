package cn.zswltech.mithras.service.enums.projreview;

import cn.zswltech.mithras.common.enums.PullDown;

public enum ProjReviewMaterialCommentsEnum implements PullDown {

    AUDITED("已审核"),
    SUPPLEMENT_INFO("补充基础资料")
    ;

    ProjReviewMaterialCommentsEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static ProjReviewMaterialCommentsEnum of(String code) {
        for (ProjReviewMaterialCommentsEnum value : ProjReviewMaterialCommentsEnum.values()) {
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
