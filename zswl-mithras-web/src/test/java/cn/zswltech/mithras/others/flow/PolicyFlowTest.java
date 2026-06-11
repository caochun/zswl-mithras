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
 * @create: 2022-11-22
 **/

public class PolicyFlowTest extends FlowTest {

    @Test
    public void rentCollectionFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/保单创建审批流程.bpmn20.xml",
                "bpmn/保单变更审批流程.bpmn20.xml");
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

            // 资产管理岗
            UserTaskExt userTaskExtFinanceManager = UserTaskExt.builder()
                    .activityId("userTask_assetmanagement")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.assetmanagement.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(), ApprovalButtonTypeEnum.DISAGREE.name(),ApprovalButtonTypeEnum.BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtFinanceManager.getActivityId(), userTaskExtFinanceManager);

            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            modelIdList.add(modelId);
            log.info("流程模型id:{}", modelId);
        }
        log.info("流程模型id列表:{}", JSON.toJSONString(modelIdList));
    }

}
