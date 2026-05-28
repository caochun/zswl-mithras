package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.service.service.ProcessModifyRemarkService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;

/**
 * @author yibin
 */
@Component
public class ProcessModifyRemarkEndHandler extends AbstractProcessEndHandler {
    @Resource
    private ProcessModifyRemarkService remarkService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(),
                GroupCreditReviewCreateFlow.name(),
                GroupCreditEstablishModifyFlow.name(),
                GroupCreditReviewModifyFlow.name(),
                ContractModifyFlow.name(),
                ProjReviewCreateFlow.name(),
                ProjReviewModifyFlow.name(),
                FundFinancingModifyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        remarkService.processEnd(
                Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(),
                endContext.getModelKey(), Long.valueOf(endContext.getProcessInstanceId()));
    }
}
