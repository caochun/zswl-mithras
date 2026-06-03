package cn.zswltech.mithras.service.service.flow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.*;
import cn.zswltech.flow.core.domain.entity.AddSignRecord;
import cn.zswltech.flow.core.domain.req.NodeBackRecordReq;
import cn.zswltech.flow.core.domain.req.task.CcProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.*;
import cn.zswltech.flow.core.enums.*;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.flow.core.service.impl.FlowAddSignRecordService;
import cn.zswltech.flow.core.util.FlowableUtils;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.flow.search.*;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.convert.flow.FlowProcessConvert;
import cn.zswltech.mithras.service.convert.flow.FlowTaskConvert;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.CapitalSource;
import cn.zswltech.mithras.service.flow.ccuser.FlowNodeCcConfigService;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandlerFactory;
import cn.zswltech.mithras.service.flow.dynamicform.afterlease.AfterLeaseCheckPlanHandler;
import cn.zswltech.mithras.service.flow.dynamicform.afterlease.AssetManagerReviewHandler;
import cn.zswltech.mithras.service.flow.dynamicform.projreview.LawManagerReviewHandler;
import cn.zswltech.mithras.service.mapper.SystemConfigMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.SystemConfig;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.workflow.application.ProcAttentionRecordService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.JobEnum.yinZhangGuanLi;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;

/**
 * 任务
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:56 PM
 */
@Service
public class MyTaskService {
    // 按照时间切分新老流程 2025-02-13 19:58:19.707
    // 用时间作区分是因为动态表单硬编码在了代码逻辑中（因为老流程没有配动态表单，但是业务方希望老流程也能展示），按照流程模型适配业务的情况找到了一个切分点
    private static final LocalDateTime KEY_DATE = LocalDateTime.of(2025, 2, 13, 19, 58, 20);

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private FlowTaskConvert flowTaskConvert;
    @Resource
    private FlowModelApiService modelApiService;
    @Resource
    private FlowAddSignRecordService addSignRecordService;
    @Resource
    private FlowNotifyApiService notifyApiService;
    @Resource
    private DynamicFormHandlerFactory dynamicFormHandlerFactory;
    @Resource
    private TaskService taskService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowVariableApiService variableApiService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ProcAttentionRecordService procAttentionRecordService;
    @Resource
    private SystemConfigMapper systemConfigMapper;
    @Resource
    private FlowAddSignRecordService signRecordService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Resource
    FlowNodeCcConfigService flowNodeCcConfigService;
    @Resource
    private FlowUserApiService userApiService;

