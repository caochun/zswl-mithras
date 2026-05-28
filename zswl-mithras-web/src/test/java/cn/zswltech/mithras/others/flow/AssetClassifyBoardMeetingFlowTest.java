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
 * @date 2023/1/6
 * @description
 */
public class AssetClassifyBoardMeetingFlowTest extends FlowTest {
    @Test
    public void assetClassifyBoardMeetingFlow() throws Exception {
        String fileName = "/bpmn/资产五级分类董事会审批流程.bpmn20.xml";
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

        // 董事会
        UserTaskExt userTaskReviewMeeting = UserTaskExt.builder()
                .activityId("userTask_boardMeeting")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.director.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskReviewMeeting.getActivityId(), userTaskReviewMeeting);

        // 秘书汇总
        UserTaskExt userTaskSecretary = UserTaskExt.builder()
                .activityId("userTask_secretary")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                .jobCode(JobEnum.boardsecretary.name())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Collections.singletonList(ApprovalButtonTypeEnum.SUBMIT.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskSecretary.getActivityId(), userTaskSecretary);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("发布id:{}", deployId);
    }
}
