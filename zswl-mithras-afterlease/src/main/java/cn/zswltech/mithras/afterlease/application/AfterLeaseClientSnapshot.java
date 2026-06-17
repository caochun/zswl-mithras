package cn.zswltech.mithras.afterlease.application;

public class AfterLeaseClientSnapshot {

    private Long clientId;

    private String clientName;

    private String clientType;

    private Long belongSponsorId;

    private Long belongDeptId;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public Long getBelongSponsorId() {
        return belongSponsorId;
    }

    public void setBelongSponsorId(Long belongSponsorId) {
        this.belongSponsorId = belongSponsorId;
    }

    public Long getBelongDeptId() {
        return belongDeptId;
    }

    public void setBelongDeptId(Long belongDeptId) {
        this.belongDeptId = belongDeptId;
    }
}
