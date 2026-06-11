package cn.zswltech.mithras.policy.job.service;

public interface PolicyJobService {

    void policyAddJobHandler();

    void policyNoticeHandler();

    void policyStartReminderProcessHandler();

    void policyNodeAutoCommit();
}
