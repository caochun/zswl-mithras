package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociationReportContractReceiptBottomSnapshot {

    Long contractId;

    Long remainPrincipal;

    String leaseType;

    String province;
}
