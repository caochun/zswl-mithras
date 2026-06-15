package cn.zswltech.mithras.associationreport.application;

public interface AssociationReportWorkflowPort {

    void startApplyFlow(Long applyId, String modelKey);

    void startPushFlow(Long applyId, String reportCategoryNames);

    void ccComprehensiveDept(String processInstanceId);
}
