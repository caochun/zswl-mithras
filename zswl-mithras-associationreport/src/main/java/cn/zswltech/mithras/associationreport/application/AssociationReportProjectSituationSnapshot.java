package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociationReportProjectSituationSnapshot {

    Long clientId;

    String clientName;

    Integer isRelated;

    Long belongGroupClientId;

    Long principalBalance;

    Long marginBalance;
}
