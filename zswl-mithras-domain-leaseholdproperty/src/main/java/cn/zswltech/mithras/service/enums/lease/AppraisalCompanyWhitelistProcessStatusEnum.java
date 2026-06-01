package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@AllArgsConstructor
@Getter
public enum AppraisalCompanyWhitelistProcessStatusEnum implements PullDown {
    NEW_UN_SUBMIT("新建未提交"),
    NEW_UNDER_APPROVAL("新建审批中"),
    CANCEL_NEW("取消新建"),
    NEW_APPROVAL_PASS("新建审批通过"),
    NEW_REJECT("新建审批拒绝"),

    CHANGE_UN_SUBMIT("变更未提交"),
    CHANGE_UNDER_APPROVAL("变更审批中"),
    CANCEL_CHANGE("取消变更"),
    CHANGE_REJECT("变更审批拒绝"),
    CHANGE_APPROVAL_PASS("变更审批通过"),

    OUT_UN_SUBMIT("出库未提交"),
    OUT_UNDER_APPROVAL("出库审批中"),
    CANCEL_OUT("取消出库"),
    OUT_REJECT("出库审批拒绝"),
    OUT_APPROVAL_PASS("出库审批通过");

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
