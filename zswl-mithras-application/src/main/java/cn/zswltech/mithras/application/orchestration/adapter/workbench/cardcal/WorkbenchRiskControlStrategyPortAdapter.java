package cn.zswltech.mithras.application.orchestration.adapter.workbench.cardcal;

import cn.zswltech.mithras.riskcontrol.common.AlertState;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyMapper;
import cn.zswltech.mithras.workbench.application.port.cardcal.WorkbenchRiskControlStrategyPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class WorkbenchRiskControlStrategyPortAdapter implements WorkbenchRiskControlStrategyPort {
    @Resource
    private RiskControlStrategyMapper riskControlStrategyMapper;

    @Override
    public long countAbnormalStrategies() {
        return riskControlStrategyMapper.selectList(Wrappers.<RiskControlStrategy>lambdaQuery()
                        .isNotNull(RiskControlStrategy::getLimitValueOne))
                .stream()
                .map(RiskControlStrategy::currentAlertState)
                .filter(alertState -> !AlertState.NORMAL.equals(alertState))
                .count();
    }

    @Override
    public Long getCurrentValueOne(String metricName) {
        RiskControlStrategy strategy = riskControlStrategyMapper.selectOne(Wrappers.<RiskControlStrategy>lambdaQuery()
                .eq(RiskControlStrategy::getMetricName, metricName)
                .last("LIMIT 1"));
        return strategy == null ? null : strategy.getCurrentValueOne();
    }
}
