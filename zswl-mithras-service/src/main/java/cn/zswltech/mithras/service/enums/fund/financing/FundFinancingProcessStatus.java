package cn.zswltech.mithras.service.enums.fund.financing;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.service.projfms.ProcessStatus;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author: jackerhe
 * @date: 2023/2/21 5:04 下午
 **/
public enum FundFinancingProcessStatus implements PullDown, ProcessStatus {
    /**
     * 初始状态
     */
    NEW_UN_SUBMIT("新建未提交",false),
    /**
     * 新建提交了审批-审批未结束
     */
    NEW_UNDER_APPROVAL("新建审批中",false),
    /**
     * 新建提交了审批-关闭（作废）
     */
    CANCEL_NEW("取消新建",true),
    /**
     * 新建提交了审批-审批通过
     */
    NEW_APPROVAL_PASS("新建审批通过",false),
    /**
     * 新建提交了审批-审批拒绝
     */
    NEW_APPROVAL_REJECT("新建审批拒绝", false),
    /**
     * 变更未提交审批
     */
    CHANGING_UN_SUBMIT("变更未提交",false),
    /**
     * 变更提交了审批-审批未结束
     */
    CHANGING_UNDER_APPROVAL("变更审批中",false),
    /**
     * 变更提交了审批-审批未结束-取消流程
     * 变更提交了审批-审批未结束-关闭
     */
    CANCEL_CHANGE("取消变更",false),
    /**
     * 变更提交了审批-审批通过
     */
    CHANGING_APPROVAL_PASS("变更审批通过",false),
    /**
     * 变更直接生效
     */
    CHANGING_EFFECT("变更生效", false),
    /**
     * 变更审批拒绝
     */
    CHANGING_APPROVAL_REJECT("变更审批拒绝", false)
    ;

    FundFinancingProcessStatus(String display, boolean finalState) {
        this.display = display;
        this.finalState = finalState;
    }

    private final String display;
    private final boolean finalState;

    public static FundFinancingProcessStatus of(String code) {
        for (FundFinancingProcessStatus value : FundFinancingProcessStatus.values()) {
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

    public static boolean isInProcess(String currentProcessStatus) {
        return Objects.equals(currentProcessStatus, NEW_UNDER_APPROVAL.name()) || Objects.equals(currentProcessStatus, CHANGING_UNDER_APPROVAL.name());
    }
}
