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
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 立项
 *
 * @author wangchuanhao
 * @date 2022/8/8 10:24 PM
 */
public class ProjEstablishFlowTest extends FlowTest {

    @Test
    public void projEstablishFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/立项创建审批流程.bpmn20.xml",
                "bpmn/立项修改审批流程.bpmn20.xml");
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

            // 风险管理部负责人
            UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
                    .activityId("userTask_riskDeptMaster")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.riskdeptmanager.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    // 设置风控经理
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.projEstablish_setRiskManager.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);

            // 风控经理
            UserTaskExt userTaskExtRiskManager = UserTaskExt.builder()
                    .activityId("userTask_riskManager")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR.getType())
                    .userDefineVarName("riskControlManager")
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.COLLABORATE.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
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
