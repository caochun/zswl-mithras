package cn.zswltech.mithras.workbench.application.cardcal;

public interface WorkbenchRiskControlStrategyPort {
    long countAbnormalStrategies();

    Long getCurrentValueOne(String metricName);
}
