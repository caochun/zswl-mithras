package cn.zswltech.mithras.service.flow.listener.process.duration;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.domain.req.ModelPageReq;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.NodeStartEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.workflow.application.flow.ProjNodeTimeService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;

/**
 * @author luyi
 * 统计任务耗时
 */
@Component
@Slf4j
public class ProjRelateNodeListener {


    @Component
    public static class NodeEndListener implements ApplicationListener<NodeEndEvent> {
        @Override
        public void onApplicationEvent(NodeEndEvent event) {
            log.info("节点结束，记录时间. inst id:{}, node:{}", event.getNodeCommonContext().getProcessInstanceId(), event.getNodeCommonContext().getActivityId());
            record(event.getNodeCommonContext(), false);
        }
    }


    @Component
    public static class NodeStartListener implements ApplicationListener<NodeStartEvent> {
        @Override
        public void onApplicationEvent(NodeStartEvent event) {
            log.info("节点开始，记录时间. inst id:{}, node:{}", event.getNodeCommonContext().getProcessInstanceId(), event.getNodeCommonContext().getActivityId());
            record(event.getNodeCommonContext(), true);
        }
    }


    private static void record(NodeCommonContext nodeCommonContext, boolean start) {
        try {
            String activityId = nodeCommonContext.getActivityId();
            String modelKey = nodeCommonContext.getModelKey();
            String businessKey = nodeCommonContext.getBusinessKey();
            Long reviewId = null;
            Long establishId = null;
            Integer establishType = null;

            //普通立项
            if (equalsAny(modelKey,
                    ProjEstablishCreateFlow.name(),
                    ProjEstablishModifyFlow.name())) {
                establishId = Long.valueOf(businessKey);
                establishType = 1;
            }
            //集团授信立项
            if (equalsAny(modelKey,
                    GroupCreditReviewCreateFlow.name(),
                    GroupCreditReviewModifyFlow.name())) {
                establishId = Long.valueOf(businessKey);
                establishType = 2;
            }

            //评审流程
            if (ProjReviewCreateFlow.name().equals(modelKey)
                    || ProjReviewModifyFlow.name().equals(modelKey)) {
                reviewId = Long.valueOf(businessKey);
                ProjReviewBaseInfo review = getBean(ProjReviewBaseInfoService.class).getById(reviewId);
                Long groupCreditReviewId = review.getGroupCreditReviewId();
                if (null != groupCreditReviewId) {
                    establishId = groupCreditReviewId;
                    establishType = 2;
                } else {
                    establishId = review.getProjEstablishId();
                    establishType = 1;
                }
            }
            if (null == establishId) {
                return;
            }
            //
            String key = modelKey + "_" + activityId + "_" + (start ? "start_at" : "end_at");
            getBean(ProjNodeTimeService.class).saveKey(establishId, establishType, reviewId, key, LocalDateTime.now(), true);
        } catch (Exception e) {
            log.warn("记录项目维度节点时间失败", e);
        }
    }

    //全量初始化历史数据
    public void historyInit() {
        //默认初始化最近6个月的
        initTask();
    }


