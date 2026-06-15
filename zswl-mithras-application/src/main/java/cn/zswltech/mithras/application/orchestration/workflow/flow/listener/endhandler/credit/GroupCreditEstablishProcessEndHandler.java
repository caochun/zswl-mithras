package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.credit;

import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.credit.application.groupcredit.establish.GroupCreditEstablishService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

@Component
public class GroupCreditEstablishProcessEndHandler extends AbstractProcessEndHandler {

    @Resource
    private GroupCreditEstablishService groupCreditEstablishService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        groupCreditEstablishService.processEnd(endContext.getModelKey(), Long.valueOf(endContext.getBusinessKey()), endContext.getEndType(), Long.valueOf(endContext.getStartUserId()), endContext.getProcessInstanceId());
    }
}
