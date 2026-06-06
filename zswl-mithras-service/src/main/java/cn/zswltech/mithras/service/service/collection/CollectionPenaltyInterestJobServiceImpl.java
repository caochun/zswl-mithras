package cn.zswltech.mithras.service.service.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.application.job.CollectionPenaltyInterestJobService;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionOverdueRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueHistory;
import cn.zswltech.mithras.collection.mapper.model.CollectionOverdueRecordInfo;
import cn.zswltech.mithras.contract.overdue.application.collection.OverdueCollectionRefreshService;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description 收款相关定时任务
 */
@Slf4j
@Component
public class CollectionPenaltyInterestJobServiceImpl implements CollectionPenaltyInterestJobService {
    @Resource
    private CollectionOverdueRecordInfoService collectionOverdueRecordInfoService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionOverdueRecordInfoMapper collectionOverdueRecordInfoMapper;
    @Resource
    private CollectionOverdueHistoryService collectionOverdueHistoryService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private OverdueCollectionRefreshService overdueCollectionRefreshService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void penaltyInterestJobHandler() {
        log.info("PenaltyInterestJob, start.");
        List<CollectionBaseInfo> list = collectionBaseInfoMapper.overdueList();
        Map<Long, Integer> contractPenaltyInterestRateMap = new HashMap<>();
        for (CollectionBaseInfo info : list) {
            CollectionOverdueRecordInfo overdueRecordInfo = collectionOverdueRecordInfoMapper.selectOne(Wrappers.<CollectionOverdueRecordInfo>lambdaQuery()
                    .eq(CollectionOverdueRecordInfo::getCollectionId, info.getId()).orderByDesc(CollectionOverdueRecordInfo::getRecordDate).last("LIMIT 1"));
            if (overdueRecordInfo != null && LocalDate.now().equals(overdueRecordInfo.getRecordDate())) {
                continue;
            }
            if (!contractPenaltyInterestRateMap.containsKey(info.getContractId())) {
                contractPenaltyInterestRateMap.put(info.getContractId(), collectionOverdueRecordInfoService.getDailyRate(info.getContractId()));
            }
            CollectionOverdueRecordInfo recordInfo = new CollectionOverdueRecordInfo();
            recordInfo.setCollectionId(info.getId());
            long overdueAmount = LongUtil.null2zero(info.getCashFlowAmount()) - (LongUtil.null2zero(info.getCollectionPrincipal()) + LongUtil.null2zero(info.getCollectionInterest()));
            if (overdueAmount <= 0) {
                continue;
            }
            Integer rate = contractPenaltyInterestRateMap.get(info.getContractId());
            long dayAmount = Util.mithrasInteger2BigDecimal(rate).multiply(Util.mithrasLong2BigDecimal(overdueAmount)).multiply(BigDecimal.valueOf(100L)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
            long last = LongUtil.null2zero(info.getPenaltyInterest()) + dayAmount;
            recordInfo.setOverdueAmount(overdueAmount);
            // 保留两位小数到分
            recordInfo.setDayPenaltyInterest(Util.mithrasLongDecimalTwo(dayAmount));
            // 保留两位小数到分
            recordInfo.setLastPenaltyInterest(Util.mithrasLongDecimalTwo(last));
            recordInfo.setRecordDate(LocalDate.now());
            //如果第一次，需要判断时间，查询逾期记录，生成前三天数据
            if (overdueRecordInfo == null) {
                CollectionOverdueRecordInfo collectionOverdueRecordInfo;
                long sunLast = LongUtil.null2zero(info.getPenaltyInterest());
                for (LocalDate date = info.getPlanCollectionDate().plusDays(1); date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
                    collectionOverdueRecordInfo = BeanUtil.copyProperties(recordInfo, CollectionOverdueRecordInfo.class);
                    collectionOverdueRecordInfo.setRecordDate(date);
                    sunLast += last;
                    collectionOverdueRecordInfo.setLastPenaltyInterest(Util.mithrasLongDecimalTwo(sunLast));
                    collectionOverdueRecordInfoMapper.insert(collectionOverdueRecordInfo);
                }
                last = sunLast + dayAmount;
            }
            // 保留两位小数到分
            recordInfo.setLastPenaltyInterest(Util.mithrasLongDecimalTwo(last));
            collectionOverdueRecordInfoMapper.insert(recordInfo);
            // 保留两位小数到分
            info.setPenaltyInterest(Util.mithrasLongDecimalTwo(last));
            collectionBaseInfoMapper.updateById(info);
        }
        log.info("PenaltyInterestJob, end.");
    }


    /**
     * 生成逾期记录，超时三天开始生成，从逾期第一天开始，记录每天逾期金额，用于计算
    **/
    @Override
    @Transactional(rollbackFor = Throwable.class)
    @Deprecated
    public void penaltyInterestJobHandler2(LocalDate localDate) {
        try {
            log.info("PenaltyInterestJob2, start");
            //记录昨天逾期金额
            collectionOverdueHistoryService.overdueSnapshot(localDate);
            //查询某一天的逾期记录
            List<CollectionOverdueHistory> historyByDay = collectionOverdueHistoryService.getHistoryByDay(localDate);
            //维护税率
            Set<Long> contractIds = historyByDay.stream().map(CollectionOverdueHistory::getContractId).collect(Collectors.toSet());
            if (ObjectUtil.isEmpty(contractIds)) {
                return;
            }
            Set<Long> collectIds = historyByDay.stream().map(CollectionOverdueHistory::getCollectionId).collect(Collectors.toSet());
            Map<Long, Integer> dailyRateBatchMap = collectionOverdueRecordInfoService.getDailyRateBatch(new ArrayList<>(contractIds));
            Map<Long, CollectionBaseInfo> collectionMap = collectionBaseInfoMapper.selectBatchIds(collectIds).stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e, (a, b) -> a));
            //获取每笔租金的最后一次罚息记录情况
            List<CollectionOverdueRecordInfo> lastRecordByCollectionId = collectionOverdueRecordInfoService.getLastRecordByCollectionId(new ArrayList<>(collectIds));
            Map<Long, CollectionOverdueRecordInfo> lastRecodeMap = new HashMap<>();
            if (ObjectUtil.isNotEmpty(lastRecordByCollectionId)) {
                lastRecodeMap = lastRecordByCollectionId.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toMap(CollectionOverdueRecordInfo::getCollectionId, e -> e, (a, b) -> b));
            }
            CollectionBaseInfo collectionBaseInfo;
            List<CollectionBaseInfo> updateCollectionBaseInfo = new ArrayList<>();
            List<CollectionOverdueRecordInfo> addCollectionOverdueRecordInfo = new ArrayList<>();
            for (CollectionOverdueHistory info : historyByDay) {
                collectionBaseInfo = collectionMap.get(info.getCollectionId());
                if (ObjectUtil.isEmpty(collectionBaseInfo) || ObjectUtil.equal(collectionBaseInfo.getPenaltyInterestCalculateFlag(), YesOrNoNumberEnum.YES.getCode())) {
                    continue;
                }
                CollectionOverdueRecordInfo overdueRecordInfo = collectionOverdueRecordInfoMapper.selectOne(Wrappers.<CollectionOverdueRecordInfo>lambdaQuery()
                        .eq(CollectionOverdueRecordInfo::getCollectionId, info.getCollectionId()).orderByDesc(CollectionOverdueRecordInfo::getRecordDate).last("LIMIT" + " 1"));
                if (overdueRecordInfo != null && LocalDate.now().minusDays(1).equals(overdueRecordInfo.getRecordDate())) {
                    continue;
                }
                CollectionOverdueRecordInfo recordInfo = new CollectionOverdueRecordInfo();
                recordInfo.setCollectionId(info.getCollectionId());
                long overdueAmount = LongUtil.null2zero(info.getOverdueAmount());
                if (overdueAmount <= 0) {
                    continue;
                }
                Integer rate = dailyRateBatchMap.get(info.getContractId());
                BigDecimal dayAmount = Util.mithrasInteger2BigDecimal(rate).multiply(Util.mithrasLong2BigDecimal(overdueAmount)).multiply(BigDecimal.valueOf(100L)).setScale(10, BigDecimal.ROUND_HALF_UP);
                BigDecimal last = new BigDecimal(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest())).add(dayAmount);
                recordInfo.setOverdueAmount(overdueAmount);
                // 保留两位小数到分
                recordInfo.setDayPenaltyInterest(dayAmount.longValue());
                // 保留两位小数到分
                recordInfo.setLastPenaltyInterest(last.longValue());
                //记录昨天产生的罚息
                recordInfo.setRecordDate(localDate.minusDays(1));
                Map<LocalDate, Long> historyMap = new HashMap<>();
                List<CollectionOverdueHistory> historyByDay1 = collectionOverdueHistoryService.getHistoryByDay(collectionBaseInfo.getId());
                if (ObjectUtil.isNotEmpty(historyByDay1)) {
                    historyByDay1.forEach(history -> {
                        //生成时间是第二天凌晨
                        historyMap.put(history.getCreateTime().toLocalDate().minusDays(1), history.getOverdueAmount());
                    });
                }
                //如果第一次，需要判断时间，查询逾期记录，生成前三天数据
                if (overdueRecordInfo == null && collectionBaseInfo.getPlanCollectionDate().isBefore(localDate)) {
                    CollectionOverdueRecordInfo collectionOverdueRecordInfo;
                    BigDecimal sunLast = new BigDecimal(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
                    //查询每一天的逾期记录
                    Long nowDueAmount = overdueAmount;
                    BigDecimal nowAmount;
                    //从第二天开始计算罚息，且只能生成昨天的罚息
                    for (LocalDate date = collectionBaseInfo.getPlanCollectionDate().plusDays(1); date.isBefore(LocalDate.now().minusDays(1)); date =
                            date.plusDays(1)) {
                        //今日逾期
                        if (LongUtil.null2zero(historyMap.get(date)) > 0) {
                            nowDueAmount = historyMap.get(date);
                        }
                        nowAmount = Util.mithrasInteger2BigDecimal(rate).multiply(Util.mithrasLong2BigDecimal(nowDueAmount)).multiply(BigDecimal.valueOf(100L)).setScale(10, BigDecimal.ROUND_HALF_UP);
                        collectionOverdueRecordInfo = BeanUtil.copyProperties(recordInfo, CollectionOverdueRecordInfo.class);
                        collectionOverdueRecordInfo.setRecordDate(date);
                        collectionOverdueRecordInfo.setOverdueAmount(nowDueAmount);
                        collectionOverdueRecordInfo.setDayPenaltyInterest(nowAmount.longValue());
                        sunLast = sunLast.add(nowAmount);
                        collectionOverdueRecordInfo.setLastPenaltyInterest(sunLast.longValue());
                        addCollectionOverdueRecordInfo.add(collectionOverdueRecordInfo);
                    }
                    last = sunLast.add(dayAmount);
                } else {
                    //新逻辑会产生中间几天不再计息的情况，这里需要补充断掉的记录
                    //这里需要找到断掉的时间
                    CollectionOverdueRecordInfo lastOverdueRecordInfo = lastRecodeMap.get(info.getCollectionId());
                    if (ObjectUtil.isNotEmpty(lastOverdueRecordInfo)) {
                        BigDecimal nowAmount;
                        Long nowDueAmount = 0L;
                        BigDecimal sunLast = new BigDecimal(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
                        CollectionOverdueRecordInfo collectionOverdueRecordInfo;
                        //从第二天开始计算罚息，且只能生成昨天的罚息
                        for (LocalDate date = lastOverdueRecordInfo.getRecordDate(); date.isBefore(LocalDate.now()); date =
                                date.plusDays(1)) {
                            //今日逾期
                            if (LongUtil.null2zero(historyMap.get(date)) > 0) {
                                nowDueAmount = historyMap.get(date);
                            }
                            nowAmount = Util.mithrasInteger2BigDecimal(rate).multiply(Util.mithrasLong2BigDecimal(nowDueAmount)).multiply(BigDecimal.valueOf(100L)).setScale(0, BigDecimal.ROUND_HALF_UP);
                            collectionOverdueRecordInfo = BeanUtil.copyProperties(recordInfo, CollectionOverdueRecordInfo.class);
                            collectionOverdueRecordInfo.setRecordDate(date);
                            collectionOverdueRecordInfo.setOverdueAmount(nowDueAmount);
                            collectionOverdueRecordInfo.setDayPenaltyInterest(nowAmount.longValue());
                            sunLast = sunLast.add(nowAmount);
                            collectionOverdueRecordInfo.setLastPenaltyInterest(Util.mithrasLongDecimalTwo(sunLast.longValue()));
                            addCollectionOverdueRecordInfo.add(collectionOverdueRecordInfo);
                        }
                        last = last.add(sunLast);
                    }
                }
                // 保留两位小数到分
                recordInfo.setLastPenaltyInterest(last.longValue());
                addCollectionOverdueRecordInfo.add(recordInfo);
                // 保留两位小数到分
                collectionBaseInfo.setPenaltyInterest(last.longValue());
                updateCollectionBaseInfo.add(collectionBaseInfo);
            }
            if (CollUtil.isNotEmpty(updateCollectionBaseInfo)) {
                collectionBaseInfoService.updateBatchById(updateCollectionBaseInfo);
            }
            if (CollUtil.isNotEmpty(addCollectionOverdueRecordInfo)) {
                collectionOverdueRecordInfoService.saveBatch(addCollectionOverdueRecordInfo);
            }
            log.info("PenaltyInterestJob, end.");
        } catch (Exception e) {
            log.error("PenaltyInterestJob2 error", e);
        }
    }

    /**
     * 统一逻辑
     **/
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void penaltyInterestJobHandler3(LocalDate localDate){
        try {
            log.info("PenaltyInterestJob3, start");
            //记录昨天逾期金额
            collectionOverdueHistoryService.overdueSnapshot(localDate);
            //查询某一天的逾期记录
            List<CollectionOverdueHistory> historyByDay = collectionOverdueHistoryService.getHistoryByDay(localDate);
            if (ObjectUtil.isEmpty(historyByDay)) {
                return;
            }
            List<Long> collectionIds = historyByDay.stream().map(CollectionOverdueHistory::getCollectionId).collect(Collectors.toList());
            collectionOverdueRecordInfoService.doRerunPenaltyInterest(collectionIds);
            //维护核销状态
            collectionOverdueRecordInfoService.modifyWriteOffStatus(collectionIds);
            log.info("PenaltyInterestJob3, end.");
        } catch (Exception e) {
            log.error("PenaltyInterestJob3 error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void overdueSnapshot() {
        try {
            collectionOverdueHistoryService.overdueSnapshot();
        } catch (Exception e) {
            log.error("overdueSnapshotJob error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateClientPenaltyInterest() {
        try {
            //查询逾期客户
            List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                    .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()));
            if(ObjectUtil.isNotEmpty(list)) {
                Set<Long> clientSet = list.stream().map(CollectionBaseInfo::getClientId).collect(Collectors.toSet());
                if (ObjectUtil.isNotEmpty(clientSet)) {
                    clientSet.forEach(id -> {
                        overdueCollectionRefreshService.refreshClient(id);
                    });
                }
            }
        } catch (Exception e) {
            log.error("overdueSnapshotJob error", e);
        }
    }

}
