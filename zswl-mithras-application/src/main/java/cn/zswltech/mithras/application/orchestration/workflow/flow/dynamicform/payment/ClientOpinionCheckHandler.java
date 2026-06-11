package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.payment;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ProjClientRole;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.foundation.exception.MithrasException;
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
 * @description: 阻断用户操作的舆情信息检查处理器
 * @author: zhaozhengkang
 * @date: 2023/7/26 10:07
 */
@Component
public class ClientOpinionCheckHandler implements DynamicFormHandler {
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
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Long paymentId = Long.valueOf(taskResp.getBusinessKey());
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
        if (paymentBaseInfo == null) {
            throw new RuntimeException("付款申请不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if (contractBaseInfo == null) {
            throw new RuntimeException("合同不存在");
        }
        Set<Long> relateClientIds = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
                        .eq(ProjClientRole::getMainId, contractBaseInfo.getMainId())
                        .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()))
                .stream().map(ProjClientRole::getClientId).collect(Collectors.toSet());
        Set<String> uscCodes = clientService.list(Wrappers.<Client>lambdaQuery().in(Client::getId, relateClientIds)).stream().map(Client::getUscCode).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(uscCodes)) {
            throw new RuntimeException("付款合同相关客户不存在");
        }
        List<RiskControlOpinionMonitor> needHandleOpinions = riskControlOpinionMonitorService.list(Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                .notIn(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.HANDLED.name()
                        , RiskControlOpinionHandleStatus.IGNORED.name(), RiskControlOpinionHandleStatus.CLOSED.name())
                .in(RiskControlOpinionMonitor::getCreditCode, uscCodes));
        if (ObjectUtil.isNotEmpty(needHandleOpinions)) {
            throw new MithrasException("该合同存在未处理完成的舆情信息，请处理后再提交！");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.payment_clientOpinionCheck;
    }
}
