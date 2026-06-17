package cn.zswltech.mithras.collection.application.job;

public interface CollectionRentDueContractInfoPort {

    CollectionRentDueContractInfo getLatestContractInfo(Long contractId);
}
