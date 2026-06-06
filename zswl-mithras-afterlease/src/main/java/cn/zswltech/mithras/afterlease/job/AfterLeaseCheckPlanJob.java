package cn.zswltech.mithras.afterlease.job;

import cn.zswltech.mithras.afterlease.application.job.AfterLeaseCheckPlanJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description 租后检查计划定时任务
 */
@Slf4j
@Component
public class AfterLeaseCheckPlanJob {

    @Resource
    private AfterLeaseCheckPlanJobService afterLeaseCheckPlanJobService;

    @XxlJob("startCheckPlan")
    public void startCheckPlan() {
        afterLeaseCheckPlanJobService.startCheckPlan();
    }

    @XxlJob("updateCheckPlanStatus")
    public void updateCheckPlanStatus() {
        afterLeaseCheckPlanJobService.updateCheckPlanStatus();
    }

    @XxlJob("checkPlanToBeInitiated")
    public void checkPlanToBeInitiated() {
        afterLeaseCheckPlanJobService.checkPlanToBeInitiated();
    }

    @XxlJob("afterLeaseCheckRemind")
    @Transactional(rollbackFor = Throwable.class)
    public void afterLeaseCheckRemind() {
        afterLeaseCheckPlanJobService.afterLeaseCheckRemind(XxlJobHelper.getJobParam());
    }
}
