package cn.zswltech.mithras.associationreport.application;

import java.time.LocalDate;
import java.util.List;

public interface AssociationReportGuanYuanDataPort {

    List<AssociationReportPayIncomeSnapshot> listPayIncome(LocalDate queryFrom, LocalDate queryTo);

    List<AssociationReportProjectSituationSnapshot> listProjectSituation(LocalDate targetDate);
}
