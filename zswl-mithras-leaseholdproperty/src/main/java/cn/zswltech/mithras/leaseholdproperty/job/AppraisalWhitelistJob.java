package cn.zswltech.mithras.leaseholdproperty.job;

import cn.zswltech.mithras.leaseholdproperty.application.port.AppraisalWhitelistJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@Slf4j
@Component
public class AppraisalWhitelistJob {

    @Resource
    private AppraisalWhitelistJobPort appraisalWhitelistJobPort;

    @XxlJob("AppraisalWhitelistDailyJob")
    public void AppraisalWhitelistDailyJob() {
        appraisalWhitelistJobPort.appraisalWhitelistDailyJob();
    }
}
