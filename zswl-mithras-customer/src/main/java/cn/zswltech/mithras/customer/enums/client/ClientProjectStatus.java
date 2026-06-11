package cn.zswltech.mithras.customer.enums.client;

import cn.zswltech.mithras.foundation.metadata.PullDown;

/**
 * 流程状态
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public enum ClientProjectStatus implements PullDown {

    NON_ESTABLISH("未立项"),
    ESTABLISH("立项"),
    PRICING("定价"),
    REVIEW("评审"),
    CONTRACT("合同"),
    PAYMENT("付款申请")
    ;

    ClientProjectStatus(String display) {
        this.display = display;
    }

    public final String display;

    public static ClientProjectStatus of(String code) {
        for (ClientProjectStatus value : ClientProjectStatus.values()) {
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
