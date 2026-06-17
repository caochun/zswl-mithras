package cn.zswltech.mithras.associationreport.application;

import java.time.LocalDate;
import java.util.List;

public interface AssociationReportContractReceiptBottomPort {

    List<AssociationReportContractReceiptBottomSnapshot> listByReportDate(LocalDate reportDate);
}
