package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.workbench.application.port.cardcal.WorkbenchRiskControlStrategyPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description: 逾期率
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_061Calculator implements CardCalculator {
    @Resource
    private WorkbenchRiskControlStrategyPort riskControlStrategyPort;

    @Override
    public String metricCode() {
        return "WCM_061";
    }

    @Override
    public String calculate() {
        Long currentValueOne = riskControlStrategyPort.getCurrentValueOne("逾期率");
        if (currentValueOne == null) {
            return "0.00";
        }
        return new BigDecimal(currentValueOne)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).toString();
    }
}
