package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingPlanMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private FundReceiptRepayBaseInfoMapper repayBaseInfoMapper;
    @Resource
    private FundReceiptRepayCashFlowMapper repayCashFlowMapper;
    @Resource
    private FundReceiptFlowDetailMapper receiptFlowDetailMapper;

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
        Map<Long, Long> repayPrincipalMap = queryRemainingAmount(financingIds, FinancingTypeEnum.INDIRECT);
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

    private Map<Long, Long> queryRemainingAmount(Collection<Long> financingIdList, FinancingTypeEnum financingTypeEnum) {
        if (CollectionUtil.isEmpty(financingIdList)) {
            return Collections.emptyMap();
        }

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<FundReceiptRepayBaseInfo> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(FundReceiptRepayBaseInfo::getId);
        queryWrapper.in(FundReceiptRepayBaseInfo::getFinancingId, financingIdList);
        switch (financingTypeEnum) {
            case DIRECT:
                queryWrapper.eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name());
                break;
            case INDIRECT:
                queryWrapper.isNull(FundReceiptRepayBaseInfo::getFinancingType);
                break;
            default:
                break;
        }
        List<FundReceiptRepayBaseInfo> receiptRepayList = repayBaseInfoMapper.selectList(queryWrapper);
        List<Long> receiptRepayIdList = receiptRepayList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(receiptRepayIdList)) {
            return Collections.emptyMap();
        }

        List<FundReceiptRepayCashFlow> cashFlowList = repayCashFlowMapper.selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                .in(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayIdList)
                .ne(FundReceiptRepayCashFlow::getPhase, 0));

        if (CollectionUtil.isEmpty(cashFlowList)) {
            return Collections.emptyMap();
        }

        Map<String, List<FundReceiptFlowDetail>> flowDetailMap = Optional.ofNullable(receiptFlowDetailMapper.selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .in(FundReceiptFlowDetail::getCashFlowCode, cashFlowList.stream().map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toList()))
                        .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())))
                .orElse(new ArrayList<>()).stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        for (FundReceiptRepayCashFlow repayCashFlow : cashFlowList) {
            List<FundReceiptFlowDetail> flowDetailList = flowDetailMap.get(repayCashFlow.getCashFlowCode());
            if (CollectionUtil.isEmpty(flowDetailList)) {
                continue;
            }
            long alreadyWriteOffAmount = flowDetailList.stream()
                    .filter(f -> Objects.nonNull(f.getPrincipalAmount()))
                    .mapToLong(FundReceiptFlowDetail::getPrincipalAmount)
                    .sum();
            repayCashFlow.setPrincipleAmount(Optional.ofNullable(repayCashFlow.getPrincipleAmount()).orElse(0L) - alreadyWriteOffAmount);
        }
        return cashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId,
                Collectors.summingLong(e -> Objects.isNull(e.getPrincipleAmount()) ? 0 : e.getPrincipleAmount())));
    }

}
