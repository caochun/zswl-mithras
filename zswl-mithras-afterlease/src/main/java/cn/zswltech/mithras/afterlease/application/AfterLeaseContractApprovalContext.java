package cn.zswltech.mithras.afterlease.application;

public class AfterLeaseContractApprovalContext {

    private Long clientId;

    private Long bizDeptId;

    private Long bizDeptLeaderId;

    private Long bizDivisionLeaderId;

    private String bizType;

    private String projName;

    private String projCosponsorUserIds;

    private Long applyCreditAmount;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getBizDeptId() {
        return bizDeptId;
    }

    public void setBizDeptId(Long bizDeptId) {
        this.bizDeptId = bizDeptId;
    }

    public Long getBizDeptLeaderId() {
        return bizDeptLeaderId;
    }

    public void setBizDeptLeaderId(Long bizDeptLeaderId) {
        this.bizDeptLeaderId = bizDeptLeaderId;
    }

    public Long getBizDivisionLeaderId() {
        return bizDivisionLeaderId;
    }

    public void setBizDivisionLeaderId(Long bizDivisionLeaderId) {
        this.bizDivisionLeaderId = bizDivisionLeaderId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getProjCosponsorUserIds() {
        return projCosponsorUserIds;
    }

    public void setProjCosponsorUserIds(String projCosponsorUserIds) {
        this.projCosponsorUserIds = projCosponsorUserIds;
    }

    public Long getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(Long applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }
}
