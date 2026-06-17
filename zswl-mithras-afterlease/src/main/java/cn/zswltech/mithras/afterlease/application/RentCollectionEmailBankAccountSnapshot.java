package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RentCollectionEmailBankAccountSnapshot {
    Long id;
    String accountName;
    String accountNumber;
    String accountBank;
}
