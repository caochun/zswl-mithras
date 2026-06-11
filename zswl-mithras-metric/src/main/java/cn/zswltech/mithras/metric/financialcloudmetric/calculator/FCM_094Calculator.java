package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.DepartmentPaymentCache;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 平均新增合同利率  本年新增合同加权平均利率
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_094Calculator implements FinancialCloudMetricCalculator {

    @Resource
    private DepartmentPaymentCache departmentPaymentCache;

    @Resource
    private ContractNewestPriceReader contractNewestPriceReader;

    @Override
    public String metricCode() {
        return "FCM_094";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        ConcurrentHashMap<Long, List<PaymentActualDetail>> yearPaymentActual = departmentPaymentCache.getYearPaymentActual(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        if (CollUtil.isEmpty(yearPaymentActual)) {
            return BigDecimal.ZERO;
        }

        Set<Long> contractIds = yearPaymentActual.keySet();
        // 获取这些合同的最新版本的金额(key) 和 利率(value)
        Map<Long, Pair<Long, Integer>> rateMap = contractNewestPriceReader.queryNewestRate(contractIds);
        if (rateMap.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 计算合同总金额
        BigDecimal totalAmount = yearPaymentActual.values().stream().map(list -> list.stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = BigDecimal.ZERO;
        // 计算加权总额
        for (Map.Entry<Long, Pair<Long, Integer>> entry : rateMap.entrySet()) {
            List<PaymentActualDetail> paymentActualDetails = yearPaymentActual.get(entry.getKey());
            if (CollUtil.isEmpty(paymentActualDetails)) {
                continue;
            }
            long contractAmount = paymentActualDetails.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            Integer contractRate = entry.getValue().getValue();
            average = BigDecimal.valueOf(contractAmount)
                    .multiply(BigDecimal.valueOf(contractRate))
                    .divide(totalAmount, 10, RoundingMode.HALF_UP)
                    .add(average);
        }
        return average.setScale(4, RoundingMode.HALF_UP);
    }
}
