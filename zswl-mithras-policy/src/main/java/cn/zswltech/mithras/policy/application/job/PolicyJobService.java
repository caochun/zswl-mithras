package cn.zswltech.mithras.policy.application.job;

public interface PolicyJobService {

    void policyAddJobHandler();

    void policyNoticeHandler();

    void policyStartReminderProcessHandler();

    void policyNodeAutoCommit();
}
