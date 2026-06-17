package cn.zswltech.flow.core.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.zswltech.flow.core.aop.ParamCheck;
import cn.zswltech.flow.core.command.AddCommentCommand;
import cn.zswltech.flow.core.command.SaveTaskDelegateRecordCommand;
import cn.zswltech.flow.core.command.TurnToTargetNodeCommand;
import cn.zswltech.flow.core.dao.NodeBackRecordMapper;
import cn.zswltech.flow.core.dao.ProcessAuthMapper;
import cn.zswltech.flow.core.dao.ProcessInstanceExtMapper;
import cn.zswltech.flow.core.dao.TaskMapper;
import cn.zswltech.flow.core.dao.VoteRecordMapper;
import cn.zswltech.flow.core.domain.entity.AddSignRecord;
import cn.zswltech.flow.core.domain.entity.NodeBackRecord;
import cn.zswltech.flow.core.domain.entity.ProcessAuth;
import cn.zswltech.flow.core.domain.entity.VoteRecord;
import cn.zswltech.flow.core.domain.req.execution.*;
import cn.zswltech.flow.core.domain.resp.NodeDefineResp;
import cn.zswltech.flow.core.domain.resp.NodeResp;
import cn.zswltech.flow.core.enums.*;
import cn.zswltech.flow.core.exception.FlowException;
import cn.zswltech.flow.core.extension.provider.UserProvider;
import cn.zswltech.flow.core.extension.resp.UserResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.flow.core.service.impl.FlowBusinessStatusService;
import cn.zswltech.flow.core.service.impl.FlowCacheService;
import cn.zswltech.flow.core.service.impl.FlowModelService;
import cn.zswltech.flow.core.service.impl.FlowNotifyService;
import cn.zswltech.flow.core.util.FlowableUtils;
import cn.zswltech.flow.core.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.SingletonMap;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作相关
 *
 * This class intentionally keeps the upstream flow-core package name. It is a
 * runtime override/patch for the flow-core bean and must not be moved without
 * verifying all flow-core bean wiring.
 *
 * @author wangchuanhao
 * @date 2022/6/4 12:25 PM
 */
@Service
@Slf4j
public class FlowExecutionApiService {

    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ManagementService managementService;
    @Resource
    private FlowModelService flowModelService;
    @Resource
    private FlowBusinessStatusService flowBusinessStatusService;
    @Resource
    private FlowNotifyService flowNotifyService;
    @Resource
    private FlowAddSignRecordService flowAddSignRecordService;
    @Resource
    private TaskMapper taskMapper;
    @Resource
    private UserProvider userProvider;
    @Resource
    private ProcessAuthMapper processAuthMapper;
    @Resource
    private HistoryService historyService;
    @Resource
    private NodeBackRecordMapper nodeBackRecordMapper;
    @Resource
    private FlowModelApiService flowModelApiService;
    @Resource
    private ProcessInstanceExtMapper processInstanceExtMapper;
    @Resource
    private VoteRecordMapper voteRecordMapper;
    @Resource
    private FlowCacheService flowCacheService;