    public PageR<ProcessListRSP> myProcessApplyingList(ProcessListREQ req) {
        ProcessPageReq flowReq = flowProcessConvert.req2FlowReq(req);
        flowReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        //flowReq.setNotEqualsCurActivityId(FlowConstants.START_USER_TASK);
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        flowReq.setSortType(1);
        flowReq.setNoWithDraw(1);
        flowReq.setNoBackToStep(1);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (CollectionUtils.isEmpty(flowRespPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
        }
        List<ProcessListRSP> rspList = flowRespPage.getContents().stream().map(flowProcessConvert::flowResp2RSP).collect(Collectors.toList());
        flowProcessConvert.processListRSPFillName(rspList);
        return PageR.of(rspList, flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
    }

    public PageR<TaskListRSP> myProcessWithdrawList(TaskListREQ req) {
        TaskSystemPageReq flowReq = flowTaskConvert.req2FlowReq(req);
        flowReq.setIsRunning(1);
        flowReq.setStartUserBackFlag(1);
        flowReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setSortType(1);
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
        }
        List<TaskListRSP> rspList = flowTaskPage.getContents().stream().map(flowTaskConvert::flowResp2RSP).collect(Collectors.toList());
        flowTaskConvert.taskListRSPFillName(rspList);
        return PageR.of(rspList, flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
    }

    public PageR<BackToStepTaskListRSP> myProcessBackToStepList(TaskListREQ req) {
        TaskSystemPageReq flowReq = flowTaskConvert.req2FlowReq(req);
        flowReq.setIsRunning(1);
        flowReq.setStartUserBackFlag(2);
        flowReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setSortType(1);
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
        }
        List<BackToStepTaskListRSP> rspList = flowTaskPage.getContents().stream().map(flowTaskConvert::flowResp2BackStepRSP).collect(Collectors.toList());

        // 找驳回人
        List<String> processInstanceIdList = rspList.stream().map(BackToStepTaskListRSP::getProcessInstanceId).collect(Collectors.toList());
        NodeBackRecordReq nodeBackRecordReq = new NodeBackRecordReq();
        nodeBackRecordReq.setPageSize(Integer.MAX_VALUE);
        nodeBackRecordReq.setSinkTaskActivityId(FlowConstants.START_USER_TASK);
        nodeBackRecordReq.setStatus(1);
        nodeBackRecordReq.setType(2);
        nodeBackRecordReq.setProcessInstanceIdList(processInstanceIdList);
        Map<String, NodeBackRecordResp> nodeBackRecordRespMap = taskApiService.queryNodeBackRecord(nodeBackRecordReq).getContents()
                .stream().collect(Collectors.toMap(NodeBackRecordResp::getProcessInstanceId, n -> n, (k1, k2) -> k1));

        rspList.forEach(r -> {
            NodeBackRecordResp nodeBackRecordResp = nodeBackRecordRespMap.get(r.getProcessInstanceId());
            if (Objects.isNull(nodeBackRecordResp)) {
                return;
            }
            r.setBackActivityId(nodeBackRecordResp.getSourceTaskActivityId());
            r.setBackNodeName(nodeBackRecordResp.getSourceNodeName());
            r.setBackUserId(Optional.ofNullable(nodeBackRecordResp.getHandlerId()).map(Long::valueOf).orElse(null));
        });

        flowTaskConvert.baskStepRSPFillName(rspList);
        return PageR.of(rspList, flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
    }

    public PageR<ProcessListRSP> myProcessFinishList(ProcessListREQ req) {
        ProcessPageReq flowReq = flowProcessConvert.req2FlowReq(req);
        flowReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.REJECT.getType(),
                ProcessBusinessStatusEnum.CANCEL.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.REJECT_ALL.getType()));
        flowReq.setSortType(1);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (CollectionUtils.isEmpty(flowRespPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
        }
        List<ProcessListRSP> rspList = flowRespPage.getContents().stream().map(flowProcessConvert::flowResp2RSP).collect(Collectors.toList());
        flowProcessConvert.processListRSPFillName(rspList);
        return PageR.of(rspList, flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
    }

    public PageR<ReceiveTaskListRSP> myReceiveTodoList(TaskListREQ req) {
        TaskSystemPageReq flowReq = flowTaskConvert.req2FlowReq(req);
        flowReq.setIsRunning(1);
        flowReq.setAssignee(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setNotEqualsActivityId(FlowConstants.START_USER_TASK);
        flowReq.setSortType(1);
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
        }
        // 填充流程数据
        List<String> processInstanceIdList = flowTaskPage.getContents().stream().map(TaskResp::getProcessInstanceId).distinct().collect(Collectors.toList());
        Map<String, ProcessResp> processRespMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            ProcessPageReq processFlowReq = new ProcessPageReq();
            processFlowReq.setProcessInstanceIdList(processInstanceIdList);
            processFlowReq.setPageSize(Integer.MAX_VALUE);
            processRespMap.putAll(taskApiService.queryProcess(processFlowReq).getContents().stream().collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
        }
        List<ReceiveTaskListRSP> rspList = flowTaskPage.getContents().stream().map(resp -> flowTaskConvert.flowResp2ReceiveRSP(resp, processRespMap.get(resp.getProcessInstanceId()))).collect(Collectors.toList());
        // 超过12小时判为超时
        rspList.forEach(rsp -> rsp.setOvertimeFlag(LocalDateTimeUtil.between(rsp.getTaskCreateTime(), LocalDateTime.now(), ChronoUnit.HOURS) > 11 ? 1 : 0));
        flowTaskConvert.receiveTaskListRSPFillName(rspList);
        return PageR.of(rspList, flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
    }

    public PageR<ReceiveTaskListRSP> myReceiveDoneList(TaskListREQ req) {
        TaskSystemPageReq flowReq = flowTaskConvert.req2FlowReq(req);
        flowReq.setIsDone(1);
        flowReq.setAssignee(String.valueOf(AccountUtil.getLoginInfo().getId()));
        flowReq.setSortType(3);
        Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
        }
        // 填充流程数据
        List<String> processInstanceIdList = flowTaskPage.getContents().stream()
                .filter(resp -> ProcessBusinessStatusEnum.RUNNING.getType().equals(resp.getProcessStatus()))
                .map(TaskResp::getProcessInstanceId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, ProcessResp> processRespMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            ProcessPageReq processFlowReq = new ProcessPageReq();
            processFlowReq.setProcessInstanceIdList(processInstanceIdList);
            processFlowReq.setPageSize(Integer.MAX_VALUE);
            processRespMap.putAll(taskApiService.queryProcess(processFlowReq).getContents().stream().collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
        }
        List<ReceiveTaskListRSP> rspList = flowTaskPage.getContents().stream().map(resp -> flowTaskConvert.flowResp2ReceiveRSP(resp, processRespMap.getOrDefault(resp.getProcessInstanceId(), new ProcessResp()))).collect(Collectors.toList());
        // 已办任务不计算超时逻辑
        rspList.forEach(rsp -> rsp.setOvertimeFlag(0));
        flowTaskConvert.receiveTaskListRSPFillName(rspList);
        return PageR.of(rspList, flowTaskPage.getTotal(), flowTaskPage.getPages(), flowTaskPage.getCurPage(), flowTaskPage.getPageSize());
    }

    public PageR<ProcessCcListRSP> myReceiveCcList(ProcessCcListREQ req) {
        CcProcessPageReq ccFlowReq = flowProcessConvert.req2CcFlowReq(req);
        ccFlowReq.setReceiverId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        ccFlowReq.setSortType(1);
        Page<CcProcessResp> ccProcessPage = taskApiService.queryCcProcess(ccFlowReq);
        if (CollectionUtils.isEmpty(ccProcessPage.getContents())) {
            return PageR.of(new ArrayList<>(), ccProcessPage.getTotal(), ccProcessPage.getPages(), ccProcessPage.getCurPage(), ccProcessPage.getPageSize());
        }
        List<ProcessCcListRSP> rspList = ccProcessPage.getContents().stream().map(flowProcessConvert::flowCcResp2RSP).collect(Collectors.toList());
        flowProcessConvert.ccProcessRSPFillName(rspList);
        return PageR.of(rspList, ccProcessPage.getTotal(), ccProcessPage.getPages(), ccProcessPage.getCurPage(), ccProcessPage.getPageSize());
    }

    @Resource
    private SystemConfigService systemConfigService;

    public PageR<ProcessListRSP> searchList(ProcessListREQ req) {
        ProcessPageReq flowReq = flowProcessConvert.req2FlowReq(req);
        flowReq.setCurLoginUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        if (controlProcessViewAuth(flowReq)) {
            // 控制权限
            flowReq.setAuthFlag(1);
        }
        flowReq.setSortType(1);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (CollectionUtils.isEmpty(flowRespPage.getContents())) {
            return PageR.of(new ArrayList<>(), flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
        }
        List<ProcessListRSP> rspList = flowRespPage.getContents().stream().map(flowProcessConvert::flowResp2RSP).collect(Collectors.toList());
        flowProcessConvert.processListRSPFillName(rspList);
        return PageR.of(rspList, flowRespPage.getTotal(), flowRespPage.getPages(), flowRespPage.getCurPage(), flowRespPage.getPageSize());
    }

    public TaskDetailRSP taskDetail(TaskBaseREQ req) {
        TaskResp flowResp = taskApiService.querySystemTaskById(req.getTaskId());

        if (Objects.isNull(flowResp)) {
            throw new MithrasException("任务记录不存在");
        }
        // 权限校验 非任务审批人 不能看详情
        if (!String.valueOf(AccountUtil.getLoginInfo().getId()).equals(flowResp.getAssignee())) {
            throw new MithrasException("非任务审批人无权查看详情");
        }
        TaskDetailRSP rsp = flowTaskConvert.flowResp2Detail(flowResp);
        flowTaskConvert.taskListRSPFillName(Collections.singletonList(rsp));

        UserTaskExt userTaskExt = modelApiService.findUserTaskExtByProcessDefinitionId(flowResp.getProcessDefineId(), flowResp.getTaskActivityId());
        boolean collaborateFlag = Objects.nonNull(addSignRecordService.findByTaskId(rsp.getTaskId()));
        // 判断表单 按钮
        rsp.setOperateTabShowFlag(true);
        rsp.setCcTabShowFlag(true);
        rsp.setCcTabReadOnlyFlag(Boolean.FALSE);
        //在途流程查看设置
        if (String.valueOf(ProcessBusinessStatusEnum.RUNNING.getType()).equals(rsp.getProcessStatus())) {
            //普通流程阶段通过配置是否默认赋值
            List<Long> ccUesrList = flowNodeCcConfigService.getCcUesrList(flowResp.getProcessInstanceId(),rsp.getModelKey(), rsp.getTaskActivityId(),rsp.getBusinessKey());
            if(CollectionUtils.isNotEmpty(ccUesrList)){
                rsp.setCcTabReadOnlyFlag(Boolean.TRUE);
                rsp.setCcUerList(ccUesrList);
            }
        }
        // 是否显示逐级审批下拉框 2023-02-09 由于项目评审网关问题，逐级审批下拉框改为每个节点都可下拉
        Map<String, NodeDefineResp> nodeDefineMap = modelApiService.getNodeDefineListByProcessDefinitionId(rsp.getProcessDefineId())
                .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
        Set<String> nextUserTaskIdSet = FlowableUtils.collectNextUserTask(nodeDefineMap, FlowConstants.START_USER_TASK);
//        rsp.setMultiBackOptionFlag(!nextUserTaskIdSet.contains(rsp.getTaskActivityId()));
        rsp.setMultiBackOptionFlag(true);

        // 如果 是协同任务 或者 任务非进行中 是没有表单的
        rsp.setDynamicFormKeyList(collaborateFlag || !String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus())
                ? new ArrayList<>() : Optional.ofNullable(userTaskExt.getDynamicFormList()).orElse(new ArrayList<>()));
        rsp.setDynamicFormData(new HashMap<>());
        // 该节点表单信息不为空 填充
        if (CollectionUtils.isNotEmpty(rsp.getDynamicFormKeyList())) {
            rsp.getDynamicFormKeyList().forEach(e -> {
                DynamicFormHandler handler = dynamicFormHandlerFactory.getHandler(e);
                if (Objects.isNull(handler)) {
                    return;
                }
                handler.collect(rsp);
            });
        } else {
            if (CharSequenceUtil.equalsAny(rsp.getModelKey(), NewAfterLeaseCheckReportCommonlyFlow.name(), NewAfterLeaseCheckReportFlow.name()) && Objects.nonNull(rsp.getProcessStartTime()) && rsp.getProcessStartTime().isAfter(KEY_DATE)) {
                rsp.getDynamicFormKeyList().add(FlowDynamicFormEnum.follow_up_rental_inspection_form.name());
                NewAfterLeaseCheckPlanClient planClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(rsp.getBusinessKey()));
                if (Objects.nonNull(planClient)) {
                    AfterLeaseCheckPlanHandler.FormData formData = new AfterLeaseCheckPlanHandler.FormData();
                    formData.setNextCheckWay(planClient.getTmpNextCheckWay());
                    formData.setNextDeadline(planClient.getTmpNextDeadline());
                    rsp.getDynamicFormData().put(FlowDynamicFormEnum.follow_up_rental_inspection_form.name(), formData);
                }
                //租后检查资产管理岗协同时展示资产管理复核表单
                if("userTask_assetManager".equals(flowResp.getTaskActivityId())){
                    rsp.getDynamicFormKeyList().add(FlowDynamicFormEnum.afterLeaseCheckReport_assetManager.name());
                    //反显已选资产管理复核岗的用户
//                    AssetManagerReviewHandler.FormData formData = new AssetManagerReviewHandler.FormData();
//                    // 读资产管理复核岗的用户 有值显值 没值返回默认的
//                    List<String> userIdList = userApiService.getTaskApprover(rsp.getProcessInstanceId(), "assetManagementReview");
//                    if(userIdList.size()>0){
//                        formData.setUserId(userIdList.get(0));
//                    }
//                    rsp.getDynamicFormData().put(FlowDynamicFormEnum.afterLeaseCheckReport_assetManager.name(), formData);
                }
            }
            //项目评审创建、项目评审变更流程法务经理审批协同时展示法务经理复核表单
            if (CharSequenceUtil.equalsAny(flowResp.getTaskActivityId(),"userTask_lawManager","userTask_lawManager_back")&&CharSequenceUtil.equalsAny(rsp.getModelKey(), ProjReviewCreateFlow.name(), ProjReviewModifyFlow.name()) && Objects.nonNull(rsp.getProcessStartTime()) && rsp.getProcessStartTime().isAfter(KEY_DATE)) {
                rsp.getDynamicFormKeyList().add(FlowDynamicFormEnum.projReview_lawManagerReview.name());
                //反显已选法务经理复核岗的用户
//                LawManagerReviewHandler.FormData formData = new LawManagerReviewHandler.FormData();
//                // 读法务经理复核岗的用户 有值显值 没值返回默认的
//                List<String> userIdList = userApiService.getTaskApprover(rsp.getProcessInstanceId(), "userTask_lawManager_review");
//                if(userIdList.size()>0){
//                    formData.setUserId(userIdList.get(0));
//                }
//                rsp.getDynamicFormData().put(FlowDynamicFormEnum.projReview_lawManagerReview.name(), formData);
            }

            if (CharSequenceUtil.equalsAny(flowResp.getTaskActivityId(),
                    "userTask_riskManager","userTask_riskManager_back",
                    "userTask_lawManager","userTask_lawManager_back",
                    "userTask_lawManager_review","userTask_lawManager_back_review"
            )&&CharSequenceUtil.equalsAny(rsp.getModelKey(), ProjReviewCreateFlow.name(), ProjReviewModifyFlow.name()) && Objects.nonNull(rsp.getProcessStartTime()) && rsp.getProcessStartTime().isAfter(KEY_DATE)) {
                rsp.getDynamicFormKeyList().add(FlowDynamicFormEnum.projReview_setCreditWithdrawal.name());
            }
        }
        // 当前任务为进行中 且 当前任务节点id为发起人 且 当前登陆用户等于流程发起人 可编辑
        rsp.setCanEditFlag(String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus())
                && FlowConstants.START_USER_TASK.equals(rsp.getTaskActivityId())
                && AccountUtil.getLoginInfo().getId().equals(rsp.getStartUserId()));
        if (CollectionUtils.isEmpty(userTaskExt.getCanBackActivityIdList())) {
            rsp.setCanBackNodeList(new ArrayList<>());
        } else {
            // 尝试替换 虽然写法有点恶心  但能比较快速解决问题
            Map<String, Object> paramMap = variableApiService.getVariables(flowResp.getProcessInstanceId(), Collections.singletonList(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK));
            if (CollectionUtil.isNotEmpty(paramMap)) {
                Object obj = paramMap.get(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK);
                if (Objects.equals(Boolean.FALSE, obj)) {
                    List<String> newList = new ArrayList<>(userTaskExt.getCanBackActivityIdList().size());
                    for (String s : userTaskExt.getCanBackActivityIdList()) {
                        if (Objects.equals(s, FlowConstants.PARALLEL_RISK_MANAGER_BACK)) {
                            newList.add(FlowConstants.PARALLEL_RISK_MANAGER);
                        } else if (Objects.equals(s, FlowConstants.PARALLEL_LAW_MANAGER_BACK)) {
                            newList.add(FlowConstants.PARALLEL_LAW_MANAGER);
                        } else {
                            newList.add(s);
                        }
                    }
                    userTaskExt.setCanBackActivityIdList(newList);
                }
            }
            rsp.setCanBackNodeList(userTaskExt.getCanBackActivityIdList().stream()
                    .filter(nodeDefineMap::containsKey)
                    .map(a -> ProcessNodeRSP.builder()
                            .activityId(a)
                            .name(nodeDefineMap.get(a).getName())
                            .build())
                    .collect(Collectors.toList()));
            // 写死处理下 如果模型是项目评审模型 退回节点 发起人节点名字要改成项目经理 感觉很烂但是有需求
            if (ProcessModelTypeEnum.ProjReviewCreateFlow.name().equals(rsp.getModelKey())
                    || ProcessModelTypeEnum.ProjReviewModifyFlow.name().equals(rsp.getModelKey())) {
                rsp.getCanBackNodeList().forEach(c -> c.setName(FlowConstants.START_USER_TASK.equals(c.getActivityId()) ? "项目经理" : c.getName()));
            }
        }

        // 如果该任务是协同任务 就只有提交按钮
        // 按钮是否可点击 一般任务进行中时操作按钮（除撤回）皆可点击 任务不是进行中（判断撤回是否可点击）
        if (collaborateFlag) {
            rsp.setDynamicButtonList(Arrays.asList(TaskDetailRSP.DynamicButton.builder()
                    .buttonKey(ApprovalButtonTypeEnum.SUBMIT.name())
                    .buttonName(ApprovalButtonTypeEnum.SUBMIT.getDisplay())
                    // taskStatus == 1 任务运行中
                    .clickFlag(String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus()))
                    .build()));
        } else {
            rsp.setDynamicButtonList(CollectionUtils.isEmpty(userTaskExt.getButtonList()) ? new ArrayList<>()
                    : userTaskExt.getButtonList().stream().map(ApprovalButtonTypeEnum::getByName).filter(Objects::nonNull).map(b ->
                    {
                        //有个流程要特殊处理
                        TaskDetailRSP.DynamicButton build = TaskDetailRSP.DynamicButton.builder()
                                .buttonKey(b.name())
                                .buttonName(b.getDisplay())
                                .clickFlag(judgeButtonCanClick(b, rsp))
                                .sort(Optional.of(ApprovalButtonTypeEnum.valueOf(b.name())).map(ApprovalButtonTypeEnum::getSort).orElse(100))
                                .build();

                        NodeDefineResp userTaskComprehensiveDept = nodeDefineMap.get("userTask_comprehensiveDept");
                        if (ObjectUtil.isNotNull(userTaskComprehensiveDept) && flowResp.getModelKey().equals(RentPaymentNotifyFlow.name())
                                && "userTask_comprehensiveDept".equals(flowResp.getTaskActivityId())) {
                            build.setButtonName("已盖章");
                        }
                        return build;
                    }
            ).collect(Collectors.toList()));
        }

        // 流程结束 补充业务数据版本
        if (!String.valueOf(ProcessBusinessStatusEnum.RUNNING.getType()).equals(rsp.getProcessStatus())) {
            CommonVersion processBusinessVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getProcessInstanceId, rsp.getProcessInstanceId())
                    .last("LIMIT 1")
            );
            rsp.setBusinessVersion(Optional.ofNullable(processBusinessVersion).map(CommonVersion::getVersion).orElse(null));
        }

        // 设置是否关注了流程
        rsp.setAttentionFlag(Optional.ofNullable(flowResp.getAssignee()).map(u -> procAttentionRecordService.findAttentionType(flowResp.getProcessInstanceId(), Long.valueOf(flowResp.getAssignee()))).orElse(0));

        // 前端展示UI版本
        String modelKey = flowResp.getModelKey();
        rsp.setUiVersion(this.ensureUIVersion(modelKey, flowResp.getTaskActivityId()));

        // 已读抄送
        notifyApiService.readCcMessage(String.valueOf(AccountUtil.getLoginInfo().getId()), rsp.getProcessInstanceId());
        //这里排序
        if (ObjectUtil.isNotEmpty(rsp.getDynamicButtonList())) {
            rsp.setDynamicButtonList(rsp.getDynamicButtonList().stream().sorted(Comparator.comparingInt(TaskDetailRSP.DynamicButton::getSort)).collect(Collectors.toList()));
        }
        AddSignRecord addSignRecord = signRecordService.findByTaskId(req.getTaskId());
        //目前只支持立项、评审、合同
        String moduleName = ProcessModelTypeEnum.getByName(modelKey).getBusinessModuleName();
        rsp.setCollaborateFlag(Boolean.FALSE);
        if (CharSequenceUtil.isNotBlank(moduleName) && Arrays.asList("PROJ_ESTABLISH", "PROJ_REVIEW", "CONTRACT", "CLIENT_TRANSFER").contains(moduleName)
                && !Objects.equals(modelKey, ProjReviewPricingApprovalFlow.name()) && !Objects.equals(modelKey, ProjReviewPricingModifyApprovalFlow.name())) {
            rsp.setCollaborateFlag(ObjectUtil.isNotNull(addSignRecord));
        }
        //付款实际核销确认 流程，在出纳节点，付款明细中  我方账户名为“浙江浙商融资租赁有限公司”且资金来源为“银行贷款”时，弹框提示：银行贷款资金复核前请通知银行
        if("PaymentActualDetailFlow".equals(rsp.getModelKey())&&"出纳".equals(rsp.getTaskName())){
            //取合同编号
            String contractCode = rsp.getContractCode();
            //根据合同编号取合同id
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getContractCode,contractCode));
            if(!contractBaseInfoList.isEmpty()){
                Long contractId = contractBaseInfoList.get(0).getId();
                //查询付款记录信息
                List<PaymentActualDetailUnconfirmed> list = paymentActualDetailUnconfirmedService.list(Wrappers.<PaymentActualDetailUnconfirmed>lambdaQuery()
                        .eq(PaymentActualDetailUnconfirmed::getContractId,contractId)
                        .eq(PaymentActualDetailUnconfirmed::getCapitalSource, CapitalSource.BANK_LOAN)
                        .eq(PaymentActualDetailUnconfirmed::getOurAccountNumber,"8110801013202242052"));
                if(!list.isEmpty()){
                    rsp.setReturnMsg("银行贷款资金复核前请通知银行");
                }
            }
        }
        return rsp;
    }

    public ProcessDetailRSP processDetail(ProcessBaseREQ req) {
        ProcessResp flowResp = taskApiService.queryProcessById(req.getProcessInstanceId());
        if (Objects.isNull(flowResp)) {
            throw new MithrasException("流程记录不存在");
        }
        // 权限校验 流程未授权 不能看详情
        if (controlProcessViewAuth() &&
                !processApiService.haveProcessAuth(req.getProcessInstanceId(), String.valueOf(AccountUtil.getLoginInfo().getId()))) {
            // 非管理员且没有流程查看权限
            throw new MithrasException("无权查看流程详情");
        }

        ProcessDetailRSP rsp = flowProcessConvert.flowResp2Detail(flowResp);
        UserTaskExt userTaskExt = modelApiService.findUserTaskExtByProcessInstanceId(flowResp.getProcessInstanceId(), flowResp.getCurTaskActivityIds());
        flowProcessConvert.processListRSPFillName(Collections.singletonList(rsp));

        // 填充表单等数据 发起人看自己流程 才有操作栏
        rsp.setDynamicFormKeyList(new ArrayList<>());
        rsp.setDynamicFormData(new HashMap<>());
        if (CharSequenceUtil.equalsAny(rsp.getModelKey(), NewAfterLeaseCheckReportCommonlyFlow.name(), NewAfterLeaseCheckReportFlow.name()) && Objects.nonNull(rsp.getStartTime()) && rsp.getStartTime().isAfter(KEY_DATE)) {
            rsp.getDynamicFormKeyList().add(FlowDynamicFormEnum.follow_up_rental_inspection_form.name());
            Map<String, Object> dynamicFormData = rsp.getDynamicFormData();
            if (CollUtil.isEmpty(dynamicFormData)) {
                dynamicFormData = new HashMap<>();
            }
            NewAfterLeaseCheckPlanClient planClient = afterLeaseCheckPlanClientService.getById(Long.valueOf(rsp.getBusinessKey()));
            if (Objects.nonNull(planClient)) {
                AfterLeaseCheckPlanHandler.FormData formData = new AfterLeaseCheckPlanHandler.FormData();
                formData.setNextCheckWay(planClient.getTmpNextCheckWay());
                formData.setNextDeadline(planClient.getTmpNextDeadline());
                dynamicFormData.put(FlowDynamicFormEnum.follow_up_rental_inspection_form.name(), formData);
                rsp.setDynamicFormData(dynamicFormData);
            }
        }
        if (AccountUtil.getLoginInfo().getId().equals(rsp.getStartUserId())) {
            String curActivitId = rsp.getCurTaskActivityIds();
            rsp.setOperateTabShowFlag(true);
            rsp.setCcTabShowFlag(true);
            rsp.setSnapshotButtonShowFlag(true);
            rsp.setCanEditFlag(FlowConstants.START_USER_TASK.equals(curActivitId));
            // 判断当前节点在哪 取消按钮只要流程没结束就可以点击 撤回发起人按钮 只要流程没结束且不在发起人节点就可以点击
            ProcessDetailRSP.DynamicButton cancelButton = ProcessDetailRSP.DynamicButton.builder()
                    .buttonKey(ApprovalButtonTypeEnum.CANCEL.name())
                    .buttonName(ApprovalButtonTypeEnum.CANCEL.getDisplay())
                    .clickFlag(Objects.isNull(rsp.getEndTime()))
                    .build();
            ProcessDetailRSP.DynamicButton withdrawStartUserButton = ProcessDetailRSP.DynamicButton.builder()
                    .buttonKey(ApprovalButtonTypeEnum.WITHDRAW_START_USER.name())
                    .buttonName(ApprovalButtonTypeEnum.WITHDRAW_START_USER.getDisplay())
                    .clickFlag(Objects.isNull(rsp.getEndTime()) && !FlowConstants.START_USER_TASK.equals(curActivitId))
                    .build();
            ProcessDetailRSP.DynamicButton submitStartUserButton = null;
            if (userTaskExt != null && !CollectionUtils.isEmpty(userTaskExt.getButtonList())) {
                for (String button : userTaskExt.getButtonList()) {
                    ApprovalButtonTypeEnum submitButtonEnum = ApprovalButtonTypeEnum.getByName(button);
                    if (ApprovalButtonTypeEnum.SUBMIT.name().equalsIgnoreCase(submitButtonEnum.name())) {
                        if (String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getProcessStatus())
                                && Objects.equals(userTaskExt.getActivityId(), FlowConstants.START_USER_TASK)) {
                            submitStartUserButton = ProcessDetailRSP.DynamicButton.builder()
                                    .buttonKey(ApprovalButtonTypeEnum.SUBMIT.name())
                                    .buttonName(ApprovalButtonTypeEnum.SUBMIT.getDisplay())
                                    .clickFlag(Objects.isNull(rsp.getEndTime()))
                                    .build();
                            break;
                        }
                    }
                }
            }
            //租赁物相关审核不允许撤回，这里特殊处理
            if (StrUtil.equalsAny(flowResp.getModelKey(), ProcessModelTypeEnum.LeaseCreateFlow.name(), ProcessModelTypeEnum.LeaseModifyFlow.name())) {
                rsp.setDynamicButtonList(Collections.singletonList(cancelButton));
            } else {
                List<ProcessDetailRSP.DynamicButton> list = new ArrayList<>();
                list.add(withdrawStartUserButton);
                list.add(cancelButton);
                rsp.setDynamicButtonList(list);
                if (submitStartUserButton != null) {
                    rsp.getDynamicButtonList().add(submitStartUserButton);
                }
            }
            /*if (ProcessModelTypeEnum.PaymentCreateFlow.name().equalsIgnoreCase(rsp.getModelKey())) {
                contractBaseInfoService.copyLendingMaterial(Long.valueOf(rsp.getBusinessKey()));
            }*/
        } else {
            rsp.setOperateTabShowFlag(false);
            rsp.setCcTabShowFlag(false);
            rsp.setSnapshotButtonShowFlag(false);
            rsp.setDynamicButtonList(new ArrayList<>());
        }

        // 流程结束 补充业务数据版本
        if (!String.valueOf(ProcessBusinessStatusEnum.RUNNING.getType()).equals(rsp.getProcessStatus())) {
            CommonVersion processBusinessVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getProcessInstanceId, rsp.getProcessInstanceId())
                    .last("LIMIT 1")
            );
            rsp.setBusinessVersion(Optional.ofNullable(processBusinessVersion).map(CommonVersion::getVersion).orElse(null));
        }

        // 设置是否关注了流程
        rsp.setAttentionFlag(procAttentionRecordService.findAttentionType(flowResp.getProcessInstanceId(), AccountUtil.getLoginInfo().getId()));

        // 前端展示UI版本
        if (StrUtil.isNotBlank(flowResp.getCurTaskActivityIds())) {
            String[] array = flowResp.getCurTaskActivityIds().split(",");
            rsp.setUiVersion(this.ensureUIVersion(flowResp.getModelKey(), array[0]));
        }

        // 已读抄送
        notifyApiService.readCcMessage(String.valueOf(AccountUtil.getLoginInfo().getId()), rsp.getProcessInstanceId());
        return rsp;
    }

    private boolean judgeButtonCanClick(ApprovalButtonTypeEnum buttonTypeEnum, TaskListRSP rsp) {
        switch (buttonTypeEnum) {
            case SUBMIT:
                // 如果流程参数中存在评审会秘书在汇票阶段选择了不同意，则发起人提交按钮不可使用
                if (String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus())
                        && Objects.equals(rsp.getTaskActivityId(), FlowConstants.START_USER_TASK)) {
                    Map<String, Object> map = variableApiService.getVariables(rsp.getProcessInstanceId(), Collections.singletonList(FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE));
                    return !Objects.equals(map.get(FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE), FlowConstants.PROJ_REVIEW_SECRETARY_CHOICE_DISAGREE);
                }
                //项目评审流程-评审会秘书第一次不可编辑
                /*if (CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name()) && Objects.equals(rsp.getTaskActivityId(), FlowConstants.JURY_SECRETARY_COLLECT)) {
                    //判断是否
                    ProcessHistoryREQ historyREQ = new ProcessHistoryREQ();
                    historyREQ.setProcessInstanceId(rsp.getProcessInstanceId());
                    PageR<ProcessHistoryRSP> history = processService.history(historyREQ);
                    if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getList())) {
                        //
                        List<String> collect = history.getList().stream().map(ProcessHistoryRSP::getTaskActivityId).filter(e -> ObjectUtil.equals(e, FlowConstants.JURY_SECRETARY_COLLECT)).collect(Collectors.toList());
                        if (ObjectUtil.isNotEmpty(collect) && collect.size() > 0) {
                            //未经过，不可编辑
                            return String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus());
                        } else {
                            return false;
                        }
                    } else {
                        //为空不可使用
                        return false;
                    }
                }*/
            case ZL_PR_AGREE:
            case ZL_PR_CONDITION_AGREE:
            case ZL_PR_MEETING_SECRETARY_DISAGREE:
                //项目评审流程-评审会秘书第一次不可编辑
                /*if (CharSequenceUtil.equalsAny(rsp.getModelKey(), ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name()) && Objects.equals(rsp.getTaskActivityId(), FlowConstants.JURY_SECRETARY_COLLECT)) {
                    //判断是否
                    ProcessHistoryREQ historyREQ = new ProcessHistoryREQ();
                    historyREQ.setProcessInstanceId(rsp.getProcessInstanceId());
                    PageR<ProcessHistoryRSP> history = processService.history(historyREQ);
                    if (ObjectUtil.isNotEmpty(history) && ObjectUtil.isNotEmpty(history.getList())) {
                        //
                        List<String> collect = history.getList().stream().map(ProcessHistoryRSP::getTaskActivityId).filter(e -> ObjectUtil.equals(e, FlowConstants.JURY_SECRETARY_COLLECT)).collect(Collectors.toList());
                        if (ObjectUtil.isNotEmpty(collect) && collect.size() > 0) {
                            //未经过，不可编辑
                            return false;
                        }
                    }
                }*/
            case VOTE_CONDITION_AGREE:
            case DISAGREE:
            case AGREE:
            case VOTE_AGREE:
            case VOTE_DISAGREE:
            case VOTE_ABSTAIN:
            case VOTE_BACK:
            case BACK_TO_START_USER:
            case BACK_TO_STEP:
            case COLLABORATE:
            case ZL_PR_RE_VOTE:
            case DISAGREE_BACK_TO_START_USER:
            case FOLLOWING:
            case RANDOM_RETURN:
                return String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus());
            case ZL_PR_RECONSIDER:
                // 如果不是评审会秘书退回则复议按钮不可用
                Map<String, Object> map = variableApiService.getVariables(rsp.getProcessInstanceId(), Arrays.asList(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK, FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION));
                Object isMeetingBack = map.get(FlowConstants.PROJ_REVIEW_IS_REVIEW_MEETING_BACK);
                if (Objects.equals(isMeetingBack, Boolean.FALSE)) {
                    return false;
                }
                // 如果点击了复议但是还未走过汇票，在中间节点被退回了，仍旧可以点击复议
                Object isStartUserReconsideration = map.get(FlowConstants.PROJ_REVIEW_IS_START_USER_RECONSIDERATION);
                if (Objects.equals(isStartUserReconsideration, Boolean.TRUE)) {
                    return true;
                }
                // 只允许复议一次
                return String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus()) && processApiService.queryExecutionTimes(rsp.getProcessInstanceId(), CommentTypeEnum.ZL_PR_RECONSIDER) == 0;
            case CANCEL:
                return String.valueOf(ProcessBusinessStatusEnum.RUNNING.getType()).equals(rsp.getProcessStatus());
            case WITHDRAW_TASK:
                if (!String.valueOf(ProcessBusinessStatusEnum.RUNNING.getType()).equals(rsp.getProcessStatus())
                        || String.valueOf(TaskBusinessStatusEnum.RUNNING.getStatus()).equals(rsp.getTaskStatus())
                        || String.valueOf(TaskBusinessStatusEnum.AUTO_CANCELED.getStatus()).equals(rsp.getTaskStatus())
                ) {
                    // 流程已结束 或 任务未结束 或 该任务不是他处理的 不能撤回
                    return false;
                }
                // 判断当前节点是否会签节点 判断流程当前节点是否任务节点的下一节点
                UserTaskExt userTaskExt = modelApiService.findUserTaskExtByProcessDefinitionId(rsp.getProcessDefineId(), rsp.getTaskActivityId());
