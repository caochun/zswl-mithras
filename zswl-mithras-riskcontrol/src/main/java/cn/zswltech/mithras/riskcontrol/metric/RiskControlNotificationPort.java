package cn.zswltech.mithras.riskcontrol.metric;

import cn.zswltech.mithras.riskcontrol.common.AlertState;

import java.util.List;

public interface RiskControlNotificationPort {

    void sendIndicatorWarning(List<Long> to, Long strategyId, String metricCode, AlertState alertState);
}