    /**
     * 通过
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void pass(ExecutionPassReq req) {
        TaskEntityImpl task = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        if (!req.getHandlerId().equals(task.getAssignee())) {
            if (Boolean.TRUE.equals(req.getCheckAssignee())) {
                throw FlowException.build("任务处理人与任务分配人不一致");
            }
            // 原任务审批人和处理人不同 把 任务审批人换成处理人
            taskService.setAssignee(task.getId(), req.getHandlerId());
        }

        PassBizTypeEnum passBizTypeEnum = PassBizTypeEnum.getByName(req.getButtonKey());
        if (Objects.isNull(passBizTypeEnum)) {
            throw FlowException.build("任务通过类型不合法");
        }
        checkButton(task.getId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), passBizTypeEnum.getButtonTypeEnum());

        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(),
                passBizTypeEnum.getCommentTypeEnum(),
                req.getMessage(), req.getHandlerId(), true));
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        Map<String, Object> varMap = new HashMap<>();
        // 审批通过的时候判断是否一票通过 有前加签标的不能通过当前节点
        AddSignRecord addSignRecord = flowAddSignRecordService.findByTaskId(task.getId());
        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (ParallelApprovalMethedEnum.ONE.getType().equals(userTaskExt.getParallelApprovalMethed())) {
            if (Objects.isNull(addSignRecord) || !AddSignTypeEnum.BEFORE.getType().equals(addSignRecord.getType())) {
                // 或签节点 且 非前加签任务
                varMap.put(ProcessNodeVariableEnum.MORE_COMPLETE.generateNodeVarName(task.getTaskDefinitionKey()), true);
                handleApprovalOne(task, false);
            }
        }
        if (req.getCustomVarMap() != null) {
            varMap.putAll(req.getCustomVarMap());
        }
        // 记录任务状态
        flowBusinessStatusService.recordBusinessStatus(req.getTaskId(), BusinessDataEnum.TASK.getType(), TaskBusinessStatusEnum.PASS.getStatus(), passBizTypeEnum.name());
        if (passBizTypeEnum.getIsVote()) {
            // 投票节点 记录 投票数据 (需要统计一次投票)
            Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            voteRecordMapper.insertSelective(VoteRecord.builder()
                    .type(passBizTypeEnum.name())
                    .taskId(task.getId())
                    .taskActivityId(task.getTaskDefinitionKey())
                    .processInstanceId(task.getProcessInstanceId())
                    .voterId(req.getHandlerId())
                    .note(req.getMessage())
                    // 多实例任务 父执行实例的id是一样的 用于标记为一次投票
                    .voteKey(execution.getParentId())
                    .build());
        }

        // 审批通过的时候判断是否处理前加签逻辑
        if (Objects.nonNull(addSignRecord)) {
            flowAddSignRecordService.passTask(task.getId());
            // FIXME 此处有并发问题
            if (AddSignTypeEnum.BEFORE.getType().equals(addSignRecord.getType())) {
                if (flowAddSignRecordService.countUndoneSubTask(addSignRecord.getPTaskId()) == 0) {
                    // 前加签 所有子任务都完成了 父任务审批人加签
                    HistoricTaskInstance historyTask = historyService.createHistoricTaskInstanceQuery().taskId(addSignRecord.getPTaskId()).singleResult();
                    String pTaskAssignee = Optional.ofNullable(historyTask.getAssignee()).orElse(historyTask.getOwner());
                    Execution newExecution = runtimeService.addMultiInstanceExecution(task.getTaskDefinitionKey(), historyTask.getProcessInstanceId(),
                            new SingletonMap<>(ProcessNodeVariableEnum.ASSIGNEE.generateNodeVarName(task.getTaskDefinitionKey()), pTaskAssignee));
//                    TaskEntityImpl pTask = queryRunningTaskWithCheck(addSignRecord.getPTaskId());
//                    taskService.setAssignee(pTask.getId(), pTask.getOwner());
//                    taskService.setOwner(pTask.getId(), null);
                }
            }
        }

        taskService.complete(task.getId(), varMap);

        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), passBizTypeEnum.getCommentTypeEnum().name());
    }

    /**
     * 一键通过
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void passAll(ExecutionProcessBaseReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        managementService.executeCommand(new AddCommentCommand(null, null, processInstance.getId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.YJTG,
                req.getMessage(), req.getHandlerId(), true));
        // 全局变量设置结束标志
        runtimeService.setVariable(req.getProcessInstanceId(), ProcessGlobalVariableEnum.END_FLAG.generateGlobalVarName(), ProcessBusinessStatusEnum.PASS_ALL.getType());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<EndEvent> endEvents = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(req.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.YJTG.name());
        managementService.executeCommand(new TurnToTargetNodeCommand(processInstance.getId(), endEvents.get(0).getId()));
    }

    /**
     * 一键拒绝
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void rejectAll(ExecutionProcessBaseReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        managementService.executeCommand(new AddCommentCommand(null, null, processInstance.getId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.YJJJ,
                req.getMessage(), req.getHandlerId(), true));
        // 全局变量设置结束标志
        runtimeService.setVariable(req.getProcessInstanceId(), ProcessGlobalVariableEnum.END_FLAG.generateGlobalVarName(), ProcessBusinessStatusEnum.REJECT_ALL.getType());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<EndEvent> endEvents = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(req.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.YJJJ.name());
        managementService.executeCommand(new TurnToTargetNodeCommand(processInstance.getId(), endEvents.get(0).getId()));
    }

    /**
     * 拒绝 跳转到结束节点
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void reject(ExecutionTaskBaseReq req) {
        TaskEntityImpl task = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        if (!req.getHandlerId().equals(task.getAssignee())) {
            if (Boolean.TRUE.equals(req.getCheckAssignee())) {
                throw FlowException.build("任务处理人与任务分配人不一致");
            }
            // 原任务审批人和处理人不同 把 任务审批人换成处理人
            taskService.setAssignee(task.getId(), req.getHandlerId());
        }
        checkButton(task.getId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), ApprovalButtonTypeEnum.DISAGREE);

        managementService.executeCommand(new AddCommentCommand(req.getTaskId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), CommentTypeEnum.SPJJ,
                req.getMessage(), req.getHandlerId(), true));

        // 记录任务状态
        flowBusinessStatusService.recordBusinessStatus(task.getId(), BusinessDataEnum.TASK.getType(), TaskBusinessStatusEnum.REJECT.getStatus());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        // 结束标志标为拒绝
        runtimeService.setVariable(task.getProcessInstanceId(), ProcessGlobalVariableEnum.END_FLAG.generateGlobalVarName(), ProcessBusinessStatusEnum.REJECT.getType());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List<EndEvent> endEvents = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        managementService.executeCommand(new TurnToTargetNodeCommand(task.getProcessInstanceId(), endEvents.get(0).getId()));
        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.SPJJ.name());
    }

    /**
     * 撤回流程 跳转到结束节点
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void cancel(ExecutionProcessBaseReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        if (!req.getHandlerId().equals(processInstance.getStartUserId())) {
            throw FlowException.build("非流程发起人，无法取消流程");
        }

        managementService.executeCommand(new AddCommentCommand(null, null, processInstance.getId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.QXLC,
                req.getMessage(), req.getHandlerId(), true));
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        // 结束标志标为撤回
        runtimeService.setVariable(processInstance.getId(), ProcessGlobalVariableEnum.END_FLAG.generateGlobalVarName(), ProcessBusinessStatusEnum.CANCEL.getType());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<EndEvent> endEvents = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        managementService.executeCommand(new TurnToTargetNodeCommand(processInstance.getId(), endEvents.get(0).getId()));
        processInstanceExtMapper.recordLastOperate(req.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.QXLC.name());
    }

    /**
     * 转交 一对一转交 一般是用户自己转
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void delegate(ExecutionDelegateReq req) {
        TaskEntityImpl originTask = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(originTask.getProcessInstanceId());
        // 将原任务数据更新
        originTask.setCreateTime(new Date());
        originTask.setAssignee(req.getEmployee());
        taskService.saveTask(originTask);
        // 创建新任务并结束掉 保留转办记录
        managementService.executeCommand(SaveTaskDelegateRecordCommand.builder()
                .oldTask(originTask)
                .employer(req.getHandlerId())
                .businessStatusEnum(TaskBusinessStatusEnum.DELEGATE)
                .overrideCreateDate(originTask.getCreateTime())
                .message(req.getMessage())
                .build());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(originTask.getId(), originTask.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(originTask.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.ZJ.name());
    }

    /**
     * 转办 把一个节点上的所有任务都清空 转办给一个人处理 一般是管理员操作
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void transfer(ExecutionTransferReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        if (StringUtils.isBlank(req.getTaskActivityId())) {
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(req.getProcessInstanceId());
            req.setTaskActivityId(activeActivityIds.get(0));
        }
        List<Task> taskList = taskService.createTaskQuery().active().processInstanceId(req.getProcessInstanceId()).taskDefinitionKey(req.getTaskActivityId()).list();
        if (CollectionUtils.isEmpty(taskList)) {
            throw FlowException.build("流程实例不存在或当前节点无任务");
        }
        managementService.executeCommand(new AddCommentCommand(null, null, req.getProcessInstanceId(), taskList.get(0).getProcessDefinitionId(), CommentTypeEnum.ZB,
                req.getMessage(), req.getHandlerId(), true));
        // 加签
        runtimeService.addMultiInstanceExecution(req.getTaskActivityId(), req.getProcessInstanceId(),
                new SingletonMap<>(ProcessNodeVariableEnum.ASSIGNEE.generateNodeVarName(req.getTaskActivityId()), req.getEmployee()));
        // 减签
        taskList.forEach(t -> {
            runtimeService.deleteMultiInstanceExecution(t.getExecutionId(), false);
        });
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(req.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.ZB.name());
    }

    /**
     * 跳转到指定节点 一般是根据流程操作
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void jump(ExecutionJumpReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        // 查询可跳转节点
        Set<String> canJumpActivityIdSet = queryCanJumpNodes(req.getProcessInstanceId()).stream().map(NodeResp::getActivityId).collect(Collectors.toSet());
        if (!canJumpActivityIdSet.contains(req.getTaskActivityId())) {
            throw FlowException.build("不可跳转至指定节点");
        }
//        if (inParallelBranch(processInstance, req.getTaskActivityId())) {
//            throw FlowException.build("不可跳转至并行流程节点内");
//        }
        if (isDynamicFormNode(processInstance.getProcessDefinitionId(), req.getTaskActivityId())) {
            throw FlowException.build("不支持跳转到系统特殊处理节点");
        }
        managementService.executeCommand(new AddCommentCommand(null, null, processInstance.getId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.TZ,
                req.getMessage(), req.getHandlerId(), true));
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        managementService.executeCommand(new TurnToTargetNodeCommand(processInstance.getId(), req.getTaskActivityId()));
        processInstanceExtMapper.recordLastOperate(req.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.TZ.name());
    }

    /**
     * 跳转到指定节点 一般是根据流程操作
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void randomReturn(ExecutionRandomReturnReq req) {
        TaskEntityImpl task = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        // 查询可跳转节点--所有节点
        Set<String> canJumpActivityIdSet = queryCanJumpNodes(req.getProcessInstanceId()).stream().map(NodeResp::getActivityId).collect(Collectors.toSet());
        if (!canJumpActivityIdSet.contains(req.getTaskActivityId())) {
            throw FlowException.build("不可跳转至指定节点");
        }
        if (inParallelBranch(processInstance, req.getTaskActivityId())) {
            throw FlowException.build("不可跳转至并行流程节点内");
        }
        if (isDynamicFormNode(processInstance.getProcessDefinitionId(), req.getTaskActivityId())) {
            throw FlowException.build("不支持跳转到系统特殊处理节点");
        }
        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        // !!! 兼容处理并行网关节点，找到所有运行中的任务节点，上面的代码不改动是为了保留被点击退回节点的一些校验和操作
        List<Task> runningTask = taskService.createTaskQuery().active().processInstanceId(task.getProcessInstanceId()).list();
        if (CollectionUtil.isEmpty(runningTask)) {
            throw FlowException.build("不存在运行中的任务节点");
        }
        List<NodeBackRecord> nodeBackRecordList = new LinkedList<>();
        Date now = new Date();
        for (Task t : runningTask) {
            // 驳回记录
            NodeBackRecord nodeBackRecord = NodeBackRecord.builder()
                    .processInstanceId(processInstance.getId())
                    .jumpToSourceFlag(Objects.isNull(req.getJumpToSourceFlag()) ? 0 : req.getJumpToSourceFlag())
                    .processStartUserId(processInstance.getStartUserId())
                    .handlerId(req.getHandlerId())
                    .type(2)
                    .status(1)
                    .sinkTaskActivityId(req.getTaskActivityId())
                    // 为了支持并行网关，这里需要进行判断后确定退回后直达本节点的节点位置
                    .sourceTaskActivityId(this.ensureBackTargetActivityId(task))
                    .gmtCreate(now)
                    .gmtModify(now)
                    .build();
            nodeBackRecordList.add(nodeBackRecord);
        }
        nodeBackRecordMapper.insertList(nodeBackRecordList);
        FlowNode destFlowNode = flowModelService.findSingleFlowNode(task.getProcessDefinitionId(), req.getTaskActivityId());
        if (destFlowNode == null) {
            throw FlowException.build("流程节点未找到");
        }
        //目前只逐级审批
        flowBusinessStatusService.recordBusinessStatus(task.getId(), BusinessDataEnum.TASK.getType(), TaskBusinessStatusEnum.BACK_TO_STEP.getStatus(), BackBizTypeEnum.BACK_TO_START_USER.name());
        // 或签节点处理人已确定
        if (ParallelApprovalMethedEnum.ONE.getType().equals(userTaskExt.getParallelApprovalMethed())) {
            handleApprovalOne(task, false);
        }

        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.BH.name());
        //记录流程操作历史
        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), BackBizTypeEnum.BACK_TO_STEP.getCommentTypeEnum(),
                req.getMessage(), req.getHandlerId(), true));
        //处理任务
        managementService.executeCommand(new TurnToTargetNodeCommand(task.getProcessInstanceId(), req.getTaskActivityId()));
        //抄送数据
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
    }

    private boolean isDynamicFormNode(String processDefineId, String taskActivityId) {
        UserTaskExt userTaskExt = flowModelApiService.findUserTaskExtByProcessDefinitionId(processDefineId, taskActivityId);
        return UserDefineTypeEnum.CUSTOM.getType().equals(userTaskExt.getApproverType());
    }

    /**
     * 当前节点是否处于并行分支内
     * @param processInstance
     * @param taskActivityId
     * @return
     */
    private boolean inParallelBranch(ProcessInstance processInstance, String taskActivityId) {
        Map<String, NodeDefineResp> nodeDefineMap = flowModelApiService.getNodeDefineListByProcessDefinitionId(processInstance.getProcessDefinitionId())
                .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
        Set<String> preNodeIdList = FlowableUtils.collectPreParallelGateway(nodeDefineMap, taskActivityId);
        return preNodeIdList.size() % 2 == 1;
    }

