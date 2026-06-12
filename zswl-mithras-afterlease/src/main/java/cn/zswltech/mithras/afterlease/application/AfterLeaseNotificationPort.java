package cn.zswltech.mithras.afterlease.application;

public interface AfterLeaseNotificationPort {

    void sendCheckPlanTimeoutRemind(Long toId, Long planClientId, String relation);

    void sendReportApprovalRemind(
            Long toId,
            String taskId,
            String businessKey,
            String subModule,
            String clientName,
            String modelName,
            String processInstanceId);
}
