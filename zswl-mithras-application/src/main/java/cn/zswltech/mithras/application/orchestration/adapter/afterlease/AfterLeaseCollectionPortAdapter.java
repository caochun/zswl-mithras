package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCollectionPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCollectionEmailSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeaseReceiptCollectionSnapshot;
import cn.zswltech.mithras.afterlease.application.AfterLeasePenaltyCollection;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AfterLeaseCollectionPortAdapter implements AfterLeaseCollectionPort {
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Override
    public Page<AfterLeaseReceiptCollectionSnapshot> pageReceiptCollectionByPaymentCode(String paymentCode, long page, long pageSize) {
        Page<CollectionBaseInfo> collectionPage = collectionBaseInfoService.page(new Page<>(page, pageSize),
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getPaymentCode, paymentCode)
                        .gt(CollectionBaseInfo::getPenaltyInterest, 0L));
        Page<AfterLeaseReceiptCollectionSnapshot> snapshotPage =
                new Page<>(collectionPage.getCurrent(), collectionPage.getSize(), collectionPage.getTotal());
        snapshotPage.setPages(collectionPage.getPages());
        snapshotPage.setRecords(collectionPage.getRecords().stream()
                .map(this::toReceiptCollectionSnapshot)
                .collect(Collectors.toList()));
        return snapshotPage;
    }

    @Override
    public AfterLeaseCollectionEmailSnapshot getEmailSnapshotById(Long collectionId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getById(collectionId);
        if (collectionBaseInfo == null) {
            return null;
        }
        return AfterLeaseCollectionEmailSnapshot.builder()
                .id(collectionBaseInfo.getId())
                .contractId(collectionBaseInfo.getContractId())
                .cashFlowItem(collectionBaseInfo.getCashFlowItem())
                .planCollectionDate(collectionBaseInfo.getPlanCollectionDate())
                .writeOffStatus(collectionBaseInfo.getWriteOffStatus())
                .build();
    }

    @Override
    public List<AfterLeasePenaltyCollection> listUnpaidPenaltyRentByContractId(Long contractId) {
        return collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, contractId)
                        .eq(CollectionBaseInfo::getCashFlowItem, "RENT")
                        .gt(CollectionBaseInfo::getPenaltyInterest, 0)
                        .apply(" penalty_interest > IFNULl(collection_penalty_interest, 0)")
                        .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                        .orderByAsc(CollectionBaseInfo::getReceiptCode))
                .stream()
                .map(this::toPenaltyCollection)
                .collect(Collectors.toList());
    }

    @Override
    public LocalDate findFirstUnpaidPenaltyPlanDate(Long contractId) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .gt(CollectionBaseInfo::getPenaltyInterest, 0)
                .apply(" penalty_interest > IFNULl(collection_penalty_interest, 0)")
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                .last("LIMIT 1"));
        return collectionBaseInfo == null ? null : collectionBaseInfo.getPlanCollectionDate();
    }

    @Override
    public Long sumRemainingPenaltyInterest(Long contractId) {
        return collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, contractId))
                .stream()
                .mapToLong(collectionBaseInfo -> nullToZero(collectionBaseInfo.getPenaltyInterest())
                        - nullToZero(collectionBaseInfo.getPenaltyInterestDeductionAmount())
                        - nullToZero(collectionBaseInfo.getCollectionPenaltyInterest()))
                .sum();
    }

    @Override
    public void updatePenaltyCollections(List<AfterLeasePenaltyCollection> collections) {
        collectionBaseInfoService.updateBatchById(collections.stream()
                .map(this::toCollectionBaseInfo)
                .collect(Collectors.toList()));
    }

    @Override
    public void updateEmailNoticeCount(Long collectionId, Integer emailNoticeCount) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setId(collectionId);
        collectionBaseInfo.setEmailNoticeCount(emailNoticeCount);
        collectionBaseInfoService.updateById(collectionBaseInfo);
    }

    private AfterLeaseReceiptCollectionSnapshot toReceiptCollectionSnapshot(CollectionBaseInfo collectionBaseInfo) {
        return AfterLeaseReceiptCollectionSnapshot.builder()
                .phase(collectionBaseInfo.getPhase())
                .planCollectionDate(collectionBaseInfo.getPlanCollectionDate())
                .principal(collectionBaseInfo.getPrincipal())
                .interest(collectionBaseInfo.getInterest())
                .penaltyInterest(collectionBaseInfo.getPenaltyInterest())
                .writeOffStatus(collectionBaseInfo.getWriteOffStatus())
                .build();
    }

    private AfterLeasePenaltyCollection toPenaltyCollection(CollectionBaseInfo collectionBaseInfo) {
        return AfterLeasePenaltyCollection.builder()
                .id(collectionBaseInfo.getId())
                .code(collectionBaseInfo.getCode())
                .paymentCode(collectionBaseInfo.getPaymentCode())
                .penaltyInterest(collectionBaseInfo.getPenaltyInterest())
                .penaltyInterestDeductionAmount(collectionBaseInfo.getPenaltyInterestDeductionAmount())
                .collectionPenaltyInterest(collectionBaseInfo.getCollectionPenaltyInterest())
                .planPenaltyInterestDate(collectionBaseInfo.getPlanPenaltyInterestDate())
                .overdueCollectionCount(collectionBaseInfo.getOverdueCollectionCount())
                .build();
    }

    private CollectionBaseInfo toCollectionBaseInfo(AfterLeasePenaltyCollection collection) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setId(collection.getId());
        collectionBaseInfo.setPenaltyInterestDeductionAmount(collection.getPenaltyInterestDeductionAmount());
        collectionBaseInfo.setPlanPenaltyInterestDate(collection.getPlanPenaltyInterestDate());
        collectionBaseInfo.setOverdueCollectionCount(collection.getOverdueCollectionCount());
        return collectionBaseInfo;
    }

    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }
}
