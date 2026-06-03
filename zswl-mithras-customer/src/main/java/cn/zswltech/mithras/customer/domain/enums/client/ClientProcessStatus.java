package cn.zswltech.mithras.customer.domain.enums.client;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * 流程状态
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public enum ClientProcessStatus implements PullDown {

    /**
     * 新建时无需审批
     */
    //NEW_NO_APPROVAL_REQUIRED("-"),

//    /**
//     * 修改后无需审批
//     */
//    NO_APPROVAL_REQUIRED("变更中（无需审批）"),

    /**
     * 修改后审批中
     */
    UNDER_APPROVAL("变更审批中"),

    /**
     * 修改后待提交审批
     */
    UN_SUBMIT("变更中"),

    /**
     * 变更审批通过
     */
    APPROVAL_PASS("变更审批通过"),

    /**
     * 客户发起流程后取消
     */
    APPROVAL_REJECT("取消变更"),

    /**
     * 新建时无需审批
     * 无需审批时 修改数据后点击确认
     */
    EFFECT_BLANK("-"),
    ;

    ClientProcessStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ClientProcessStatus of(String code) {
        for (ClientProcessStatus value : ClientProcessStatus.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
