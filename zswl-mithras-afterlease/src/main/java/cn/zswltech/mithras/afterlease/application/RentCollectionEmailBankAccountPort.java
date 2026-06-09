package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;

public interface RentCollectionEmailBankAccountPort {
    BaseDataBankAccount getById(Long bankId);

    BaseDataBankAccount findByAccount(String accountBank, String accountName, String accountNumber);
}
