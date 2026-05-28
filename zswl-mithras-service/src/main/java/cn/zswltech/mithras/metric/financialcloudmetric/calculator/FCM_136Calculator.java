package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

/**
 * @description: 逾期率 当前处于逾期的项目逾期未还租金/剩余本金总额
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_136Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;

    @Override
    public String metricCode() {
        return "FCM_136";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        LocalDate lastDayOfMonth = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        if (dateTime.getMonthValue() == LocalDate.now().getMonthValue()) {
            lastDayOfMonth = LocalDate.now();
        }
        // 1. 计算系统剩余逾期本金+利息
        Triple<BigDecimal, BigDecimal, BigDecimal> triple = remainingPrincipalServiceImpl.overdueAmount(lastDayOfMonth);
        Map<Long, Pair<BigDecimal, BigDecimal>> prePrincipalInterest = remainingPrincipalServiceImpl.prePrincipalInterest(lastDayOfMonth);
        BigDecimal prePrincipal = BigDecimal.ZERO;
        BigDecimal preInterest = BigDecimal.ZERO;
        for (Map.Entry<Long, Pair<BigDecimal, BigDecimal>> entry : prePrincipalInterest.entrySet()) {
            Pair<BigDecimal, BigDecimal> pair = entry.getValue();
            prePrincipal = prePrincipal.add(pair.getKey());
            preInterest = preInterest.add(pair.getValue());
        }
        // 3. 计算R
        if (triple.getRight().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return triple.getLeft().add(triple.getMiddle())
                .multiply(BigDecimal.valueOf(1000000))
                .divide(triple.getLeft().add(triple.getMiddle()).add(prePrincipal), 4, RoundingMode.HALF_UP);
    }
}
