package cn.zswltech.mithras.afterlease.application;

public class AfterLeaseContractSnapshot {

    private Long id;

    private Long clientId;

    private String contractCode;

    private Long applyCreditAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public Long getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(Long applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }
}
