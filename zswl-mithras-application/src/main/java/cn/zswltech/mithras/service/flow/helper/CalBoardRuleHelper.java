package cn.zswltech.mithras.service.flow.helper;
import cn.zswltech.mithras.workflow.domain.enums.ProcessVarEnum;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import org.flowable.engine.HistoryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * 合同模块、付款模块是否走到董事长
 * 判断条件中不再动态计算风险敞口，而需关联本项目在最新一次审批通过的项目评审流程中的网关判断结果
 * https://eyvwt4pfvp.feishu.cn/docx/doxcn2bwUODQ3cz9r5nzl6N74rc
 *
 * @author wangchuanhao
 * @date 2022/9/8 3:52 PM
 */
@Component
public class CalBoardRuleHelper {

    @Resource
    private HistoryService historyService;
    @Resource
    private FlowTaskApiService taskApiService;

    /**
     * 判断合同是否经过董事长审批
     * @param contractId
     * @return
     */
    public boolean contractNeedChairmanApprove(Long contractId) {
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ContractCreateFlow.name()));
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        flowReq.setBusinessKey(String.valueOf(contractId));
        flowReq.setSortType(1);
        flowReq.setPageSize(1);
        flowReq.setPageIndex(1);
        ProcessResp processResp = taskApiService.queryProcess(flowReq).getContents().stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            return false;
        }
        // 判断有没有走到过董事长
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processResp.getProcessInstanceId())
                .activityId("userTask_chairman")
                .count() > 0;
    }

    /**
     * 判断是否需要董事会审批
     * 查看上一次
     * @param projReviewId
     * @return
     */
    public boolean needBoardApprove(Long projReviewId) {
        // 找到该业务所属的流程 最新的 审批通过的 流程类型为创建、变更
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()));
        flowReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        flowReq.setBusinessKey(String.valueOf(projReviewId));
        flowReq.setSortType(1);
        flowReq.setPageSize(1);
        flowReq.setPageIndex(1);
        ProcessResp processResp = taskApiService.queryProcess(flowReq).getContents().stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            return false;
        }
        // 判断有没有走到过董事会投票节点
        Long directorVoteTaskCount = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processResp.getProcessInstanceId())
                .activityId("userTask_directorVote")
                .count();
        if (directorVoteTaskCount == 0) {
            directorVoteTaskCount = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processResp.getProcessInstanceId())
                    .activityId("userTask_directorSecretaryCollect")
                    .count();
        }
        return directorVoteTaskCount > 0;
//        Boolean insideProvinceFlag = (Boolean) historyService.createHistoricVariableInstanceQuery().excludeTaskVariables().variableName(ProcessVarEnum.InsideProvinceFlag.name()).singleResult().getValue();
//        String projectType = (String) historyService.createHistoricVariableInstanceQuery().excludeTaskVariables().variableName(ProcessVarEnum.ProjectType.name()).singleResult().getValue();
//        Double riskExposure = (Double) historyService.createHistoricVariableInstanceQuery().excludeTaskVariables().variableName(ProcessVarEnum.riskExposure.name()).singleResult().getValue();
//        if (Objects.isNull(insideProvinceFlag) || StringUtils.isBlank(projectType) || Objects.isNull(riskExposure)) {
//            return false;
//        }
//        // 注意 此处逻辑需与 ProjReviewCreateFlow ProjReviewModifyFlow 的审批流走线逻辑保持一致
//        if (ProjectType.PUBLIC_UTILITIES.name().equals(projectType)) {
//            if (Boolean.TRUE.equals(insideProvinceFlag)) {
//                return riskExposure > 20000_0000;
//            } else {
//                return riskExposure > 10000_0000;
//            }
//        } else if (ProjectType.STATE_OWNED_ENTERPRISE.name().equals(projectType)) {
//            return riskExposure > 10000_0000;
//        } else {
//            return riskExposure > 8000_0000;
//        }
    }

}
