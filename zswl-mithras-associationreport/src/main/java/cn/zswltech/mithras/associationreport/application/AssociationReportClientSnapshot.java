package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociationReportClientSnapshot {

    private Long clientId;

    private String clientName;
}
