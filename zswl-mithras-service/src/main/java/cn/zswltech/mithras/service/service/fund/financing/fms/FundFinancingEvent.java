package cn.zswltech.mithras.service.service.fund.financing.fms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/12 10:06
 */
public enum FundFinancingEvent {
    /**
     * 提交审批
     */
    SUBMIT_APPROVAL,
    /**
     * 审批通过
     */
    APPROVAL_PASS,
    /**
     * 审批拒绝
     */
    APPROVAL_REJECT,
    /**
     * 取消新建
     */
    NEW_WITHDRAW,
    /**
     * 变更保存
     */
    MODIFY_SAVE,
    /**
     * 取消变更
     */
    MODIFY_WITHDRAW,
    /**
     * 确认
     */
    CONFIRM
}
