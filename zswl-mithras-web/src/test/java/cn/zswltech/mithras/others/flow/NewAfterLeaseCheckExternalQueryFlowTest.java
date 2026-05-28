package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.ApprovalMethodEnum;
import cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.service.constant.FlowConstants;
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

/**
 * @author zhaozhengkang
 * @date 2022/11/18
 * @description
 */
public class NewAfterLeaseCheckExternalQueryFlowTest extends FlowTest {
    @Test
    public void submitProjectReportFlow() throws Exception {
        String fileName = "bpmn/租后检查外部信息查询审批流程.bpmn20.xml";
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
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(),
                        ApprovalButtonTypeEnum.CANCEL.name()))
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
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name(),
                        ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtDeptMaster.getActivityId(), userTaskExtDeptMaster);

        // 业务分管领导
        UserTaskExt userTaskExtBizDivisionLeader = UserTaskExt.builder()
                .activityId("userTask_bizDivisionLeader")
                .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("bizDivisionLeader")
                .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                        ApprovalButtonTypeEnum.DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(userTaskExtBizDivisionLeader.getActivityId(), userTaskExtBizDivisionLeader);
        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);
        String deployId = modelApiService.deploy(modelId);
        log.info("部署模型id:{}", deployId);
    }
}
