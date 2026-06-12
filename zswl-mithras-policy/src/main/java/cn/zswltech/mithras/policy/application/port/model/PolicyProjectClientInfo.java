package cn.zswltech.mithras.policy.application.port.model;

import lombok.Data;

@Data
public class PolicyProjectClientInfo {

    private String projectName;

    private String projectCode;

    private Long clientId;

    private String clientName;

    private Long projectSponsorUserId;

    private String projectCosponsorUserIds;
}
