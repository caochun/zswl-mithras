package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

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
 * @date 2023/2/22
 * @description
 */
public class FundFinancingFlowTest extends FlowTest {
    @Test
    public void fundFinancingCreateFlowTest() throws Exception {
        String fileName = "/bpmn/资金融资创建审批流程.bpmn20.xml";
        InputStream bpmnStream = new ClassPathResource(fileName).getInputStream();// 获取bpmn2.0规范的xml
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
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 写死鲁桂欣
        UserTaskExt userTaskDeptHeader = UserTaskExt.builder()
                .activityId("userTask_deptHeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.MODEL_TARGET.getType())
                .assgineeList(Collections.singletonList("65"))
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskDeptHeader.getActivityId(), userTaskDeptHeader);

        // 财务主管
        UserTaskExt userTaskFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_financialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialOfficer.getActivityId(), userTaskFinancialOfficer);

        // 财务总监
        UserTaskExt userTaskFinancialDirector = UserTaskExt.builder()
                .activityId("userTask_financialDirector")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialDirector.getActivityId(), userTaskFinancialDirector);

        UserTaskExt userTaskGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.generalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskGeneralManager.getActivityId(), userTaskGeneralManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("发布id:{}", deployId);
    }

    @Test
    public void fundFinancingModifyFlowTest() throws Exception {
        String fileName = "/bpmn/资金融资起息前变更审批流程.bpmn20.xml";
        InputStream bpmnStream = new ClassPathResource(fileName).getInputStream();// 获取bpmn2.0规范的xml
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
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 写死鲁桂欣
        UserTaskExt userTaskDeptHeader = UserTaskExt.builder()
                .activityId("userTask_deptHeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.MODEL_TARGET.getType())
                .assgineeList(Collections.singletonList("65"))
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskDeptHeader.getActivityId(), userTaskDeptHeader);

        // 财务主管
        UserTaskExt userTaskFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_financialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialOfficer.getActivityId(), userTaskFinancialOfficer);

        // 财务总监
        UserTaskExt userTaskFinancialDirector = UserTaskExt.builder()
                .activityId("userTask_financialDirector")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialDirector.getActivityId(), userTaskFinancialDirector);

        UserTaskExt userTaskGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.generalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskGeneralManager.getActivityId(), userTaskGeneralManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("发布id:{}", deployId);
    }

    @Test
    public void earlySettleFlowTest() throws Exception {
        String fileName = "/bpmn/资金融资贷后变更提前结清审批流程.bpmn20.xml";
        InputStream bpmnStream = new ClassPathResource(fileName).getInputStream();// 获取bpmn2.0规范的xml
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
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 写死鲁桂欣
        UserTaskExt userTaskDeptHeader = UserTaskExt.builder()
                .activityId("userTask_deptHeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.MODEL_TARGET.getType())
                .assgineeList(Collections.singletonList("65"))
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskDeptHeader.getActivityId(), userTaskDeptHeader);

        // 财务主管
        UserTaskExt userTaskFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_financialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialOfficer.getActivityId(), userTaskFinancialOfficer);

        // 财务总监
        UserTaskExt userTaskFinancialDirector = UserTaskExt.builder()
                .activityId("userTask_financialDirector")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name(),
                        ApprovalButtonTypeEnum.COLLABORATE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskFinancialDirector.getActivityId(), userTaskFinancialDirector);

        UserTaskExt userTaskGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.generalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(
                        ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name()
                ))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskGeneralManager.getActivityId(), userTaskGeneralManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("发布id:{}", deployId);
    }
}
