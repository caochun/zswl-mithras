package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.RentCollectionDetailDataPort;
import cn.zswltech.mithras.afterlease.application.RentCollectionDetailSnapshot;
import cn.zswltech.mithras.afterlease.application.RentCollectionLeasePriceSnapshot;
import cn.zswltech.mithras.afterlease.application.RentCollectionOverdueContext;
import cn.zswltech.mithras.afterlease.application.RentCollectionRecordSnapshot;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractLeasePriceMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractLeasePriceLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePriceLib;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RentCollectionDetailDataPortAdapter implements RentCollectionDetailDataPort {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Resource
    private ContractLeasePriceLibMapper contractLeasePriceLibMapper;

    @Resource
    private ContractLeasePriceMapper contractLeasePriceMapper;

    @Override
    public RentCollectionDetailSnapshot getRentCollectionById(Long collectionId) {
        return toSnapshot(collectionBaseInfoMapper.selectById(collectionId));
    }

    @Override
    public List<RentCollectionRecordSnapshot> listCollectionRecords(Long collectionId) {
        return collectionRecordInfoMapper.selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                        .eq(CollectionRecordInfo::getCollectionId, collectionId)
                        .orderByAsc(CollectionRecordInfo::getSortId))
                .stream()
                .map(this::toRecordSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public RentCollectionOverdueContext getOverdueContext(Long collectionId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(collectionId);
        if (collectionBaseInfo == null) {
            return null;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(collectionBaseInfo.getContractId());
        if (contractBaseInfo == null) {
            return null;
        }
        ContractLeasePriceLib leasePrice = contractLeasePriceLibMapper.selectOne(Wrappers.<ContractLeasePriceLib>lambdaQuery()
                .eq(ContractLeasePriceLib::getContractId, contractBaseInfo.getId())
                .eq(ContractLeasePriceLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractLeasePriceLib::getVersion)
                .last("LIMIT 1"));
        return RentCollectionOverdueContext.builder()
                .contractBizType(contractBaseInfo.getBizType())
                .defaultInterestRate(leasePrice == null ? null : leasePrice.getDefaultInterestRate())
                .build();
    }

    @Override
    public List<RentCollectionDetailSnapshot> listRentCollectionsByContractIds(Collection<Long> contractIds) {
        if (contractIds == null || contractIds.isEmpty()) {
            return Collections.emptyList();
        }
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getContractId, contractIds)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPhase, 0))
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentCollectionDetailSnapshot> listRentCollectionsByContractId(Long contractId) {
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, contractId)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()))
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentCollectionDetailSnapshot> listOverdueRentCollectionsByReceiptIds(Collection<Long> receiptIds) {
        if (receiptIds == null || receiptIds.isEmpty()) {
            return Collections.emptyList();
        }
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getReceiptId, receiptIds)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                        .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()))
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentCollectionDetailSnapshot> listRentCollectionsByIds(Collection<Long> collectionIds) {
        if (collectionIds == null || collectionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getId, collectionIds))
                .stream()
                .map(this::toSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentCollectionLeasePriceSnapshot> listLeasePricesByContractIds(Collection<Long> contractIds) {
        if (contractIds == null || contractIds.isEmpty()) {
            return Collections.emptyList();
        }
        return contractLeasePriceMapper.selectList(Wrappers.<ContractLeasePrice>lambdaQuery()
                        .in(ContractLeasePrice::getContractId, contractIds))
                .stream()
                .map(this::toLeasePriceSnapshot)
                .collect(Collectors.toList());
    }

    private RentCollectionDetailSnapshot toSnapshot(CollectionBaseInfo collectionBaseInfo) {
        if (collectionBaseInfo == null) {
            return null;
        }
        return RentCollectionDetailSnapshot.builder()
                .id(collectionBaseInfo.getId())
                .contractId(collectionBaseInfo.getContractId())
                .receiptId(collectionBaseInfo.getReceiptId())
                .receiptCode(collectionBaseInfo.getReceiptCode())
                .code(collectionBaseInfo.getCode())
                .phase(collectionBaseInfo.getPhase())
                .writeOffStatus(collectionBaseInfo.getWriteOffStatus())
                .collectionDate(collectionBaseInfo.getCollectionDate())
                .collectionAmount(collectionBaseInfo.getCollectionAmount())
                .principal(collectionBaseInfo.getPrincipal())
                .interest(collectionBaseInfo.getInterest())
                .penaltyInterest(collectionBaseInfo.getPenaltyInterest())
                .penaltyInterestDeductionAmount(collectionBaseInfo.getPenaltyInterestDeductionAmount())
                .planPenaltyInterestDate(collectionBaseInfo.getPlanPenaltyInterestDate())
                .planCollectionAmount(collectionBaseInfo.getPlanCollectionAmount())
                .planCollectionDate(collectionBaseInfo.getPlanCollectionDate())
                .collectionPenaltyInterest(collectionBaseInfo.getCollectionPenaltyInterest())
                .overdueCollectionCount(collectionBaseInfo.getOverdueCollectionCount())
                .emailNoticeCount(collectionBaseInfo.getEmailNoticeCount())
                .build();
    }

    private RentCollectionRecordSnapshot toRecordSnapshot(CollectionRecordInfo recordInfo) {
        return RentCollectionRecordSnapshot.builder()
                .id(recordInfo.getId())
                .collectionType(recordInfo.getCollectionType())
                .collectionDate(recordInfo.getCollectionDate())
                .principal(recordInfo.getPrincipal())
                .interest(recordInfo.getInterest())
                .penaltyInterest(recordInfo.getPenaltyInterest())
                .build();
    }

    private RentCollectionLeasePriceSnapshot toLeasePriceSnapshot(ContractLeasePrice leasePrice) {
        return RentCollectionLeasePriceSnapshot.builder()
                .contractId(leasePrice.getContractId())
                .repayTimesTotal(leasePrice.getRepayTimesTotal())
                .build();
    }
}
