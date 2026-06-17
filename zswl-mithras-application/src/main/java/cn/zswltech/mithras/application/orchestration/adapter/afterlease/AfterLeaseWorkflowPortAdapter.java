package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkflowPort;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.flow.convert.FlowTaskConvert;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AfterLeaseWorkflowPortAdapter implements AfterLeaseWorkflowPort {

    private static final String NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY_FLOW = "NewAfterLeaseCheckExternalQueryFlow";
    private static final String RENT_COLLECTION_EXEMPTION_FLOW = "RentCollectionExemptionFlow";
    private static final String NEW_AFTER_LEASE_CHECK_REPORT_FLOW = "NewAfterLeaseCheckReportFlow";
    private static final String NEW_AFTER_LEASE_CHECK_REPORT_COMMONLY_FLOW = "NewAfterLeaseCheckReportCommonlyFlow";
    private static final String APPLY_CREDIT_AMOUNT = "applyCreditAmount";
    private static final String START_USER_TASK = "userTask_startUser";
    private static final String PROJECT_MANAGER = "project_manager";

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FlowTaskConvert flowTaskConvert;
    @Resource
    private BizProcessDataService bizProcessDataService;

    @Override
    public String startExternalQueryApproval(ExternalQueryApprovalStartContext context) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY_FLOW);
        startProcessReq.setVariables(context.getVariables());
        startProcessReq.setStartUserId(currentUserId());
        startProcessReq.setBusinessKey(String.valueOf(context.getQueryId()));
        startProcessReq.setProcessInstanceName(context.getClientName() + "租后检查外部信息查询");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(context.getDeptId())
                .map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, context.getClientId());
        return processInstanceId;
    }

    @Override
    public String startPenaltyReductionApproval(PenaltyReductionApprovalStartContext context) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(RENT_COLLECTION_EXEMPTION_FLOW);
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("bizDeptLeader", Objects.nonNull(context.getBizDeptLeaderId()) ?
                        ListUtil.toList(String.valueOf(context.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(context.getBizDivisionLeaderId()) ?
                        ListUtil.toList(String.valueOf(context.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of(APPLY_CREDIT_AMOUNT, NumberUtil.div(String.valueOf(context.getApplyCreditAmount()), GlobalConstants.MONEY_MULTIPLE)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue())
        ));
        startProcessReq.setStartUserId(currentUserId());
        startProcessReq.setBusinessKey(String.valueOf(context.getReductionId()));
        startProcessReq.setSubModule(context.getBizType());
        startProcessReq.setProcessInstanceName(context.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(context.getProjCosponsorUserIds()) ?
                new ArrayList<>() : JSONUtil.parseArray(context.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(context.getBizDeptId())
                .map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, context.getClientId());
        return processInstanceId;
    }

    @Override
    public boolean canModifyAtCurrentProcessNode(Long businessKey, List<String> modelKeys) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(businessKey));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(modelKeys);
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            return true;
        }
        return isSpecificNode(processResp, START_USER_TASK) || isSpecificNode(processResp, PROJECT_MANAGER);
    }

    @Override
    public List<ApprovalReminderTask> listReportApprovalReminderTasks() {
        TaskSystemPageReq flowReq = new TaskSystemPageReq();
        flowReq.setDynamicFilterParam(new HashMap<>());
        flowReq.setModelKeyList(Arrays.asList(NEW_AFTER_LEASE_CHECK_REPORT_FLOW, NEW_AFTER_LEASE_CHECK_REPORT_COMMONLY_FLOW));
        flowReq.setIsRunning(1);
        flowReq.setSortType(1);
        Page<TaskResp> flowTaskPage = flowTaskApiService.querySystemTask(flowReq);
        if (CollectionUtils.isEmpty(flowTaskPage.getContents())) {
            return Collections.emptyList();
        }

        List<String> processInstanceIdList = flowTaskPage.getContents().stream()
                .map(TaskResp::getProcessInstanceId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, ProcessResp> processRespMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(processInstanceIdList)) {
            ProcessPageReq processFlowReq = new ProcessPageReq();
            processFlowReq.setProcessInstanceIdList(processInstanceIdList);
            processFlowReq.setPageSize(Integer.MAX_VALUE);
            processRespMap.putAll(flowTaskApiService.queryProcess(processFlowReq).getContents()
                    .stream()
                    .collect(Collectors.toMap(ProcessResp::getProcessInstanceId, p -> p)));
        }

        List<ReceiveTaskListRSP> rspList = flowTaskPage.getContents().stream()
                .map(resp -> flowTaskConvert.flowResp2ReceiveRSP(resp, processRespMap.get(resp.getProcessInstanceId())))
                .collect(Collectors.toList());
        flowTaskConvert.receiveTaskListRSPFillName(rspList);
        return rspList.stream().map(this::toApprovalReminderTask).collect(Collectors.toList());
    }

    private String currentUserId() {
        return Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
    }

    private boolean isSpecificNode(ProcessResp processResp, String activityId) {
        return Arrays.stream(processResp.getCurTaskActivityIds().split(","))
                .anyMatch(activityId::equals);
    }

    private ApprovalReminderTask toApprovalReminderTask(ReceiveTaskListRSP rsp) {
        ApprovalReminderTask task = new ApprovalReminderTask();
        task.setAssignee(rsp.getAssignee());
        task.setTaskId(rsp.getTaskId());
        task.setBusinessKey(rsp.getBusinessKey());
        task.setSubModule(rsp.getSubModule());
        task.setClientName(rsp.getClientName());
        task.setModelName(rsp.getModelName());
        task.setProcessInstanceId(rsp.getProcessInstanceId());
        task.setTaskCreateTime(rsp.getTaskCreateTime());
        return task;
    }
}
