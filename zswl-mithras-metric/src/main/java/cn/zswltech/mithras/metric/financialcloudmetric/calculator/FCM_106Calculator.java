package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 期末项目平均收益率 全部合同的剩余本金*irr/剩余本金总额（剩余本金=付款核销金额-已核销首期租金-已核销本金）
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class FCM_106Calculator implements FinancialCloudMetricCalculator {
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private ContractNewestPriceReader contractNewestPriceReader;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public String metricCode() {
        return "FCM_106";
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        //获取期末项目相关的所有的投放合同
        List<ContractBaseInfo> contracts = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .notIn(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name(), ContractStatus.CLOSED.name(), ContractStatus.NEW.name()));

        Set<Long> contractIds = contracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(contractIds)) {
            return BigDecimal.ZERO;
        }

        // 获取所有已核销的投放记录
        List<PaymentActualDetail> actualDetails = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getContractId, contractIds)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));

        if (CollUtil.isEmpty(actualDetails)) {
            return BigDecimal.ZERO;
        }
        //计算总投放额
        BigDecimal totalAmount = actualDetails.stream()
                .map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, List<PaymentActualDetail>> groupByPaymentId = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));

        //获取投放相关合同的IRR
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectBatchIds(groupByPaymentId.keySet());
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
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : groupByPaymentId.entrySet()) {
            Long receiptId = paymentReceiptIdMap.get(entry.getKey());
            Integer irr = irrMap.get(receiptId);
            if (ObjectUtil.isNull(irr)) {
                continue;
            }
            BigDecimal oneContractTotal = entry.getValue().stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averageIrr = oneContractTotal.divide(totalAmount, 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(irr)).add(averageIrr);
        }
        return averageIrr.setScale(4, RoundingMode.HALF_UP);
    }
}
