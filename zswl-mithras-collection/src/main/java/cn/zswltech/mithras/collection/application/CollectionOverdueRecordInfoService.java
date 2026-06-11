package cn.zswltech.mithras.collection.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestREQ;
import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestRSP;
import cn.zswltech.mithras.dto.collection.PenaltyInterestListRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionOverdueRecordInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionOverdueRecordInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @create: 2022-08-22
 **/
@Slf4j
@Service
public class CollectionOverdueRecordInfoService extends ServiceImpl<CollectionOverdueRecordInfoMapper, CollectionOverdueRecordInfo> {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;
    @Resource
    private CollectionOverdueRecordInfoMapper collectionOverdueRecordInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;

    public CollectionPenaltyInterestRSP detail(CollectionBaseInfoDetailREQ req) {
        CollectionPenaltyInterestRSP rsp = new CollectionPenaltyInterestRSP();
        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(req.getId());
        rsp.setDailyRate(getDailyRate(baseInfo.getContractId()));
        rsp.setSumAmount(LongUtil.null2zero(baseInfo.getPenaltyInterest()) + LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()));
        rsp.setWriteOffAmount(baseInfo.getCollectionPenaltyInterest());
        rsp.setPenaltyInterestAmount(baseInfo.getPenaltyInterest());
        rsp.setLastAmount(LongUtil.null2zero(rsp.getPenaltyInterestAmount()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest()));
//        rsp.setComment(baseInfo.getComment());
        rsp.setCreditAmount(LongUtil.null2zero(baseInfo.getPenaltyInterestDeductionAmount()));
        return rsp;
    }

    public PageR<PenaltyInterestListRSP> list(CollectionPenaltyInterestREQ req) {
        Page<CollectionOverdueRecordInfo> pageList = collectionOverdueRecordInfoMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CollectionOverdueRecordInfo>lambdaQuery().eq(CollectionOverdueRecordInfo::getCollectionId, req.getId()).orderByDesc(CollectionOverdueRecordInfo::getRecordDate));
        List<PenaltyInterestListRSP> rsps = new LinkedList<>();
        for (CollectionOverdueRecordInfo record : pageList.getRecords()) {
            PenaltyInterestListRSP rsp = new PenaltyInterestListRSP();
            BeanUtil.copyProperties(record, rsp);
            rsps.add(rsp);
        }
        return PageR.of(rsps, pageList.getTotal(),
                pageList.getPages(),
                pageList.getCurrent(),
                pageList.getSize());
    }

