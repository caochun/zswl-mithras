package cn.zswltech.mithras.afterlease.job;

import cn.zswltech.mithras.afterlease.application.job.AfterLeaseExternalQueryCreateJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/18 16:44
 */
@Component
public class AfterLeaseCheckExternalQueryJob {

    @Resource
    private AfterLeaseExternalQueryCreateJobService afterLeaseExternalQueryCreateJobService;

    @XxlJob(value = "createExternalQuery")
    public void createExternalQuery() {
        afterLeaseExternalQueryCreateJobService.createExternalQuery();
    }
}
