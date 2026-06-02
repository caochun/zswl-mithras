package cn.zswltech.mithras.riskcontrol.opinion;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @author yibin
 */
public enum RiskControlOpinionHandleStatus implements PullDown {

    UNSUBMITTED("未提交"),
    IGNORED("无需处理"),
    CLOSED("已关闭"),
    PEND_HANDLE("待处理"),
    HANDLE_ING("处理中"),
    REJECTED("处理被拒绝"),
    HANDLED("已处理");

    public final String display;

    RiskControlOpinionHandleStatus(String display) {
        this.display = display;
    }


    public static RiskControlOpinionHandleStatus of(String name) {
        for (RiskControlOpinionHandleStatus value : RiskControlOpinionHandleStatus.values()) {
            if (value.name().equals(name)) {
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
