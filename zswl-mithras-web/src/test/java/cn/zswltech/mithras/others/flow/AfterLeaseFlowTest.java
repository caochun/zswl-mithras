package cn.zswltech.mithras.others.flow;

import cn.hutool.core.collection.ListUtil;
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
import java.util.HashMap;
import java.util.List;


public class AfterLeaseFlowTest extends FlowTest {

    //租后调整流程
    @Test
    public void adjustFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/租后展期评审流程.bpmn20.xml", "bpmn/租后项目调整还款计划审批流程.bpmn20.xml");
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
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.ZL_PR_RECONSIDER.name(), ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
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
                    .activityId(FlowConstants.PARALLEL_RISK_MANAGER)
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("riskControlManager")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    // 风控文件
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_setRiskFile.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtRiskManager.getActivityId(), userTaskExtRiskManager);

            // 法务经理
            UserTaskExt userTaskExtLawManager = UserTaskExt.builder()
                    .activityId(FlowConstants.PARALLEL_LAW_MANAGER)
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("legalManagerUser")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_setLawFile.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtLawManager.getActivityId(), userTaskExtLawManager);

            // 风控经理（评审会退回）
            UserTaskExt userTaskExtRiskManagerBack = UserTaskExt.builder()
                    .activityId(FlowConstants.PARALLEL_RISK_MANAGER_BACK)
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("riskControlManager")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    // 风控文件
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_setRiskFile.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtRiskManagerBack.getActivityId(), userTaskExtRiskManagerBack);

            // 法务经理（评审会退回）
            UserTaskExt userTaskExtLawManagerBack = UserTaskExt.builder()
                    .activityId(FlowConstants.PARALLEL_LAW_MANAGER_BACK)
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("legalManagerUser")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_setLawFile.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtLawManagerBack.getActivityId(), userTaskExtLawManagerBack);

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

            // 首席风险官
            UserTaskExt userTaskExtChiefRiskOfficer = UserTaskExt.builder()
                    .activityId("userTask_chiefRiskOfficer")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.chiefriskofficer.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.BACK_TO_STEP.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    // 加入并行网关后，由于存在评审会退回分支，此处可退回使用评审会退回分支的id，在详情接口中判断是否需要替换成非评审会退回的id
                    .canBackActivityIdList(ListUtil.toList(FlowConstants.PARALLEL_RISK_MANAGER_BACK, FlowConstants.PARALLEL_LAW_MANAGER_BACK))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtChiefRiskOfficer.getActivityId(), userTaskExtChiefRiskOfficer);

            // 评审会秘书
            UserTaskExt userTaskExtJurySecretary = UserTaskExt.builder()
                    .activityId("userTask_jurySecretary")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.secretaryjury.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_chooseJudges.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtJurySecretary.getActivityId(), userTaskExtJurySecretary);

            // 评审委员非公开投票 投票节点
            UserTaskExt userTaskExtJuryVote = UserTaskExt.builder()
                    .activityId("userTask_juryVote")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.CUSTOM.getType())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.VOTE_AGREE.name(), ApprovalButtonTypeEnum.VOTE_CONDITION_AGREE.name(), ApprovalButtonTypeEnum.VOTE_DISAGREE.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtJuryVote.getActivityId(), userTaskExtJuryVote);

            // 评审会秘书统计投票结果
            UserTaskExt userTaskExtJurySecretaryCollect = UserTaskExt.builder()
                    .activityId("userTask_jurySecretaryCollect")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.secretaryjury.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.ZL_PR_MEETING_SECRETARY_DISAGREE.name(), ApprovalButtonTypeEnum.ZL_PR_CONDITION_AGREE.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .canBackActivityIdList(Arrays.asList("userTask_startUser", "userTask_riskManager", "userTask_lawManager", "userTask_juryVote"))
                    // 显示投票结果 上传会议纪要
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.adjust_showJudgesVotingResults.name(), FlowDynamicFormEnum.adjust_setMeetingFile.name(), FlowDynamicFormEnum.adjust_setMeetingRecord.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtJurySecretaryCollect.getActivityId(), userTaskExtJurySecretaryCollect);

            // 评审会秘书发起会议纪要审批 投票节点
            UserTaskExt userTaskExtJuryMeetingReview = UserTaskExt.builder()
                    .activityId("userTask_juryMeetingReview")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.CUSTOM.getType())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.VOTE_AGREE.name(), ApprovalButtonTypeEnum.VOTE_BACK.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtJuryMeetingReview.getActivityId(), userTaskExtJuryMeetingReview);

            // 会议纪要评审汇票
            UserTaskExt userTaskExtJuryMeetingCollect = UserTaskExt.builder()
                    .activityId("userTask_juryMeetingCollect")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.secretaryjury.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name(), ApprovalButtonTypeEnum.ZL_PR_RE_VOTE.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    // 显示投票结果 上传会议纪要
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_showMeetingVotingResults.name(), FlowDynamicFormEnum.adjust_setMeetingFile.name(), FlowDynamicFormEnum.adjust_setMeetingRecord.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtJuryMeetingCollect.getActivityId(), userTaskExtJuryMeetingCollect);

            // 总经理节点
            UserTaskExt userTaskExtGeneralManager = UserTaskExt.builder()
                    .activityId("userTask_generalManager")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.generalmanager.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtGeneralManager.getActivityId(), userTaskExtGeneralManager);

            // 董事会秘书
            UserTaskExt userTaskExtDirectorSecretary = UserTaskExt.builder()
                    .activityId("userTask_directorSecretary")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.boardsecretary.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name()))
                    // 显示董事会成员
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_showDirectors.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtDirectorSecretary.getActivityId(), userTaskExtDirectorSecretary);

            // 董事会投票
            UserTaskExt userTaskExtDirectorVote = UserTaskExt.builder()
                    .activityId("userTask_directorVote")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.CUSTOM.getType())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.VOTE_AGREE.name(), ApprovalButtonTypeEnum.VOTE_DISAGREE.name(), ApprovalButtonTypeEnum.VOTE_ABSTAIN.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtDirectorVote.getActivityId(), userTaskExtDirectorVote);

            // 董事会秘书汇票
            UserTaskExt userTaskExtDirectorSecretaryCollect = UserTaskExt.builder()
                    .activityId("userTask_directorSecretaryCollect")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.boardsecretary.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name()))
                    // 显示投票结果
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projReview_showDirectorsVotingResults.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtDirectorSecretaryCollect.getActivityId(), userTaskExtDirectorSecretaryCollect);


            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            log.info("流程模型id:{}", modelId);
            String deployId = modelApiService.deploy(modelId);
            log.info("部署模型id:{}", deployId);
        }
    }
}
