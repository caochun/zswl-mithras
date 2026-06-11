package cn.zswltech.mithras.policy.job;

import cn.zswltech.mithras.policy.job.service.PolicyJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class PolicyJob {

    @Resource
    private PolicyJobService policyJobService;

    @XxlJob("policyAddJobHandler")
    public void policyAddJobHandler() {
        policyJobService.policyAddJobHandler();
    }

    @XxlJob("policyNoticeHandler")
    public void policyNoticeHandler() {
        policyJobService.policyNoticeHandler();
    }

    @XxlJob("policyStartReminderProcessHandler")
    public void policyStartReminderProcessHandler() {
        policyJobService.policyStartReminderProcessHandler();
    }

    @XxlJob("policyNodeAutoCommit")
    public void policyNodeAutoCommit() {
        policyJobService.policyNodeAutoCommit();
    }
}
