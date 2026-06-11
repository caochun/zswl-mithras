package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 流程
 *
 * @author wangchuanhao
 * @date 2022/6/21 11:02 PM
 */
public class FlowTest extends ApplicationTest {

    protected static final Logger log = LoggerFactory.getLogger(FlowTest.class);

    @Resource
    protected FlowModelApiService modelApiService;
    @Resource
    protected BpmnXMLConverter bpmnXMLConverter;
    @Resource
    protected BpmnJsonConverter bpmnJsonConverter;

    @Test
    public void testFlow() throws Exception {
        InputStream bpmnStream = new ClassPathResource("bpmn/租赁新测试模型.bpmn20.xml").getInputStream();// 获取bpmn2.0规范的xml
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
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        UserTaskExt userTask2 = UserTaskExt.builder()
                .activityId("userTask_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTask2AssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .build();
        globalExt.getUserTaskExtMap().put(userTask2.getActivityId(), userTask2);

        UserTaskExt userTaskExtRiskManager = UserTaskExt.builder()
                .activityId("userTask_3")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTask3AssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskManager.getActivityId(), userTaskExtRiskManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
    }

    @Test
    public void mithrasFlowComplexTestModel() throws Exception {
        InputStream bpmnStream = new ClassPathResource("bpmn/租赁审批流复合测试模型.bpmn20.xml").getInputStream();// 获取bpmn2.0规范的xml
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
        UserTaskExt userTaskExtStartUser = UserTaskExt.builder()
                .activityId(FlowConstants.START_USER_TASK)
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.START_USER.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        UserTaskExt userTask1 = UserTaskExt.builder()
                .activityId("userTask_1")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTask1AssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTask1.getActivityId(), userTask1);

        UserTaskExt userTask2 = UserTaskExt.builder()
                .activityId("userTask_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTask2AssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTask2.getActivityId(), userTask2);

        UserTaskExt userTaskPrepareMeeting = UserTaskExt.builder()
                .activityId("userTask_prepareMeeting")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTaskPrepareMeetingAssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskPrepareMeeting.getActivityId(), userTaskPrepareMeeting);

        // 投票节点
        UserTaskExt userTaskVote = UserTaskExt.builder()
                .activityId("userTask_vote")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTaskVoteAssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.VOTE_AGREE.name(), ApprovalButtonTypeEnum.VOTE_CONDITION_AGREE.name(), ApprovalButtonTypeEnum.VOTE_DISAGREE.name(), ApprovalButtonTypeEnum.VOTE_ABSTAIN.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskVote.getActivityId(), userTaskVote);

        UserTaskExt userTaskCollect = UserTaskExt.builder()
                .activityId("userTask_collect")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTaskCollectAssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.BACK_TO_STEP.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .canBackActivityIdList(Arrays.asList("userTask_1", "userTask_2"))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskCollect.getActivityId(), userTaskCollect);

        UserTaskExt userTaskGeneralManager = UserTaskExt.builder()
                .activityId("userTask_generalManager")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("userTaskGeneralManagerAssigneeList")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskGeneralManager.getActivityId(), userTaskGeneralManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
    }

    @Test
    public void deployModel() {
        List<String> modelIdList = Arrays.asList("5587500","5587502");
        for (String modelId : modelIdList) {
            String deployId = modelApiService.deploy(modelId);
            log.info("发布id:{}", deployId);
        }
    }


}
