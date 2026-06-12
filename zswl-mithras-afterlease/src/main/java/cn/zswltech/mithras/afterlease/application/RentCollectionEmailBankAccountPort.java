package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;

public interface RentCollectionEmailBankAccountPort {
    BaseDataBankAccount getById(Long bankId);

    BaseDataBankAccount findByAccount(String accountBank, String accountName, String accountNumber);
}
