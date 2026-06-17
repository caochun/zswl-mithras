package cn.zswltech.mithras.collection.application.job;

import lombok.Data;

import java.util.List;

@Data
public class CollectionRentDueContractInfo {

    private Long clientId;

    private String contractCode;

    private Long projSponsorUserId;

    private List<Long> projCosponsorUserIds;

    private Long bizDeptLeaderId;
}
