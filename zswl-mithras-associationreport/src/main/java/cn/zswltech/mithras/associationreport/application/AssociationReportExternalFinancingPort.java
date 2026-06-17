package cn.zswltech.mithras.associationreport.application;

import java.time.LocalDate;
import java.util.List;

public interface AssociationReportExternalFinancingPort {

    List<AssociationReportExternalFinancingSnapshot> listExternalFinancing(LocalDate targetDate);
}
