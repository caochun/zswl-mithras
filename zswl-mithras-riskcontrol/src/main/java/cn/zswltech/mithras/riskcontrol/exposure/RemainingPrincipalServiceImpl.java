package cn.zswltech.mithras.riskcontrol.exposure;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.margin.model.MarginBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.persistence.PaginationProcessor;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/13 14:23
 */
@Service
@Slf4j
public class RemainingPrincipalServiceImpl implements RemainingPrincipalService {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;

    protected final Map<Long, Pair<BigDecimal, BigDecimal>> overRemainingPrincipalMap = new ConcurrentHashMap<>();
    protected final Map<Long, Pair<BigDecimal, BigDecimal>> preRemainingPrincipalMap = new ConcurrentHashMap<>();

    /**
     * 剩余的本金+利息
     *
     * @param endDate
     * @return
     */
    public BigDecimal remainingAmount(LocalDate endDate) {
        // 查询有投放的合同ID
        Set<Long> contractIds = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate))
                .stream()
                .map(PaymentActualDetail::getContractId)
                .collect(Collectors.toSet());
        // 没有投放的合同，快速返回
        if (contractIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 原子类，用于累加
        AtomicReference<BigDecimal> remainingAmount = new AtomicReference<>(BigDecimal.ZERO);
        PaginationProcessor<CollectionBaseInfo> processor =
                new PaginationProcessor<CollectionBaseInfo>(1000, CollectionBaseInfoMapper.class) {
                    @Override
                    protected void doProcess(List<CollectionBaseInfo> list) {
                        BigDecimal coll = BigDecimal.ZERO;
                        for (CollectionBaseInfo collection : list) {
                            long remainingPrincipal = LongUtil.null2zero(collection.getPrincipal()) - LongUtil.null2zero(collection.getCollectionPrincipal());
                            long remainingInterest = LongUtil.null2zero(collection.getInterest()) - LongUtil.null2zero(collection.getCollectionInterest());
                            coll = coll.add(BigDecimal.valueOf(remainingPrincipal)).add(BigDecimal.valueOf(remainingInterest));
                        }
                        // 一页处理结束，累加
                        remainingAmount.getAndAccumulate(coll, BigDecimal::add);
                    }
                };
        // 查询有投放的，且计划收款日期大于endDate的合同
        processor.setWrapper(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIds)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .ne(CollectionBaseInfo::getWriteOffStatus,
                        CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()));
        processor.process();
        return remainingAmount.get();
    }


    public Map<Long, BigDecimal> remainingPrincipalGroupByContractId(RemainingPrincipalQueryDto dto) {
        Set<Long> targetContractIds = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .select(PaymentActualDetail::getContractId)
                .in(ObjectUtil.isNotEmpty(dto.getClientIds()), PaymentActualDetail::getClientId, dto.getClientIds())
                .in(ObjectUtil.isNotEmpty(dto.getContractIds()), PaymentActualDetail::getContractId, dto.getContractIds())
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                .le(ObjectUtil.isNotEmpty(dto.getEndDate()), PaymentActualDetail::getPaidInDate, dto.getEndDate())).stream().map(PaymentActualDetail::getContractId).collect(Collectors.toSet());

        // 过滤一下已经结清的合同
        List<ContractBaseInfo> infoList = SpringUtil.getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name()));
        if (CollUtil.isNotEmpty(infoList)) {
            Set<Long> settleIdSet = infoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            targetContractIds.removeIf(settleIdSet::contains);
        }
        return remainingPrincipal(targetContractIds, dto.getEndDate());
    }


    @Override
    public BigDecimal remainingPrincipal(LocalDate endDate) {
        //统计实付
        BigDecimal principal = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate))
                .stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 统计实收
        int i = 1;
        BigDecimal recevied = BigDecimal.ZERO;
        while (true) {
            Page<CollectionBaseInfo> page = collectionBaseInfoMapper.selectPage(new Page<>(i, 500),
                    Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name())
                            .le(ObjectUtil.isNotEmpty(endDate), CollectionBaseInfo::getPlanCollectionDate, endDate));
            if (page.getRecords().isEmpty()) {
                break;
            }

            // 需要找到实际的收款记录
            Map<Long, List<CollectionRecordInfo>> longListMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                            .in(CollectionRecordInfo::getCollectionId, page.getRecords().stream().map(CollectionBaseInfo::getId).collect(Collectors.toList())))
                    .stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
            for (CollectionBaseInfo record : page.getRecords()) {
                // 首期租金需要单独处理
                List<CollectionRecordInfo> collectionRecordInfos = longListMap.get(record.getId());
                // 只需要计算本金
                if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                    recevied = collectionRecordInfos.stream().map(CollectionRecordInfo::getPrincipal)
                            .map(LongUtil::null2zero)
                            .map(BigDecimal::new).reduce(recevied, BigDecimal::add);
                }
                // 首期租金
                if (record.getCashFlowItem().equals(CashFlowItemEnum.FIRST_RENT.name())) {
                    recevied = recevied.add(BigDecimal.valueOf(LongUtil.null2zero(record.getCollectionAmount())));
                }
            }
            i++;
        }
        return principal.subtract(recevied);
    }

    @Override
    public Map<Long, BigDecimal> remainingPrincipal(Set<Long> contractIds, LocalDate endDate) {
        Map<Integer, List<Long>> contractIdMap = contractIds.stream().collect(Collectors.groupingBy(id -> id.hashCode() % 20));
        Map<Long, BigDecimal> result = new HashMap<>();
        Map<Long, BigDecimal> contractPrincipal = new HashMap<>();

        contractIdMap.forEach((k, subContractIds) -> {
            // 获取合同分组的实付
            Map<Long, List<PaymentActualDetail>> payments = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                            .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                            .in(PaymentActualDetail::getContractId, subContractIds)
                            .le(ObjectUtil.isNotEmpty(endDate), PaymentActualDetail::getPaidInDate, endDate))
                    .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
            // 获取合同分组的实收
            Map<Long, List<CollectionBaseInfo>> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .in(CollectionBaseInfo::getContractId, subContractIds)
                            .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name()))
                    .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

            for (Map.Entry<Long, List<CollectionBaseInfo>> entry : collectionBaseInfos.entrySet()) {
                Map<Long, List<CollectionRecordInfo>> longListMap = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                                .le(CollectionRecordInfo::getCollectionDate, endDate)
                                .in(CollectionRecordInfo::getCollectionId, entry.getValue().stream().map(CollectionBaseInfo::getId).collect(Collectors.toList())))
                        .stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));

                // 获取已收金额
                for (CollectionBaseInfo record : entry.getValue()) {
                    BigDecimal recevied = BigDecimal.ZERO;
                    List<CollectionRecordInfo> collectionRecordInfos = longListMap.get(record.getId());
                    if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                        // 只需要计算本金
                        if (CollUtil.isNotEmpty(collectionRecordInfos)) {
                            recevied = collectionRecordInfos.stream().map(CollectionRecordInfo::getPrincipal)
                                    .filter(Objects::nonNull)
                                    .map(BigDecimal::new).reduce(recevied, BigDecimal::add);
                        }

                        // 首期租金
                        if (record.getCashFlowItem().equals(CashFlowItemEnum.FIRST_RENT.name())) {
                            recevied = recevied.add(BigDecimal.valueOf(LongUtil.null2zero(record.getCollectionAmount())));
                        }
                    }
                    contractPrincipal.put(entry.getKey(), recevied.add(contractPrincipal.getOrDefault(entry.getKey(), BigDecimal.ZERO)));
                }
            }

            // 计算剩余本金
            payments.forEach((contractId, paymentActualDetails) -> {
                BigDecimal paid = paymentActualDetails.stream()
                        .map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.put(contractId, paid.subtract(contractPrincipal.getOrDefault(contractId, BigDecimal.ZERO)));
            });
        });
        return result;
    }

    @Override
    public BigDecimal remainingPrincipal(Long contractId, LocalDate endDate) {
        Map<Long, BigDecimal> map = remainingPrincipal(Collections.singleton(contractId), endDate);
        return map.get(contractId);
    }

    /**
     * 统计客户剩余本金, 若是客户id未找到收付款信息，则剩余本金为0L
     *
     * @param dto 查询条件
     * @return key: 客户id value: 剩余本金
     */
    @Override
    public Map<Long, Long> remainingPrincipalGroupByClientId(RemainingPrincipalQueryDto dto) {
        if (ObjectUtil.isEmpty(dto.getClientIds()) && ObjectUtil.isEmpty(dto.getContractIds())) {
            return Collections.emptyMap();
        }
        // 统计客户维度的实付
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailMapper.selectList(
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(ObjectUtil.isNotEmpty(dto.getContractIds()), PaymentActualDetail::getContractId,
                                dto.getContractIds())
                        .in(ObjectUtil.isNotEmpty(dto.getClientIds()), PaymentActualDetail::getClientId,
                                dto.getClientIds())
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(ObjectUtil.isNotEmpty(dto.getEndDate()), PaymentActualDetail::getPaidInDate, dto.getEndDate()));

        Map<Long, List<PaymentActualDetail>> detailGroupByClientIds = paymentActualDetails.stream()
                .collect(Collectors.groupingBy(PaymentActualDetail::getClientId));

        Map<Long, BigDecimal> clientIdToPrincipal = new HashMap<>();
        detailGroupByClientIds.forEach((clientId, details) -> {
            BigDecimal principal = clientIdToPrincipal.getOrDefault(clientId, BigDecimal.ZERO);
            BigDecimal reduce = details.stream().map(PaymentActualDetail::getPaidInAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new).reduce(principal, BigDecimal::add);
            clientIdToPrincipal.put(clientId, reduce);
        });

        // 客户维度计算的实收
        List<CollectionBaseInfo> collections = collectionBaseInfoMapper.selectList(
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(ObjectUtil.isNotEmpty(dto.getContractIds()),
                                CollectionBaseInfo::getContractId, dto.getContractIds())
                        .in(ObjectUtil.isNotEmpty(dto.getClientIds()),
                                CollectionBaseInfo::getClientId, dto.getClientIds())
                        .in(CollectionBaseInfo::getWriteOffStatus,
                                Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                                        CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()))
                        .in(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name())
                        .le(ObjectUtil.isNotEmpty(dto.getEndDate()), CollectionBaseInfo::getPlanCollectionDate, dto.getEndDate()));
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

        // 客户维度计算剩余本金
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


    /**
     * 计算逾期本金+利息
     *
     * @return 逾期本金+利息 +剩余本金
     */
    public Triple<BigDecimal, BigDecimal, BigDecimal> overdueAmount(LocalDate dateTime) {
        if (dateTime.getMonthValue() == LocalDate.now().getMonthValue()) {
            dateTime = LocalDate.now();
        }
        BigDecimal overdueInterest = BigDecimal.ZERO;
        BigDecimal overduePrincipal = BigDecimal.ZERO;
        Map<Long, Pair<BigDecimal, BigDecimal>> longPairMap = overdueAmountCalculator(dateTime);
        if (CollUtil.isEmpty(longPairMap)) {
            return Triple.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        for (Map.Entry<Long, Pair<BigDecimal, BigDecimal>> entry : longPairMap.entrySet()) {
            Pair<BigDecimal, BigDecimal> pair = entry.getValue();
            overdueInterest = overdueInterest.add(pair.getValue());
            overduePrincipal = overduePrincipal.add(pair.getKey());
        }
        Map<Long, Pair<BigDecimal, BigDecimal>> predPrincipalInterest = prePrincipalInterest(dateTime);
        // 将结果取出加总
        BigDecimal decimal = BigDecimal.ZERO;
        for (Map.Entry<Long, Pair<BigDecimal, BigDecimal>> entry : predPrincipalInterest.entrySet()) {
            Pair<BigDecimal, BigDecimal> pair = entry.getValue();
            decimal = decimal.add(pair.getKey());
        }
        return Triple.of(overduePrincipal, overdueInterest, overduePrincipal.add(decimal));
    }

    /**
     * 获取指定日期之后的本金+利息
     */
    public Map<Long, Pair<BigDecimal, BigDecimal>> prePrincipalInterest(LocalDate dateTime) {
        if (CollUtil.isNotEmpty(preRemainingPrincipalMap)) {
            return preRemainingPrincipalMap;
        }
        Map<Long, Pair<BigDecimal, BigDecimal>> result = new HashMap<>();
        // 获取剩余本金 和 利息
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
        preRemainingPrincipalMap.putAll(result);
        return result;
    }

    public Map<Long, Pair<BigDecimal, BigDecimal>> overdueAmountCalculator(LocalDate dateTime) {
        if (CollUtil.isNotEmpty(overRemainingPrincipalMap)) {
            return overRemainingPrincipalMap;
        }
        Map<Long, Pair<BigDecimal, BigDecimal>> result = new HashMap<>();
        Map<Long, List<CollectionBaseInfo>> collect = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPhase, 0)
                        .lt(CollectionBaseInfo::getPlanCollectionDate, dateTime))
                .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

        // hashcode 分成20份查数据库，防止OOM
        Map<Integer, List<Long>> partitionedContractIds = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            partitionedContractIds.put(i, new ArrayList<>());
        }

        collect.keySet().forEach(contractId -> {
            int partitionIndex = Math.abs(Objects.hash(contractId) % 20);
            partitionedContractIds.get(partitionIndex).add(contractId);
        });

        // 遍历每个分片并执行查询或其他操作
        for (Map.Entry<Integer, List<Long>> entry : partitionedContractIds.entrySet()) {
            List<Long> batchContractIds = entry.getValue();
            if (batchContractIds.isEmpty()) {
                continue;
            }
            // 查询实际的收款
            List<Long> collectionBaseInfoIds = new ArrayList<>();
            BigDecimal overdueInterest = BigDecimal.ZERO;
            BigDecimal overduePrincipal = BigDecimal.ZERO;
            for (Long contractId : batchContractIds) {
                List<CollectionBaseInfo> collectionBaseInfos = collect.get(contractId);
                collectionBaseInfoIds.addAll(collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList()));
                List<CollectionRecordInfo> collectionRecordInfos = SpringUtil.getBean(CollectionRecordInfoMapper.class).selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                        .in(CollectionRecordInfo::getCollectionId, collectionBaseInfoIds)
                        .le(CollectionRecordInfo::getCollectionDate, dateTime.with(TemporalAdjusters.lastDayOfMonth())));
                // 检查实际收款和预计收款找出逾期记录
                if (CollUtil.isEmpty(collectionRecordInfos)) {
                    // 未收款，逾期，将本金和利息添加到逾期记录中
                    BigDecimal principal = collectionBaseInfos.stream().map(e -> LongUtil.null2zero(e.getPrincipal() - e.getCollectionPrincipal()))
                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal interest = collectionBaseInfos.stream().map(e -> LongUtil.null2zero(e.getInterest() - e.getCollectionInterest()))
                            .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                    overdueInterest = overdueInterest.add(principal.add(interest));
                    overduePrincipal = overduePrincipal.add(principal);
                    continue;
                }

                // 不为空，需要找到每个期项的实际收款
                Map<Long, List<CollectionRecordInfo>> recordListMap = collectionRecordInfos.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
                List<CollectionBaseInfo> overdueRecordInContract = collectionBaseInfos.stream().filter(collectionBaseInfo -> {
                    List<CollectionRecordInfo> infos = recordListMap.get(collectionBaseInfo.getId());
                    if (CollUtil.isEmpty(infos)) {
                        // 未收款，逾期
                        return true;
                    }
                    // 计算实际收款 本金 + 利息
                    long actualReceipt = infos.stream().mapToLong(o -> LongUtil.null2zero(o.getPrincipal()) + LongUtil.null2zero(o.getInterest())).sum();
                    // 未收款，逾期
                    return actualReceipt < collectionBaseInfo.getPlanCollectionAmount();
                }).collect(Collectors.toList());
                if (overdueRecordInContract.isEmpty()) {
                    continue;
                }
                // 计算逾期记录
                BigDecimal principal = overdueRecordInContract.stream().map(e -> LongUtil.null2zero(e.getPrincipal()) - LongUtil.null2zero(e.getCollectionPrincipal()))
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal interest = overdueRecordInContract.stream().map(e -> LongUtil.null2zero(e.getInterest()) - LongUtil.null2zero(e.getCollectionInterest()))
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

                overdueInterest = overdueInterest.add(interest);
                overduePrincipal = overduePrincipal.add(principal);

                result.put(contractId, Pair.of(overduePrincipal, overdueInterest));
            }
        }
        overRemainingPrincipalMap.putAll(result);
        return result;
    }

    @Override
    public Map<Long, Long> overdueAmountGroupByClient() {
        // 统计逾期本金+利息
        List<CollectionBaseInfo> overdueRecord = collectionBaseInfoMapper.selectList(
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .ne(CollectionBaseInfo::getWriteOffStatus,
                                CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDateTime.now()));
        Map<Long, Long> clientToOverdueAmount = new HashMap<>();
        for (CollectionBaseInfo record : overdueRecord) {
            Long key = record.getClientId();
            Long amount = clientToOverdueAmount.getOrDefault(key, 0L);
            amount += record.getRemainingPrincipalInterest();
            clientToOverdueAmount.put(key, amount);
        }
        return clientToOverdueAmount;
    }

    public BigDecimal totalDepositByClientIds(Set<Long> targetClients) {
        return marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .in(MarginBaseInfo::getClientId, targetClients)).stream()
                .map(MarginBaseInfo::getCollectionAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Long, BigDecimal> totalDepositGroupByClientIds(Set<Long> targetClients) {
        Map<Long, List<MarginBaseInfo>> group = marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .in(MarginBaseInfo::getClientId, targetClients)).stream()
                .collect(Collectors.groupingBy(MarginBaseInfo::getClientId));
        if (group.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, BigDecimal> res = new HashMap<>();
        for (Map.Entry<Long, List<MarginBaseInfo>> entry : group.entrySet()) {
            BigDecimal oneClientDeposit = entry.getValue().stream().map(MarginBaseInfo::getCollectionAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
            res.put(entry.getKey(), oneClientDeposit);
        }
        return res;
    }

    public Map<Long, Long> depositGroupByClientId(Set<Long> targetClients) {
        Map<Long, List<MarginBaseInfo>> group = marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                        .in(MarginBaseInfo::getClientId, targetClients)).stream()
                .collect(Collectors.groupingBy(MarginBaseInfo::getClientId));
        if (group.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Long> res = new HashMap<>();
        for (Map.Entry<Long, List<MarginBaseInfo>> entry : group.entrySet()) {
            long oneClientDeposit = entry.getValue().stream().map(MarginBaseInfo::getCollectionAmount).map(LongUtil::null2zero)
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
            res.put(entry.getKey(), oneClientDeposit);
        }
        return res;
    }

    @Override
    public void clear() {
        overRemainingPrincipalMap.clear();
        preRemainingPrincipalMap.clear();
    }
}

