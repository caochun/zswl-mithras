package cn.zswltech.mithras.workflow.flow.listener.endhandler;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ContractModifyFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.FundFinancingModifyFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.GroupCreditEstablishModifyFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.GroupCreditReviewCreateFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.GroupCreditReviewModifyFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ProjReviewCreateFlow;
import static cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum.ProjReviewModifyFlow;

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
