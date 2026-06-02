package cn.zswltech.mithras.service.service.workbench.cardcal;

import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlStrategyService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description: 不良率
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_063Calculator implements CardCalculator {
    @Resource
    private RiskControlStrategyService riskControlStrategyService;

    @Override
    public String metricCode() {
        return "WCM_063";
    }

    @Override
    public String calculate() {
        RiskControlStrategy theOne = riskControlStrategyService.getOne(Wrappers.<RiskControlStrategy>lambdaQuery()
                .eq(RiskControlStrategy::getMetricName, "不良率"));
        Long currentValueOne = theOne.getCurrentValueOne();
        if (currentValueOne == null) {
            return "0.00";
        }
        return new BigDecimal(currentValueOne)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString();
    }
}
