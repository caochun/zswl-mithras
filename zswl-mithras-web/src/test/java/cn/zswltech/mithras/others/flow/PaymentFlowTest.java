package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 付款
 *
 * @author wangchuanhao
 * @date 2022/8/22 15:27 PM
 */
public class PaymentFlowTest extends FlowTest {

    @Test
    public void paymentFlow() throws Exception {
        InputStream bpmnStream = new ClassPathResource("bpmn/付款申请创建审批流程.bpmn20.xml").getInputStream();// 获取bpmn2.0规范的xml
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

        // 业务部门负责人
        UserTaskExt userTaskExtDeptMaster = UserTaskExt.builder()
                .activityId("userTask_deptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDeptLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtDeptMaster.getActivityId(), userTaskExtDeptMaster);

        // 业务分管领导
        UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
                .activityId("userTask_bizDivisionLeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDivisionLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);

        // 风控经理
        UserTaskExt userTaskExtRiskManager = UserTaskExt.builder()
                .activityId("userTask_riskManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("riskControlManager")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskManager.getActivityId(), userTaskExtRiskManager);

        // 风险管理部负责人
        UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
                .activityId("userTask_riskDeptMaster")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.riskdeptmanager.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);

        // 财务主管
        UserTaskExt userTaskExtFinanceManager = UserTaskExt.builder()
                .activityId("userTask_financeManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.MODEL_TARGET.getType())
                // 周斐
                .assgineeList(Arrays.asList("106"))
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtFinanceManager.getActivityId(), userTaskExtFinanceManager);

        // 财务总监
        UserTaskExt userTaskExtChiefFinancialOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefFinancialOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.financialdirector.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefFinancialOfficer.getActivityId(), userTaskExtChiefFinancialOfficer);

        // 首席风险官
        UserTaskExt userTaskExtChiefRiskOfficer = UserTaskExt.builder()
                .activityId("userTask_chiefRiskOfficer")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chiefriskofficer.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChiefRiskOfficer.getActivityId(), userTaskExtChiefRiskOfficer);

        // 放款审核岗审批
        UserTaskExt userTaskExtLoanReviewPost = UserTaskExt.builder()
                .activityId("userTask_loanReviewPost")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.loanreviewpost.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                // 补充文件
                .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.payment_setLoanApprovalFile.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtLoanReviewPost.getActivityId(), userTaskExtLoanReviewPost);

        // 总经理节点
        UserTaskExt userTaskExtGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("generalManagerNodeApprover")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtGeneralManager.getActivityId(), userTaskExtGeneralManager);

        // 董事长
        UserTaskExt userTaskExtChairman = UserTaskExt.builder()
                .activityId("userTask_chairman")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.chairman.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtChairman.getActivityId(), userTaskExtChairman);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
    }

}
