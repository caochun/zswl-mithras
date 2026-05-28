package cn.zswltech.mithras.service.service.projfms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:03
 */
public enum ProjProcessState implements ProcessStatus {

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
    NEW_REJECT("新建审批拒绝", true),
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
     * 变更提交了审批-审批拒绝
     */
    CHANGE_REJECT("变更审批拒绝", true),
    /**
     * 变更提交了审批-审批通过
     */
    CHANGING_APPROVAL_PASS("变更审批通过",false),

    /**
     * 初始状态
     */
    UN_SUBMIT("未提交",false),
    /**
     * 提交了审批-审批未结束
     */
    UNDER_APPROVAL("审批中",false),

    CANCEL("已取消", false),
    //名称不统一，为和其他模块保持一致，冗余
    CANCELED("已取消", false),

    /**
     * 提交了审批-审批通过
     */
    APPROVAL_PASS("审批通过",true),
    /**
     * 提交了审批-审批拒绝
     */
    APPROVAL_REJECT("审批拒绝", true);

    ProjProcessState(String display,boolean finalState) {
        this.display = display;
        this.finalState = finalState;
    }

    public final String display;
    public final boolean finalState;

    public static ProjProcessState of(String code) {
        for (ProjProcessState value : ProjProcessState.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public int toInt() {
        return this.ordinal();
    }
}
