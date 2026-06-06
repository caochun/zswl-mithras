package cn.zswltech.mithras.afterlease.job;

import cn.zswltech.mithras.afterlease.application.job.AfterLeaseCheckGuarantorInitJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @version 1.0
 * @description 租后检查初始化任务-初始化担保人信息
 * @since 2025/9/21 16:25
 **/
@Slf4j
@Component
public class AfterLeaseCheckInitJob {

    @Resource
    private AfterLeaseCheckGuarantorInitJobService afterLeaseCheckGuarantorInitJobService;

    @XxlJob("afterLeaseCheckInitGuarantorJob")
    public void afterLeaseCheckInitGuarantorJob() {
        afterLeaseCheckGuarantorInitJobService.initGuarantor();
    }
}
