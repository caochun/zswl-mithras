package cn.zswltech.mithras.service.enums.client;

/**
 * @author yibin
 */
public enum ClientTransferStatus {

    /**
     * 移交审批中
     */
    to_be_approved("移交审批中"),

    /**
     * 等待正式移交时间到
     */
    timed_approved("待移交"),

    approved("已移交"),

    rejected("审批拒绝");

    ClientTransferStatus(String display) {
        this.display = display;
    }

    public final String display;

}
