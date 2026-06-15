package cn.zswltech.mithras.workbench.application.cardcal;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description: 风险超限情况
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_059Calculator implements CardCalculator {
    @Resource
    private WorkbenchRiskControlStrategyPort riskControlStrategyPort;

    @Override
    public String metricCode() {
        return "WCM_059";
    }

    @Override
    public String calculate() {
        return String.valueOf(riskControlStrategyPort.countAbnormalStrategies());
    }
}
