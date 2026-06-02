package cn.zswltech.mithras.service.overdue.infrastructure.service;

import cn.zswltech.mithras.contract.overdue.application.job.SchedulingJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/23 10:28
 */
@Component
public class SchedulingJobServiceImpl {

    @Resource
    private SchedulingJobService schedulingJobService;

    @XxlJob("overdueClientInfoUpdateTask")
    public void overdueClientInfoUpdateTask() {
        schedulingJobService.overdueClientInfoUpdateTask();
    }
}
