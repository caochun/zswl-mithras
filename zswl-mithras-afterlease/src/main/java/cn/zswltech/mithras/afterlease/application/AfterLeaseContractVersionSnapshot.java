package cn.zswltech.mithras.afterlease.application;

public class AfterLeaseContractVersionSnapshot {

    private Long id;

    private Long contractId;

    private String version;

    private Long applyCreditAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(Long applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }
}