//    @Transactional(rollbackFor = Throwable.class)
//    public void update(CollectionBaseInfoUpdateREQ req) {
//        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(req.getId());
//        if (baseInfo == null){
//            throw new MithrasException("无记录");
//        }
//
//        baseInfo.setComment(req.getComment());
//        baseInfo.setPenaltyInterestAmount(req.getPenaltyInterestAmount());
//        baseInfo.setPenaltyInterestUpdate(1);
//        if (req.getPenaltyInterestAmount().equals(baseInfo.getPenaltyInterest())){
//            baseInfo.setPenaltyInterestUpdate(0);
//        }
//        collectionBaseInfoMapper.updateById(baseInfo);
//    }

    public Integer getDailyRate(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
        ContractLeasePriceLib leasePrice = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                .eq(ContractLeasePriceLib::getContractId, contractId)
                .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractLeasePriceLib::getVersion)
                .last("LIMIT 1"));
        if (!ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (leasePrice == null || leasePrice.getDefaultInterestRate() == null) {
                return 500;
            }
        }
        return LongUtil.null2zero(leasePrice.getDefaultInterestRate());
    }

    public Map<Long, Integer> getDailyRateBatch(List<Long> contractIds) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectBatchIds(contractIds);
        Map<Long, Integer> contractLeasePriceMap = contractLeasePriceLibMapper.queryNewestLib(new HashSet<>(contractIds)).stream().collect(Collectors.toMap(ContractLeasePriceLib::getContractId,
                ContractLeasePriceLib::getDefaultInterestRate, (a, b) -> a));
        Map<Long, Integer> map = new HashMap<>();
        contractBaseInfos.forEach(contractBaseInfo -> {
            map.put(contractBaseInfo.getId(), LongUtil.null2zero(contractLeasePriceMap.get(contractBaseInfo.getId())));
            if (!ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
                if (ObjectUtil.isEmpty(contractLeasePriceMap.get(contractBaseInfo.getId()))) {
                    map.put(contractBaseInfo.getId(), 500);
                }
            }
        });
        return map;
    }

    /**
     * 获取每期租金最后一笔逾期记录
     **/
    public List<CollectionOverdueRecordInfo> getLastRecordByCollectionId(List<Long> collectionIds) {
        if (CollectionUtil.isEmpty(collectionIds)) {
            return Collections.emptyList();
        }
        return collectionOverdueRecordInfoMapper.getLastRecordByCollectionId(collectionIds);
    }

    /**
     * 重新计算罚息金额，使用实际收款记录来进行计算，每次会删除已有数据，插入新数据
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void doRerunPenaltyInterest(List<Long> allCollectionIds) {
        if (CollectionUtil.isEmpty(allCollectionIds)) {
            return;
        }
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getId, allCollectionIds)
                .eq(CollectionBaseInfo::getPenaltyInterestCalculateFlag, YesOrNoNumberEnum.NO.getCode())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .le(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()));
        if (ObjectUtil.isEmpty(collectionBaseInfos)) {
            return;
        }
        List<Long> collectionIds = collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
        //查询核销明细
        Map<Long, List<CollectionRecordInfo>> collectId2Record = collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .in(CollectionRecordInfo::getCollectionId, collectionIds)).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
        //构建新罚息记录
        List<CollectionOverdueRecordInfo> newCollectionOverdueRecord = new ArrayList<>();
        Map<Long, Integer> dailyRateBatchMap = this.getDailyRateBatch(collectionBaseInfos.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()));
        collectionBaseInfos.forEach(collectionBaseInfo -> {
            BigDecimal tempAmount = BigDecimal.ZERO;
            //获取每天逾期金额
            Map<LocalDate, Long> amountMap = getAmountDetail(collectId2Record.get(collectionBaseInfo.getId()), collectionBaseInfo);
            //
            for (LocalDate date = collectionBaseInfo.getPlanCollectionDate(); date.isBefore(LocalDate.now()); date = date.plusDays(1)) {
                Long amount = amountMap.getOrDefault(date, 0L);
                Integer rate = dailyRateBatchMap.get(collectionBaseInfo.getContractId());
                CollectionOverdueRecordInfo recordInfo = new CollectionOverdueRecordInfo();
                //long nowAmount = Util.mithrasInteger2BigDecimal(rate).multiply(Util.mithrasLong2BigDecimal(amount).setScale(2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100L)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
                BigDecimal nowAmount = CollectionFinancialUtil.mithrasInteger2BigDecimal(rate).multiply(CollectionFinancialUtil.mithrasLong2BigDecimal(amount)).multiply(BigDecimal.valueOf(100L)).setScale(10, BigDecimal.ROUND_HALF_UP);
                if (nowAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                recordInfo.setCollectionId(collectionBaseInfo.getId());
                recordInfo.setRecordDate(date);
                recordInfo.setOverdueAmount(amount);
                recordInfo.setDayPenaltyInterest(nowAmount.longValue());
                tempAmount = tempAmount.add(nowAmount);
                recordInfo.setLastPenaltyInterest(CollectionFinancialUtil.mithrasLongDecimalTwo(tempAmount.longValue()));
                newCollectionOverdueRecord.add(recordInfo);
            }
            //更新收款
            collectionBaseInfo.setPenaltyInterest(CollectionFinancialUtil.mithrasLongDecimalTwo(tempAmount.longValue()) - LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()));
        });
        //插入数据，更新数据
        //删除罚息记录
        collectionOverdueRecordInfoMapper.delete(Wrappers.<CollectionOverdueRecordInfo>lambdaQuery()
                .in(CollectionOverdueRecordInfo::getCollectionId, collectionIds));
        if (!newCollectionOverdueRecord.isEmpty()) {
            this.saveBatch(newCollectionOverdueRecord);
        }
        collectionBaseInfos.forEach(collectionBaseInfoMapper::updateById);
    }

    @Transactional
    public void modifyWriteOffStatus(List<Long> collectionIds) {
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectBatchIds(collectionIds);
        if(ObjectUtil.isEmpty(collectionBaseInfos)) {
            return;
        }
        //获取收款情况
        collectionBaseInfos.forEach(baseInfo -> {
            //检查核销状态
            Long amount = LongUtil.null2zero(baseInfo.getCollectionAmount());
            CollectionWriteOffStatusEnum collectionWriteOffStatusEnum;
            Long applayMount = LongUtil.null2zero(baseInfo.getCashFlowAmount()) + LongUtil.null2zero(baseInfo.getPenaltyInterest()) - LongUtil.null2zero(baseInfo.getCollectionPenaltyInterest());
            if (amount <= 0) {
                collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.UNCOLLECTION;
            } else if (amount < applayMount) {
                collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF;
            } else {
                collectionWriteOffStatusEnum = CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED;
            }
            baseInfo.setWriteOffStatus(collectionWriteOffStatusEnum.name());
        });
        collectionBaseInfos.forEach(collectionBaseInfoMapper::updateById);
    }

    //获取实际没天逾期记录.还款后次日方减去金额
    private Map<LocalDate, Long> getAmountDetail(List<CollectionRecordInfo> recordInfos, CollectionBaseInfo collectionBaseInfo) {
        Map<LocalDate, Long> rspMap = new HashMap<>();
        Map<LocalDate, Long> recordMap = new HashMap<>();
        Long planCollectionAmount = collectionBaseInfo.getPlanCollectionAmount();
        if (ObjectUtil.isNotEmpty(recordInfos)) {
            for (CollectionRecordInfo record : recordInfos) {
                if (record.getCollectionDate().isAfter(collectionBaseInfo.getPlanCollectionDate())) {
                    recordMap.put(record.getCollectionDate(), recordMap.getOrDefault(record.getCollectionDate(), 0L) + record.getCollectionAmount());
                } else {
                    planCollectionAmount = planCollectionAmount - record.getCollectionAmount();
                }
            }
        }
        for (LocalDate beganDate = collectionBaseInfo.getPlanCollectionDate(); beganDate.isBefore(LocalDate.now()); ) {
            //计算每天的逾期金额
            planCollectionAmount = planCollectionAmount - recordMap.getOrDefault(beganDate, 0L);
            if (LongUtil.null2zero(planCollectionAmount) > 0) {
                rspMap.put(beganDate, planCollectionAmount);
            }
            beganDate = beganDate.plusDays(1);
        }
        return rspMap;
    }


}
