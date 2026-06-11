package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MarginCollectionRecordInfo {
    private Long id;
    private Long contractId;
    private Long clientId;
    private String code;
    private String contractCode;
    private Integer phase;
    private String paymentCode;
    private LocalDate collectionDate;
    private Long collectionAmount;
    private String writeOffStatus;
    private Long cashFlowAmount;
    private String cashFlowItem;
    private Long interest;
    private Long penaltyInterest;
    private Long principal;
    private LocalDate planCollectionDate;
    private Long planCollectionAmount;
    private Long collectionPrincipal;
    private Long collectionInterest;
    private Long collectionPenaltyInterest;
    private Long ourAccountId;
    private String ourAccountName;
    private String ourAccountNumber;
    private String ourAccountBank;
}
