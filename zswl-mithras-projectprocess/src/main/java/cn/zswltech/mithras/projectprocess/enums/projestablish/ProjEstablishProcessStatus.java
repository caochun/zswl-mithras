package cn.zswltech.mithras.projectprocess.enums.projestablish;

@Deprecated
public enum ProjEstablishProcessStatus {
    /**
     * 审批中
     */
    UNDER_APPROVAL("审批中"),
    UN_SUBMIT("未提交"),
    APPROVAL_PASS("审批通过"),
    APPROVAL_REJECT("审批拒绝")
    ;

    ProjEstablishProcessStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ProjEstablishProcessStatus of(String code) {
        for (ProjEstablishProcessStatus value : ProjEstablishProcessStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
