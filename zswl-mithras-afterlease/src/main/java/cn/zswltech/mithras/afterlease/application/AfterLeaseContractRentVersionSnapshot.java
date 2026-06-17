package cn.zswltech.mithras.afterlease.application;

import java.time.LocalDate;

public class AfterLeaseContractRentVersionSnapshot {

    private Long contractId;

    private String version;

    private LocalDate cashFlowDate;

    private Long rent;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getCashFlowDate() {
        return cashFlowDate;
    }

    public void setCashFlowDate(LocalDate cashFlowDate) {
        this.cashFlowDate = cashFlowDate;
    }

    public Long getRent() {
        return rent;
    }

    public void setRent(Long rent) {
        this.rent = rent;
    }
}
