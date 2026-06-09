package cn.zswltech.mithras.riskcontrol.application;

import cn.zswltech.mithras.api.riskcontrol.RiskControlOpinionMonitorApi;

import java.util.Set;

public interface RiskControlOpinionMonitorApplicationService extends RiskControlOpinionMonitorApi {
    Set<String> focusClientUscCodes();
}
