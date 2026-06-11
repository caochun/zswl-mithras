package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
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
 * 用于测试消息的特殊流程
 *
 * @author wangchuanhao
 * @date 2023/1/6 2:48 PM
 */
public class SpecialTestFlowTest extends FlowTest {

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
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.CANCEL.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

        UserTaskExt userTask2 = UserTaskExt.builder()
                .activityId("userTask_2")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTask2.getActivityId(), userTask2);

        UserTaskExt userTaskExtRiskManager = UserTaskExt.builder()
                .activityId("userTask_3")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET.getType())
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtRiskManager.getActivityId(), userTaskExtRiskManager);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
    }

}
