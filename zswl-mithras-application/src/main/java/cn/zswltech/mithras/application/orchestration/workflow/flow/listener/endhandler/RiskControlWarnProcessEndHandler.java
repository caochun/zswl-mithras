package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlFlowVariable;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.*;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author yibin
 */
@Slf4j
@Component
public class RiskControlWarnProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;

    @Override
    public void handle(ProcessEndContext endContext) {
        Long id = Long.valueOf(endContext.getBusinessKey());
        log.info("client transfer process warn end. id:{}, endType:{}, startUserId:{}, processInstanceId:{}",
                id, endContext.getEndType(), endContext.getStartUserId(), endContext.getProcessInstanceId());
        boolean passed = ProcessBusinessStatusEnum.success(endContext.getEndType());
        boolean rejected = endContext.getEndType().intValue() == ProcessBusinessStatusEnum.REJECT.getType().intValue()
                || endContext.getEndType().intValue() == ProcessBusinessStatusEnum.REJECT_ALL.getType().intValue();
        //默认关闭
        boolean handleFlay = false;
        if (CharSequenceUtil.equalsAny(endContext.getModelKey(), RiskControlWarnNotPaymentFlow.name(), RiskControlWarnPaymentFlow.name())) {
            Integer variable = (Integer) getBean(RuntimeService.class).getVariable(endContext.getProcessInstanceId(), RiskControlFlowVariable.HANDLE_TYPE);
            if (ObjectUtil.isNotEmpty(variable) && variable > 0) {
                handleFlay = true;
            }
        }
        if (handleFlay) {
            if (passed) {
                // 审批通过
                riskControlWarnMonitorService
                        .updateById(new RiskControlWarnMonitor().setHandleStatus(RiskControlOpinionHandleStatus.HANDLED.name()).setId(id));
            } else if (rejected) {
                // 审批拒绝
                riskControlWarnMonitorService.updateById(new RiskControlWarnMonitor().setHandleStatus(RiskControlOpinionHandleStatus.REJECTED.name()).setId(id));
            } else {
                riskControlWarnMonitorService.updateById(new RiskControlWarnMonitor().setHandleStatus(RiskControlOpinionHandleStatus.PEND_HANDLE.name()).setId(id));
            }
        } else {
            if (passed) {
                // 审批通过
                riskControlWarnMonitorService.updateById(new RiskControlWarnMonitor().setHandleStatus(RiskControlOpinionHandleStatus.CLOSED.name()).setId(id));
            } else if (rejected) {
                // 审批拒绝
                riskControlWarnMonitorService.updateById(new RiskControlWarnMonitor().setHandleStatus(RiskControlOpinionHandleStatus.REJECTED.name()).setId(id));
            } else {
                riskControlWarnMonitorService.updateById(new RiskControlWarnMonitor().setHandleStatus(RiskControlOpinionHandleStatus.PEND_HANDLE.name()).setId(id));
            }
        }

    }

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return CharSequenceUtil.equalsAny(endContext.getModelKey(), RiskControlWarnNotPaymentFlow.name(), RiskControlWarnPaymentFlow.name());
    }
}
