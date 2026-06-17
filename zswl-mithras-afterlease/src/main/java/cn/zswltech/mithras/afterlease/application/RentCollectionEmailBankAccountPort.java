package cn.zswltech.mithras.afterlease.application;

public interface RentCollectionEmailBankAccountPort {
    RentCollectionEmailBankAccountSnapshot getById(Long bankId);

    RentCollectionEmailBankAccountSnapshot findByAccount(String accountBank, String accountName, String accountNumber);
}
