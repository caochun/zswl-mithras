package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.MissingFactorException;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: 上月资产负债率
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_056Calculator implements CardCalculator {
    @Resource
    private RiskMetricFactorService factorService;

    @Override
    public String metricCode() {
        return "WCM_056";
    }

    @Override
    public String calculate() {
        LocalDate lastMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        try {
            RiskMetricFactor factor1 = factorService.getFactor("负债合计@期末余额", "资产负债表", lastMonth);
            RiskMetricFactor factor2 = factorService.getFactor("资产总计@期末余额", "资产负债表", lastMonth);
            if (factor1 == null || factor2 == null) {
                return "0.00";
            }
            if (factor2.getFactorValue() == null || factor2.getFactorValue() == 0L) {
                return "0.00";
            }
            return new BigDecimal(factor1.getFactorValue())
                    .divide(new BigDecimal(factor2.getFactorValue()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2).toString();
        } catch (MissingFactorException e) {
            return "上月财报准备中";
        }
    }
}
