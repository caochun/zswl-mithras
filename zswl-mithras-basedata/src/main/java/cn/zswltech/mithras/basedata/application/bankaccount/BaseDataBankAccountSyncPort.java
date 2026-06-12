package cn.zswltech.mithras.basedata.application.bankaccount;

import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;

public interface BaseDataBankAccountSyncPort {

    void syncBankAccount(BaseDataBankAccount baseDataBankAccount);
}
