package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
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
import java.util.List;

/**
 * 项目评审
 *
 * @author wangchuanhao
 * @date 2022/8/8 10:27 PM
 */
public class ProjReviewPricingApprovalFlowTest extends FlowTest {

    @Test
    public void ProjReviewPricingApprovalFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/项目评审定价审批流程.bpmn20.xml", "bpmn/项目评审定价变更审批流程.bpmn20.xml");
        for (String fileName : fileNameList) {
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

            // 财务主管1
            UserTaskExt userTaskFinanceOfficer1 = UserTaskExt.builder()
                    .activityId("userTask_financeOfficer_1")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.financialofficer.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                            ApprovalButtonTypeEnum.COLLABORATE.name(),
                            ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                            ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_setFinanceFile.name(), FlowDynamicFormEnum.projReview_pricingChooseApproveAuth.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskFinanceOfficer1.getActivityId(), userTaskFinanceOfficer1);

            // 业务分管领导
            UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
                    .activityId("userTask_bizDivisionLeader")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("bizDivisionLeader")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                            ApprovalButtonTypeEnum.COLLABORATE.name(),
                            ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                            ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);

            // 定价委员会
            UserTaskExt userTaskPriceCommittee = UserTaskExt.builder()
                    .activityId("userTask_priceCommittee")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.CUSTOM.getType())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .canBackActivityIdList(Collections.singletonList("userTask_financeOfficer_1"))
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.VOTE_AGREE.name(), ApprovalButtonTypeEnum.VOTE_DISAGREE.name(), ApprovalButtonTypeEnum.BACK_TO_STEP.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskPriceCommittee.getActivityId(), userTaskPriceCommittee);

            // 财务主管2
            UserTaskExt userTaskFinanceOfficer2 = UserTaskExt.builder()
                    .activityId("userTask_financeOfficer_2")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.financialofficer.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                            ApprovalButtonTypeEnum.COLLABORATE.name(),
                            ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                            ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_setPricingMeetingFile.name(), FlowDynamicFormEnum.projReview_pricingShowVoteResult.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskFinanceOfficer2.getActivityId(), userTaskFinanceOfficer2);

            // 财务总监
            UserTaskExt userTaskChiefFinancialOfficer = UserTaskExt.builder()
                    .activityId("userTask_chiefFinancialOfficer")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.financialdirector.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                            ApprovalButtonTypeEnum.COLLABORATE.name(),
                            ApprovalButtonTypeEnum.BACK_TO_START_USER.name(),
                            ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskChiefFinancialOfficer.getActivityId(), userTaskChiefFinancialOfficer);

            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            log.info("流程模型id:{}", modelId);
            String deployId = modelApiService.deploy(modelId);
            log.info("模型部署id:{}", deployId);
        }
    }

}
