package cn.zswltech.mithras.others.flow;

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
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 14:19
 */
public class FtpFlow extends FlowTest{
    @Test
    public void buildFlow() throws Exception {
        List<String> fileNameList = Arrays.asList("bpmn/季度最低收益率指导创建审批流程.bpmn20.xml",
                "bpmn/季度最低收益率指导变更审批流程.bpmn20.xml", "bpmn/月度ftp定价指导创建审批流程.bpmn20.xml",
                "bpmn/月度ftp定价指导变更审批流程.bpmn20.xml");
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

            // 定价会秘书
            UserTaskExt userTaskExtSecretary1 = UserTaskExt.builder()
                    .activityId("userTask_secretary_1")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.pricingcommitteesecretary.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.SUBMIT.name(), ApprovalButtonTypeEnum.BACK_TO_START_USER.name(), ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.ftp_chooseJudges.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtSecretary1.getActivityId(), userTaskExtSecretary1);

            // 定价委员非公开投票 投票节点
            UserTaskExt userTaskExtPricingCommittee = UserTaskExt.builder()
                    .activityId("userTask_pricing_committee")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.CUSTOM.getType())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ALL.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.VOTE_AGREE.name(),
                            ApprovalButtonTypeEnum.VOTE_DISAGREE.name(),
                            ApprovalButtonTypeEnum.WITHDRAW_TASK.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtPricingCommittee.getActivityId(), userTaskExtPricingCommittee);

            // 评审会秘书统计投票结果
            UserTaskExt userTaskExtSecretary2 = UserTaskExt.builder()
                    .activityId("userTask_secretary_2")
                    .approvalMethod(ApprovalMethodEnum.PARALLEL.getType())
                    .approverType(UserDefineTypeEnum.PROCESS_START_JOB.getType())
                    .jobCode(JobEnum.pricingcommitteesecretary.name())
                    .parallelApprovalMethed(ParallelApprovalMethedEnum.ONE.getType())
                    .buttonList(Arrays.asList(ApprovalButtonTypeEnum.AGREE.name(),
                            ApprovalButtonTypeEnum.DISAGREE_BACK_TO_START_USER.name()))
                    // 显示投票结果 上传会议纪要
                    .dynamicFormList(Arrays.asList(FlowDynamicFormEnum.ftp_setSupplement.name(),
                            FlowDynamicFormEnum.ftp_showVotingResults.name(),
                            FlowDynamicFormEnum.ftp_setMeetingFile.name()))
                    .build();
            globalExt.getUserTaskExtMap().put(userTaskExtSecretary2.getActivityId(), userTaskExtSecretary2);

            req.setEditorJson(editorJsonNode.toString());
            req.setGlobalExt(globalExt);
            String modelId = modelApiService.save(req);
            modelIdList.add(modelId);
            log.info("流程模型id:{}", modelId);
        }
        log.info("流程模型id列表:{}", JSON.toJSONString(modelIdList));
    }
}
