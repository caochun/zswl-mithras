package cn.zswltech.mithras.payment.flow.dynamicform.payment;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 付款核销流程-财务经理节点补充收款信息
 */
@Component
public class PaymentShowPaymentInfoHandler implements DynamicFormHandler {
    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {
//        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.valueOf(rsp.getBusinessKey()));
//        if (paymentBaseInfo == null) {
//            throw new RuntimeException("付款申请不存在");
//        }
//        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
//        if (contractBaseInfo == null) {
//            throw new RuntimeException("合同不存在");
//        }
//        Set<Long> relateClientIds = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
//                        .eq(ProjClientRole::getMainId, contractBaseInfo.getMainId())
//                        .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()))
//                .stream().map(ProjClientRole::getClientId).collect(Collectors.toSet());
//        Set<String> uscCodes = clientService.list(Wrappers.<Client>lambdaQuery().in(Client::getId, relateClientIds)).stream().map(Client::getUscCode).collect(Collectors.toSet());
//        if (ObjectUtil.isEmpty(uscCodes)) {
//            throw new RuntimeException("付款合同相关客户不存在");
//        }
//        List<RiskControlOpinionMonitor> needHandleOpinions = riskControlOpinionMonitorService.list(Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
//                .notIn(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.HANDLED.name()
//                        , RiskControlOpinionHandleStatus.IGNORED.name())
//                .in(RiskControlOpinionMonitor::getCreditCode, uscCodes));
//        rsp.getDynamicFormData().put("needHandleOpinions", needHandleOpinions);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.payment_showPaymentInfo;
    }
}
