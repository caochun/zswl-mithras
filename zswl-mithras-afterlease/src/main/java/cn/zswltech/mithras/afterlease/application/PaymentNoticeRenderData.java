package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class PaymentNoticeRenderData {
    String clientName;
    String contractCode;
    Integer phase;
    LocalDate planCollectionDate;
    Long planCollectionAmount;
    Long principal;
    Long interest;
    String accountName;
    String accountBank;
    String accountNumber;
    String sponsorUserName;
    String sponsorTelephone;
}
