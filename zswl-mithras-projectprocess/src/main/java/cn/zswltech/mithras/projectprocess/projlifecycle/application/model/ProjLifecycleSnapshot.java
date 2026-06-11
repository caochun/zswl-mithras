package cn.zswltech.mithras.projectprocess.projlifecycle.application.model;

import lombok.Data;

import java.time.LocalDateTime;

public class ProjLifecycleSnapshot {

    @Data
    public static class ProjEstablish {
        private Long id;
        private String projEstablishStatus;
        private LocalDateTime createTime;
    }

    @Data
    public static class ProjReview {
        private Long id;
        private Long projEstablishId;
        private Long groupCreditReviewId;
        private String projReviewStatus;
        private LocalDateTime createTime;
    }

    @Data
    public static class Contract {
        private Long id;
        private Long projReviewId;
        private String contractStatus;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    @Data
    public static class ContractPrice {
        private Long contractId;
        private Long amount;
    }

    @Data
    public static class Payment {
        private Long id;
        private Long contractId;
        private Long applyPaymentAmount;
        private String paymentStatus;
    }

    @Data
    public static class PaymentActualDetail {
        private Long paymentId;
        private Long contractId;
        private Long paidInAmount;
    }

    @Data
    public static class Collection {
        private Long contractId;
        private Long planCollectionAmount;
        private Long collectionAmount;
    }
}
