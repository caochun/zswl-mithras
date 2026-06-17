package cn.zswltech.mithras.afterlease.application;

public interface RentCollectionEmailPledgeAccountPort {
    RentCollectionEmailBankAccountSnapshot findPledgeAccount(Long contractId);
}
