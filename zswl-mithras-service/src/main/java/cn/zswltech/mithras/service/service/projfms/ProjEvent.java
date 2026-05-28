package cn.zswltech.mithras.service.service.projfms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/2 17:45
 */
public enum ProjEvent {
    /**
     * 新建保存
     */
    NEW_SAVE,
    /**
     * 提交审批
     */
    SUBMIT_APPROVAL,
    /**
     * 关闭
     */
    DISABLE,
    /**
     * 审批通过
     */
    APPROVAL_PASS,
    /**
     * 变更保存
     */
    MODIFY_SAVE,
    /**
     * 取消新建
     */
    NEW_WITHDRAW,
    /**
     * 取消变更
     */
    MODIFY_WITHDRAW,
    /**
     * 新建审批拒绝
     */
    NEW_REJECT,
    /**
     * 变更审批拒绝
     */
    MODIFY_REJECT;
}
