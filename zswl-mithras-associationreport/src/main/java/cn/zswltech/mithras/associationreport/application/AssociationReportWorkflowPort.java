package cn.zswltech.mithras.associationreport.application;

public interface AssociationReportWorkflowPort {

    void startPushFlow(Long applyId, String reportCategoryNames);

    void ccComprehensiveDept(String processInstanceId);
}
