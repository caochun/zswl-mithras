package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ContractRemainingPrincipalReader {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    public Map<Long, BigDecimal> remainingPrincipalGroupByContractId(LocalDate endDate) {
        Set<Long> targetContractIds = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .select(PaymentActualDetail::getContractId)
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate))
                .stream()
                .map(PaymentActualDetail::getContractId)
                .collect(Collectors.toSet());

        List<ContractBaseInfo> infoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name()));
        if (CollUtil.isNotEmpty(infoList)) {
            Set<Long> settleIdSet = infoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            targetContractIds.removeIf(settleIdSet::contains);
        }
        return remainingPrincipal(targetContractIds, endDate);
    }

    public Map<Long, BigDecimal> remainingPrincipal(Set<Long> contractIds, LocalDate endDate) {
        Map<Integer, List<Long>> contractIdMap = contractIds.stream().collect(Collectors.groupingBy(id -> id.hashCode() % 20));
        Map<Long, BigDecimal> result = new HashMap<>();
        Map<Long, BigDecimal> contractPrincipal = new HashMap<>();

        contractIdMap.forEach((k, subContractIds) -> {
            Map<Long, List<PaymentActualDetail>> payments = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                            .in(PaymentActualDetail::getContractId, subContractIds)
                            .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate))
                    .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
            Map<Long, List<CollectionBaseInfo>> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .in(CollectionBaseInfo::getContractId, subContractIds)
                            .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name()))
                    .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

            for (Map.Entry<Long, List<CollectionBaseInfo>> entry : collectionBaseInfos.entrySet()) {
                Map<Long, List<CollectionRecordInfo>> longListMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                                .le(CollectionRecordInfo::getCollectionDate, endDate)
                                .in(CollectionRecordInfo::getCollectionId, entry.getValue().stream().map(CollectionBaseInfo::getId).collect(Collectors.toList())))
                        .stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));

                for (CollectionBaseInfo record : entry.getValue()) {
                    BigDecimal received = BigDecimal.ZERO;
                    List<CollectionRecordInfo> collectionRecordInfos = longListMap.get(record.getId());
                    if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                        received = collectionRecordInfos.stream().map(CollectionRecordInfo::getPrincipal)
                                .filter(Objects::nonNull)
                                .map(BigDecimal::new).reduce(received, BigDecimal::add);

                        if (record.getCashFlowItem().equals(CashFlowItemEnum.FIRST_RENT.name())) {
                            received = received.add(BigDecimal.valueOf(LongUtil.null2zero(record.getCollectionAmount())));
                        }
                    }
                    contractPrincipal.put(entry.getKey(), received.add(contractPrincipal.getOrDefault(entry.getKey(), BigDecimal.ZERO)));
                }
            }

            payments.forEach((contractId, paymentActualDetails) -> {
                BigDecimal paid = paymentActualDetails.stream()
                        .map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.put(contractId, paid.subtract(contractPrincipal.getOrDefault(contractId, BigDecimal.ZERO)));
            });
        });
        return result;
    }
}
