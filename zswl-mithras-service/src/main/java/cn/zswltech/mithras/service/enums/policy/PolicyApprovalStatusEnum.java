package cn.zswltech.mithras.service.enums.policy;

import cn.zswltech.mithras.common.enums.PullDown;

/**
 * @create: 2023-06-16
 **/
public enum PolicyApprovalStatusEnum implements PullDown {
    NEW_UN_SUBMIT("新建未提交"),
    NEW_UNDER_APPROVAL("新建审批中"),
    CANCEL_NEW("取消新建"),
    NEW_APPROVAL_PASS("新建审批通过"),
    CHANGING_UN_SUBMIT("变更未提交"),
    CHANGING_UNDER_APPROVAL("变更审批中"),
    CANCEL_CHANGE("取消变更"),
    CHANGING_APPROVAL_PASS("变更审批通过"),
    APPROVAL_REJECT("审批拒绝"),

    ;

    PolicyApprovalStatusEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static PolicyApprovalStatusEnum of(String code) {
        for (PolicyApprovalStatusEnum value : PolicyApprovalStatusEnum.values()) {
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
