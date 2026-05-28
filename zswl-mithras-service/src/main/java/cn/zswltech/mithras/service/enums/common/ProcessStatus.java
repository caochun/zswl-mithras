package cn.zswltech.mithras.service.enums.common;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:56
 */
public enum ProcessStatus {
    /**
     * 审批中
     */
    UNDER_APPROVAL("审批中"),
    UN_SUBMIT("未提交"),
    APPROVAL_PASS("审批通过"),
    APPROVAL_REJECT("审批拒绝"),
    CANCEL("已关闭"),
    CANCELED("已取消"),

    //资产管理岗审批状态确认机
    CONFIRMED("已确认"),
    UN_CONFIRMED("待确认");

    ProcessStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ProcessStatus of(String code) {
        for (ProcessStatus value : ProcessStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
