package cn.zswltech.mithras.policy.job;

import cn.zswltech.mithras.policy.application.port.PolicyJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class PolicyJob {

    @Resource
    private PolicyJobPort policyJobPort;

    @XxlJob("policyAddJobHandler")
    public void policyAddJobHandler() {
        policyJobPort.policyAddJobHandler();
    }

    @XxlJob("policyNoticeHandler")
    public void policyNoticeHandler() {
        policyJobPort.policyNoticeHandler();
    }

    @XxlJob("policyStartReminderProcessHandler")
    public void policyStartReminderProcessHandler() {
        policyJobPort.policyStartReminderProcessHandler();
    }

    @XxlJob("policyNodeAutoCommit")
    public void policyNodeAutoCommit() {
        policyJobPort.policyNodeAutoCommit();
    }
}
