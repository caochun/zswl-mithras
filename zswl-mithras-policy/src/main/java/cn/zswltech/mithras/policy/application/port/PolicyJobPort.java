package cn.zswltech.mithras.policy.application.port;

public interface PolicyJobPort {

    void policyAddJobHandler();

    void policyNoticeHandler();

    void policyStartReminderProcessHandler();

    void policyNodeAutoCommit();
}
