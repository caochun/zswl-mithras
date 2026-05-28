package cn.zswltech.mithras.service.enums.projlifecycle;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

/**
 * @author luyi
 */

@Getter
public enum ProjLifecycleStatus implements PullDown {

    ESTABLISH_NOT_EFFECT("立项未生效"),
    REVIEW_NOT_START("立项生效未评审"),
    REVIEW_NOT_EFFECT("评审未生效"),
    CONTRACT_NOT_CREATE("评审生效未建合同"),
    CONTRACT_NOT_EFFECT("合同未生效"), NOT_PAY("合同生效未申请付款"),
    NOT_INVEST("已申请付款未投放"), NOT_SETTLE("已投放未结清"),
    SETTLE("已结清");

    ProjLifecycleStatus(String display) {
        this.display = display;
    }

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
