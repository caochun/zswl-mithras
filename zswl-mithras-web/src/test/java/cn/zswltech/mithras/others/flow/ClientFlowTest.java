package cn.zswltech.mithras.others.flow;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum.*;
import static cn.zswltech.flow.core.enums.ApprovalMethodEnum.PARALLEL;
import static cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum.ONE;
import static cn.zswltech.flow.core.enums.UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR;
import static cn.zswltech.flow.core.enums.UserDefineTypeEnum.START_USER;
import static cn.zswltech.mithras.workflow.flow.constant.FlowConstants.START_USER_TASK;

/**
 * 客户流程
 *
 * @author wangchuanhao
 * @date 2022/8/8 10:16 PM
 */
public class ClientFlowTest extends FlowTest {

    @Test
    @SneakyThrows
    public void clientTransferFlow() {
        InputStream bpmnStream = new ClassPathResource("bpmn/客户移交.bpmn20.xml").getInputStream();
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
        //发起人
        UserTaskExt startUser = UserTaskExt.builder().activityId(START_USER_TASK)
                .approvalMethod(PARALLEL.getType())
                .approverType(START_USER.getType())
                .parallelApprovalMethed(ONE.getType())
                .buttonList(ListUtil.toList(SUBMIT.name(), CANCEL.name()))
                .skipFirst(true)
                .build();
        globalExt.getUserTaskExtMap().put(startUser.getActivityId(), startUser);
        //接收人
        UserTaskExt receiverTask = UserTaskExt.builder().activityId("userTask_receiver")
                .approvalMethod(PARALLEL.getType())
                .approverType(PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("receiver")
                .parallelApprovalMethed(ONE.getType())
                .buttonList(ListUtil.toList(AGREE.name(), DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(receiverTask.getActivityId(), receiverTask);
        //
        UserTaskExt originDeptLeaderTask = UserTaskExt.builder().activityId("userTask_originDeptLeader")
                .approvalMethod(PARALLEL.getType())
                .approverType(PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("originDeptLeader")
                .parallelApprovalMethed(ONE.getType())
                .buttonList(ListUtil.toList(AGREE.name(), DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(originDeptLeaderTask.getActivityId(), originDeptLeaderTask);
        //
        UserTaskExt originDivisionLeaderTask = UserTaskExt.builder().activityId("userTask_originDivisionLeader")
                .approvalMethod(PARALLEL.getType())
                .approverType(PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("originDivisionLeader")
                .parallelApprovalMethed(ONE.getType())
                .buttonList(ListUtil.toList(AGREE.name(), DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(originDivisionLeaderTask.getActivityId(), originDivisionLeaderTask);
        //
        UserTaskExt targetDeptLeaderTask = UserTaskExt.builder().activityId("userTask_targetDeptLeader")
                .approvalMethod(PARALLEL.getType())
                .approverType(PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("targetDeptLeader")
                .parallelApprovalMethed(ONE.getType())
                .buttonList(ListUtil.toList(AGREE.name(), DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(targetDeptLeaderTask.getActivityId(), targetDeptLeaderTask);
        //
        UserTaskExt targetDivisionLeaderTask = UserTaskExt.builder().activityId("userTask_targetDivisionLeader")
                .approvalMethod(PARALLEL.getType())
                .approverType(PROCESS_START_TARGET_BY_VAR.getType())
                .userDefineVarName("targetDivisionLeader")
                .parallelApprovalMethed(ONE.getType())
                .buttonList(ListUtil.toList(AGREE.name(), DISAGREE.name()))
                .build();
        globalExt.getUserTaskExtMap().put(targetDivisionLeaderTask.getActivityId(), targetDivisionLeaderTask);

        req.setEditorJson(editorJsonNode.toString());
        req.setGlobalExt(globalExt);
        String modelId = modelApiService.save(req);
        log.info("流程模型id:{}", modelId);

    }

    /**
     * 客户修改
     *
     * @throws Exception
     */
    @Test
    public void clientModifyFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/客户修改审批流程.bpmn20.xml");
        List<String> modelIdList = new ArrayList<>();
        for (String fileName : fileNameList) {
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
                    .activityId(START_USER_TASK)
                    .approvalMethod(PARALLEL.getType())
                    .approverType(START_USER.getType())
                    .parallelApprovalMethed(ONE.getType())
                    .buttonList(Arrays.asList(SUBMIT.name(), CANCEL.name()))
                    .skipFirst(true)
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtStartUser.getActivityId(), userTaskExtStartUser);

            // 风控经理节点
            UserTaskExt userTaskExtRiskManager = UserTaskExt.builder()
                    .activityId(FlowConstants.RISK_MANAGER_TASK)
                    .approvalMethod(PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.riskmanager.name())
                    .parallelApprovalMethed(ONE.getType())
                    .buttonList(Arrays.asList(AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtRiskManager.getActivityId(), userTaskExtRiskManager);
            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            modelIdList.add(modelId);
            log.info("流程模型id:{}", modelId);
        }
        log.info("流程模型id列表:{}", JSON.toJSONString(modelIdList));
    }

}