//                TaskEntityImpl curProcessTask = (TaskEntityImpl) taskService.createTaskQuery()
//                        .active()
//                        .processInstanceId(rsp.getProcessInstanceId())
//                        .list()
//                        .get(0);
                List<Task> runningTaskList = taskService.createTaskQuery().processInstanceId(rsp.getProcessInstanceId()).active().list();
                if (CollectionUtil.isEmpty(runningTaskList)) {
                    return false;
                }
                if (ParallelApprovalMethedEnum.ALL.getType().equals(userTaskExt.getParallelApprovalMethed())) {
                    // 会签 判断流程当前所在节点是否等于任务节点
                    return userTaskExt.getActivityId().equals(runningTaskList.get(0).getTaskDefinitionKey());
                } else {
                    // 判断流程当前节点是否任务节点的下一节点
                    Map<String, NodeDefineResp> nodeDefineMap = modelApiService.getNodeDefineListByProcessDefinitionId(rsp.getProcessDefineId())
                            .stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, n -> n));
                    // 找到流程图中本节点的所有下一个用户节点
                    Set<String> nextUserTaskIdSet = new HashSet<>();
                    FlowableUtils.findNextUserTask(nodeDefineMap, userTaskExt.getActivityId(), nextUserTaskIdSet);
                    // 根据当前节点的下一个节点类型来判断是否可以撤回
                    NodeDefineEnum nodeType = FlowableUtils.nextNodeType(nodeDefineMap.get(userTaskExt.getActivityId()), nodeDefineMap);
                    switch (nodeType) {
                        case USER_TASK:
                        case EXCLUSIVE_GATEWAY: {
                            return nextUserTaskIdSet.contains(runningTaskList.get(0).getTaskDefinitionKey());
                        }
                        case PARALLEL_GATEWAY: {
                            for (Task task : runningTaskList) {
                                nextUserTaskIdSet.removeIf(e -> Objects.equals(e, task.getTaskDefinitionKey()));
                            }
                            return nextUserTaskIdSet.size() == 0;
                        }
                        default: {
                            return false;
                        }
                    }
                }
            default:
                return false;
        }
    }

    public MyProcessCountRSP myProcessCount() {
        MyProcessCountRSP rsp = new MyProcessCountRSP();

        // 申请中
        ProcessPageReq applyingReq = new ProcessPageReq();
        applyingReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        //applyingReq.setNotEqualsCurActivityId(FlowConstants.START_USER_TASK);
        applyingReq.setNoWithDraw(1);
        applyingReq.setNoBackToStep(1);
        applyingReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        rsp.setApplyingCount(taskApiService.queryProcessCount(applyingReq));

        // 我的撤回
        TaskSystemPageReq withdrawReq = new TaskSystemPageReq();
        withdrawReq.setIsRunning(1);
        withdrawReq.setStartUserBackFlag(1);
        withdrawReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        rsp.setWithdrawCount(taskApiService.querySystemTaskCount(withdrawReq));

        // 退回我的
        TaskSystemPageReq backReq = new TaskSystemPageReq();
        backReq.setIsRunning(1);
        backReq.setStartUserBackFlag(2);
        backReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        rsp.setBackToStepCount(taskApiService.querySystemTaskCount(backReq));

        // 申请中
        ProcessPageReq finishReq = new ProcessPageReq();
        finishReq.setStartUserId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        finishReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.REJECT.getType(),
                ProcessBusinessStatusEnum.CANCEL.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.REJECT_ALL.getType()));
        rsp.setFinishCount(taskApiService.queryProcessCount(finishReq));

        //待发起
        rsp.setPendingCount(getBean(CommonProcessPrepareService.class).myCount());

        return rsp;
    }

    public ReceiveProcessCountRSP myReceiveCount() {
        ReceiveProcessCountRSP rsp = new ReceiveProcessCountRSP();

        // 待办
        TaskSystemPageReq todoReq = new TaskSystemPageReq();
        todoReq.setIsRunning(1);
        todoReq.setAssignee(String.valueOf(AccountUtil.getLoginInfo().getId()));
        todoReq.setNotEqualsActivityId(FlowConstants.START_USER_TASK);
        rsp.setTodoCount(taskApiService.querySystemTaskCount(todoReq));

        // 已办
        TaskSystemPageReq doneReq = new TaskSystemPageReq();
        doneReq.setIsDone(1);
        doneReq.setAssignee(String.valueOf(AccountUtil.getLoginInfo().getId()));
        rsp.setDoneCount(taskApiService.querySystemTaskCount(doneReq));

        // 抄送我的
        CcProcessPageReq ccFlowReq = new CcProcessPageReq();
        ccFlowReq.setReceiverId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        rsp.setCcCount(taskApiService.queryCcProcessCount(ccFlowReq));

        return rsp;
    }

    /**
     * 是否控制流程查看权限
     *
     * @return
     */
    private boolean controlProcessViewAuth() {
        return controlProcessViewAuth(null);
    }

    private boolean controlProcessViewAuth(ProcessPageReq flowReq) {
        if (sysUserService.adminAuth()) {
            return false;
        }
        List<OrgDO> userOrgList = sysUserService.getUserDeptList();
        //特殊岗位看所有
        List<String> jobCodeList = sysUserService.queryUserJobList(AccountUtil.getLoginInfo().getId());
        if (jobCodeList.contains(yinZhangGuanLi.name()/*印章管理岗*/)) {
            return false;
        }

        // 领导层不控权限
        boolean specialDeptFlag = userOrgList.stream().anyMatch(o -> equalsAny(o.getCode(),
                "YYGLB", "LDC", "DSH", "FXGLWYH", "XMPSWYH", "FLHGB_ZCBQ"
        ));
        if (specialDeptFlag) {
            return false;
        }
        // 风控负责人、放款审核岗 不控权限
        boolean specialJobFlag = sysUserService.currentUserIsSpecificJob(JobEnum.riskdeptmanager.name(), JobEnum.loanreviewpost.name());
        if (specialJobFlag) {
            return false;
        }
        //通过ViewAllFlowConfig配置化权限，TODO

        // 通过jobModelMap配置指定某些岗位能查看某些流程的全量数据
        String jobModelMapJsonStr = systemConfigService.getConfig("flow.search.job_model_map").getData().getConfigValue();
        Map<String, List<String>> jobModelMap = JSON.parseObject(jobModelMapJsonStr, new TypeReference<Map<String, List<String>>>() {
        });
        List<String> notAuthModelKeyList = new ArrayList<>();
        jobModelMap.forEach((jobCode, modelList) -> {
            if (sysUserService.currentUserIsSpecificJob(jobCode)) {
                notAuthModelKeyList.addAll(modelList);
            }
        });
        if (ObjectUtil.isNotEmpty(notAuthModelKeyList)) {
            if (flowReq != null) {
                flowReq.setNotAuthModelKeyList(notAuthModelKeyList);
            }
            return false;
        }
        return true;
    }

    private Integer ensureUIVersion(String processModelType, String currentActivityId) {
        if (StrUtil.isBlank(processModelType) || StrUtil.isBlank(currentActivityId)) {
            return 0;
        }
        String key = String.format("flow.%s.%s.ui.version", processModelType, currentActivityId);
        SystemConfig systemConfig = systemConfigMapper.selectOne(Wrappers.<SystemConfig>lambdaQuery().eq(SystemConfig::getConfigKey, key).last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(systemConfig) || StrUtil.isBlank(systemConfig.getConfigValue())) {
            return 0;
        }
        return Integer.parseInt(systemConfig.getConfigValue());
    }

    //查询某个流程下审批中任务 循环调用，性能较差，不建议实时调用
    public List<TaskResp> getProcessRunningTask(List<String> businessKeyList, String modelKey) {
        //查询流程中数据
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKey(modelKey);
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        flowReq.setBusinessKeyList(businessKeyList);
        flowReq.setSortType(1);
        flowReq.setNoWithDraw(1);
        flowReq.setNoBackToStep(1);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return null;
        }
        List<TaskResp> taskRespList = new ArrayList<>();
        flowRespPage.getContents().forEach(flow -> {
            //查询任务ID
            TaskSystemPageReq taskReq = new TaskSystemPageReq();
            taskReq.setIsRunning(1);
            taskReq.setProcessInstanceId(flow.getProcessInstanceId());
            taskReq.setSortType(1);
            Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(taskReq);
            if (ObjectUtil.isNotEmpty(flowTaskPage) || ObjectUtil.isNotEmpty(flowTaskPage.getContents())) {
                taskRespList.addAll(flowTaskPage.getContents());
            }
        });
        return taskRespList;
    }

    public List<Long> getProcessEndContractTask(List<String> businessKeyList) {
        //查询流程中数据
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ContractEarlySettleFlow.name(), ProcessModelTypeEnum.ContractNormalSettleFlow.name()));
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        flowReq.setBusinessKeyList(businessKeyList);
        flowReq.setSortType(1);
        flowReq.setPageSize(10000);
        Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if (ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return new ArrayList<>();
        }
        List<Long> result = flowRespPage.getContents().stream().map(flow -> Long.valueOf(flow.getBusinessKey())).distinct().collect(Collectors.toList());
        return result;
    }
}
