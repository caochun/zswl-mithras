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
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 客户流程
 *
 * @author wangchuanhao
 * @date 2022/8/8 10:16 PM
 */
public class ContractFlowTest extends FlowTest {

//    /**
//     * 合同修改
//     * @throws Exception
//     */
//    @Test
//    public void contractModifyFlow() throws Exception {
//        List<String> fileNameList = Arrays.asList(
//                "bpmn/合同展期审批流程.bpmn20.xml",
//                "bpmn/合同提前结清审批流程.bpmn20.xml",
//                "bpmn/合同提前还款审批流程.bpmn20.xml",
//                "bpmn/合同调整还款计划审批流程.bpmn20.xml"
//                );
//        List<String> modelIdList = new ArrayList<>();
//        for (String fileName : fileNameList) {
//            InputStream bpmnStream = new ClassPathResource(fileName).getInputStream();// 获取bpmn2.0规范的xml
//            XMLInputFactory xif = XMLInputFactory.newInstance();
//            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
//            XMLStreamReader xtr = xif.createXMLStreamReader(in);
//            // 然后转为bpmnModel
//            BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
//            // bpmnModel转json
//            ObjectNode editorJsonNode = bpmnJsonConverter.convertToJson(bpmnModel);
//            SaveModelReq req = new SaveModelReq();
//            GlobalExt globalExt = new GlobalExt();
//            globalExt.setUserTaskExtMap(new HashMap<>());
//
//            // 发起人节点
//            UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
//                    .activityId(FlowConstants.START_USER_TASK)
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.START_USER.getType())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
//                    .skipFirst(true)
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);
//
//            // 业务部门负责人
//            UserTaskExt userTaskExtDeptMaster = UserTaskExt.builder()
//                    .activityId("userTask_deptMaster")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
//                    .userDefineVarName("bizDeptLeader")
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtDeptMaster.getActivityId(), userTaskExtDeptMaster);
//
//            // 业务分管领导
//            UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
//                    .activityId("userTask_bizDivisionLeader")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
//                    .userDefineVarName("bizDivisionLeader")
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);
//
//            // 财务主管
//            UserTaskExt userTaskExtFinanceManager = UserTaskExt.builder()
//                    .activityId("userTask_financeOfficer")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
//                    .jobCode(JobEnum.financialofficer.name())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtFinanceManager.getActivityId(), userTaskExtFinanceManager);
//
//            // 法务经理
//            UserTaskExt userTaskExtLawManager = UserTaskExt.builder()
//                    .activityId("userTask_lawManager")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
//                    .jobCode(JobEnum.legalmanager.name())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtLawManager.getActivityId(), userTaskExtLawManager);
//
//            // 风险管理部负责人
//            UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
//                    .activityId("userTask_riskDeptMaster")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
//                    .jobCode(JobEnum.riskdeptmanager.name())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);
//
//            // 财务总监
//            UserTaskExt userTaskExtChiefFinancialOfficer = UserTaskExt.builder()
//                    .activityId("userTask_chiefFinancialOfficer")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
//                    .jobCode(JobEnum.financialdirector.name())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtChiefFinancialOfficer.getActivityId(), userTaskExtChiefFinancialOfficer);
//
//            // 首席风险官
//            UserTaskExt userTaskExtChiefRiskOfficer = UserTaskExt.builder()
//                    .activityId("userTask_chiefRiskOfficer")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
//                    .jobCode(JobEnum.chiefriskofficer.name())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtChiefRiskOfficer.getActivityId(), userTaskExtChiefRiskOfficer);
//
//            // 总经理节点
//            UserTaskExt userTaskExtGeneralManager = UserTaskExt.builder()
//                    .activityId("userTask_generalManager")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
//                    .userDefineVarName("generalManagerNodeApprover")
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtGeneralManager.getActivityId(), userTaskExtGeneralManager);
//
//            // 董事长
//            UserTaskExt userTaskExtChairman = UserTaskExt.builder()
//                    .activityId("userTask_chairman")
//                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
//                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
//                    .jobCode(JobEnum.chairman.name())
//                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
//                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
//                    .build();
//            globalExt.getUserTaskExtMap().put(userTaskExtChairman.getActivityId(), userTaskExtChairman);
//
//            req.setEditorJson(editorJsonNode.toString());
//            req.setGlobalExt(globalExt);
//            String modelId = modelApiService.save(req);
//            modelIdList.add(modelId);
//            log.info("流程模型id:{}", modelId);
//        }
//        log.info("流程模型id列表:{}", JSON.toJSONString(modelIdList));
//    }

    @Test
    public void contractModifyFlow() throws Exception {
        String fileName = "bpmn/合同变更审批流程.bpmn20.xml";
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

        // 发起人节点
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 法务经理确认
        UserTaskExt userTaskExtLawManager = UserTaskExt.builder()
                .activityId("userTask_lawManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.legalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtLawManager.getActivityId(), userTaskExtLawManager);

        // 发起人确认
        UserTaskExt userTaskExtStartUser2 = UserTaskExt.builder()
                .activityId("userTask_startUser_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser2.getActivityId(), userTaskExtStartUser2);

        // 业务部门负责人
        UserTaskExt userTaskExtDeptMaster = UserTaskExt.builder()
                .activityId("userTask_deptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDeptLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtDeptMaster.getActivityId(), userTaskExtDeptMaster);

        // 业务分管领导
        UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
                .activityId("userTask_bizDivisionLeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDivisionLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);

        // 财务主管
        UserTaskExt userTaskExtFinanceManager = UserTaskExt.builder()
                .activityId("userTask_financeOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtFinanceManager.getActivityId(), userTaskExtFinanceManager);

        // 法务经理审批
        UserTaskExt userTaskExtLawManager2 = UserTaskExt.builder()
                .activityId("userTask_lawManager_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.legalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtLawManager2.getActivityId(), userTaskExtLawManager2);

        // 风险管理部负责人
        UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
                .activityId("userTask_riskDeptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.riskdeptmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);

        // 财务总监
        UserTaskExt userTaskExtChiefFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefFinancialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefFinancialOfficer.getActivityId(), userTaskExtChiefFinancialOfficer);

        // 首席风险官
        UserTaskExt userTaskExtChiefRiskOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefRiskOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chiefriskofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefRiskOfficer.getActivityId(), userTaskExtChiefRiskOfficer);

        // 总经理节点
        UserTaskExt userTaskExtGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.generalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtGeneralManager.getActivityId(), userTaskExtGeneralManager);

        // 董事长
        UserTaskExt userTaskExtChairman = UserTaskExt.builder()
                .activityId("userTask_chairman")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chairman.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChairman.getActivityId(), userTaskExtChairman);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("流程部署id:{}", deployId);
    }

    /**
     * 合同创建
     *
     * @throws Exception
     */
    @Test
    public void contractCreateFlow() throws Exception {
        String fileName = "bpmn/合同创建审批流程.bpmn20.xml";
        InputStream bpmnStream = new ClassPathResource(fileName).getInputStream();// 获取bpmn2.0规范的xml
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        // 然后转为bpmnModel
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
        // bpmnModel转json
        ObjectNode editorJsonNode = bpmnJsonConverter.convertToJson(bpmnModel);
        SaveModelReq req = new SaveModelReq();
        GlobalExt globalExt = new GlobalExt();
        globalExt.setUserTaskExtMap(new HashMap<>());

        // 发起人节点
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 法务经理确认
        UserTaskExt userTaskExtLawManager = UserTaskExt.builder()
                .activityId("userTask_lawManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.legalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtLawManager.getActivityId(), userTaskExtLawManager);

        // 发起人确认
        UserTaskExt userTaskExtStartUser2 = UserTaskExt.builder()
                .activityId("userTask_startUser_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser2.getActivityId(), userTaskExtStartUser2);

        // 业务部门负责人
        UserTaskExt userTaskExtDeptMaster = UserTaskExt.builder()
                .activityId("userTask_deptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDeptLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtDeptMaster.getActivityId(), userTaskExtDeptMaster);

        // 业务分管领导
        UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
                .activityId("userTask_bizDivisionLeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDivisionLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);

        // 财务主管
        UserTaskExt userTaskExtFinanceManager = UserTaskExt.builder()
                .activityId("userTask_financeOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtFinanceManager.getActivityId(), userTaskExtFinanceManager);

        // 法务经理审批
        UserTaskExt userTaskExtLawManager2 = UserTaskExt.builder()
                .activityId("userTask_lawManager_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.legalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtLawManager2.getActivityId(), userTaskExtLawManager2);

        // 风险管理部负责人
        UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
                .activityId("userTask_riskDeptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.riskdeptmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);

        // 财务总监
        UserTaskExt userTaskExtChiefFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefFinancialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefFinancialOfficer.getActivityId(), userTaskExtChiefFinancialOfficer);

        // 首席风险官
        UserTaskExt userTaskExtChiefRiskOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefRiskOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chiefriskofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefRiskOfficer.getActivityId(), userTaskExtChiefRiskOfficer);

        // 总经理节点
        UserTaskExt userTaskExtGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.generalmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtGeneralManager.getActivityId(), userTaskExtGeneralManager);

        // 董事长
        UserTaskExt userTaskExtChairman = UserTaskExt.builder()
                .activityId("userTask_chairman")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chairman.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChairman.getActivityId(), userTaskExtChairman);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("流程部署id:{}", deployId);
    }

    /**
     * 合同正常结清
     *
     * @throws Exception
     */
    @Test
    public void contractNormalSettleFlow() throws Exception {
        InputStream bpmnStream = new ClassPathResource("bpmn/合同正常结清审批流程.bpmn20.xml").getInputStream();// 获取bpmn2.0规范的xml
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        // 然后转为bpmnModel
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
        // bpmnModel转json
        ObjectNode editorJsonNode = bpmnJsonConverter.convertToJson(bpmnModel);
        SaveModelReq req = new SaveModelReq();
        GlobalExt globalExt = new GlobalExt();
        globalExt.setUserTaskExtMap(new HashMap<>());

        // 发起人节点
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 业务部门负责人
        UserTaskExt userTaskExtDeptMaster = UserTaskExt.builder()
                .activityId("userTask_deptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDeptLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtDeptMaster.getActivityId(), userTaskExtDeptMaster);

        // 业务分管领导
        UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
                .activityId("userTask_bizDivisionLeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDivisionLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);

        // 资产管理
        UserTaskExt userTaskExtAssetManager = UserTaskExt.builder()
                .activityId("userTask_assetManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.assetmanagement.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtAssetManager.getActivityId(), userTaskExtAssetManager);

        // 财务主管
        UserTaskExt userTaskExtFinanceOfficer = UserTaskExt.builder()
                .activityId("userTask_financeOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtFinanceOfficer.getActivityId(), userTaskExtFinanceOfficer);

        // 风险管理部负责人
        UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
                .activityId("userTask_riskDeptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.riskdeptmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);

        // 财务总监
        UserTaskExt userTaskExtChiefFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefFinancialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefFinancialOfficer.getActivityId(), userTaskExtChiefFinancialOfficer);

        // 首席风险官
        UserTaskExt userTaskExtChiefRiskOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefRiskOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chiefriskofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefRiskOfficer.getActivityId(), userTaskExtChiefRiskOfficer);

        // 总经理节点 TODO 产品说总经理不存在 节点名字不变，审批人改为分管领导
        UserTaskExt userTaskExtGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDivisionLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtGeneralManager.getActivityId(), userTaskExtGeneralManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
    }

    /**
     * 合同起租
     *
     * @throws Exception
     */
    @Test
    public void contractStartRentFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/合同起租审批流程.bpmn20.xml",
                "bpmn/合同新增借据审批流程.bpmn20.xml"
        );
        List<String> modelIdList = new ArrayList<>();
        for (String fileName : fileNameList) {
            // 合同起租审批流程 合同新增借据审批流程
            InputStream bpmnStream = new ClassPathResource(fileName).getInputStream();// 获取bpmn2.0规范的xml
            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            // 然后转为bpmnModel
            BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
            // bpmnModel转json
            ObjectNode editorJsonNode = bpmnJsonConverter.convertToJson(bpmnModel);
            SaveModelReq req = new SaveModelReq();
            GlobalExt globalExt = new GlobalExt();
            globalExt.setUserTaskExtMap(new HashMap<>());

            // 发起人节点
            UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                    .activityId(FlowConstants.START_USER_TASK)
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.START_USER.getType())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                    .skipFirst(true)
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

            // 财务经理节点
            UserTaskExt userTaskExtRiskManager = UserTaskExt.builder()
                    .activityId("userTask_financeManager")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.financialmanager.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtRiskManager.getActivityId(), userTaskExtRiskManager);

            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            log.info("流程模型id:{}", modelId);
            modelIdList.add(modelId);
        }
        log.info("流程模型id列表:{}", JSON.toJSONString(modelIdList));
    }

    /**
     * 合同LPR调整流程
     *
     * @throws Exception
     */
    @Test
    public void contractLPRChangeFlow() throws Exception {
        // 合同展期审批流程 合同提前还款审批流程 合同调息审批流程
        InputStream bpmnStream = new ClassPathResource("bpmn/合同LPR调整审批流程.bpmn20.xml").getInputStream();// 获取bpmn2.0规范的xml
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        // 然后转为bpmnModel
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
        // bpmnModel转json
        ObjectNode editorJsonNode = bpmnJsonConverter.convertToJson(bpmnModel);
        SaveModelReq req = new SaveModelReq();
        GlobalExt globalExt = new GlobalExt();
        globalExt.setUserTaskExtMap(new HashMap<>());

        // 发起人节点
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        // 财务经理节点
        UserTaskExt userTaskExtFinanceManager = UserTaskExt.builder()
                .activityId("userTask_financeManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtFinanceManager.getActivityId(), userTaskExtFinanceManager);

        // 财务主管
        UserTaskExt userTaskExtFinanceOfficer = UserTaskExt.builder()
                .activityId("userTask_financeOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtFinanceOfficer.getActivityId(), userTaskExtFinanceOfficer);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
    }


}
