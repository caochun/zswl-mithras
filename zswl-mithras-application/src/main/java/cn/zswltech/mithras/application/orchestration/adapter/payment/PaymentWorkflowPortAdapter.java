package cn.zswltech.mithras.application.orchestration.adapter.payment;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.payment.application.PaymentWorkflowPort;
import cn.zswltech.mithras.payment.application.PaymentWorkflowProcessSnapshot;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.service.ProcessService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Component
public class PaymentWorkflowPortAdapter implements PaymentWorkflowPort {

    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private ProcessService processService;

    @Override
    public PaymentWorkflowProcessSnapshot getProcessByInstanceId(String processInstanceId) {
        return toSnapshot(flowTaskApiService.queryProcessById(processInstanceId));
    }

    @Override
    public PaymentWorkflowProcessSnapshot getLatestPassedProjectReviewProcess(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()));
        processPageReq.setBusinessKeyList(Collections.singletonList(String.valueOf(projReviewId)));
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        processPageReq.setSortType(1);
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (CollectionUtil.isEmpty(processRespPage.getContents())) {
            return null;
        }
        return toSnapshot(processRespPage.getContents().get(0));
    }

    @Override
    public PaymentWorkflowProcessSnapshot getRunningPaymentCreateProcess(Long paymentId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(String.valueOf(paymentId));
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        processPageReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.PaymentCreateFlow.name()));
        Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (processRespPage.getTotal() > 1) {
            throw new MithrasException("当前付款申请存在两个流程");
        }
        if (processRespPage.getTotal() == 0 || CollectionUtil.isEmpty(processRespPage.getContents())) {
            return null;
        }
        return toSnapshot(processRespPage.getContents().get(0));
    }

    @Override
    public boolean isPaymentCreateProcess(String modelKey) {
        return Objects.equals(ProcessModelTypeEnum.PaymentCreateFlow.name(), modelKey);
    }

    @Override
    public boolean isPaymentActualDetailInProcess(Long paymentActualDetailId) {
        return processService.isInProcess(String.valueOf(paymentActualDetailId),
                Collections.singletonList(ProcessModelTypeEnum.PaymentActualDetailFlow.name()));
    }

    @Override
    public boolean isAutoRentOrReceiptInProcess(Long contractId) {
        return processService.isInProcess(String.valueOf(contractId),
                Arrays.asList(ProcessModelTypeEnum.ContractStartRentAutoFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name()));
    }

    private PaymentWorkflowProcessSnapshot toSnapshot(ProcessResp processResp) {
        if (processResp == null) {
            return null;
        }
        return PaymentWorkflowProcessSnapshot.builder()
                .processInstanceId(processResp.getProcessInstanceId())
                .modelKey(processResp.getModelKey())
                .curTaskActivityIds(processResp.getCurTaskActivityIds())
                .curAssigneeIds(processResp.getCurAssigneeIds())
                .build();
    }
}
