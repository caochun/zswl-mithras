package cn.zswltech.mithras.associationreport.enums;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 18:56
 */
public enum AssociationProcessStatusEnum {
    /**
     * 审批中
     */
    UNDER_APPROVAL("审批中"),
    UN_SUBMIT("待提交"),
    APPROVAL_PASS("审批通过"),
    APPROVAL_REJECT("审批拒绝"),
    CANCEL("已关闭");

    AssociationProcessStatusEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static AssociationProcessStatusEnum of(String code) {
        for (AssociationProcessStatusEnum value : AssociationProcessStatusEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
