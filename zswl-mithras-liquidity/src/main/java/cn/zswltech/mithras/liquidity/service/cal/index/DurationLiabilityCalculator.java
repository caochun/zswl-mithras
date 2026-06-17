package cn.zswltech.mithras.liquidity.service.cal.index;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.liquidity.bo.LiquidityFundReceiptFlowDetailSnapshot;
import cn.zswltech.mithras.liquidity.bo.LiquidityFundReceiptFlowPlanSnapshot;
import cn.zswltech.mithras.liquidity.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 负债久期
 *
 * 数据范围：合同状态=起息，应付日＞计算日的融资合同
 * 1. 单个合同的久期：sum（每期应偿还本息*（应付日-计算日））/计算日（不含计算日当天）以后剩余未付本息合计/365）
 * 2. 单个合同的久期根据计算日（不含计算日当天）以后未付本息合计值进行加权计算得出负债久期：sum（每个合同计算日后未付本息合计*合同久期）/全部合同计算日以后未付本息合计）
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Component
public class DurationLiabilityCalculator extends AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> {

    private static final String REPAY_CASH_FLOW_ITEM = "REPAY";

    @Override
    public void calculate(Object obj, LiquidityIndexCalculatorBo bo) {
        // 取现金流，改为合同维度
        BigDecimal result = BigDecimal.ZERO;
        Map<Long, List<LiquidityFundReceiptFlowPlanSnapshot>> flowPlanMap = LiquidityIndicatorIndexHolder.FUND_RECEIPT_FLOW_PLAN.entrySet().stream().filter(f -> f.getKey().isAfter(bo.getQueryDateStart()))
                .map(Map.Entry::getValue).map(m -> m.get(REPAY_CASH_FLOW_ITEM)).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.groupingBy(LiquidityFundReceiptFlowPlanSnapshot::getReceiptRepayId));

        Map<Long, BigDecimal> financingDurationMap = flowPlanMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, flowPlanEntry -> {
            // sum(每期应偿还本息 * （应付日-计算日）)
            return flowPlanEntry.getValue().stream().map(m -> {
                long repayAmount = LongUtil.null2zero(m.getPrincipalAmount()) + LongUtil.null2zero(m.getInterestAmount());
                long gapDay = ChronoUnit.DAYS.between(bo.getQueryDateStart(), m.getCashFlowDate());
                return BigDecimal.valueOf(repayAmount).multiply(BigDecimal.valueOf(gapDay));
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
        }));
        // 计算日（不含计算日当天）以后剩余未付本息合计
        Map<Long, BigDecimal> remainingMap = flowPlanMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, flowPlanEntry -> {
            Map<String, List<LiquidityFundReceiptFlowDetailSnapshot>> flowDetailMap = LiquidityIndicatorIndexHolder.FUND_RECEIPT_FLOW_DETAIL.getOrDefault(flowPlanEntry.getKey(), new HashMap<>());
            return flowPlanEntry.getValue().stream().map(flowPlan -> {
                BigDecimal remainingAmount = BigDecimal.valueOf(LongUtil.null2zero(flowPlan.getPrincipalAmount()) + LongUtil.null2zero(flowPlan.getInterestAmount()));
                // 处理单笔现金流
                List<LiquidityFundReceiptFlowDetailSnapshot> flowDetailList = flowDetailMap.get(flowPlan.getCashFlowCode());
                if (CollectionUtil.isNotEmpty(flowDetailList)) {
                    long alreadyWriteOffAmount = flowDetailList.stream().filter(f -> Objects.nonNull(f.getTotalAmount())).mapToLong(LiquidityFundReceiptFlowDetailSnapshot::getTotalAmount).sum();
                    remainingAmount = BigDecimal.valueOf(LongUtil.null2zero(flowPlan.getPrincipalAmount()) + LongUtil.null2zero(flowPlan.getInterestAmount()) - alreadyWriteOffAmount);
                }
                return remainingAmount;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
        }));
        // 每个合同计算日后未付本息合计*每个合同的久期 = 每期应偿还本息*（应付日-计算日））/365
        BigDecimal reduce = financingDurationMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        // 全部合同计算日以后未付本息合计
        BigDecimal remainingSum = remainingMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if(remainingSum.compareTo(BigDecimal.ZERO) != 0) {
            result = reduce.divide(BigDecimal.valueOf(365).multiply(remainingSum), 10, RoundingMode.HALF_UP);
        }
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name()));
    }




    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_INDEX;
    }

    @Override
    public String indexName() {
        return "durationLiability";
    }
}
