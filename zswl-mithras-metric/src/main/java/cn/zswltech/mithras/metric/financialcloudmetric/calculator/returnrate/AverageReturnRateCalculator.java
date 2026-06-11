package cn.zswltech.mithras.metric.financialcloudmetric.calculator.returnrate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.FinancialCloudMetricCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.ContractNewestPriceReader;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.DepartmentPaymentCache;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.TimeDimension;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/25 13:40
 */
public abstract class AverageReturnRateCalculator implements FinancialCloudMetricCalculator {
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private ContractNewestPriceReader contractNewestPriceReader;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private DepartmentPaymentCache departmentPaymentCache;

    private final Object lock = new Object();
    private static final Map<String, Long> DEPT_CODE_ID = new HashMap<>();

    public static void clear() {
        DEPT_CODE_ID.clear();
    }


    protected abstract Pair<ConditionKey, String> condition();

    /**
     * 时间维度
     *
     * @return
     */
    protected abstract TimeDimension timeDimension();

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        // 根据业务部门id筛选
        if (DEPT_CODE_ID.isEmpty()) {
            synchronized (lock) {
                if (DEPT_CODE_ID.isEmpty()) {
                    DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                            .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
                }
            }
        }
        ConcurrentHashMap<Long, List<PaymentActualDetail>> paymentThisMonth = new ConcurrentHashMap<>();
        if (timeDimension() == TimeDimension.MONTH) {
            paymentThisMonth = departmentPaymentCache.getMonthPaymentActual(dateTime);
        }
        if (timeDimension() == TimeDimension.TOTAL) {
            paymentThisMonth = departmentPaymentCache.getYearPaymentActual(dateTime);
        }

        if (ObjectUtil.isEmpty(paymentThisMonth)) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        // 这里需要判断是整体还是部门然后进行付款的过滤
        ConcurrentHashMap<Long, Map<Long, Integer>> deptPaymentCache = new ConcurrentHashMap<>(departmentPaymentCache.DEPT_PAYMENT_CACHE);
        if (condition() != null) {
            // 非整体的需要在这里过滤一下别的部门的付款，需要考虑有分润的合同和合同业务部门维度的汇总
            // 还要考虑投放了无分润的情况
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .in(ContractBaseInfo::getId, paymentThisMonth.keySet())
                    .eq(ContractBaseInfo::getBizDeptId, DEPT_CODE_ID.get(condition().getValue())));
            if (CollUtil.isNotEmpty(contractBaseInfos)) {
                List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).distinct().collect(Collectors.toList());
                // 然后过滤
                for (Long contractId : contractIds) {
                    if (!deptPaymentCache.containsKey(contractId)) {
                        // 不存在就手动补一个100%
                        Map<Long, Integer> hashMap = new HashMap<>();
                        hashMap.put(DEPT_CODE_ID.get(condition().getValue()), 1000000);
                        deptPaymentCache.put(contractId, hashMap);
                    }
                }
            }
            // 对总金额进行计算，需要按照分润比来
            for (Map.Entry<Long, Map<Long, Integer>> mapEntry : deptPaymentCache.entrySet()) {
                for (Map.Entry<Long, Integer> entry : mapEntry.getValue().entrySet()) {
                    // 获取该合同下该部门的分润比
                    Long orgId = DEPT_CODE_ID.get(condition().getValue());
                    if (Objects.equals(orgId, entry.getKey())) {
                        BigDecimal decimal = paymentThisMonth.getOrDefault(mapEntry.getKey(), new ArrayList<>())
                                .stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                        totalAmount = new BigDecimal(entry.getValue())
                                .divide(new BigDecimal(1000000), 10, RoundingMode.HALF_UP)
                                .multiply(decimal).add(totalAmount);
                    }
                }
            }

        } else {
            // 整体的
            totalAmount = paymentThisMonth.values().stream().map(e -> e.stream().map(PaymentActualDetail::getPaidInAmount)
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 避免0除
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        //获取投放相关合同的IRR
        // 查询付款拿到借据ID，没有生效的借据ID取临时的关联ID
        List<Long> paymentIds = paymentThisMonth.values().stream().map(e -> e.stream().map(PaymentActualDetail::getPaymentId).distinct().collect(Collectors.toList()))
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectBatchIds(paymentIds);
        // 比较关键，转化用
        Map<Long, Long> paymentReceiptIdMap = new HashMap<>();
        Set<Long> receiptIds = paymentBaseInfos.stream().map(e -> {
            Long l = Objects.nonNull(e.getReceiptIdFinal()) ? e.getReceiptIdFinal() : e.getReceiptId();
            paymentReceiptIdMap.put(e.getId(), l);
            return l;
        }).collect(Collectors.toSet());
        Map<Long, Integer> irrMap = contractNewestPriceReader.queryNewestReceiptIrr(receiptIds);

        // 计算加权平均IRR
        BigDecimal averageIrr = BigDecimal.ZERO;
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : paymentThisMonth.entrySet()) {
            // 如果是整体的不用考虑部门分润
            if (timeDimension() == TimeDimension.TOTAL && condition() == null) {
                Map<Long, List<PaymentActualDetail>> map = entry.getValue().stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
                for (Map.Entry<Long, List<PaymentActualDetail>> paymentIdDetailsMap : map.entrySet()) {
                    Long receiptId = paymentReceiptIdMap.get(paymentIdDetailsMap.getKey());
                    Integer irrPercent = irrMap.get(receiptId);
                    // 做计算
                    averageIrr = paymentIdDetailsMap.getValue().stream().map(PaymentActualDetail::getPaidInAmount)
                            .map(BigDecimal::new)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .multiply(new BigDecimal(LongUtil.null2zero(irrPercent)))
                            .divide(totalAmount, 10, RoundingMode.HALF_UP)
                            .add(averageIrr);
                }
            } else {
                //部门分润，唯一的区别就是付款的金额需要被乘以比例
                Map<Long, List<PaymentActualDetail>> map = entry.getValue().stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
                for (Map.Entry<Long, List<PaymentActualDetail>> paymentIdDetailsMap : map.entrySet()) {
                    Long receiptId = paymentReceiptIdMap.get(paymentIdDetailsMap.getKey());
                    Integer irrPercent = irrMap.get(receiptId);

                    // 计算
                    Map<Long, Integer> deptWeight = deptPaymentCache.get(entry.getKey());
                    if (CollUtil.isEmpty(deptWeight)) {
                        continue;
                    }
                    for (Map.Entry<Long, Integer> integerEntry : deptWeight.entrySet()) {
                        Long orgId = DEPT_CODE_ID.get(condition().getValue());
                        if (integerEntry.getKey().equals(orgId)) {
                            BigDecimal sum = entry.getValue().stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            averageIrr = sum.multiply(new BigDecimal(integerEntry.getValue()))
                                    .multiply(new BigDecimal(LongUtil.null2zero(irrPercent)))
                                    .divide(new BigDecimal(1000000L), 10, RoundingMode.HALF_UP)
                                    .divide(totalAmount, 10, RoundingMode.HALF_UP)
                                    .add(averageIrr);
                        }
                    }
                }
            }
        }
        // 获取项目数量
        return averageIrr.setScale(4, RoundingMode.HALF_UP);
    }
}
