package cn.zswltech.mithras.basedata.application;

import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;

public interface BaseDataBankAccountSyncPort {

    void syncBankAccount(BaseDataBankAccount baseDataBankAccount);
}
