package cn.zswltech.mithras.ftp.oldftp.fms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/12 10:06
 */
public enum FtpEvent {
    /**
     * 提交审批
     */
    SUBMIT_APPROVAL,
    /**
     * 审批通过
     */
    APPROVAL_PASS,
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
    MODIFY_WITHDRAW;
}
