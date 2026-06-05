package cn.zswltech.mithras.associationreport.service.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.associationreport.excel.AssociationReportBaseModel;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportApplyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportCreateREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportImportREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportImportRSP;
import cn.zswltech.mithras.dto.associationreport.AssociationReportListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportListRSP;

import java.util.List;

public interface AssociationReportApplicationService {

    void pushSubmit(List<Long> ids);

    String getTemplateFileUrl(String reportCategoryCode);

    String create(AssociationReportCreateREQ req);

    AssociationReport findByReportInstanceId(String reportInstanceId);

    AssociationReportImportRSP storeFromExcel(AssociationReportImportREQ req);

    PageR<AssociationReportListRSP> pageList(AssociationReportListREQ req);

    AssociationReport getById(Long id);

    void deleteById(Long id);

    List<AssociationReport> getReportList(List<String> reportInstanceIdList);

    List<AssociationReportBaseModel> getReportList(String reportCategoryCode, String reportInstanceId);

    List<AssociationReport> getReportList(AssociationReportApplyREQ req);

    void recalculate(Long applyId);

    boolean updateBatchById(List<AssociationReport> associationReportList);
}
