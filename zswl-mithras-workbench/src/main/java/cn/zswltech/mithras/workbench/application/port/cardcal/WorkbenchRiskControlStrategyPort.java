package cn.zswltech.mithras.workbench.application.port.cardcal;

public interface WorkbenchRiskControlStrategyPort {
    long countAbnormalStrategies();

    Long getCurrentValueOne(String metricName);
}
