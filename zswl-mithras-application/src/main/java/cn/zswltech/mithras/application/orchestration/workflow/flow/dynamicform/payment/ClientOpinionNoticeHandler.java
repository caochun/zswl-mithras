package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.payment;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.api.payment.dto.PaymentTransactionStructureInfoReq;
import cn.zswltech.mithras.api.payment.dto.PaymentTransactionStructureInfoRsp;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.application.orchestration.client.ProjClientRoleService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 不会阻断用户操作的舆情信息检查处理器，仅提醒
 * @author: zhaozhengkang
 * @date: 2023/7/26 10:07
 */
@Component
public class ClientOpinionNoticeHandler implements DynamicFormHandler {
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ClientService clientService;
    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        Long paymentId = Long.valueOf(rsp.getBusinessKey());
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (paymentBaseInfo == null) {
            throw new RuntimeException("付款申请不存在");
        }

        PaymentTransactionStructureInfoReq structureInfoReq = new PaymentTransactionStructureInfoReq();
        structureInfoReq.setPaymentId(paymentId);
        List<PaymentTransactionStructureInfoRsp> infoRspList = paymentBaseInfoService.getPaymentTransactionStructureInfoRspList(structureInfoReq);

        Set<String> uscCodes = clientService.list(Wrappers.<Client>lambdaQuery().in(Client::getId, infoRspList.stream().map(PaymentTransactionStructureInfoRsp::getClientId).collect(Collectors.toList())))
                .stream().map(Client::getUscCode).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(uscCodes)) {
            throw new RuntimeException("付款合同相关客户不存在");
        }
        List<RiskControlOpinionMonitor> needHandleOpinions = riskControlOpinionMonitorService.list(Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                .notIn(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.HANDLED.name()
                        , RiskControlOpinionHandleStatus.IGNORED.name(), RiskControlOpinionHandleStatus.CLOSED.name())
                .in(RiskControlOpinionMonitor::getCreditCode, uscCodes));
        rsp.getDynamicFormData().put("needHandleOpinions", needHandleOpinions);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.payment_clientOpinionNotice;
    }
}
