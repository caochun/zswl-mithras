package cn.zswltech.mithras.service.flow.listener.process.duration;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.domain.req.ModelPageReq;
import cn.zswltech.flow.core.extension.event.NodeEndEvent;
import cn.zswltech.flow.core.extension.event.NodeStartEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.flow.ContractNodeTimeService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.third.financial.impl.events.PaymentWriteOffEvent;
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

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;

/**
 * @author luyi
 * 统计任务耗时
 */
@Component
@Slf4j
public class ContractRelateNodeListener {


    /**
     * 项目投放监听
     */
    @Component
    public static class FirstInvestListener implements ApplicationListener<PaymentWriteOffEvent> {
        @Override
        public void onApplicationEvent(PaymentWriteOffEvent event) {
            try {
                PaymentActualDetail paymentActualDetail = event.getPaymentActualDetail();
                Long contractId = paymentActualDetail.getContractId();
                ContractBaseInfo contract = getBean(ContractBaseInfoMapper.class).selectById(contractId);
                Long reviewId = contract.getProjReviewId();
                //
                String key = "firstInvestAt";
                getBean(ContractNodeTimeService.class).saveKey(reviewId, contractId, key, LocalDateTime.now(), false);
            } catch (Exception e) {
                log.warn("记录第一次投放时间失败", e);
            }
        }
    }

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
            Long contractId = null;
            Long reviewId = null;

            //合同流程
            if (ContractStartRentFlow.name().equals(modelKey)
                    || ContractCreateFlow.name().equals(modelKey)) {
                contractId = Long.valueOf(businessKey);
                ContractBaseInfo contract = getBean(ContractBaseInfoMapper.class).selectById(contractId);
                reviewId = contract.getProjReviewId();
            }
            //付款流程
            if (PaymentCreateFlow.name().equals(modelKey)) {
                Long paymentId = Long.valueOf(businessKey);
                PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectById(paymentId);
                ContractBaseInfo contract = getBean(ContractBaseInfoMapper.class).selectById(paymentBaseInfo.getContractId());
                reviewId = contract.getProjReviewId();
            }
            if (null == reviewId) {
                return;
            }
            //
            String key = modelKey + "_" + activityId + "_" + (start ? "start_at" : "end_at");
            getBean(ContractNodeTimeService.class).saveKey(reviewId, contractId, key, LocalDateTime.now(), true);
        } catch (Exception e) {
            log.warn("记录合同维度节点时间失败", e);
        }
    }

    //全量初始化历史数据
    public void historyInit() {
        //默认初始化最近6个月的
        initTask();
        //第一笔投放时间记录
        initInvest();
    }

    private void initInvest() {
        Long reviewId = null;
        List<PaymentActualDetail> allDetail = getBean(PaymentActualDetailService.class).list();
        List<ContractBaseInfo> contractList = getBean(ContractBaseInfoService.class).listByIds(allDetail.stream().map(PaymentActualDetail::getContractId).collect(Collectors.toList()));
        Map<Long, Long> contractMap = contractList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId));
//        Map<Long, Long> projMap = getBean(ProjReviewBaseInfoService.class).listByIds(contractList.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList()))
//                .stream().filter(e -> e.getProjEstablishId() != null).collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId));
        for (PaymentActualDetail detail : allDetail) {
            reviewId = contractMap.get(detail.getContractId());
            if (null == reviewId) {
                continue;
            }
            LocalDateTime createTime = detail.getCreateTime();
            String key = "firstInvestAt";
            getBean(ContractNodeTimeService.class).saveKey(reviewId, detail.getContractId(), key, createTime, false);
        }
    }


    private void initTask() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -6);
        Date from = instance.getTime();
        //流程类型
        List<String> flows = ListUtil.of(ContractCreateFlow.name(), PaymentCreateFlow.name());
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

        for (TaskInfo task : taskList) {
            Long reviewId = null;
            Long contractId = null;
            try {
                //ProjEstablishCreateFlow:2:1060051
                String processDefinitionId = task.getProcessDefinitionId();
                String activityId = task.getTaskDefinitionKey();
                String processDefinitionKey = processDefinitionId.split(":")[0];

                Long bk = Long.valueOf(businessKeyMap.get(task.getProcessInstanceId()));
                //合同流程
                if (processDefinitionId.startsWith(ContractCreateFlow.name()) ||
                        processDefinitionId.startsWith(ContractStartRentFlow.name())) {
                    contractId = bk;
                    ContractBaseInfo contract = getBean(ContractBaseInfoMapper.class).selectById(contractId);
                    reviewId = contract.getProjReviewId();
                }
                //付款流程
                if (processDefinitionId.startsWith(PaymentCreateFlow.name())) {
                    Long paymentId = bk;
                    PaymentBaseInfo paymentBaseInfo = getBean(PaymentBaseInfoMapper.class).selectById(paymentId);
                    ContractBaseInfo contract = getBean(ContractBaseInfoMapper.class).selectById(paymentBaseInfo.getContractId());
                    contractId = contract.getId();
                    reviewId = contract.getProjReviewId();
                }
                if (null == contractId) {
                    return;
                }
                //
                Date startTime = task.getCreateTime();
                getBean(ContractNodeTimeService.class).saveKey(reviewId, contractId, processDefinitionKey + "_" + activityId + "_" + "start_at", LocalDateTimeUtil.of(startTime), true);
                if (task instanceof HistoricTaskInstance) {
                    Date endTime = ((HistoricTaskInstance) task).getEndTime();
                    if (null != endTime) {
                        getBean(ContractNodeTimeService.class).saveKey(reviewId, contractId, processDefinitionKey + "_" + activityId + "_" + "end_at", LocalDateTimeUtil.of(endTime), true);
                    }
                }
            } catch (Exception e) {
                log.warn("历史task记录时间失败,task id:{}", task.getId(), e);
            }
        }
    }
}
