package cn.zswltech.mithras.afterlease.application;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;
import java.util.List;

public interface AfterLeaseCollectionPort {
    Page<AfterLeaseReceiptCollectionSnapshot> pageReceiptCollectionByPaymentCode(String paymentCode, long page, long pageSize);

    AfterLeaseCollectionEmailSnapshot getEmailSnapshotById(Long collectionId);

    List<AfterLeasePenaltyCollection> listUnpaidPenaltyRentByContractId(Long contractId);

    LocalDate findFirstUnpaidPenaltyPlanDate(Long contractId);

    Long sumRemainingPenaltyInterest(Long contractId);

    void updatePenaltyCollections(List<AfterLeasePenaltyCollection> collections);

    void updateEmailNoticeCount(Long collectionId, Integer emailNoticeCount);
}
