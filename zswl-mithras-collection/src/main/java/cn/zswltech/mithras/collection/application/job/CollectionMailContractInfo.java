package cn.zswltech.mithras.collection.application.job;

import lombok.Data;

@Data
public class CollectionMailContractInfo {

    private Long id;

    private String contractCode;

    private String contractStatus;

    private boolean settled;

    private Long clientId;

    private Long projSponsorUserId;
}
