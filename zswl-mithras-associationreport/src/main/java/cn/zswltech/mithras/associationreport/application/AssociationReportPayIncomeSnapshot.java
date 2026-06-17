package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociationReportPayIncomeSnapshot {

    Long deptId;

    String industryDisplay;

    String riskControlIndustryClassifyDisplay;

    String leaseTypeDisplay;

    Long projectAmount;

    String orgScaleDisplay;
}
