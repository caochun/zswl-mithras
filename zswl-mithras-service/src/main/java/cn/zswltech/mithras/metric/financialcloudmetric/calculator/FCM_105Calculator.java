//package cn.zswltech.mithras.metric.financialcloudmetric.calculator;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.DepartmentPaymentCache;
//import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
//import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
//import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
//import cn.zswltech.mithras.service.service.contract.ContractPriceService;
//import cn.zswltech.mithras.service.util.LongUtil;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.temporal.TemporalAdjusters;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * @description: 整体新增投放平均收益率（%） 每笔付款核销金额*对应的irr/付款核销金额合计（付款核销日在当年的）
// * @author: zhaozhengkang
// * @date: 2023/4/13 09:57
// */
//@Component
//public class FCM_105Calculator implements FinancialCloudMetricCalculator {
//
//    @Resource
//    private ContractPriceService contractPriceService;
//    @Resource
//    private DepartmentPaymentCache departmentPaymentCache;
//
//    @Override
//    public String metricCode() {
//        return "FCM_105";
//    }
//
//    @Override
//    public BigDecimal calculate(LocalDate dateTime) {
//        ConcurrentHashMap<Long, List<PaymentActualDetail>> yearPaymentActual = departmentPaymentCache.getYearPaymentActual(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
//        if (CollUtil.isEmpty(yearPaymentActual)) {
//            return BigDecimal.ZERO;
//        }
//
//        long totalAmount = yearPaymentActual.values().stream().map(list -> list.stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
//                .reduce(BigDecimal.ZERO, BigDecimal::add)).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
//
//        //获取投放相关合同的IRR
//        Set<Long> contractIds = yearPaymentActual.keySet();
//        Map<Long, Integer> irrMap = contractPriceService.queryNewestIrr(contractIds);
//
//        if (CollUtil.isEmpty(irrMap)) {
//            return BigDecimal.ZERO;
//        }
//
//        BigDecimal averageIrr = BigDecimal.ZERO;
//        for (Map.Entry<Long, List<PaymentActualDetail>> entry : yearPaymentActual.entrySet()) {
//            Integer irr = irrMap.get(entry.getKey());
//            BigDecimal oneContractTotal = entry.getValue().stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//            if (ObjectUtil.isEmpty(irr)) {
//                continue;
//            }
//            averageIrr = oneContractTotal.multiply(new BigDecimal(LongUtil.null2zero(irr)))
//                    .divide(new BigDecimal(totalAmount), 10, RoundingMode.HALF_UP)
//                    .add(averageIrr);
//        }
//        return averageIrr.setScale(4, RoundingMode.HALF_UP);
//    }
//}
//
//
//
