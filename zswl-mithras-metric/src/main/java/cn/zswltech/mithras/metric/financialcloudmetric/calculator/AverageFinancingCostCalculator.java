package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingPlanMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: （%）
 * @author: zhaozhengkang
 * @date: 2023/4/25 09:49
 */
public abstract class AverageFinancingCostCalculator implements FinancialCloudMetricCalculator {
    @Resource
    protected FundFinancingBaseInfoMapper financingBaseInfoMapper;
    @Resource
    private FundFinancingPlanMapper financingPlanMapper;
    @Resource
    private FinancingRemainingAmountReader remainingAmountReader;

    private final Object lock = new Object();
    private static final Map<Long, Integer> COST = new HashMap<>();

    public static void clear() {
        COST.clear();
    }

    protected abstract List<FundFinancingBaseInfo> getFinancings(LocalDate dateTime);

    protected BigDecimal cal(LocalDate dateTime) {
        List<FundFinancingBaseInfo> financings = getFinancings(dateTime);
        if (financings.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<Long> financingIds = financings.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
        if (COST.isEmpty()) {
            synchronized (lock) {
                if (COST.isEmpty()) {
                    Map<Long, Integer> collect = financingPlanMapper.selectList(Wrappers.<FundFinancingPlan>lambdaQuery())
                            .stream().filter(info -> info.getComprehensiveInterestRate() != null)
                            .collect(Collectors.toMap(FundFinancingPlan::getFinancingId, FundFinancingPlan::getComprehensiveInterestRate, (a, b) -> a));
                    COST.putAll(collect);
                }
            }
        }
        // 这里不能取融资金额，需要使用剩余本金
        Map<Long, Long> repayPrincipalMap = remainingAmountReader.queryRemainingAmount(financingIds, FinancingTypeEnum.INDIRECT);
        if (repayPrincipalMap.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = repayPrincipalMap.values().stream().map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal averageCost = BigDecimal.ZERO;
        for (FundFinancingBaseInfo financing : financings) {
            Integer cost = COST.get(financing.getId());
            if (cost == null) {
                continue;
            }
            if (repayPrincipalMap.get(financing.getId()) != null) {
                averageCost = averageCost.add(
                        new BigDecimal(LongUtil.null2zero(repayPrincipalMap.get(financing.getId())))
                                .multiply(new BigDecimal(cost))
                                .divide(total, 20, RoundingMode.HALF_UP));
            }
        }
        return averageCost.setScale(10, RoundingMode.HALF_UP);
    }
}
