package cn.zswltech.mithras.others.flow;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author dingqi
 * @date 2023/5/6
 * @description
 */
public class ParallelGatewayTestFlowTest extends FlowTest {
    @Resource
    private FlowProcessApiService flowProcessApiService;

    @Test
    public void startFlowTest() {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey("ParallelGatewayTestFlow");
        startProcessReq.setBusinessKey("1");
        startProcessReq.setStartUserId("3");
        String id = flowProcessApiService.start(startProcessReq);
        log.info("流程实例id: {}", id);
    }

    @Test
    public void parallelGatewayTest() throws Exception {
        String filePath = "/Users/mockorz/Downloads/测试并行网关流程模型.bpmn20.xml";
        InputStream bpmnStream = FileUtil.getInputStream(filePath);// 获取bpmn2.0规范的xml
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, StandardCharsets.UTF_8);
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        // 然后转为bpmnModel
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
        // bpmnModel转json
        ObjectNode editorJsonNode = bpmnJsonConverter.convertToJson(bpmnModel);
        SaveModelReq req = new SaveModelReq();
        GlobalExt globalExt = new GlobalExt();
        globalExt.setUserTaskExtMap(new HashMap<>());

        // 发起人
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name(), ApprovalButtonTypeEnum.WITHDRAW_START_USER.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 业务负责人 写死
        UserTaskExt userTaskBusinessHead = UserTaskExt.builder()
                .activityId("userTask_businesshead")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.MODEL_TARGET.getType())
                .assgineeList(Collections.singletonList("95"))
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskBusinessHead.getActivityId(), userTaskBusinessHead);

        // 分管领导 写死
        UserTaskExt userTaskBizLeader = UserTaskExt.builder()
                .activityId("userTask_bizLeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.MODEL_TARGET.getType())
                .assgineeList(Collections.singletonList("52"))
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskBizLeader.getActivityId(), userTaskBizLeader);

        // 风控经理
        UserTaskExt userTaskRiskmanager = UserTaskExt.builder()
                .activityId("userTask_riskmanager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.riskmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskRiskmanager.getActivityId(), userTaskRiskmanager);

        // 财务经理
        UserTaskExt userTaskFinancialmanager = UserTaskExt.builder()
                .activityId("userTask_financialmanager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialmanager.getActivityId(), userTaskFinancialmanager);

        // 法务经理
        UserTaskExt userTaskLegalmanager = UserTaskExt.builder()
                .activityId("userTask_legalmanager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.legalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskLegalmanager.getActivityId(), userTaskLegalmanager);

        // 财务总监
        UserTaskExt userTaskFinancialdirector = UserTaskExt.builder()
                .activityId("userTask_financialdirector")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialdirector.getActivityId(), userTaskFinancialdirector);

        // 首席风险官
        UserTaskExt userTaskChiefriskofficer = UserTaskExt.builder()
                .activityId("userTask_chiefriskofficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chiefriskofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskChiefriskofficer.getActivityId(), userTaskChiefriskofficer);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("流程部署id:{}", deployId);
    }
}
