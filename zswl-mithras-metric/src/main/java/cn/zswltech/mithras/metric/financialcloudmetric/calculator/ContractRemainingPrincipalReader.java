package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public BigDecimal remainingPrincipal(LocalDate endDate) {
        BigDecimal principal = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate))
                .stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

        int i = 1;
        BigDecimal received = BigDecimal.ZERO;
        while (true) {
            Page<CollectionBaseInfo> page = collectionBaseInfoMapper.selectPage(new Page<>(i, 500),
                    Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name())
                            .le(ObjectUtil.isNotEmpty(endDate), CollectionBaseInfo::getPlanCollectionDate, endDate));
            if (page.getRecords().isEmpty()) {
                break;
            }

            Map<Long, List<CollectionRecordInfo>> longListMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                            .in(CollectionRecordInfo::getCollectionId, page.getRecords().stream().map(CollectionBaseInfo::getId).collect(Collectors.toList())))
                    .stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
            for (CollectionBaseInfo record : page.getRecords()) {
                List<CollectionRecordInfo> collectionRecordInfos = longListMap.get(record.getId());
                if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                    received = collectionRecordInfos.stream().map(CollectionRecordInfo::getPrincipal)
                            .map(LongUtil::null2zero)
                            .map(BigDecimal::new).reduce(received, BigDecimal::add);
                }
                if (record.getCashFlowItem().equals(CashFlowItemEnum.FIRST_RENT.name())) {
                    received = received.add(BigDecimal.valueOf(LongUtil.null2zero(record.getCollectionAmount())));
                }
            }
            i++;
        }
        return principal.subtract(received);
    }

    public Map<Long, Long> remainingPrincipalGroupByClientId(Set<Long> clientIds, LocalDate endDate) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailMapper.selectList(
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getClientId, clientIds)
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate));

        Map<Long, List<PaymentActualDetail>> detailGroupByClientIds = paymentActualDetails.stream()
                .collect(Collectors.groupingBy(PaymentActualDetail::getClientId));

        Map<Long, BigDecimal> clientIdToPrincipal = new HashMap<>();
        detailGroupByClientIds.forEach((clientId, details) -> {
            BigDecimal principal = clientIdToPrincipal.getOrDefault(clientId, BigDecimal.ZERO);
            BigDecimal reduce = details.stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new).reduce(principal, BigDecimal::add);
            clientIdToPrincipal.put(clientId, reduce);
        });

        List<CollectionBaseInfo> collections = collectionBaseInfoMapper.selectList(
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getClientId, clientIds)
                        .in(CollectionBaseInfo::getWriteOffStatus,
                                Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                                        CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()))
                        .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name())
                        .le(ObjectUtil.isNotEmpty(endDate), CollectionBaseInfo::getPlanCollectionDate, endDate));
        Map<Long, BigDecimal> clientIdToReceived = new HashMap<>();

        Map<Long, List<CollectionBaseInfo>> collectionMap = collections.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getClientId));
        collectionMap.forEach((clientId, collectionBaseInfos) -> {
            BigDecimal received = clientIdToReceived.getOrDefault(clientId, BigDecimal.ZERO);
            BigDecimal reduce = collectionBaseInfos.stream()
                    .map(CollectionBaseInfo::getReceipt)
                    .map(LongUtil::null2zero)
                    .map(BigDecimal::new)
                    .reduce(received, BigDecimal::add);
            clientIdToReceived.put(clientId, reduce);
        });

        Map<Long, Long> clientIdToRemainingPrincipal = new HashMap<>();
        for (Map.Entry<Long, BigDecimal> entry : clientIdToPrincipal.entrySet()) {
            Long clientId = entry.getKey();
            BigDecimal principal = entry.getValue();
            BigDecimal received = clientIdToReceived.getOrDefault(clientId, BigDecimal.ZERO);
            BigDecimal remainingPrincipal = principal.subtract(received);
            clientIdToRemainingPrincipal.put(clientId, remainingPrincipal.longValue());
        }
        return clientIdToRemainingPrincipal;
    }

    public Triple<BigDecimal, BigDecimal, BigDecimal> overdueAmount(LocalDate dateTime) {
        if (dateTime.getMonthValue() == LocalDate.now().getMonthValue()) {
            dateTime = LocalDate.now();
        }
        BigDecimal overdueInterest = BigDecimal.ZERO;
        BigDecimal overduePrincipal = BigDecimal.ZERO;
        Map<Long, Pair<BigDecimal, BigDecimal>> overduePrincipalInterest = overdueAmountCalculator(dateTime);
        if (CollUtil.isEmpty(overduePrincipalInterest)) {
            return Triple.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        for (Map.Entry<Long, Pair<BigDecimal, BigDecimal>> entry : overduePrincipalInterest.entrySet()) {
            Pair<BigDecimal, BigDecimal> pair = entry.getValue();
            overdueInterest = overdueInterest.add(pair.getValue());
            overduePrincipal = overduePrincipal.add(pair.getKey());
        }
        Map<Long, Pair<BigDecimal, BigDecimal>> prePrincipalInterest = prePrincipalInterest(dateTime);
        BigDecimal prePrincipal = BigDecimal.ZERO;
        for (Map.Entry<Long, Pair<BigDecimal, BigDecimal>> entry : prePrincipalInterest.entrySet()) {
            Pair<BigDecimal, BigDecimal> pair = entry.getValue();
            prePrincipal = prePrincipal.add(pair.getKey());
        }
        return Triple.of(overduePrincipal, overdueInterest, overduePrincipal.add(prePrincipal));
    }

    public Map<Long, Pair<BigDecimal, BigDecimal>> prePrincipalInterest(LocalDate dateTime) {
        Map<Long, Pair<BigDecimal, BigDecimal>> result = new HashMap<>();
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .ge(CollectionBaseInfo::getPlanCollectionDate, dateTime.with(TemporalAdjusters.lastDayOfMonth())));
        collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId))
                .forEach((k, v) -> {
                    BigDecimal prePrincipal = v.stream().map(e -> LongUtil.null2zero(e.getPrincipal()))
                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal preInterest = v.stream().map(e -> LongUtil.null2zero(e.getInterest()))
                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                    result.put(k, Pair.of(prePrincipal, preInterest));
                });
        return result;
    }

    public Map<Long, Pair<BigDecimal, BigDecimal>> overdueAmountCalculator(LocalDate dateTime) {
        Map<Long, Pair<BigDecimal, BigDecimal>> result = new HashMap<>();
        Map<Long, List<CollectionBaseInfo>> collect = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPhase, 0)
                        .lt(CollectionBaseInfo::getPlanCollectionDate, dateTime))
                .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

        Map<Integer, List<Long>> partitionedContractIds = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            partitionedContractIds.put(i, new ArrayList<>());
        }

        collect.keySet().forEach(contractId -> {
            int partitionIndex = Math.abs(Objects.hash(contractId) % 20);
            partitionedContractIds.get(partitionIndex).add(contractId);
        });

        for (Map.Entry<Integer, List<Long>> entry : partitionedContractIds.entrySet()) {
            List<Long> batchContractIds = entry.getValue();
            if (batchContractIds.isEmpty()) {
                continue;
            }
            List<Long> collectionBaseInfoIds = new ArrayList<>();
            BigDecimal overdueInterest = BigDecimal.ZERO;
            BigDecimal overduePrincipal = BigDecimal.ZERO;
            for (Long contractId : batchContractIds) {
                List<CollectionBaseInfo> collectionBaseInfos = collect.get(contractId);
                collectionBaseInfoIds.addAll(collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()));
                List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                        .in(CollectionRecordInfo::getCollectionId, collectionBaseInfoIds)
                        .le(CollectionRecordInfo::getCollectionDate, dateTime.with(TemporalAdjusters.lastDayOfMonth())));
                if (CollUtil.isEmpty(collectionRecordInfos)) {
                    BigDecimal principal = collectionBaseInfos.stream().map(e -> LongUtil.null2zero(e.getPrincipal() - e.getCollectionPrincipal()))
                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal interest = collectionBaseInfos.stream().map(e -> LongUtil.null2zero(e.getInterest() - e.getCollectionInterest()))
                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                    overdueInterest = overdueInterest.add(principal.add(interest));
                    overduePrincipal = overduePrincipal.add(principal);
                    continue;
                }

                Map<Long, List<CollectionRecordInfo>> recordListMap = collectionRecordInfos.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
                List<CollectionBaseInfo> overdueRecordInContract = collectionBaseInfos.stream().filter(collectionBaseInfo -> {
                    List<CollectionRecordInfo> infos = recordListMap.get(collectionBaseInfo.getId());
                    if (CollUtil.isEmpty(infos)) {
                        return true;
                    }
                    long actualReceipt = infos.stream().mapToLong(o -> LongUtil.null2zero(o.getPrincipal()) + LongUtil.null2zero(o.getInterest())).sum();
                    return actualReceipt < collectionBaseInfo.getPlanCollectionAmount();
                }).collect(Collectors.toList());
                if (overdueRecordInContract.isEmpty()) {
                    continue;
                }
                BigDecimal principal = overdueRecordInContract.stream().map(e -> LongUtil.null2zero(e.getPrincipal()) - LongUtil.null2zero(e.getCollectionPrincipal()))
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal interest = overdueRecordInContract.stream().map(e -> LongUtil.null2zero(e.getInterest()) - LongUtil.null2zero(e.getCollectionInterest()))
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

                overdueInterest = overdueInterest.add(interest);
                overduePrincipal = overduePrincipal.add(principal);

                result.put(contractId, Pair.of(overduePrincipal, overdueInterest));
            }
        }
        return result;
    }
}
