package cn.zswltech.mithras.afterlease.application.job;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.afterlease.application.AfterLeaseNotificationPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkdayCalendarPort;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/9 11:20
 */
@Slf4j
@Component
public class CheckPlanTimeOutNotifyTask {

    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckClientService;
    @Resource
    private AfterLeaseCheckPlanBaseService afterLeaseCheckPlanBaseService;
    @Resource
    private AfterLeaseNotificationPort notificationPort;
    @Resource
    private AfterLeaseWorkdayCalendarPort workdayCalendarPort;

    @XxlJob("checkPlanTimeOutNotifyTask")
    public void checkPlanTimeOutNotifyTask() {
        log.info("检查计划超时通知任务开始执行");
        String param = XxlJobHelper.getJobParam();
        int timeOutDay = Integer.parseInt(param);
        LocalDate line = workdayCalendarPort.getNextWorkdayAfterDays(null,timeOutDay);
        // XMX-39 查询到过期未提交且未通知过的计划
        List<NewAfterLeaseCheckPlanClient> toBeNotifyPalns = afterLeaseCheckClientService.list(Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .le(NewAfterLeaseCheckPlanClient::getCheckTime, line)
                .in(NewAfterLeaseCheckPlanClient::getApprovalStatus, ProcessStatus.UN_SUBMIT.name(), ProcessStatus.CANCELED.name())
                .eq(NewAfterLeaseCheckPlanClient::getCheckWay, AfterLeaseCheckWayEnum.SITE.name())
                .eq(NewAfterLeaseCheckPlanClient::getIsNotify, false));

        if (ObjectUtil.isEmpty(toBeNotifyPalns)) {
            log.info("检查计划超时通知任务执行结束，未查询到过期未提交且未通知过的计划");
            return;
        }

        toBeNotifyPalns.forEach(client -> {
            NewAfterLeaseCheckPlanBase plan = afterLeaseCheckPlanBaseService.getOne(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery().eq(NewAfterLeaseCheckPlanBase::getId, client.getPlanId()));
            String relation;
            try{
                relation = String.format("距%s的【%s】第【%s】次租后检查现场打卡已超10日，请在【%s】前完成报告提交。",
                        client.getClientName(),
                        StrUtil.sub(plan.getPlanName(), -13, -9),
                        StrUtil.sub(plan.getPlanName(), -5, -4),
                        plan.getDeadLine());
            }catch (Exception e){
                relation = String.format("距%s租后检查现场打卡已超10日，请及时完成报告提交。",
                        client.getClientName());
            }
            notificationPort.sendCheckPlanTimeoutRemind(client.getBelongSponsorId(), client.getId(), relation);
            client.setIsNotify(true);
        });
        afterLeaseCheckClientService.updateBatchById(toBeNotifyPalns);
        log.info("检查计划超时通知任务执行结束");
    }

    // XMX-129 租后检查管理台账 - 设置逾期天数
    @XxlJob("checkPlanSetOverdueDays")
    public void checkPlanSetOverdueDays() {
        // 首次执行带参数，初始化所有数据，后续每天处理未提交的即可
        String param = XxlJobHelper.getJobParam();
        // 当前时间
        LocalDate now = LocalDate.now();
        // 未提交的计划和审批中的
        List<NewAfterLeaseCheckPlanClient> unSubmitPlans = afterLeaseCheckClientService.list(
                Wrappers.<NewAfterLeaseCheckPlanClient>lambdaQuery()
                .in(StrUtil.isEmpty(param),NewAfterLeaseCheckPlanClient::getApprovalStatus, Arrays.asList(ProcessStatus.UN_SUBMIT.name(), ProcessStatus.UNDER_APPROVAL.name()))
        );

        if (ObjectUtil.isEmpty(unSubmitPlans)) {
            return;
        }

        // 非一般检查没有DeadLine
        Map<Long, List<NewAfterLeaseCheckPlanBase>> planMap = afterLeaseCheckPlanBaseService.list(Wrappers.<NewAfterLeaseCheckPlanBase>lambdaQuery()
                        .in(StrUtil.isEmpty(param), NewAfterLeaseCheckPlanBase::getId, unSubmitPlans.stream().map(NewAfterLeaseCheckPlanClient::getPlanId).distinct().collect(Collectors.toList()))
                        .isNotNull(NewAfterLeaseCheckPlanBase::getDeadLine))
                .stream().collect(Collectors.groupingBy(NewAfterLeaseCheckPlanBase::getId));


        unSubmitPlans.parallelStream().forEach(client -> {
            // 现场检查 逾期天数=报告提交日期 -（租后截止日+10个工作日）
            // 非现场检查 逾期天数=报告提交日期-租后截止日
            // 未逾期为0
            if(planMap.containsKey(client.getPlanId())){
                planMap.get(client.getPlanId()).forEach(plan -> {
                    LocalDate deadLine = AfterLeaseCheckWayEnum.SITE.name().equals(client.getCheckWay()) ? workdayCalendarPort.getNextWorkdayAfterDays(plan.getDeadLine(), 10) : plan.getDeadLine();
                    // 如果提交日期为空，则取当前时间
                    LocalDate commitTime = ObjectUtil.isNotNull(client.getCommitTime()) ? client.getCommitTime().toLocalDate() : now;
                    if(commitTime.isAfter(deadLine)){
                        client.setOverdueDays((int) ChronoUnit.DAYS.between(deadLine, commitTime));
                    }else{
                        client.setOverdueDays(0);
                    }
                });
            }
        });

        afterLeaseCheckClientService.updateBatchById(unSubmitPlans);
    }

}
