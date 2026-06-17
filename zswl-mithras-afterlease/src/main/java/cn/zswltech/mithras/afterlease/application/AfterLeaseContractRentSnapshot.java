package cn.zswltech.mithras.afterlease.application;

import java.time.LocalDate;

public class AfterLeaseContractRentSnapshot {

    private Long contractId;

    private Integer cashFlowPhase;

    private LocalDate cashFlowDate;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getCashFlowPhase() {
        return cashFlowPhase;
    }

    public void setCashFlowPhase(Integer cashFlowPhase) {
        this.cashFlowPhase = cashFlowPhase;
    }

    public LocalDate getCashFlowDate() {
        return cashFlowDate;
    }

    public void setCashFlowDate(LocalDate cashFlowDate) {
        this.cashFlowDate = cashFlowDate;
    }
}
