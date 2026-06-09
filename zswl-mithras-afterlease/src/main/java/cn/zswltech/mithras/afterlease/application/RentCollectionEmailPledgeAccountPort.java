package cn.zswltech.mithras.afterlease.application;

import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;

public interface RentCollectionEmailPledgeAccountPort {
    BaseDataBankAccountListRSP findPledgeAccount(Long contractId);
}
