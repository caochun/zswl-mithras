package cn.zswltech.mithras.others.flow;

import cn.zswltech.flow.core.domain.req.SaveModelReq;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.flow.core.model.ext.GlobalExt;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
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

import static cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum.AGREE;
import static cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum.BACK_TO_START_USER;
import static cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum.CANCEL;
import static cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum.SUBMIT;
import static cn.zswltech.flow.core.enums.ApprovalMethodEnum.PARALLEL;
import static cn.zswltech.flow.core.enums.ParallelApprovalMethedEnum.ONE;
import static cn.zswltech.flow.core.enums.UserDefineTypeEnum.START_USER;
import static cn.zswltech.mithras.workflow.flow.constant.FlowConstants.START_USER_TASK;

/**
 * 客户流程
 *
 * @author wangchuanhao
 * @date 2022/8/8 10:16 PM
 */
public class CreditReportFlowTest extends FlowTest {

    /**
     * 客户修改
     *
     * @throws Exception
     */
    @Test
    public void creditReportFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/征信报送审批流程.bpmn20.xml");
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

            // 风险管理部负责人
            UserTaskExt userTaskExtRiskDeptMaster = UserTaskExt.builder()
                    .activityId("userTask_riskDeptMaster")
                    .approvalMethod(PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.riskdeptmanager.name())
                    .parallelApprovalMethed(ONE.getType())
                    .buttonList(Arrays.asList(AGREE.name(), BACK_TO_START_USER.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtRiskDeptMaster.getActivityId(), userTaskExtRiskDeptMaster);
            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            modelIdList.add(modelId);
            log.info("流程模型id:{}", modelId);
        }
        log.info("流程模型id列表:{}", JSON.toJSONString(modelIdList));
    }

}
