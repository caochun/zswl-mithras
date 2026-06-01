package cn.zswltech.mithras.service.enums.newftp;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.service.projfms.ProcessStatus;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/12 09:59
 */
public enum NewFtpProcessStatus implements PullDown, ProcessStatus {
    /**
     * 初始状态
     */
    NEW_UN_SUBMIT("新建未提交", false),
    /**
     * 新建提交了审批-审批未结束
     */
    NEW_UNDER_APPROVAL("新建审批中", false),
    /**
     * 新建提交了审批-关闭（作废）
     */
    CANCEL_NEW("取消新建", true),
    /**
     * 新建提交了审批-审批通过
     */
    NEW_APPROVAL_PASS("新建审批通过", false),
    /**
     * 变更未提交审批
     */
    CHANGING_UN_SUBMIT("变更未提交", false),
    /**
     * 变更提交了审批-审批未结束
     */
    CHANGING_UNDER_APPROVAL("变更审批中", false),
    /**
     * 变更提交了审批-审批未结束-取消流程
     * 变更提交了审批-审批未结束-关闭
     */
    CANCEL_CHANGE("取消变更", false),
    /**
     * 变更提交了审批-审批通过
     */
    CHANGING_APPROVAL_PASS("变更审批通过", false),
    ;

    NewFtpProcessStatus(String display, boolean finalState) {
        this.display = display;
        this.finalState = finalState;
    }

    private final String display;
    private final boolean finalState;

    public static NewFtpProcessStatus of(String code) {
        for (NewFtpProcessStatus value : NewFtpProcessStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public int toInt() {
        return this.ordinal();
    }

    @Override
    public String display() {
        return display;
    }
}
