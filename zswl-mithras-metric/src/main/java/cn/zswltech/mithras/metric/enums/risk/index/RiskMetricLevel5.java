package cn.zswltech.mithras.metric.enums.risk.index;

import lombok.Getter;

/**
 * @author yibin
 */
@Getter
public enum RiskMetricLevel5 {

    NORMAL("正常"), FOCUS("关注"), SECONDARY("次级"), SUSPICIOUS("可疑"), LOSS("损失");

    RiskMetricLevel5(String display) {
        this.display = display;
    }

    public final String display;
}
