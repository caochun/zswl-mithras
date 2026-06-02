package cn.zswltech.mithras.service.service.workbench.cardcal;

import cn.zswltech.mithras.riskcontrol.common.AlertState;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlStrategyService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 风险超限情况
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_059Calculator implements CardCalculator {
    @Resource
    private RiskControlStrategyService riskControlStrategyService;

    @Override
    public String metricCode() {
        return "WCM_059";
    }

    @Override
    public String calculate() {
        List<RiskControlStrategy> riskControlStrategies = riskControlStrategyService.list(Wrappers.<RiskControlStrategy>lambdaQuery()
                .isNotNull(RiskControlStrategy::getLimitValueOne));
        long count = riskControlStrategies.stream().map(RiskControlStrategy::currentAlertState).filter(alertState -> !alertState.equals(AlertState.NORMAL)).count();
        return String.valueOf(count);
    }
}