    private void initTask() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -6);
        Date from = instance.getTime();
        //流程类型
        List<String> flows = ListUtil.of(ProjEstablishCreateFlow.name(), ProjEstablishModifyFlow.name(),
                GroupCreditReviewCreateFlow.name(), GroupCreditReviewModifyFlow.name(),
                ProjReviewCreateFlow.name(), ProjReviewModifyFlow.name());
        Set<String> instanceIdList = new HashSet();
        Map<String, String> businessKeyMap = new HashMap<>();
        //运行中的流程实例
        List<ProcessInstance> runningInstList = getBean(RuntimeService.class).createProcessInstanceQuery().startedAfter(from)
                .processDefinitionKeys(new HashSet<>(flows)).list();
        instanceIdList.addAll(runningInstList.stream().map(ProcessInstance::getProcessInstanceId).collect(Collectors.toSet()));
        businessKeyMap.putAll(runningInstList.stream().collect(Collectors.toMap(ProcessInstance::getProcessInstanceId, ProcessInstance::getBusinessKey)));
        //历史流程实例
        List<HistoricProcessInstance> historyInstanceList = getBean(HistoryService.class).createHistoricProcessInstanceQuery().startedAfter(from)
                .processDefinitionKeyIn(flows).list();
        instanceIdList.addAll(historyInstanceList.stream().map(e -> (HistoricProcessInstanceEntity) e).map(HistoricProcessInstanceEntity::getProcessInstanceId).collect(Collectors.toSet()));
        businessKeyMap.putAll(historyInstanceList.stream().map(e -> (HistoricProcessInstanceEntity) e)
                .collect(Collectors.toMap(HistoricProcessInstanceEntity::getProcessInstanceId, HistoricProcessInstanceEntity::getBusinessKey)));
        //审批中
        List<TaskInfo> taskList = getBean(TaskService.class).createTaskQuery().processInstanceIdIn(instanceIdList).list()
                .stream().map(e -> (TaskInfo) e).collect(Collectors.toList());
        //已审批
        taskList.addAll(getBean(HistoryService.class).createHistoricTaskInstanceQuery().processInstanceIdIn(instanceIdList).list()
                .stream().map(e -> (TaskInfo) e).collect(Collectors.toList()));
        ModelPageReq req = new ModelPageReq();
        req.setPageSize(500);

//        taskList = taskList.stream().filter(t -> t.getProcessInstanceId().equals("1858032")).collect(Collectors.toList());
        int index = 1;
        for (TaskInfo task : taskList) {
            log.info("总共{}个task，当前第{}个", taskList.size(), index++);
            Long reviewId = null;
            Long establishId = null;
            Integer establishType = null;
            try {
                //ProjEstablishCreateFlow:2:1060051
                String processDefinitionId = task.getProcessDefinitionId();
                String activityId = task.getTaskDefinitionKey();
                String processDefinitionKey = processDefinitionId.split(":")[0];
                log.info("处理task：{}, 实例id：{}", task.getId(), task.getProcessInstanceId());
                Long bk = Long.valueOf(businessKeyMap.get(task.getProcessInstanceId()));
                //立项流程
                if (processDefinitionId.startsWith(ProjEstablishCreateFlow.name()) ||
                        processDefinitionId.startsWith(ProjEstablishModifyFlow.name())) {
                    establishId = bk;
                    establishType = 1;
                }

                //授信立项流程
                if (processDefinitionId.startsWith(GroupCreditReviewCreateFlow.name()) ||
                        processDefinitionId.startsWith(GroupCreditReviewModifyFlow.name())) {
                    establishId = bk;
                    establishType = 2;
                }

                //评审流程
                if (processDefinitionId.startsWith(ProjReviewCreateFlow.name()) ||
                        processDefinitionId.startsWith(ProjReviewModifyFlow.name())) {
                    reviewId = bk;
                    ProjReviewBaseInfo review = getBean(ProjReviewBaseInfoService.class).getById(reviewId);
                    Long groupCreditReviewId = review.getGroupCreditReviewId();
                    if (null != groupCreditReviewId) {
                        establishId = groupCreditReviewId;
                        establishType = 2;
                    } else {
                        establishId = review.getProjEstablishId();
                        establishType = 1;
                    }
                }
                if (establishId == null) {
                    return;
                }
                //
                Date startTime = task.getCreateTime();
                getBean(ProjNodeTimeService.class).saveKey(establishId, establishType, reviewId, processDefinitionKey + "_" + activityId + "_" + "start_at", LocalDateTimeUtil.of(startTime), true);
                if (task instanceof HistoricTaskInstance) {
                    Date endTime = ((HistoricTaskInstance) task).getEndTime();
                    if (null != endTime) {
                        getBean(ProjNodeTimeService.class).saveKey(establishId, establishType, reviewId, processDefinitionKey + "_" + activityId + "_" + "end_at", LocalDateTimeUtil.of(endTime), true);
                    }
                }
            } catch (Exception e) {
                log.warn("历史task记录时间失败,task id:{}", task.getId(), e);
            }
        }
    }
}
