package cn.zswltech.mithras.projectprocess.enums.projreview;


import cn.zswltech.mithras.service.config.enumscan.PullDown;

@Deprecated
public enum ProjReviewProcessStatus implements PullDown {
    UNDER_APPROVAL("审批中"),
    UN_SUBMIT("未提交"),
    APPROVAL_PASS("审批通过"),
    APPROVAL_REJECT("审批拒绝")
    ;

    ProjReviewProcessStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ProjReviewProcessStatus of(String code) {
        for (ProjReviewProcessStatus value : ProjReviewProcessStatus.values()) {
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
