package cn.zswltech.mithras.credit.domain.groupcredit.establish.enums;


import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * 集团授信立项流程状态
 *
 * @author wangchuanhao
 * @date 2022/9/21 12:48 AM
 */
public enum GroupCreditEstablishProcessStatus implements PullDown {
    /**
     * 审批中
     */
    NEW_UN_SUBMIT("新建未提交"),
    NEW_UNDER_APPROVAL("新建审批中"),
    CANCEL_NEW("取消新建"),
    NEW_APPROVAL_PASS("新建审批通过"),
//    NEW_REJECT("新建审批拒绝"),
    CHANGING_UN_SUBMIT("变更未提交"),
    CHANGING_UNDER_APPROVAL("变更审批中"),
    CANCEL_CHANGE("取消变更"),
//    CHANGE_REJECT("变更审批拒绝"),
    CHANGING_APPROVAL_PASS("变更审批通过"),

    ;

    GroupCreditEstablishProcessStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static GroupCreditEstablishProcessStatus of(String code) {
        for (GroupCreditEstablishProcessStatus value : GroupCreditEstablishProcessStatus.values()) {
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
