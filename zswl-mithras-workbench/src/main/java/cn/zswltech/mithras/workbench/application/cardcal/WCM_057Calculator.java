package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.workbench.application.port.cardcal.WorkbenchFinancialMetricFactorPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description: 上月净资产收益率  净利润/Average（期初净资产+期末净资产） 苍穹-利润表
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_057Calculator implements CardCalculator {
    @Resource
    private WorkbenchFinancialMetricFactorPort financialMetricFactorPort;

    @Override
    public String metricCode() {
        return "WCM_057";
    }

    @Override
    public String calculate() {
        LocalDate lastMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        try {
            Long factor1 = financialMetricFactorPort.getFactorValue("负债和所有者权益（或股东权益）总计@期末余额",
                    "资产负债表", lastMonth);
            Long factor2 = financialMetricFactorPort.getFactorValue("负债和所有者权益（或股东权益）总计@年初余额",
                    "资产负债表", lastMonth);
            Long factor3 = financialMetricFactorPort.getFactorValue("五、净利润（净亏损以“－”号填列）@本年累计数", "利润表", lastMonth);
            return BigDecimal.valueOf(factor3).multiply(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(factor1 + factor2), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))).setScale(2).toString();
        } catch (WorkbenchFinancialMetricFactorMissingException e) {
            return "上月财报准备中";
        }
    }
}
