package cn.zswltech.mithras.leaseholdproperty.application.port;

public interface LeaseholdContractContextPort {

    LeaseholdContractContextSnapshot getByContractId(Long contractId);

    Integer getStockContractFlag(Long projReviewId);
}
