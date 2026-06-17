package cn.zswltech.mithras.collection.application;

import lombok.Data;

@Data
public class CollectionDetailContractInfo {

    private Long clientId;

    private String contractCode;

    private Long bizDeptId;

    private String projName;

    private Long projSponsorUserId;

    private String bizType;

    private String leaseType;

    private String contractStatusDisplay;
}
