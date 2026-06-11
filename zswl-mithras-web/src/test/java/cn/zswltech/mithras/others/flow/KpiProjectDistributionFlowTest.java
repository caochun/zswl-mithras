package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
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
import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
public class KpiProjectDistributionFlowTest extends FlowTest {
    @Test
    public void kpiProjectDistributionFlowTest() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/绩效考核项目分配创建审批流程.bpmn20.xml", "bpmn/绩效考核项目分配变更审批流程.bpmn20.xml");
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

            // 财务主管
            UserTaskExt userTaskExtFinancialofficer = UserTaskExt.builder()
                    .activityId("userTask_financialofficer")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.financialofficer.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtFinancialofficer.getActivityId(), userTaskExtFinancialofficer);

            // 项目主办
            UserTaskExt userTaskExtSponsorUser = UserTaskExt.builder()
                    .activityId("userTask_sponsorUser")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("sponsorUser")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtSponsorUser.getActivityId(), userTaskExtSponsorUser);

            // 项目协办
            UserTaskExt userTaskExtCosponsorUser = UserTaskExt.builder()
                    .activityId("userTask_cosponsorUser")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("cosponsorUser")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtCosponsorUser.getActivityId(), userTaskExtCosponsorUser);

            // 跨部门推荐人
            UserTaskExt userTaskExtOtherDeptRecommend = UserTaskExt.builder()
                    .activityId("userTask_otherDeptRecommend")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("otherDeptRecommend")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtOtherDeptRecommend.getActivityId(), userTaskExtOtherDeptRecommend);

            // 分管领导
            UserTaskExt userTaskExtLeaderincharge = UserTaskExt.builder()
                    .activityId("userTask_leaderincharge")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("leaderincharge")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtLeaderincharge.getActivityId(), userTaskExtLeaderincharge);

            // 绩效考核岗
            UserTaskExt userTaskExtKpimanagement = UserTaskExt.builder()
                    .activityId("userTask_kpimanagement")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.kpimanagement.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtKpimanagement.getActivityId(), userTaskExtKpimanagement);

            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            log.info("流程模型id:{}", modelId);
            String deployId = modelApiService.deploy(modelId);
            log.info("发布id:{}", deployId);
        }
    }
}
