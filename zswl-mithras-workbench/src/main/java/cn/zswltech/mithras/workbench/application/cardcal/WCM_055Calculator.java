package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.workbench.application.port.cardcal.WorkbenchFinancialMetricFactorPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: 上月流动比率
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_055Calculator implements CardCalculator {
    @Resource
    private WorkbenchFinancialMetricFactorPort financialMetricFactorPort;

    @Override
    public String metricCode() {
        return "WCM_055";
    }

    @Override
    public String calculate() {
        //FCM_132
        LocalDate lastMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        try {
            Long factor1 = financialMetricFactorPort.getFactorValue("流动资产合计@期末余额", "资产负债表", lastMonth);
            Long factor2 = financialMetricFactorPort.getFactorValue("流动负债合计@期末余额", "资产负债表", lastMonth);
            if (factor1 == null || factor2 == null) {
                return "0.00";
            }
            if (factor2 == 0L) {
                return "0.00";
            }
            return new BigDecimal(factor1)
                    .divide(new BigDecimal(factor2), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2).toString();
        } catch (WorkbenchFinancialMetricFactorMissingException e) {
            return "上月财报准备中";
        }
    }
}
