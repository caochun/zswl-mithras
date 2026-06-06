package cn.zswltech.mithras.afterlease.application.job;

public interface AfterLeaseCheckPlanJobService {

    void startCheckPlan();

    void updateCheckPlanStatus();

    void checkPlanToBeInitiated();

    void afterLeaseCheckRemind(String jobParam);
}
