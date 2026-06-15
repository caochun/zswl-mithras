package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

@Data
public class MarginContractInfo {
    private Long clientId;
    private Long projSponsorUserId;
    private Long bizDeptId;
    private String contractCode;
    private String bizType;
    private String leaseType;
    private String projName;
    private String contractStatus;
}
