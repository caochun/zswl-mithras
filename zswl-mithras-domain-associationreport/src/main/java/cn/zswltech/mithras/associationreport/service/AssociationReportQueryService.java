package cn.zswltech.mithras.associationreport.service;

import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;

public interface AssociationReportQueryService {

    AssociationReport findByReportInstanceId(String reportInstanceId);

    AssociationReport findByCategoryYearPeriod(String category, int year, int period);
}
