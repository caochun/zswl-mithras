package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociationReportMainBusinessClientSnapshot {

    Long id;

    String clientName;

    String uscCode;
}