    /**
     * 驳回 一般是针对任务进行驳回
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void backToStep(ExecutionBackToStepReq req) {
        TaskEntityImpl task = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        // 查询可驳回节点
//        Set<String> canBackNodeIdSet = queryCanBackNodes(task.getId()).stream().map(NodeResp::getActivityId).collect(Collectors.toSet());
//        if (!canBackNodeIdSet.contains(req.getTaskActivityId())) {
//            throw FlowException.build("不可驳回至指定节点");
//        }
        if (!req.getHandlerId().equals(task.getAssignee())) {
            if (Boolean.TRUE.equals(req.getCheckAssignee())) {
                throw FlowException.build("任务处理人与任务分配人不一致");
            }
            // 原任务审批人和处理人不同 把 任务审批人换成处理人
            taskService.setAssignee(task.getId(), req.getHandlerId());
        }
        BackBizTypeEnum backBizTypeEnum = BackBizTypeEnum.calType(req.getButtonKey(), req.getJumpToSourceFlag());
        if (Objects.isNull(backBizTypeEnum)) {
            throw FlowException.build("任务退回类型不合法");
        }
        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        checkButton(task.getId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), backBizTypeEnum.getButtonTypeEnum());
        // 校验可退回节点数据
        if (BackBizTypeEnum.BACK_TO_STEP.equals(backBizTypeEnum)) {
            if (CollectionUtils.isEmpty(userTaskExt.getCanBackActivityIdList())
                    || !userTaskExt.getCanBackActivityIdList().contains(req.getTaskActivityId())) {
                throw FlowException.build("不允许退回该节点");
            }
        }
        // !!! 兼容处理并行网关节点，找到所有运行中的任务节点，上面的代码不改动是为了保留被点击退回节点的一些校验和操作
        List<Task> runningTask = taskService.createTaskQuery().active().processInstanceId(task.getProcessInstanceId()).list();
        if (CollectionUtil.isEmpty(runningTask)) {
            throw FlowException.build("不存在运行中的任务节点");
        }
        List<NodeBackRecord> nodeBackRecordList = new LinkedList<>();
        Date now = new Date();
        // 流程模型

        for (Task t : runningTask) {
            // 驳回记录
            NodeBackRecord nodeBackRecord = NodeBackRecord.builder()
                    .processInstanceId(processInstance.getId())
                    .jumpToSourceFlag(req.getJumpToSourceFlag())
                    .processStartUserId(processInstance.getStartUserId())
                    .handlerId(req.getHandlerId())
                    .type(2)
                    .status(1)
                    .sinkTaskActivityId(req.getTaskActivityId())
//                    .sourceTaskActivityId(t.getTaskDefinitionKey())
                    // 为了支持并行网关，这里需要进行判断后确定退回后直达本节点的节点位置
                    .sourceTaskActivityId(this.ensureBackTargetActivityId(task))
                    .gmtCreate(now)
                    .gmtModify(now)
                    .build();
            nodeBackRecordList.add(nodeBackRecord);
        }
//        nodeBackRecordMapper.insertSelective(nodeBackRecord);
        nodeBackRecordMapper.insertList(nodeBackRecordList);

        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), backBizTypeEnum.getCommentTypeEnum(),
                req.getMessage(), req.getHandlerId(), true));
        FlowNode destFlowNode = flowModelService.findSingleFlowNode(task.getProcessDefinitionId(), req.getTaskActivityId());
        if (destFlowNode == null) {
            throw FlowException.build("流程节点未找到");
        }
        flowBusinessStatusService.recordBusinessStatus(task.getId(), BusinessDataEnum.TASK.getType(), TaskBusinessStatusEnum.BACK_TO_STEP.getStatus(), backBizTypeEnum.name());
        // 驳回 删除已走过路径的记录 暂不需要
        // managementService.executeCommand(new ClearHistoryActinstCommand(task.getProcessInstanceId(), req.getTaskActivityId()));

        // 或签节点处理人已确定
        if (ParallelApprovalMethedEnum.ONE.getType().equals(userTaskExt.getParallelApprovalMethed())) {
            handleApprovalOne(task, false);
        }

        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.BH.name());

        managementService.executeCommand(new TurnToTargetNodeCommand(task.getProcessInstanceId(), req.getTaskActivityId()));
//        List<String> sourceActivityIds = runningTask.stream().map(TaskInfo::getTaskDefinitionKey).distinct().collect(Collectors.toList());
//        runtimeService.createChangeActivityStateBuilder().processInstanceId(processInstance.getProcessInstanceId()).moveActivityIdsToSingleActivityId(sourceActivityIds, req.getTaskActivityId()).changeState();
    }

    /**
     * 撤回任务
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void withdrawTask(ExecutionTaskBaseReq req) {
        HistoricTaskInstanceEntity task = (HistoricTaskInstanceEntity) historyService.createHistoricTaskInstanceQuery()
                .taskId(req.getTaskId())
                .processUnfinished()
                .singleResult();
        if (Objects.isNull(task)) {
            throw FlowException.build("任务不存在或流程已结束");
        }
        if (Objects.isNull(task.getEndTime())) {
            throw FlowException.build("任务未结束，无法撤回");
        }
        checkButton(task.getId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), ApprovalButtonTypeEnum.WITHDRAW_TASK);

//        TaskEntityImpl curProcessTask = (TaskEntityImpl) taskService.createTaskQuery()
//                .active()
//                .processInstanceId(task.getProcessInstanceId())
//                .list()
//                .get(0);

        // 如果任务是加签任务 不允许撤回
        if (flowAddSignRecordService.isAddSignTask(task.getId())) {
            throw FlowException.build("协同任务暂不支持撤回");
        }

        // 撤回类型 1 同节点撤回：加签；2 下一节点撤回：节点跳转
        Integer withdrawType;
//
//        // 如果是会签任务 只有还在当前节点才能撤回
//        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
//        if (ParallelApprovalMethedEnum.ALL.getType().equals(userTaskExt.getParallelApprovalMethed())) {
//            if (!task.getTaskDefinitionKey().equals(curProcessTask.getTaskDefinitionKey())) {
//                throw FlowException.build("会签已结束，不支持撤回");
//            }
//            withdrawType = 1;
//        } else {
//            // 非会签任务 判断判断流程当前所在节点是否是该节点的下一节点
//            // 找到此流程当前所在的任务节点 目前只考虑了流程同时只存在一个节点的情况
//            Map<String, NodeDefineResp> nodeDefineMap = flowModelApiService.getNodeDefineListByProcessDefinitionId(curProcessTask.getProcessDefinitionId())
//                    .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
//            Set<String> nextUserTaskIdSet = FlowableUtils.collectNextUserTask(nodeDefineMap, task.getTaskDefinitionKey());
//            if (!nextUserTaskIdSet.contains(curProcessTask.getTaskDefinitionKey())) {
//                throw FlowException.build("流程已走过下一节点，无法撤回");
//            }
//            withdrawType = 2;
//        }
        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        List<Task> runningTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).active().list();
        if (CollectionUtil.isEmpty(runningTaskList)) {
            throw FlowException.build("没有正在运行中的节点，无法撤回");
        }
        if (ParallelApprovalMethedEnum.ALL.getType().equals(userTaskExt.getParallelApprovalMethed())) {
            // 如果是会签任务 只有还在当前节点才能撤回
            if (!task.getTaskDefinitionKey().equals(runningTaskList.get(0).getTaskDefinitionKey())) {
                throw FlowException.build("会签已结束，不支持撤回");
            }
            // 同节点撤回：加签
            Execution existExecution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            Execution subExecution = runtimeService.addMultiInstanceExecution(task.getTaskDefinitionKey(), existExecution.getProcessInstanceId(),
                    new SingletonMap<>(ProcessNodeVariableEnum.ASSIGNEE.generateNodeVarName(task.getTaskDefinitionKey()), req.getHandlerId()));
        } else {
            // 判断流程当前节点是否任务节点的下一节点
            Map<String, NodeDefineResp> nodeDefineMap = flowModelApiService.getNodeDefineListByProcessDefinitionId(task.getProcessDefinitionId())
                    .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
            // 找到流程图中本节点的所有下一个用户节点
            Set<String> nextUserTaskIdSet = FlowableUtils.collectNextUserTask(nodeDefineMap, userTaskExt.getActivityId());
            // 根据当前节点的下一个节点类型来判断是否可以撤回
            NodeDefineEnum nodeType = FlowableUtils.nextNodeType(nodeDefineMap.get(userTaskExt.getActivityId()), nodeDefineMap);
            switch (nodeType) {
                case USER_TASK:
                case EXCLUSIVE_GATEWAY: {
                    if (!nextUserTaskIdSet.contains(runningTaskList.get(0).getTaskDefinitionKey())) {
                        throw FlowException.build("流程已走过下一个节点，无法撤回");
                    }
                    break;
                }
                case PARALLEL_GATEWAY: {
                    for (Task t : runningTaskList) {
                        nextUserTaskIdSet.removeIf(e -> Objects.equals(e, t.getTaskDefinitionKey()));
                    }
                    if (nextUserTaskIdSet.size() != 0) {
                        throw FlowException.build("流程已走过下一个节点，无法撤回");
                    }
                    break;
                }
                default: {
                    throw FlowException.build("当前流程状态无法撤回");
                }
            }
            // FIXME 此处需特殊逻辑，如果下一个节点只有一个且它的前驱是一个并行网关，则需要将该节点处理成一个execution拉回到当前操作节点，其他n个execution合并在并行网关上
            // FIXME 目前从业务上控制并行网关中的节点不允许撤回
            managementService.executeCommand(new TurnToTargetNodeCommand(task.getProcessInstanceId(), task.getTaskDefinitionKey(), nextUserTaskIdSet));
        }

        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        List<NodeBackRecord> nodeBackRecordList = new ArrayList<>(runningTaskList.size());
        Date now = new Date();
        for (Task t : runningTaskList) {
            NodeBackRecord nodeBackRecord = NodeBackRecord.builder()
                    .processInstanceId(processInstance.getId())
                    .jumpToSourceFlag(0)
                    .processStartUserId(processInstance.getStartUserId())
                    .handlerId(req.getHandlerId())
                    .type(1)
                    .status(1)
                    .sinkTaskActivityId(task.getTaskDefinitionKey())
                    .sourceTaskActivityId(t.getTaskDefinitionKey())
                    .gmtCreate(now)
                    .gmtModify(now)
                    .build();
            nodeBackRecordList.add(nodeBackRecord);
        }
//        nodeBackRecordMapper.insertSelective(nodeBackRecord);
        nodeBackRecordMapper.insertList(nodeBackRecordList);

        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), CommentTypeEnum.CH,
                req.getMessage(), req.getHandlerId(), true));
        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.CH.name());
    }

    /**
     * 流程撤回至指定节点
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void withdrawProcessToTargetNode(ExecutionWithdrawProcessReq req) {
        // 暂时先限制只能发起人节点撤回
        if (!"userTask_startUser".equals(req.getTaskActivityId())) {
            throw FlowException.build("仅发起人节点支持强制撤回");
        }
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());

        // 找到此流程当前所在的任务节点 目前只考虑了流程同时只存在一个节点的情况
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery()
                .active()
                .processInstanceId(processInstance.getId())
                .list()
                .get(0);

        NodeBackRecord nodeBackRecord = NodeBackRecord.builder()
                .processInstanceId(processInstance.getId())
                .jumpToSourceFlag(req.getJumpToSourceFlag())
                .processStartUserId(processInstance.getStartUserId())
                .handlerId(req.getHandlerId())
                .type(1)
                .status(1)
                .sinkTaskActivityId(req.getTaskActivityId())
                .sourceTaskActivityId(task.getTaskDefinitionKey())
                .build();
        nodeBackRecordMapper.insertSelective(nodeBackRecord);

        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), CommentTypeEnum.CHFQR,
                req.getMessage(), req.getHandlerId(), true));
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.CHFQR.name());

        managementService.executeCommand(new TurnToTargetNodeCommand(processInstance.getId(), req.getTaskActivityId()));
    }


    /**
     * 查询可跳转节点
     * 跳转 一般是以流程实例的维度进行跳转
     * 查询可跳转节点 所有可达的用户任务节点 排除掉当前所处的节点
     * 跳转时是否删除历史执行实例 暂时不删
     * @param processInstanceId
     * @return
     */
    public List<NodeResp> queryCanJumpNodes(String processInstanceId) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(processInstanceId);
        Process process = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId()).getMainProcess();
        // 查询所有可达的用户任务
        List<NodeResp> result = process.findFlowElementsOfType(UserTask.class).stream()
                .map(userTask -> NodeResp.builder()
                        .activityId(userTask.getId())
                        .name(userTask.getName())
                        .build())
                .collect(Collectors.toList());
        // 过滤掉当前任务处于的节点
        Set<String> processCurInActivityIds = taskService.createTaskQuery()
                .active()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .map(Task::getTaskDefinitionKey)
                .collect(Collectors.toSet());
        result.removeIf(nodeResp -> processCurInActivityIds.contains(nodeResp.getActivityId()));
        return result;
    }

    /**
     * 查询可驳回节点
     * 驳回 一般是针对任务进行驳回 查询可驳回节点
     * @return
     */
    public List<NodeResp> queryCanBackNodes(String taskId) {
        TaskEntityImpl task = queryRunningTaskWithCheck(taskId);
        String currActId = task.getTaskDefinitionKey();
        String processDefinitionId = task.getProcessDefinitionId();
        Process process = repositoryService.getBpmnModel(processDefinitionId).getMainProcess();
        FlowNode currentFlowElement = (FlowNode) process.getFlowElement(currActId, true);
        List<ActivityInstance> activitys =
                runtimeService.createActivityInstanceQuery().processInstanceId(task.getProcessInstanceId()).finished().orderByActivityInstanceStartTime().asc().list();
        List<String> activityIds =
                activitys.stream().filter(activity -> activity.getActivityType().equals(BpmnXMLConstants.ELEMENT_TASK_USER)).filter(activity -> !activity.getActivityId().equals(currActId)).map(ActivityInstance::getActivityId).distinct().collect(Collectors.toList());
        List<NodeResp> result = new ArrayList<>();
        for (String activityId : activityIds) {
            FlowNode toBackFlowElement = (FlowNode) process.getFlowElement(activityId, true);
            if (FlowableUtils.isReachable(process, toBackFlowElement, currentFlowElement)) {
                result.add(NodeResp.builder()
                        .activityId(toBackFlowElement.getId())
                        .name(toBackFlowElement.getName())
                        .build());
            }
        }
        // 过滤掉当前任务处于的节点
        Set<String> processCurInActivityIds = taskService.createTaskQuery()
                .active()
                .processInstanceId(task.getProcessInstanceId())
                .list()
                .stream()
                .map(Task::getTaskDefinitionKey)
                .collect(Collectors.toSet());
        result.removeIf(nodeResp -> processCurInActivityIds.contains(nodeResp.getActivityId()));
        return result;
    }

    /**
     * 前加签 等别人任务完后自己才继续任务
     * 串行 按比例通过 不允许前加签
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void beforeAddSign(ExecutionBeforeAddSignReq req) {
        TaskEntityImpl task = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        if (!req.getHandlerId().equals(task.getAssignee())) {
            if (Boolean.TRUE.equals(req.getCheckAssignee())) {
                throw FlowException.build("任务处理人与任务分配人不一致");
            }
        }
        checkButton(req.getTaskId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), ApprovalButtonTypeEnum.COLLABORATE);

        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), CommentTypeEnum.XT,
                req.getMessage(), req.getHandlerId(), true));

        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (ParallelApprovalMethedEnum.ONE.getType().equals(userTaskExt.getParallelApprovalMethed())) {
            handleApprovalOne(task, true);
        }
        List<String> executionIdList = new ArrayList<>();
        // 加签
        for (String addSignUserId : req.getAddSignUserIdList()) {
            Execution existExecution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            Execution subExecution = runtimeService.addMultiInstanceExecution(task.getTaskDefinitionKey(), existExecution.getProcessInstanceId(),
                    new SingletonMap<>(ProcessNodeVariableEnum.ASSIGNEE.generateNodeVarName(task.getTaskDefinitionKey()), addSignUserId));
            executionIdList.add(subExecution.getId());
        }
        // 新任务列表
        List<String> newTaskIdList = taskMapper.queryIdListByExecutionIdList(executionIdList);
        flowAddSignRecordService.recordList(task.getId(), newTaskIdList, AddSignTypeEnum.BEFORE);
        // 将原任务审批人更新 换成owner 这样就查不到了 但是任务还在
        // 2022-09-27 替换成结束任务 新起任务的写法
//        taskService.setOwner(task.getId(), req.getHandlerId());
//        taskService.setAssignee(task.getId(), null);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        flowBusinessStatusService.recordBusinessStatus(req.getTaskId(), BusinessDataEnum.TASK.getType(), TaskBusinessStatusEnum.BEFORE_ADD_SIGN.getStatus(), null);
        taskService.complete(task.getId());
        // 加签
        List<ProcessAuth> processAuthList = req.getAddSignUserIdList().stream().map(c -> ProcessAuth.builder()
                .processInstanceId(task.getProcessInstanceId())
                .sourceType(2)
                .taskActivityId(task.getTaskDefinitionKey())
                .userId(c)
                .build()).collect(Collectors.toList());
        processAuthMapper.insertOrUpdateList(processAuthList);

        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.XT.name());
    }

    /**
     * 一般是根据流程id 加签
     * 支持当前节点加签
     * 是否支持 后置节点加签
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void addSign(ExecutionAddSignReq req) {
        throw FlowException.build("暂未支持加签");
    }

    /**
     * 后加签 一般是任务审批时进行后加签
     * 之后的优化： 其他人拒绝后回到当前审批人 而不是结束流程
     * 审批通过并 后加签
     * 串行、按比例通过 不允许后加签
     * 一票通过 后加签会清空当前节点其他人的任务
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void afterAddSign(ExecutionAfterAddSignReq req) {
        TaskEntityImpl task = queryRunningTaskWithCheck(req.getTaskId());
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(task.getProcessInstanceId());
        Map<String, Object> varMap = new HashMap<>();
        if (req.getCustomVarMap() != null) {
            varMap.putAll(req.getCustomVarMap());
        }
        if (!req.getHandlerId().equals(task.getAssignee())) {
            if (Boolean.TRUE.equals(req.getCheckAssignee())) {
                throw FlowException.build("任务处理人与任务分配人不一致");
            }
            // 原任务审批人和处理人不同 把 任务审批人换成处理人
            taskService.setAssignee(task.getId(), req.getHandlerId());
        }
        checkButton(task.getId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), ApprovalButtonTypeEnum.COLLABORATE);

        managementService.executeCommand(new AddCommentCommand(task.getId(), task.getTaskDefinitionKey(), task.getProcessInstanceId(), task.getProcessDefinitionId(), CommentTypeEnum.HJQ,
                req.getMessage(), req.getHandlerId(), true));

        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (ParallelApprovalMethedEnum.ONE.getType().equals(userTaskExt.getParallelApprovalMethed())) {
            handleApprovalOne(task, true);
        }
        List<String> executionIdList = new ArrayList<>();
        // 加签
        for (String addSignUserId : req.getAddSignUserIdList()) {
            Execution existExecution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            Execution subExecution = runtimeService.addMultiInstanceExecution(task.getTaskDefinitionKey(), existExecution.getProcessInstanceId(),
                    new SingletonMap<>(ProcessNodeVariableEnum.ASSIGNEE.generateNodeVarName(task.getTaskDefinitionKey()), addSignUserId));
            executionIdList.add(subExecution.getId());
        }
        // 新任务列表
        List<String> newTaskIdList = taskMapper.queryIdListByExecutionIdList(executionIdList);
        flowAddSignRecordService.recordList(task.getId(), newTaskIdList, AddSignTypeEnum.AFTER);
        flowAddSignRecordService.passTask(task.getId());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            handleCc(task.getId(), task.getTaskDefinitionKey(), processInstance, req.getHandlerId(), req.getCcUserIdList());
        }
        // 加签
        List<ProcessAuth> processAuthList = req.getAddSignUserIdList().stream().map(c -> ProcessAuth.builder()
                .processInstanceId(task.getProcessInstanceId())
                .sourceType(2)
                .taskActivityId(task.getTaskDefinitionKey())
                .userId(c)
                .build()).collect(Collectors.toList());
        processAuthMapper.insertOrUpdateList(processAuthList);
        flowBusinessStatusService.recordBusinessStatus(req.getTaskId(), BusinessDataEnum.TASK.getType(), TaskBusinessStatusEnum.AFTER_ADD_SIGN.getStatus(), null);
        taskService.complete(task.getId(), varMap);

        processInstanceExtMapper.recordLastOperate(task.getProcessInstanceId(), req.getHandlerId(), CommentTypeEnum.HJQ.name());
    }

    /**
     * 流程挂起
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void suspendProcess(ExecutionProcessBaseReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        if (processInstance.isSuspended()) {
            throw FlowException.build("流程实例已挂起，无需再次挂起");
        }
        managementService.executeCommand(new AddCommentCommand(null, null, req.getProcessInstanceId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.LCGQ,
                req.getMessage(), req.getHandlerId(), true));
        flowBusinessStatusService.recordBusinessStatus(req.getProcessInstanceId(), BusinessDataEnum.PROCESS_INSTANCE.getType(), ProcessBusinessStatusEnum.SUSPEND.getType());
        runtimeService.suspendProcessInstanceById(req.getProcessInstanceId());
    }

    /**
     * 流程启用
     * @param req
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void activateProcess(ExecutionProcessBaseReq req) {
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        if (!processInstance.isSuspended()) {
            throw FlowException.build("流程实例未挂起，无需启用");
        }
        flowBusinessStatusService.recordBusinessStatus(req.getProcessInstanceId(), BusinessDataEnum.PROCESS_INSTANCE.getType(), ProcessBusinessStatusEnum.RUNNING.getType());
        runtimeService.activateProcessInstanceById(req.getProcessInstanceId());
        managementService.executeCommand(new AddCommentCommand(null, null, req.getProcessInstanceId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.LCQY,
                req.getMessage(), req.getHandlerId(), true));
    }

    /**
     * 催办
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void urging(ExecutionUrgingReq req) {
        Long taskCount = taskService.createTaskQuery()
                .active()
                .processInstanceId(req.getProcessInstanceId())
                .taskDefinitionKey(req.getTaskActivityId())
                .count();
        if (taskCount == 0) {
            throw FlowException.build("流程不存在或已挂起或该节点无任务，不能催办");
        }
        ProcessInstance processInstance = queryRunningProcessInstanceWithCheck(req.getProcessInstanceId());
        managementService.executeCommand(new AddCommentCommand(null, null, req.getProcessInstanceId(), processInstance.getProcessDefinitionId(), CommentTypeEnum.CB, req.getMessage(), req.getHandlerId(), true));
        flowNotifyService.sendNotifyProcess(req.getProcessInstanceId(), req.getHandlerId(), NotifyTypeEnum.URGING, req.getMessage(), req.getUrgingUserIdList());
    }

    /**
     * 抄送
     */
    @Transactional(rollbackFor = Exception.class)
    @ParamCheck
    public void cc(ExecutionProcessBaseReq req) {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(req.getProcessInstanceId())
                .singleResult();
        if (Objects.isNull(processInstance)) {
            throw FlowException.build("未找到流程实例");
        }
        if (CollectionUtils.isEmpty(req.getCcUserIdList())) {
            throw FlowException.build("被抄送人不能为空");
        }
        handleCc(null, null, processInstance.getId(), processInstance.getProcessDefinitionId(),
                processInstance.getName(), processInstance.getProcessDefinitionName(), processInstance.getBusinessKey(),
                req.getHandlerId(), req.getCcUserIdList());
    }

    /**
     * 寻找一个启用的任务 如果没有则会抛出异常
     * @param taskId
     * @return
     */
    private TaskEntityImpl queryRunningTaskWithCheck(String taskId) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().active().taskId(taskId).singleResult();
        if (task == null) {
            throw FlowException.build("任务不存在或流程被挂起");
        }
        return task;
    }

    /**
     * 寻找一个启用的流程 如果没有则会抛出异常
     * @param processInstanceId
     * @return
     */
    private ProcessInstance queryRunningProcessInstanceWithCheck(String processInstanceId) {
        return flowCacheService.queryRunningProcessInstanceWithCheck(processInstanceId);
    }

    /**
     * 抄送
     * 最好在节点跳转前执行
     * @param processInstance
     * @param senderId
     * @param ccUserIdList
     */
    public void handleCc(ProcessInstance processInstance, String senderId, List<String> ccUserIdList) {
        handleCc(null, null, processInstance.getId(), processInstance.getProcessDefinitionId(),
                processInstance.getName(), processInstance.getProcessDefinitionName(),
                processInstance.getBusinessKey(), senderId, ccUserIdList);
    }

    public void handleCc(String taskId, String taskActivityId, ProcessInstance processInstance, String senderId, List<String> ccUserIdList) {
        handleCc(taskId, taskActivityId, processInstance.getId(), processInstance.getProcessDefinitionId(),
                processInstance.getName(), processInstance.getProcessDefinitionName(),
                processInstance.getBusinessKey(), senderId, ccUserIdList);
    }

    private void handleCc(String taskId, String taskActivityId,
                          String processInstanceId, String processDefinitionId,
                          String processInstanceName, String modelName, String businessKey,
                          String senderId, List<String> ccUserIdList) {
        String senderName = Optional.ofNullable(userProvider.queryUser(senderId)).map(UserResp::getName).orElse("未知用户");
        String ccUserNames = ccUserIdList.stream().map(userProvider::queryUser).map(u -> Optional.ofNullable(u).map(UserResp::getName).orElse("未知用户")).collect(Collectors.joining("、"));
        managementService.executeCommand(new AddCommentCommand(taskId, taskActivityId, processInstanceId, processDefinitionId, CommentTypeEnum.CC, "抄送给" + ccUserNames, senderId));
        flowNotifyService.doSendNotify(processInstanceId, null, NotifyTypeEnum.CC.getType(), senderId, businessKey,
                MessageUtil.genCcMessage(processInstanceName, processInstanceId, modelName, senderName),
                ccUserIdList);
        List<ProcessAuth> processAuthList = ccUserIdList.stream().map(c -> ProcessAuth.builder()
                .processInstanceId(processInstanceId)
                .sourceType(3)
                .taskActivityId("PROCESS")
                .userId(c)
                .build()).collect(Collectors.toList());
        processAuthMapper.insertOrUpdateList(processAuthList);
    }

    /**
     * 或签节点处理
     * 或签节点只要有1人处理后，自动取消其他任务，并取消其他人权限
     * 操作触发节点跳转的就不需要减签了 系统自动减
     * 加签再加签的情况下 不能删除任务 父任务的assignee是空的 不要删
     */
    private void handleApprovalOne(TaskEntityImpl task, boolean needReduceSign) {
        List<Task> needDeleteTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(task.getTaskDefinitionKey()).list();
        needDeleteTaskList.removeIf(t -> t.getId().equals(task.getId()) || StringUtils.isBlank(t.getAssignee()));
        if (CollectionUtils.isNotEmpty(needDeleteTaskList)) {
            if (needReduceSign) {
                needDeleteTaskList.forEach(t -> {
                    // 减签
                    runtimeService.deleteMultiInstanceExecution(t.getExecutionId(), false);
                });
            }
            processAuthMapper.removeAuth(needDeleteTaskList.stream()
                    .map(Task::getAssignee)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()), task.getProcessInstanceId(), task.getTaskDefinitionKey());
        }
        runtimeService.setVariable(task.getProcessInstanceId(), ProcessNodeVariableEnum.ONE_ASSIGN_APPROVER.generateNodeVarName(task.getTaskDefinitionKey()), Arrays.asList(task.getAssignee()));
    }

    /**
     * 校验某个节点是否有某个按钮的权限
     */
    private void checkButton(String taskId, String processDefinitionId,
                             String taskActivityId, ApprovalButtonTypeEnum curButton) {
        // 判断是否协同任务 协同任务只能提交
        AddSignRecord addSignRecord = flowAddSignRecordService.findByTaskId(taskId);
        if (Objects.nonNull(addSignRecord)) {
            if (!ApprovalButtonTypeEnum.SUBMIT.equals(curButton)) {
                throw FlowException.build("协同任务只允许提交意见");
            }
            return;
        }
        UserTaskExt userTaskExt = flowModelService.findUserTaskExt(processDefinitionId, taskActivityId);
        if (CollectionUtils.isEmpty(userTaskExt.getButtonList())
                || !userTaskExt.getButtonList().contains(curButton.name())) {
            throw FlowException.build("当前节点不支持该按钮：" + curButton.getDisplay());
        }
    }

    private String ensureBackTargetActivityId(Task task) {
        List<NodeDefineResp> nodeDefineRespList = flowModelApiService.getNodeDefineListByProcId(task.getProcessInstanceId());
        Map<String, NodeDefineResp> nodeDefineRespMap = nodeDefineRespList.stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, e -> e));
        NodeDefineResp currentNode = nodeDefineRespMap.get(task.getTaskDefinitionKey());
        if (Objects.isNull(currentNode)) {
            throw FlowException.build("流程节点定义不存在");
        }
        // 判断是否处于并行网关内
        boolean isInParallelGateway = this.nodeIsInParallelGateway(currentNode, nodeDefineRespMap);
        if (!isInParallelGateway) {
            return task.getTaskDefinitionKey();
        }
        // 并行网关内需要找到最近的网关入口
        String gatewayId = findParallelGatewayEntrance(currentNode, nodeDefineRespMap);
        if (gatewayId != null) {
            return gatewayId;
        }
        throw FlowException.build("没有找到最近的并行网关入口节点，操作失败");
    }

    /**
     * 递归查找并行网关入口
     * @param currentNode 当前节点
     * @param nodeDefineRespMap 所有节点定义映射
     * @return 并行网关入口ID，找不到返回null
     */
    private String findParallelGatewayEntrance(NodeDefineResp currentNode, Map<String, NodeDefineResp> nodeDefineRespMap) {
        if (CollectionUtil.isEmpty(currentNode.getIncomingIds())) {
            return null;
        }
        for (String preDefKey : currentNode.getIncomingIds()) {
            NodeDefineResp preNode = nodeDefineRespMap.get(preDefKey);
            if (Objects.isNull(preNode)) {
                continue;
            }
            // 如果是并行网关，直接返回
            if (Objects.equals(preNode.getType(), NodeDefineEnum.PARALLEL_GATEWAY.getType())) {
                return preNode.getActivityId();
            }
            // 否则递归查找
            String gatewayId = findParallelGatewayEntrance(preNode, nodeDefineRespMap);
            if (gatewayId != null) {
                return gatewayId;
            }
        }
        return null;
    }

    private boolean nodeIsInParallelGateway(NodeDefineResp nodeDefineResp, Map<String, NodeDefineResp> map) {
        // 用于跟踪已访问的节点，避免循环引用
        Set<String> visited = new HashSet<>();
        boolean parallelGateWayIn = hasParallelGatewayIncoming(nodeDefineResp, map, visited);
        visited.clear();
        boolean parallelGateWayOut = hasParallelGatewayOutgoing(nodeDefineResp, map, visited);
        return parallelGateWayIn && parallelGateWayOut;
    }

    /**
     * 递归检查是否有并行网关入口
     * @param nodeDefineResp 当前节点
     * @param map 所有节点定义映射
     * @param visited 已访问节点集合
     * @return 是否有并行网关入口
     */
    private boolean hasParallelGatewayIncoming(NodeDefineResp nodeDefineResp, Map<String, NodeDefineResp> map, Set<String> visited) {
        // 避免循环引用
        if (visited.contains(nodeDefineResp.getActivityId())) {
            return false;
        }
        visited.add(nodeDefineResp.getActivityId());

        List<String> incomingIds = nodeDefineResp.getIncomingIds();
        if (CollectionUtil.isNotEmpty(incomingIds)) {
            for (String id : incomingIds) {
                NodeDefineResp ndr = map.get(id);
                if (Objects.nonNull(ndr)) {
                    // 如果直接前驱是并行网关，返回true
                    if (Objects.equals(ndr.getType(), NodeDefineEnum.PARALLEL_GATEWAY.getType())) {
                        return true;
                    }
                    // 如果遇到排他网关
                    if(Objects.equals(ndr.getType(), NodeDefineEnum.EXCLUSIVE_GATEWAY.getType())){
                        return false;
                    }
                    // 否则递归检查前驱节点
                    if (hasParallelGatewayIncoming(ndr, map, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 递归检查是否有并行网关出口
     * @param nodeDefineResp 当前节点
     * @param map 所有节点定义映射
     * @param visited 已访问节点集合
     * @return 是否有并行网关出口
     */
    private boolean hasParallelGatewayOutgoing(NodeDefineResp nodeDefineResp, Map<String, NodeDefineResp> map, Set<String> visited) {
        // 避免循环引用
        if (visited.contains(nodeDefineResp.getActivityId())) {
            return false;
        }
        visited.add(nodeDefineResp.getActivityId());

        List<String> outgoingIds = nodeDefineResp.getOutgoingIds();
        if (CollectionUtil.isNotEmpty(outgoingIds)) {
            for (String id : outgoingIds) {
                NodeDefineResp ndr = map.get(id);
                if (Objects.nonNull(ndr)) {
                    // 如果直接后继是并行网关，返回true
                    if (Objects.equals(ndr.getType(), NodeDefineEnum.PARALLEL_GATEWAY.getType())) {
                        return true;
                    }
                    // 如果遇到排他网关
                    if(Objects.equals(ndr.getType(), NodeDefineEnum.EXCLUSIVE_GATEWAY.getType())){
                        return false;
                    }
                    // 否则递归检查后继节点
                    if (hasParallelGatewayOutgoing(ndr, map, visited)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
