package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociationReportMainBusinessContractSnapshot {

    Long id;

    String contractCode;

    String leaseType;

    String bizType;

    String leaseItemTypes;
}
